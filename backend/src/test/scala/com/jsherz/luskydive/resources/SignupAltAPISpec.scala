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
import com.jsherz.luskydive.core.{SignupAltRequest, SignupRequest, SignupResponse}
import com.jsherz.luskydive.dao.StubMemberDAO
import org.scalatest.{Matchers, WordSpec}

/**
  * Ensures the alternative sign-up endpoint functions correctly.
  */
class SignupAltAPISpec extends WordSpec with Matchers with ScalatestRouteTest {

  private val dao = new StubMemberDAO()
  private val route = new SignupAPI(dao).route

  private val url = "/members/sign-up/alt"

  import com.jsherz.luskydive.core.SignupJsonSupport._

  "SignupAPI (alt)" should {

    "return success with no errors if a valid username & e-mail are given" in {
      val request = SignupAltRequest("Tyler Davey", "TylerDavey@jourrapide.com")

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual true
        responseAs[SignupResponse].errors shouldBe empty
      }
    }

    "return unsupported media type when not JSON" in {
      Seq(ContentTypes.`text/xml(UTF-8)`, ContentTypes.`text/plain(UTF-8)`).foreach { contentType =>
        val request = HttpEntity(contentType, "foo bar test")

        Post(url, request) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.UnsupportedMediaType
        }
      }
    }

    "return bad request if no name or e-mail is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

    "return bad request if no name is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"email":"foot@ball.com"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

    "return bad request if no e-mail is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"name":"Joe Bloggs"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
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
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

    "return failed with an error if a blank name (only spaces) is given" in {
      Seq(" ", "  ", "         ").foreach { name =>
        val request = SignupRequest(name, "07856216259")

        Post(url, request) ~> route ~> check {
          response.status shouldEqual StatusCodes.OK
          responseAs[SignupResponse].success shouldEqual false
          responseAs[SignupResponse].errors shouldBe Map("name" -> "error.required")
        }
      }
    }

    "return failed with an appropriate error if the phone number is in use" in {
      val request = SignupAltRequest("Nicole Howarth", StubMemberDAO.existsEmail)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual false
        responseAs[SignupResponse].errors shouldBe Map("email" -> "error.inUse")
      }
    }

    "return failed with an appropriate error if the phone number is invalid" in {
      val request = SignupRequest("Caitlin Chamberlain", "definitely-not-valid.com")

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual false
        responseAs[SignupResponse].errors shouldBe Map("email" -> "error.invalid")
      }
    }

  }

}
