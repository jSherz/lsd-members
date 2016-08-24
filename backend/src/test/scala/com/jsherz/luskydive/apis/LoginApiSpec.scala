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

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.json.{LoginRequest, LoginResponse}
import org.mockito.Matchers._
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import com.jsherz.luskydive.json.LoginJsonSupport._

/**
  * Ensures the login functions correctly.
  */
class LoginApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  private var dao: AuthDao = Mockito.spy(new StubAuthDao())
  private var route = new LoginApi(dao).route

  private val url = "/login"

  before {
    dao = Mockito.spy(new StubAuthDao())
    route = new LoginApi(dao).route
  }

  "LoginApi" should {

    "return success with no errors if a valid e-mail & password are given" in {
      val request = LoginRequest(StubAuthDao.validEmail, StubAuthDao.validPassword)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[LoginResponse].success shouldEqual true
        responseAs[LoginResponse].errors shouldBe empty
        responseAs[LoginResponse].apiKey shouldBe Some(StubAuthDao.validApiKey)

        verify(dao).login(any(), any(), any())
      }
    }

    "return unsupported media type when not JSON" in {
      Seq(ContentTypes.`text/xml(UTF-8)`, ContentTypes.`text/plain(UTF-8)`).foreach { contentType =>
        val request = HttpEntity(contentType, "foo bar test")

        Post(url, request) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.UnsupportedMediaType

          verify(dao, never()).login(any(), any(), any())
        }
      }
    }

    "return bad request if no e-mail or password are given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).login(any(), any(), any())
      }
    }

    "return bad request if no e-mail is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"password":"eggbeardHype"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).login(any(), any(), any())
      }
    }

    "return bad request if no password is given" in {
      val request = HttpEntity(ContentTypes.`application/json`, """{"email":"you@essex.gov.uk"}""")

      Post(url, request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest

        verify(dao, never()).login(any(), any(), any())
      }
    }

    "return method not allowed if not a post request" in {
      Seq(Get, Put, Delete, Patch).foreach { method =>
        method(url) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).login(any(), any(), any())
    }

    "return failed with an error if the login information is incorrect" in {
      val request = LoginRequest(StubAuthDao.invalidEmail, StubAuthDao.invalidPassword)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[LoginResponse].success shouldEqual false
        responseAs[LoginResponse].errors shouldBe Map(
          "password" -> "error.invalidEmailPass"
        )
        responseAs[LoginResponse].apiKey shouldBe None

        verify(dao).login(any(), any(), any())
      }
    }

    "return failed with an appropriate error if the account is locked" in {
      val request = LoginRequest(StubAuthDao.accountLockedEmail, StubAuthDao.accountLockedPassword)

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[LoginResponse].success shouldEqual false
        responseAs[LoginResponse].errors shouldBe Map(
          "email" -> "error.accountLocked"
        )
        responseAs[LoginResponse].apiKey shouldBe None

        verify(dao).login(any(), any(), any())
      }
    }

  }

}
