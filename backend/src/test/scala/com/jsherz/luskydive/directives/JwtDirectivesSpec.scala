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

package com.jsherz.luskydive.directives

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpHeader, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.core.{CommitteeMember, Member}
import com.jsherz.luskydive.dao.{CommitteeMemberDao, MemberDao}
import com.jsherz.luskydive.services.JwtService
import com.jsherz.luskydive.util.Util
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.{Matchers, WordSpec}
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport.CommitteeMemberFormat

import scala.concurrent.Future
import scalaz.{-\/, \/, \/-}

class JwtDirectivesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  "JwtDirective#authenticateWithJwt" should {

    "reject requests with no JWT" in {
      // We ensure a valid UUID & member would be provided here to isolate the checking for a JWT
      val directive = buildDirective(Some(UUID.randomUUID()), aMember(Some(Util.fixture[Member]("0d6a5325.json"))))

      Get("/") ~> Route.seal(directive) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "reject requests where the JWT parsing fails" in {
      val directive = buildDirective(None, aMember(Some(Util.fixture[Member]("0d6a5325.json"))))

      Get("/").addHeader(jwtHeader("not ze jwt u were lkin 4")) ~> Route.seal(directive) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "reject requests where the JWT is valid but the member does not exist" in {
      val directive = buildDirective(Some(UUID.randomUUID()), aMember(None))

      Get("/").addHeader(jwtHeader("w/e this isn't read by the mock")) ~> Route.seal(directive) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "accept requests with a valid JWT and user" in {
      val directive = buildDirective(Some(UUID.randomUUID()), aMember(Some(Util.fixture[Member]("0d6a5325.json"))))

      Get("/").addHeader(jwtHeader("wub.wub.wub")) ~> directive ~> check {
        response.status shouldEqual StatusCodes.OK
      }
    }

  }

  "JwtDirective#authenticateCommitteeWithJwt" should {

    "reject requests with no JWT" in {
      val member = aMember(Some(Util.fixture[Member]("5f89a942.json")))
      val committeeMember = aCommitteeMember(Some(Util.fixture[CommitteeMember]("f5e0e6f1.json")))

      // We ensure a valid UUID , member & committee member would be provided here to isolate the checking for a JWT
      val directive = buildCommitteeDirective(Some(UUID.randomUUID()), member, committeeMember)

      Get("/") ~> Route.seal(directive) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "reject requests where the JWT parsing fails" in {
      val member = aMember(Some(Util.fixture[Member]("5f89a942.json")))
      val committeeMember = aCommitteeMember(Some(Util.fixture[CommitteeMember]("f5e0e6f1.json")))

      val directive = buildCommitteeDirective(None, member, committeeMember)

      Get("/").addHeader(jwtHeader("index.php?hacked=true")) ~> Route.seal(directive) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "reject requests where the JWT is valid but the member does not exist" in {
      val uuid = Some(UUID.fromString("5f89a942-2704-4442-9d68-f30408b51ca1"))

      val directive = buildCommitteeDirective(uuid, aMember(None), aCommitteeMember(None))

      Get("/").addHeader(jwtHeader("not used")) ~> Route.seal(directive) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "reject requests where the JWT is valid but the member is not a committee member" in {
      val member = aMember(Some(Util.fixture[Member]("1f390207.json")))
      val committeeMember = aCommitteeMember(None)

      val directive = buildCommitteeDirective(Some(UUID.randomUUID()), member, committeeMember)

      Get("/").addHeader(jwtHeader("not used")) ~> Route.seal(directive) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "reject requests where the JWT is valid but the member is an inactive committee member" in {
      val member = aMember(Some(Util.fixture[Member]("37b50c24.json")))
      val committeeMember = aCommitteeMember(Some(Util.fixture[CommitteeMember]("956610c8.json")))

      val directive = buildCommitteeDirective(Some(UUID.randomUUID()), member, committeeMember)

      Get("/").addHeader(jwtHeader("not used")) ~> Route.seal(directive) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

  }

  private def buildDirective(serviceResponse: Option[UUID], member: Future[String \/ Option[Member]]): Route = {
    val service = mock(classOf[JwtService])
    when(service.verifyJwt(any())).thenReturn(serviceResponse)

    val memberDao = mock(classOf[MemberDao])
    when(memberDao.get(any())).thenReturn(member)

    val committeeMemberDao = mock(classOf[CommitteeMemberDao])

    new JwtDirectives(service, memberDao, committeeMemberDao).authenticateWithJwt { _ =>
      complete("Hello, world!")
    }
  }

  private def buildCommitteeDirective(serviceResponse: Option[UUID], member: Future[String \/ Option[Member]],
                                      committeeMember: Future[Option[CommitteeMember]]): Route = {
    val service = mock(classOf[JwtService])
    when(service.verifyJwt(any())).thenReturn(serviceResponse)

    val memberDao = mock(classOf[MemberDao])
    when(memberDao.get(any())).thenReturn(member)

    val committeeMemberDao = mock(classOf[CommitteeMemberDao])
    when(committeeMemberDao.forMember(any())).thenReturn(committeeMember)

    new JwtDirectives(service, memberDao, committeeMemberDao).authenticateCommitteeWithJwt { _ =>
      complete("Hello, world!")
    }
  }

  private def aMember(member: Option[Member]): Future[\/[String, Option[Member]]] = {
    Future.successful(\/-(member))
  }

  private def aCommitteeMember(committeeMember: Option[CommitteeMember]): Future[Option[CommitteeMember]] = {
    Future.successful(committeeMember)
  }

  private def anError(error: String): Future[\/[String, Option[Member]]] = {
    Future.successful(-\/(error))
  }

  private def jwtHeader(value: String): HttpHeader = {
    RawHeader("X-JWT", value)
  }

}
