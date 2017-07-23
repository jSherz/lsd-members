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

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.directives.BasicDirectives.provide
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1, Route}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.dao.{MemberDao, StubMemberDao}
import com.jsherz.luskydive.json.{SignupJsonSupport, SignupRequest, SignupResponse}
import com.jsherz.luskydive.json.MemberJsonSupport.MemberFormat
import com.jsherz.luskydive.util.{AuthenticationDirectives, Util}
import org.mockito.Matchers._
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

import scala.concurrent.ExecutionContext

/**
  * Ensures the main sign-up endpoint functions correctly.
  */
class SignupApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  private val member = Util.fixture[Member]("6066143f.json")
  private implicit val authDirective: Directive1[Member] = provide(member)
  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  private var dao: MemberDao = Mockito.spy(new StubMemberDao())
  private var route = new SignupApi(dao).route

  private val url = "/members/sign-up"

  import SignupJsonSupport._

  before {
    dao = Mockito.spy(new StubMemberDao())
    route = new SignupApi(dao).route
  }

  "SignupApi" should {

    "requires authentication" in {
      implicit val authDirective: Directive1[Member] = reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("", None)))
      route = new SignupApi(dao).route

      Post(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return success with no errors if a valid username & phone number are given" in {
      val name = "Toby Howard"
      val phoneNumber = "07918323440"
      val phoneNumberFormatted = "+447918323440"

      val request = SignupRequest(name, phoneNumber)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual true
        responseAs[SignupResponse].errors shouldBe empty

        // TODO: Try making this more specific
        verify(dao).create(any())
      }
    }

    "return unsupported media type when not JSON" in {
      Seq(ContentTypes.`text/xml(UTF-8)`, ContentTypes.`text/plain(UTF-8)`).foreach { contentType =>
        val request = HttpEntity(contentType, "foo bar test")

        Post(url, request) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.UnsupportedMediaType

          verify(dao, never()).create(any())
        }
      }
    }

    "return bad request if no name or phone number is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any())
      }
    }

    "return bad request if no name is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"phoneNumber":"07123123123"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any())
      }
    }

    "return bad request if no phone number is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"name":"Joe Bloggs"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any())
      }
    }

    "return method not allowed if not a post request" in {
      val request = SignupRequest("Toby Howard", "07918323440")

      Seq(Put, Delete, Patch).foreach { method =>
        method(url, request) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.MethodNotAllowed
      }

      verify(dao, never()).create(any())
    }

    "return failed with an error if a blank name (only spaces) is given" in {
      Seq(" ", "  ", "         ").foreach { name =>
        val request = SignupRequest(name, "07856216259")

        Post(url, request) ~> route ~> check {
          response.status shouldEqual StatusCodes.OK
          responseAs[SignupResponse].success shouldEqual false
          responseAs[SignupResponse].errors shouldBe Map("name" -> "error.required")

          verify(dao, never()).create(any())
        }
      }
    }

    "return failed with an appropriate error if the phone number is in use" in {
      val request = SignupRequest("Declan Clark", StubMemberDao.existsPhoneNumber)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual false
        responseAs[SignupResponse].errors shouldBe Map("phoneNumber" -> "error.inUse")

        verify(dao, never()).create(any())
      }
    }

    "return failed with an appropriate error if the phone number is invalid" in {
      val request = SignupRequest("Aidan Carter", "07123123")

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual false
        responseAs[SignupResponse].errors shouldBe Map("phoneNumber" -> "error.invalid")

        verify(dao, never()).create(any())
      }
    }

  }

}
