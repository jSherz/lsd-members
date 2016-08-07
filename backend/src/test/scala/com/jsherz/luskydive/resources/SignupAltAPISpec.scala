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

package com.jsherz.luskydive.resources

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.apis.SignupAPI
import com.jsherz.luskydive.core.{SignupAltRequest, SignupResponse}
import com.jsherz.luskydive.dao.{MemberDAO, StubMemberDAO}
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{never, verify}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

/**
  * Ensures the alternative sign-up endpoint functions correctly.
  */
class SignupAltAPISpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  private var dao: MemberDAO = Mockito.spy(new StubMemberDAO())
  private var route = new SignupAPI(dao).route

  private val url = "/members/sign-up/alt"

  import com.jsherz.luskydive.core.SignupJsonSupport._

  before {
    dao = Mockito.spy(new StubMemberDAO())
    route = new SignupAPI(dao).route
  }

  "SignupAPI (alt)" should {

    "return success with no errors if a valid username & e-mail are given" in {
      val name = "Tyler Davey"
      val email = "TylerDavey@jourrapide.com"

      val request = SignupAltRequest(name, email)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual true
        responseAs[SignupResponse].errors shouldBe empty

        verify(dao).create(name, None, Some(email))

      }
    }

    "return unsupported media type when not JSON" in {
      Seq(ContentTypes.`text/xml(UTF-8)`, ContentTypes.`text/plain(UTF-8)`).foreach { contentType =>
        val request = HttpEntity(contentType, "foo bar test")

        Post(url, request) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.UnsupportedMediaType

          verify(dao, never()).create(any(), any(), any())
        }
      }
    }

    "return bad request if no name or e-mail is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any(), any(), any())
      }
    }

    "return bad request if no name is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"email":"foot@ball.com"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any(), any(), any())
      }
    }

    "return bad request if no e-mail is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"name":"Joe Bloggs"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any(), any(), any())
      }
    }

    "return method not allowed if not a post request" in {
      val request = SignupAltRequest("Toby Howard", "toby@the-howards.webserv")

      Seq(Put, Delete, Patch).foreach { method =>
        method(url, request) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.MethodNotAllowed
      }

      verify(dao, never()).create(any(), any(), any())
    }

    "return failed with an error if a blank name (only spaces) is given" in {
      Seq(" ", "  ", "         ").foreach { name =>
        val request = SignupAltRequest(name, "bla@example.com")

        Post(url, request) ~> route ~> check {
          response.status shouldEqual StatusCodes.OK
          responseAs[SignupResponse].success shouldEqual false
          responseAs[SignupResponse].errors shouldBe Map("name" -> "error.required")
        }
      }

      verify(dao, never()).create(any(), any(), any())
    }

    "return failed with an appropriate error if the e-mail is in use" in {
      val request = SignupAltRequest("Nicole Howarth", StubMemberDAO.existsEmail)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual false
        responseAs[SignupResponse].errors shouldBe Map("email" -> "error.inUse")

        verify(dao, never()).create(any(), any(), any())
      }
    }

    "return failed with an appropriate error if the e-mail is invalid" in {
      val request = SignupAltRequest("Caitlin Chamberlain", "definitely-not-valid.com")

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual false
        responseAs[SignupResponse].errors shouldBe Map("email" -> "error.invalid")

        verify(dao, never()).create(any(), any(), any())
      }
    }

  }

}
