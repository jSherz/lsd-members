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

import java.time.{Duration, Instant}
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.auth0.jwt.JWT
import com.jsherz.luskydive.core.{CommitteeMember, FBSignedRequest, Member}
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport.CommitteeMemberFormat
import com.jsherz.luskydive.json.MemberJsonSupport.MemberFormat
import com.jsherz.luskydive.json.SocialLoginJsonSupport._
import com.jsherz.luskydive.json.{SocialLoginRequest, SocialLoginResponse, SocialLoginVerifyRequest}
import com.jsherz.luskydive.services.{JwtServiceImpl, SocialService}
import com.jsherz.luskydive.util.Util
import com.restfb.FacebookClient.AccessToken
import com.restfb.types.User
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.{mock, verify, when}
import org.mockito.invocation.InvocationOnMock
import scalaz.{-\/, \/, \/-}

import scala.concurrent.Future
import scala.util.Random

class SocialLoginApiSpec extends BaseApiSpec {

  "SocialLoginApi#social-login" should {

    "reject requests without an FB signed request" in {
      // All services would return valid response, isolates request parsing
      val (api, _, _) = buildApi(
        fbReq("14712371237123"),
        aMember(Util.fixture[Member]("e1442281.json")),
        noNewMemberUuid
      )

      Post("/social-login") ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

    "reject requests with an invalid signed request" in {
      val (api, _, _) = buildApi(
        None,
        aMember(Util.fixture[Member]("e1442281.json")),
        noNewMemberUuid
      )

      val request = SocialLoginRequest("blah blah blah")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
        responseAs[SocialLoginResponse] shouldEqual SocialLoginResponse(success = false, Some("Invalid signed request."),
          None, committeeMember = false)
      }
    }

    "issues a JWT if the signed request is for a valid member" in {
      val (api, _, _) = buildApi(
        fbReq("14712371237123"),
        aMember(Util.fixture[Member]("e1442281.json")),
        noNewMemberUuid
      )

      val request = SocialLoginRequest("blah blah blah")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.OK

        val socialLoginResponse = responseAs[SocialLoginResponse]
        socialLoginResponse.success shouldBe true
        socialLoginResponse.error shouldBe None
        socialLoginResponse.committeeMember shouldBe false

        val decoded = JWT.decode(socialLoginResponse.jwt.get)
        decoded.getClaim("UUID").asString() shouldEqual "e1442281-4972-456c-a94f-5b01f5b9b240"
        Duration.between(decoded.getIssuedAt.toInstant, Instant.now()).getSeconds shouldBe <=(10L)
        Duration.between(decoded.getIssuedAt.toInstant, decoded.getExpiresAt.toInstant).toHours shouldBe 24L
      }
    }

    "creates a new member if one doesn't exist for the given social ID" in {
      val userId = "881847817242"
      val expectedUuid = UUID.randomUUID()

      val (api, socialService, memberDao) = buildApi(
        fbReq(userId),
        noMember(),
        newMemberUuid(expectedUuid)
      )

      when(socialService.getNameAndEmail(userId)).thenReturn(\/-(("Betty", "Smith", "betty.smith@hotmail.co.uk")))

      val request = SocialLoginRequest("pssst: this isn't actually used")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.OK

        val socialLoginResponse = responseAs[SocialLoginResponse]
        socialLoginResponse.success shouldBe true
        socialLoginResponse.error shouldBe None
        socialLoginResponse.committeeMember shouldBe false

        val decoded = JWT.decode(socialLoginResponse.jwt.get)
        decoded.getClaim("UUID").asString() shouldEqual expectedUuid.toString

        val argumentCaptor = ArgumentCaptor.forClass(classOf[Member])
        verify(memberDao).create(argumentCaptor.capture())

        val memberPassedToDao = argumentCaptor.getValue

        memberPassedToDao.firstName shouldBe "Betty"
        memberPassedToDao.lastName shouldBe Some("Smith")
        memberPassedToDao.socialUserId shouldBe Some(userId)
        memberPassedToDao.email shouldBe Some("betty.smith@hotmail.co.uk")
      }
    }

    "have the committee member flag set to true when the member has a linked committee record" in {
      val (api, _, _) = buildApi(
        fbReq("14712371237123"),
        aMember(Util.fixture[Member]("e1442281.json"), Some(Util.fixture[CommitteeMember]("956610c8.json"))),
        noNewMemberUuid
      )

      val request = SocialLoginRequest("blah blah blah")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.OK

        val socialLoginResponse = responseAs[SocialLoginResponse]
        socialLoginResponse.success shouldBe true
        socialLoginResponse.error shouldBe None
        socialLoginResponse.committeeMember shouldBe true
      }
    }

    "returns an error when creating a member fails" in {
      val (api, socialService, _) = buildApi(
        fbReq("12312314113"),
        noMember(),
        -\/("member creation failed - error in matrix")
      )

      when(socialService.getNameAndEmail(anyString)).thenReturn(\/-(("Frap", "Hat", "head.protection@iiiiiiii.js")))

      val request = SocialLoginRequest("pssst: this isn't actually used")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
        responseAs[String] shouldEqual "Login failed - please try again later."
      }
    }

    "returns an error when looking up a member's name & email fails" in {
      val (api, socialService, _) = buildApi(
        fbReq("15151818181"),
        noMember,
        newMemberUuid(UUID.randomUUID())
      )

      when(socialService.getNameAndEmail(anyString)).thenReturn(-\/("random FB error"))

      val request = SocialLoginRequest("pssst: this isn't actually used")

      Post("/social-login", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
        responseAs[String] shouldEqual "Login failed - please try again later."
      }
    }

  }

  "SocialLoginApi#social-login/verify" should {

    "return an error when obtaining the access token fails" in {
      val (api, socialService, _) = buildApi(
        fbReq("15151818181"),
        noMember(),
        newMemberUuid(UUID.randomUUID())
      )

      when(socialService.getUserAccessToken(anyString)).thenReturn(-\/("boohoo"))

      val request = SocialLoginVerifyRequest("blah-blah-blah")

      Post("/social-login/verify", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "Verification failed."
      }
    }

    "return an error when getting the user's social ID for the access token fails" in {
      val (api, socialService, _) = buildApi(
        fbReq("15151818181"),
        noMember(),
        newMemberUuid(UUID.randomUUID())
      )

      when(socialService.getUserAccessToken(anyString)).thenReturn(\/-(AccessToken.fromQueryString("?access_token=moo")))
      when(socialService.getUserForAccessToken(anyString)).thenReturn(-\/("getting user failed"))

      val request = SocialLoginVerifyRequest("blah-blah-blah")

      Post("/social-login/verify", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "Verification failed."
      }
    }

    "return a valid JWT when all lookups succeed (and the member exists)" in {
      val member = Util.fixture[Member]("e1442281.json")
      val (api, socialService, _) = buildApi(
        fbReq("15151818181"),
        aMember(member),
        newMemberUuid(UUID.randomUUID())
      )

      val expectedUser = new User()
      expectedUser.setFirstName("Liam")
      expectedUser.setLastName("Bloggs")
      expectedUser.setId(Random.nextLong().toString)

      when(socialService.getUserAccessToken(anyString)).thenReturn(\/-(AccessToken.fromQueryString("?access_token=moo")))
      when(socialService.getUserForAccessToken(anyString)).thenReturn(\/-(expectedUser))

      val request = SocialLoginVerifyRequest("blah-blah-blah")

      Post("/social-login/verify", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.OK

        val socialResponse = responseAs[SocialLoginResponse]
        socialResponse.jwt.isDefined shouldBe true
        socialResponse.jwt foreach { jwt =>
          val decoded = JWT.decode(jwt)

          decoded.getClaim("UUID").asString() shouldEqual member.uuid.toString
        }
      }
    }

    "return a valid JWT when all lookups succeed (and a member was created)" in {
      val (api, socialService, memberDao) = buildApi(
        fbReq(""),
        noMember(),
        newMemberUuid(UUID.randomUUID())
      )

      val expectedUser = new User()
      expectedUser.setFirstName("Lianne")
      expectedUser.setLastName("Bloggs")
      expectedUser.setId("1284189814")
      expectedUser.setEmail("farm@localhost.farms")

      when(socialService.getUserAccessToken(anyString)).thenReturn(\/-(AccessToken.fromQueryString("?access_token=moo")))
      when(socialService.getUserForAccessToken(anyString)).thenReturn(\/-(expectedUser))

      // Return the passed in UUID when the member is created
      when(memberDao.create(any[Member])).thenAnswer((invocation: InvocationOnMock) => {
        \/-(Future.successful(invocation.getArgumentAt(0, classOf[Member]).uuid))
      })

      val request = SocialLoginVerifyRequest("blah-blah-blah")

      Post("/social-login/verify", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.OK

        val socialResponse = responseAs[SocialLoginResponse]

        socialResponse.success shouldBe true
        socialResponse.error shouldBe None
        socialResponse.committeeMember shouldBe false

        socialResponse.jwt.isDefined shouldBe true
        socialResponse.jwt foreach { jwt =>
          // Ensure a member was created
          val createdMemberCaptor = ArgumentCaptor.forClass(classOf[Member])
          verify(memberDao).create(createdMemberCaptor.capture())

          val createdMember = createdMemberCaptor.getValue
          createdMember.firstName shouldEqual "Lianne"
          createdMember.lastName shouldEqual Some("Bloggs")
          createdMember.email shouldEqual Some("farm@localhost.farms")

          val decoded = JWT.decode(jwt)
          decoded.getClaim("UUID").asString() shouldEqual createdMemberCaptor.getValue.uuid.toString
        }
      }
    }

  }

  private def buildApi(
                        fbReq: Option[FBSignedRequest],
                        member: Future[Option[(Member, Option[CommitteeMember])]],
                        newMemberUuid: String \/ Future[UUID]
                      ): (Route, SocialService, MemberDao) = {

    val socialService = mock(classOf[SocialService])
    when(socialService.parseSignedRequest(anyString)).thenReturn(fbReq)

    val jwtService = new JwtServiceImpl("falafel")

    val memberDao = mock(classOf[MemberDao])
    when(memberDao.forSocialId(anyString)).thenReturn(member)
    when(memberDao.create(any[Member])).thenReturn(newMemberUuid)

    val socialLoginApi = new SocialLoginApi(socialService, memberDao, jwtService)

    (socialLoginApi.route, socialService, memberDao)
  }

  private def aMember(
                       member: Member,
                       committeeMember: Option[CommitteeMember] = None
                     ): Future[Option[(Member, Option[CommitteeMember])]] = {
    Future.successful(Some((member, committeeMember)))
  }

  private def noMember(): Future[Option[(Member, Option[CommitteeMember])]] = {
    Future.successful(None)
  }


  private def fbReq(userId: String): Option[FBSignedRequest] = {
    Some(new FBSignedRequest(
      userId,
      UUID.randomUUID().toString,
      Instant.now().plus(Duration.ofHours(24)).getEpochSecond,
      Instant.now().getEpochSecond
    ))
  }

  private val noNewMemberUuid: String \/ Future[UUID] =
    -\/("noNewMemberUuid")

  private def newMemberUuid(uuid: UUID): String \/ Future[UUID] = {
    \/-(Future.successful(uuid))
  }

}
