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
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.services.JwtService
import com.jsherz.luskydive.util.Util
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.{Matchers, WordSpec}
import com.jsherz.luskydive.json.MemberJsonSupport._

import scala.concurrent.Future
import scalaz.{-\/, \/, \/-}

class JwtDirectivesSpec extends WordSpec with Matchers with ScalatestRouteTest {

  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  "JwtDirective" should {

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

  private def buildDirective(serviceResponse: Option[UUID], member: Future[\/[String, Option[Member]]]): Route = {
    val service = mock(classOf[JwtService])
    when(service.verifyJwt(any())).thenReturn(serviceResponse)

    val memberDao = mock(classOf[MemberDao])
    when(memberDao.get(any())).thenReturn(member)

    new JwtDirectives(service, memberDao).authenticateWithJwt { _ =>
      complete("Hello, world!")
    }
  }

  private def aMember(member: Option[Member]): Future[\/[String, Option[Member]]] = {
    Future.successful(\/-(member))
  }

  private def anError(error: String): Future[\/[String, Option[Member]]] = {
    Future.successful(-\/(error))
  }

  private def jwtHeader(value: String): HttpHeader = {
    RawHeader("X-JWT", value)
  }

}
