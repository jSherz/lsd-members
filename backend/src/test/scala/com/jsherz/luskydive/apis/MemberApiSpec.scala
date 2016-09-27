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

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.core.{Member, TextMessage}
import com.jsherz.luskydive.dao.{StubMemberDao, StubTextMessageDao}
import com.jsherz.luskydive.json.MemberSearchRequest
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.util.{AuthenticationDirectives, Errors, Util}
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
  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  private val searchUrl = "/members/search"
  private val getUrl = "/members/" + StubMemberDao.getExistsUuid.toString
  private val getUrlNotFound = "/members/" + StubMemberDao.getNotFoundUuid.toString
  private val getUrlError = "/members/" + StubMemberDao.getErrorUuid.toString
  private val textMessagesUrl = s"/members/${StubTextMessageDao.forMemberUuid}/text-messages"
  private val textMessagesNotFoundUrl = s"/members/${StubTextMessageDao.forMemberNotFoundUuid}/text-messages"
  private val textMessagesErrorUrl = s"/members/${StubTextMessageDao.forMemberErrorUuid}/text-messages"
  private val updateUrl = s"/members/${StubMemberDao.updateUuid}"
  private val updateWrongMemberUrl = "/members/45da34bc-01f0-4b88-b822-fb9b07a64c10"
  private val updateMember = Util.fixture[Member]("1f390207.json")
  private val updateErrorUrl = s"/members/${StubMemberDao.updateErrorUuid}"
  private val updateErrorMember = Util.fixture[Member]("c740a4dc.json")

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

    "return method not allowed when used with anything other than GET or PUT" in new Fixtured {
      Seq(Post, Delete, Patch).foreach { method =>
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

  "MemberApi#update" should {

    "require authentication" in new Fixtured {
      implicit val authDirective = AuthenticationDirectives.denyAll
      override val route = new MemberApi(dao, textMessageDao).route

      Get(updateUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }

      verify(dao, never()).update(any[Member])
    }

    "return method not allowed when used with anything other than PUT or GET" in new Fixtured {
      Seq(Post, Delete, Patch).foreach { method =>
        method(updateUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).update(any[Member])
    }

    "return bad request if the UUID in the URL and member record don't match" in new Fixtured {
      Put(updateWrongMemberUrl, updateMember) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
        responseAs[String] shouldEqual MemberApiErrors.uuidUrlBodyMismatch
      }

      verify(dao, never()).update(any[Member])
    }

    "update the member correctly" in new Fixtured {
      Put(updateUrl, updateMember) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[Member] shouldEqual updateMember
      }

      verify(dao).update(updateMember)
    }

    "return an error with the correct status" in new Fixtured {
      Put(updateErrorUrl, updateErrorMember) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
        responseAs[String] shouldEqual Errors.internalServer
      }
    }

  }

}
