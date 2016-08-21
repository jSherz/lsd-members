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

import java.sql.Date

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.dao.StubMemberDao
import com.jsherz.luskydive.json.MemberSearchRequest
import com.jsherz.luskydive.json.MemberJsonSupport._
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{never, verify}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

/**
  * Ensures the members endpoints function correctly.
  */
class MemberApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  import spray.json._

  private val dao = Mockito.spy(new StubMemberDao())

  private val route = new MemberApi(dao).route

  private val searchUrl = "/members/search"

  "MembersApi#search" should {

    "return method not allowed when used with anything other than POST" in {
      Seq(Get, Put, Delete, Patch).foreach { method =>
        method(searchUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).search(any[String])
    }

    "return bad request if the search term provided is under three characters" in {
      val invalidSearchTerms = Seq(
        """{"searchTerm":" aa   "}""",
        """{"searchTerm":"da"}""",
        """{"searchTerm":"   e   "}""",
        """{"searchTerm":"                   "}"""
      )

      invalidSearchTerms.foreach { searchTerm =>
        Post(searchUrl, HttpEntity(ContentTypes.`application/json`, searchTerm)) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.BadRequest
          responseAs[String] shouldEqual "error.invalidSearchTerm"
        }
      }

      verify(dao, never()).search(any[String])
    }

    "return the correct results" in {
      val request = MemberSearchRequest("foobar")
      val expected = StubMemberDao.searchResults

      Post(searchUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[String].parseJson shouldEqual expected.toJson
      }

      verify(dao).search("foobar")
    }

  }

}
