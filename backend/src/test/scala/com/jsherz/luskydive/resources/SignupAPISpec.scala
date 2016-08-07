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
import com.jsherz.luskydive.core.{SignupRequest, SignupResponse}
import com.jsherz.luskydive.dao.{MemberDAO, StubMemberDAO}
import org.mockito.Matchers._
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

/**
  * Ensures the main sign-up endpoint functions correctly.
  */
class SignupAPISpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  private var dao: MemberDAO = Mockito.spy(new StubMemberDAO())
  private var route = new SignupAPI(dao).route

  private val url = "/members/sign-up"

  import com.jsherz.luskydive.core.SignupJsonSupport._

  before {
    dao = Mockito.spy(new StubMemberDAO())
    route = new SignupAPI(dao).route
  }

  "SignupAPI" should {

    "return success with no errors if a valid username & phone number are given" in {
      val name = "Toby Howard"
      val phoneNumber = "07918323440"
      val phoneNumberFormatted = "+447918323440"

      val request = SignupRequest(name, phoneNumber)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual true
        responseAs[SignupResponse].errors shouldBe empty

        verify(dao).create(name, Some(phoneNumberFormatted), None)
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

    "return bad request if no name or phone number is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any(), any(), any())
      }
    }

    "return bad request if no name is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"phoneNumber":"07123123123"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any(), any(), any())
      }
    }

    "return bad request if no phone number is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"name":"Joe Bloggs"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).create(any(), any(), any())
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

      verify(dao, never()).create(any(), any(), any())
    }

    "return failed with an error if a blank name (only spaces) is given" in {
      Seq(" ", "  ", "         ").foreach { name =>
        val request = SignupRequest(name, "07856216259")

        Post(url, request) ~> route ~> check {
          response.status shouldEqual StatusCodes.OK
          responseAs[SignupResponse].success shouldEqual false
          responseAs[SignupResponse].errors shouldBe Map("name" -> "error.required")

          verify(dao, never()).create(any(), any(), any())
        }
      }
    }

    "return failed with an appropriate error if the phone number is in use" in {
      val request = SignupRequest("Declan Clark", StubMemberDAO.existsPhoneNumber)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual false
        responseAs[SignupResponse].errors shouldBe Map("phoneNumber" -> "error.inUse")

        verify(dao, never()).create(any(), any(), any())
      }
    }

    "return failed with an appropriate error if the phone number is invalid" in {
      val request = SignupRequest("Aidan Carter", "07123123")

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldEqual false
        responseAs[SignupResponse].errors shouldBe Map("phoneNumber" -> "error.invalid")

        verify(dao, never()).create(any(), any(), any())
      }
    }

  }

}
