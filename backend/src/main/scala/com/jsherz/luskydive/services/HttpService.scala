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

import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers.{HttpOrigin, HttpOriginRange}
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.Directives._
import ch.megard.akka.http.cors.CorsSettings
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
                   authDao: AuthDao
                 )
                 (implicit executionContext: ExecutionContext) {

  implicit val auth = new ApiKeyAuthenticator(authDao).authenticateWithApiKey

  val signupRoutes = new SignupApi(memberDao)
  val coursesRoutes = new CoursesApi(courseDao)
  val committeeRoutes = new CommitteeMemberApi(committeeMemberDao)
  val memberRoutes = new MemberApi(memberDao)
  val courseSpacesApi = new CourseSpacesApi(courseSpaceDao)

  val loginApi = new LoginApi(authDao)

  val routes =
    handleRejections(Cors.rejectionHandler) {
      (pathPrefix("api") & pathPrefix("v1") & Cors.cors) {
        signupRoutes.route ~
          coursesRoutes.route ~
          courseSpacesApi.route ~
          committeeRoutes.route ~
          memberRoutes.route ~
          loginApi.route
      }
    }

}

object Cors {

  private val settings = CorsSettings.defaultSettings.copy(allowedOrigins = HttpOriginRange(
    HttpOrigin("http://localhost:4200"),
    HttpOrigin("https://dev.leedsskydivers.com"),
    HttpOrigin("https://www.leedsskydivers.com"),
    HttpOrigin("https://leedsskydivers.com")
  ))

  def cors: Directive0 = ch.megard.akka.http.cors.CorsDirectives.corsDecorate(settings).map(_ ⇒ ())

  /**
    * Ensure that authentication errors still have the correct CORS headers.
    *
    * Otherwise, the AJAX frontend will not accept the request and will instead set it to a status code of 0 (failed).
    */
  val rejectionHandler = RejectionHandler.newBuilder()
    .handle { case (AuthorizationFailedRejection | AuthenticationFailedRejection(_, _)) =>
      cors {
        complete {
          HttpResponse(StatusCodes.Unauthorized, entity = HttpEntity("Authorization failed"))
        }
      }
    }
    .result()

}
