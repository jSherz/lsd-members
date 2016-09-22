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

import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.core.{Member, TextMessage}
import com.jsherz.luskydive.dao.{StubMemberDao, StubTextMessageDao}
import com.jsherz.luskydive.json.MemberSearchRequest
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.util.AuthenticationDirectives
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{never, times, verify}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

import scala.concurrent.ExecutionContext

/**
  * Ensures the members endpoints function correctly.
  */
class MemberApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  import spray.json._

  private implicit val authDirective = AuthenticationDirectives.allowAll

  private val searchUrl = "/members/search"
  private val getUrl = "/members/" + StubMemberDao.getExistsUuid.toString
  private val getUrlNotFound = "/members/" + StubMemberDao.getNotFoundUuid.toString
  private val getUrlError = "/members/" + StubMemberDao.getErrorUuid.toString
  private val textMessagesUrl = s"/members/${StubTextMessageDao.forMemberUuid}/text-messages"
  private val textMessagesNotFoundUrl = s"/members/${StubTextMessageDao.forMemberNotFoundUuid}/text-messages"
  private val textMessagesErrorUrl = s"/members/${StubTextMessageDao.forMemberErrorUuid}/text-messages"

  trait Fixtured {
    val dao = Mockito.spy(new StubMemberDao())
    val textMessageDao = Mockito.spy(new StubTextMessageDao())
    val route = new MemberApi(dao, textMessageDao).route
  }

  "MembersApi#search" should {

    "require authentication" in new Fixtured {
      implicit val authDirective = AuthenticationDirectives.denyAll
      override val route = new MemberApi(dao, textMessageDao).route

      Post(searchUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return method not allowed when used with anything other than POST" in new Fixtured {
      Seq(Get, Put, Delete, Patch).foreach { method =>
        method(searchUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).search(any[String])
    }

    "return bad request if the search term provided is under three characters" in new Fixtured {
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

    "return the correct results" in new Fixtured {
      val request = MemberSearchRequest("foobar")
      val expected = StubMemberDao.searchResults

      Post(searchUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[String].parseJson shouldEqual expected.toJson
      }

      verify(dao).search("foobar")
    }

  }

  "MemberApi#get" should {

    "require authentication" in new Fixtured {
      implicit val authDirective = AuthenticationDirectives.denyAll
      override val route = new MemberApi(dao, textMessageDao).route

      Get(getUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }

      verify(dao, never()).get(any[UUID])
    }

    "return method not allowed when used with anything other than GET" in new Fixtured {
      Seq(Post, Put, Delete, Patch).foreach { method =>
        method(getUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).get(any[UUID])
    }

    "return the member when one is found" in new Fixtured {
      Get(getUrl) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK

        responseAs[Member] shouldEqual StubMemberDao.getExistsMember
      }

      verify(dao, times(1)).get(StubMemberDao.getExistsUuid)
    }

    "return 404 when the member is not found" in new Fixtured {
      Get(getUrlNotFound) ~> route ~> check {
        response.status shouldEqual StatusCodes.NotFound

        responseAs[String] shouldEqual "Member not found"
      }

      verify(dao, times(1)).get(StubMemberDao.getNotFoundUuid)
    }

    "return 500 when the lookup fails" in new Fixtured {
      Get(getUrlError) ~> route ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
      }
    }

  }

  "MemberApi#textMessages" should {

    "require authentication" in new Fixtured {
      implicit val authDirective = AuthenticationDirectives.denyAll
      override val route = new MemberApi(dao, textMessageDao).route

      Get(textMessagesUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }

      verify(textMessageDao, never()).forMember(any[UUID])
    }

    "return method not allowed when used with anything other than GET" in new Fixtured {
      Seq(Post, Put, Delete, Patch).foreach { method =>
        method(textMessagesUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(textMessageDao, never()).forMember(any[UUID])
    }

    "return the correct text messages if the member UUID is a match" in new Fixtured {
      Get(textMessagesUrl) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK

        responseAs[Seq[TextMessage]] shouldEqual StubTextMessageDao.forMember
      }
    }

    "return an empty list if the member doesn't exist" in new Fixtured {
      Get(textMessagesNotFoundUrl) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK

        responseAs[Seq[TextMessage]] shouldEqual Seq.empty
      }
    }

    "return 500 when the lookup fails" in new Fixtured {
      Get(textMessagesErrorUrl) ~> route ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
      }
    }

  }

}
