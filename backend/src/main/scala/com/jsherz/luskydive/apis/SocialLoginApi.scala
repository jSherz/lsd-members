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

package com.jsherz.luskydive.apis

import java.util.UUID

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.jsherz.luskydive.core.{FBSignedRequest, Member}
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.json.{SocialLoginRequest, SocialLoginResponse}
import com.jsherz.luskydive.services.{JwtService, SocialService}
import org.joda.time.{DateTime, Months}

import scala.concurrent.ExecutionContext

/**
  * Used to authenticate users with a social single-sign-on.
  */
class SocialLoginApi(
                      private val service: SocialService,
                      private val memberDao: MemberDao,
                      private val jwtService: JwtService
                    )
                    (implicit ec: ExecutionContext, log: LoggingAdapter) {

  import com.jsherz.luskydive.json.SocialLoginJsonSupport._

  private val tokenValidHours = 24

  private val invalidSignedRequest: Route =
    complete(StatusCodes.Unauthorized, SocialLoginResponse(success = false, Some("Invalid signed request."), None))

  val socialLoginRoute: Route = (pathEnd & post & entity(as[SocialLoginRequest])) { req =>
    service.parseSignedRequest(req.signedRequest)
      .fold(invalidSignedRequest)(handleFbRequest)
  }

  private def handleFbRequest(request: FBSignedRequest): Route = {
    onSuccess(memberDao.forSocialId(request.userId)) {
      _.fold(lookupFailed, issueJwtForMemberLookup(request.userId))
    }
  }

  private def issueJwtForMemberLookup(userId: String)(maybeMember: Option[Member]): Route = {
    maybeMember match {
      case Some(member: Member) =>
        val jwt = jwtService.createJwt(member.uuid.get, new DateTime(), new DateTime().plusHours(tokenValidHours))

        complete(SocialLoginResponse(success = true, None, Some(jwt)))

      case None =>
        log.error(s"Member not found with social ID: $userId")
        complete(StatusCodes.Unauthorized, SocialLoginResponse(success = false, Some("Login failed."), None))
    }
  }

  private def lookupFailed(error: String): Route = {
    log.error(s"Error while looking up member by social ID: $error")
    complete(StatusCodes.InternalServerError, "Login failed - please try again later.")
  }

  val getTokenRoute: Route = (path("token") & get) {
    complete(jwtService.createJwt(UUID.randomUUID(), new DateTime(), new DateTime().plus(Months.ONE)))
  }

  val route: Route = pathPrefix("social-login") {
    socialLoginRoute ~
      getTokenRoute
  }

}
