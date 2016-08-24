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

import java.util.UUID

import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpHeader, HttpRequest, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.dao.StubAuthDao
import org.mockito.Mockito
import org.scalatest.{Matchers, WordSpec}

/**
  * Ensures the authentication directive works correctly and gracefully handles failure.
  */
class ApiKeyAuthenticatorSpec extends WordSpec with Matchers with ScalatestRouteTest {

  private val dao = Mockito.spy(new StubAuthDao())

  private val auth = new ApiKeyAuthenticator(dao)

  /**
    * When testing the directive, return the member UUID it produces.
    */
  private val authDirective: Route = auth.authenticateWithApiKey(x => complete(x.toString))

  "ApiKeyAuthenticator" should {

    "accept a valid API key and return the member's UUID" in {
      getWithApiKey(StubAuthDao.validApiKey) ~> authDirective ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual StubAuthDao.validMemberUuid.toString
      }
    }

    "reject an invalid API key" in {
      getWithApiKey(StubAuthDao.invalidApiKey) ~> Route.seal(authDirective) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The supplied authentication is invalid"
      }
    }

    "reject a missing API key" in {
      Get() ~> Route.seal(authDirective) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The resource requires authentication, which was not supplied with the request"
      }
    }

    "reject a malformed API key" in {
      getWithApiKey("not-a-uuid") ~> Route.seal(authDirective) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
        responseAs[String] shouldEqual "The resource requires authentication, which was not supplied with the request"
      }
    }

  }

  private def getWithApiKey(apiKey: UUID): HttpRequest = {
    getWithApiKey(apiKey.toString)
  }

  private def getWithApiKey(apiKey: String): HttpRequest = {
    Get().withHeaders(RawHeader("api-key", apiKey): HttpHeader)
  }

}
