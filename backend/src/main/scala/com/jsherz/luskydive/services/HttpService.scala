/**
  * MIT License
  *
  * Copyright (c) 2016 James Sherwood-Jones <james.sherwoodjones@gmail.com>
  *
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  *
  * The above copyright notice and this permission notice shall be included in all
  * copies or substantial portions of the Software.
  *
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
  */

package com.jsherz.luskydive.services

import java.util.UUID

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.{HttpOrigin, HttpOriginRange}
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import ch.megard.akka.http.cors.scaladsl.settings.CorsSettings
import ch.megard.akka.http.cors.scaladsl.{CorsDirectives, CorsRejection}
import com.jsherz.luskydive.apis._
import com.jsherz.luskydive.dao._

import scala.concurrent.ExecutionContext

/**
  * The holder of all configured routes.
  */
class HttpService(
                   memberDao: MemberDao,
                   courseDao: CourseDao,
                   committeeMemberDao: CommitteeMemberDao,
                   courseSpaceDao: CourseSpaceDao,
                   authDao: AuthDao,
                   massTextDao: MassTextDao,
                   textMessageDao: TextMessageDao,
                   textMessageReceiveApiKey: String
                 )
                 (implicit executionContext: ExecutionContext,
                  log: LoggingAdapter) {

  implicit val auth: Directive1[UUID] = new ApiKeyAuthenticator(authDao).authenticateWithApiKey

  val signupRoutes = new SignupApi(memberDao)
  val coursesRoutes = new CoursesApi(courseDao)
  val committeeRoutes = new CommitteeMemberApi(committeeMemberDao)
  val memberRoutes = new MemberApi(memberDao, textMessageDao)
  val courseSpacesApi = new CourseSpacesApi(courseSpaceDao)
  val massTextApi = new MassTextApi(massTextDao)
  val textMessageApi = new TextMessageApi(textMessageDao, memberDao, textMessageReceiveApiKey)

  val loginApi = new LoginApi(authDao)

  val cors: Cors = new Cors(log)

  val routes: server.Route =
    handleRejections(cors.rejectionHandler) {
      (pathPrefix("api") & pathPrefix("v1") & cors.cors) {
        signupRoutes.route ~
          coursesRoutes.route ~
          courseSpacesApi.route ~
          committeeRoutes.route ~
          memberRoutes.route ~
          loginApi.route ~
          massTextApi.route ~
          textMessageApi.route
      }
    }

}

class Cors(val log: LoggingAdapter) {

  private val settings = CorsSettings.defaultSettings.copy(allowedOrigins = HttpOriginRange(
    HttpOrigin("http://localhost:4200"),
    HttpOrigin("https://dev.leedsskydivers.com"),
    HttpOrigin("https://www.leedsskydivers.com"),
    HttpOrigin("https://leedsskydivers.com")
  ), allowedMethods = scala.collection.immutable.Seq(GET, PUT, POST, HEAD, OPTIONS))

  private val notFoundRoute: Route = ctx => {
    log.info(s"HTTP 404: ${ctx.request.uri}")

    ctx.complete(HttpResponse(StatusCodes.NotFound, entity = HttpEntity("Resource not found")))
  }

  def cors: Directive0 = CorsDirectives.corsDecorate(settings).map(_ ⇒ ())

  /**
    * Ensure that authentication errors still have the correct CORS headers.
    *
    * Otherwise, the AJAX frontend will not accept the request and will instead set it to a status code of 0 (failed).
    */
  def rejectionHandler: RejectionHandler = {
    RejectionHandler.newBuilder()
      .handleNotFound(notFoundRoute)
      .handle {
        case (AuthorizationFailedRejection | AuthenticationFailedRejection(_, _)) =>
          cors {
            complete {
              HttpResponse(StatusCodes.Unauthorized, entity = HttpEntity("Authorization failed"))
            }
          }
      }
      .handle {
        case (CorsRejection(origin, method, _)) =>
          ctx => {
            log.info(s"CORS rejection for origin $origin, method $method to ${ctx.request.uri} (${ctx.request.method})")

            ctx.complete {
              HttpResponse(StatusCodes.Forbidden, entity = HttpEntity("Unauthorized cross-origin request"))
            }
          }
      }
      .result()
  }

}
