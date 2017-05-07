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

import java.time.{Duration, Instant}
import java.util.UUID

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.auth0.jwt.JWT
import com.jsherz.luskydive.core.{FBSignedRequest, Member}
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.json.MemberJsonSupport.MemberFormat
import com.jsherz.luskydive.json.SocialLoginJsonSupport._
import com.jsherz.luskydive.json.{SocialLoginRequest, SocialLoginResponse}
import com.jsherz.luskydive.services.{JwtService, JwtServiceImpl, SocialService}
import com.jsherz.luskydive.util.Util
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future
import scalaz.{-\/, \/, \/-}

class SocialLoginApiSpec extends WordSpec with Matchers with ScalatestRouteTest {

  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  "SocialLoginApi" should {

    "reject requests without an FB signed request" in {
      // All services would return valid response, isolates request parsing
      val api = buildApi(
        fbReq("14712371237123"),
        aMember(Some(Util.fixture[Member]("e1442281.json")))
      )

      Post("/social-login") ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

    "reject requests with an invalid signed request" in {
      val api = buildApi(
        None,
        aMember(Some(Util.fixture[Member]("e1442281.json")))
      )

      val request = SocialLoginRequest("blah blah blah")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
        responseAs[SocialLoginResponse] shouldEqual SocialLoginResponse(success = false, Some("Invalid signed request."), None)
      }
    }

    "reject signed requests when member lookup fails" in {
      val api = buildApi(
        fbReq("14712371237123"),
        anError("YOU'VE GOT FAIL")
      )

      val request = SocialLoginRequest("blah blah blah")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
        responseAs[String] shouldEqual "Login failed - please try again later."
      }
    }

    "reject signed requests for a member that doesn't exist" in {
      val api = buildApi(
        fbReq("14712371237123"),
        aMember(None)
      )

      val request = SocialLoginRequest("blah blah blah")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
        responseAs[SocialLoginResponse] shouldEqual SocialLoginResponse(success = false, Some("Login failed."), None)
      }
    }

    "issues a JWT if the signed request is for a valid member" in {
      val api = buildApi(
        fbReq("14712371237123"),
        aMember(Some(Util.fixture[Member]("e1442281.json")))
      )

      val request = SocialLoginRequest("blah blah blah")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.OK

        val socialLoginResponse = responseAs[SocialLoginResponse]
        socialLoginResponse.success shouldBe true
        socialLoginResponse.error shouldBe None

        val decoded = JWT.decode(socialLoginResponse.jwt.get)
        decoded.getClaim("UUID").asString() shouldEqual "e1442281-4972-456c-a94f-5b01f5b9b240"
        Duration.between(decoded.getIssuedAt.toInstant, Instant.now()).getSeconds shouldBe <=(5L)
        Duration.between(decoded.getIssuedAt.toInstant, decoded.getExpiresAt.toInstant).toHours shouldBe 24L
      }
    }

  }

  private def buildApi(fbReq: Option[FBSignedRequest], member: Future[\/[String, Option[Member]]]): Route = {
    val socialService = mock(classOf[SocialService])
    when(socialService.parseSignedRequest(any())).thenReturn(fbReq)

    val jwtService = new JwtServiceImpl("falafel")

    val memberDao = mock(classOf[MemberDao])
    when(memberDao.forSocialId(any())).thenReturn(member)

    val socialLoginApi = new SocialLoginApi(socialService, memberDao, jwtService)

    socialLoginApi.route
  }

  private def aMember(member: Option[Member]): Future[\/[String, Option[Member]]] = {
    Future.successful(\/-(member))
  }

  private def anError(error: String): Future[\/[String, Option[Member]]] = {
    Future.successful(-\/(error))
  }

  private def fbReq(userId: String): Option[FBSignedRequest] = {
    Some(new FBSignedRequest(
      userId,
      UUID.randomUUID().toString,
      Instant.now().plus(Duration.ofHours(24)).getEpochSecond,
      Instant.now().getEpochSecond
    ))
  }

}
