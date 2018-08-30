/**
  * MIT License
  *
  * Copyright (c) 2016-2018 James Sherwood-Jones <james.sherwoodjones@gmail.com>
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

import java.sql.Timestamp
import java.time.{Duration, Instant, LocalDateTime}
import java.util.UUID

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core.{CommitteeMember, FBSignedRequest, Member}
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.json.{SocialLoginRequest, SocialLoginResponse, SocialLoginUrlResponse, SocialLoginVerifyRequest}
import com.jsherz.luskydive.services.{JwtService, SocialService}
import com.restfb.types.User

import scala.concurrent.ExecutionContext
import scalaz.{-\/, \/-}

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
    complete(StatusCodes.Unauthorized, SocialLoginResponse(success = false, Some("Invalid signed request."), None,
      committeeMember = false))

  private val verificationFailed = complete(StatusCodes.Unauthorized, "Verification failed.")

  val socialLoginRoute: Route = (pathEnd & post & entity(as[SocialLoginRequest])) { req =>
    service.parseSignedRequest(req.signedRequest)
      .fold(invalidSignedRequest)(handleFbRequest)
  }

  val verifyRoute: Route = (path("verify") & post & entity(as[SocialLoginVerifyRequest])) { req =>
    service.getUserAccessToken(req.verificationCode).fold(_ => verificationFailed, { accessToken =>
      val userRequest = service.getUserForAccessToken(accessToken.getAccessToken)

      userRequest.fold(_ => verificationFailed, { user =>
        onSuccess(memberDao.forSocialId(user.getId)) {
          case \/-(maybeMember: Option[(Member, Option[CommitteeMember])]) => {
            useOrCreateMemberAndIssueJwt(maybeMember, user)
          }
          case -\/(error: String) => complete(StatusCodes.InternalServerError, error)
        }
      })
    })
  }

  private def handleFbRequest(request: FBSignedRequest): Route = {
    onSuccess(memberDao.forSocialId(request.userId)) {
      _.fold(lookupFailed, issueJwtForMemberLookup(request.userId))
    }
  }

  private def useOrCreateMemberAndIssueJwt(maybeMember: Option[(Member, Option[CommitteeMember])], user: User): Route = {
    maybeMember match {
      case Some(member: (Member, Option[CommitteeMember])) =>
        val jwt = generateJwt(member._1.uuid.get)

        complete(SocialLoginResponse(success = true, None, Some(jwt), committeeMember = member._2.isDefined))

      case None =>
        log.info(s"Member not found with social ID '${user.getId}', creating one.")

        createMember(user.getId)(user.getFirstName, user.getLastName, user.getEmail)
    }
  }

  private def issueJwtForMemberLookup(userId: String)(maybeMember: Option[(Member, Option[CommitteeMember])]): Route = {
    maybeMember match {
      case Some(member: (Member, Option[CommitteeMember])) =>
        val jwt = generateJwt(member._1.uuid.get)

        complete(SocialLoginResponse(success = true, None, Some(jwt), committeeMember = member._2.isDefined))

      case None =>
        log.info(s"Member not found with social ID '$userId', creating one.")

        val createMemberF = createMember(userId) _

        service.getNameAndEmail(userId).fold(_ => genericError, createMemberF.tupled)
    }
  }

  private def createMember(userId: String)(firstName: String, lastName: String, email: String): Route = {
    val member = buildMemberForSocialId(userId, firstName, lastName, email)
    onSuccess(memberDao.create(member)) {
      case \/-(newMemberUuid: UUID) =>
        val jwt = generateJwt(newMemberUuid)

        complete(StatusCodes.OK, SocialLoginResponse(success = true, None, Some(jwt), committeeMember = false))

      case -\/(error: String) =>
        log.error(s"Failed to create member: $error")
        genericError
    }
  }

  private def generateJwt(memberUuid: UUID) = {
    jwtService.createJwt(memberUuid, Instant.now(), Instant.now().plus(Duration.ofHours(tokenValidHours)))
  }

  private val genericError = complete(StatusCodes.InternalServerError, "Login failed - please try again later.")

  private def lookupFailed(error: String): Route = {
    log.error(s"Error while looking up member by social ID: $error")
    genericError
  }

  private def buildMemberForSocialId(userId: String, firstName: String, lastName: String, email: String): Member = {
    Member(
      uuid = Some(Generators.randomBasedGenerator.generate), firstName, Option(lastName), phoneNumber = None,
      email = Option(email), lastJump = None, weight = None, height = None, driver = false, organiser = false,
      createdAt = Timestamp.valueOf(LocalDateTime.now()), updatedAt = Timestamp.valueOf(LocalDateTime.now()),
      socialUserId = Some(userId)
    )
  }

  val route: Route = pathPrefix("social-login") {
    socialLoginRoute ~
      verifyRoute
  }

}
