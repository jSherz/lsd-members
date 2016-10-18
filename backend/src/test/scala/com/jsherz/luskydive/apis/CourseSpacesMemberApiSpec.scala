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
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.dao.{StubCourseDao, StubCourseSpaceDao}
import com.jsherz.luskydive.json.{CourseSpaceDepositPaidRequest, CourseSpaceDepositPaidResponse, CourseSpaceMemberRequest, CourseSpaceMemberResponse}
import com.jsherz.luskydive.util.AuthenticationDirectives
import org.mockito.Matchers._
import org.mockito.Mockito
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

import scala.concurrent.ExecutionContext

/**
  * Defines the member related behaviour on course spaces.
  */
class CourseSpacesMemberApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  import com.jsherz.luskydive.json.CourseSpacesJsonSupport._

  private implicit val authDirective = AuthenticationDirectives.allowAll
  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  private var dao = Mockito.spy(new StubCourseSpaceDao())

  private var route = new CourseSpacesApi(dao).route

  private val addUrl = "/course-spaces/%s/add-member"
  private val removeUrl = "/course-spaces/%s/remove-member"
  private val depositPaidUrl = "/course-spaces/%s/deposit-paid"

  private val validAddUrl =
    addUrl.format(StubCourseSpaceDao.validSpaceUuid.toString)

  private val invalidAddUrl =
    addUrl.format(StubCourseSpaceDao.invalidSpaceUuid.toString)

  private val validRemoveUrl =
    removeUrl.format(StubCourseSpaceDao.validSpaceUuid.toString)

  private val invalidRemoveUrl =
    removeUrl.format(StubCourseSpaceDao.invalidSpaceUuid.toString)

  before {
    dao = Mockito.spy(new StubCourseSpaceDao())
    route = new CourseSpacesApi(dao).route
  }

  "CourseSpacesApi#addMember" should {

    "requires authentication" in {
      implicit val authDirective = AuthenticationDirectives.denyAll
      route = new CourseSpacesApi(dao).route

      Post(validAddUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return method not allowed for anything but POST" in {
      Seq(Get, Put, Delete, Patch).foreach { method =>
        method(validAddUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).addMember(any[UUID], any[UUID])
    }

    "return the correct response for adding a member succeeding" in {
      val request = CourseSpaceMemberRequest(UUID.fromString("d046f0ca-7987-43bf-af2d-bcd51ae5d562"))

      Post(validAddUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK

        val res = responseAs[CourseSpaceMemberResponse]

        res.success shouldEqual true
        res.error shouldEqual None
      }
    }

    "return the correct response for adding a member failing" in {
      val request = CourseSpaceMemberRequest(UUID.fromString("aefce06a-c457-48a1-af9c-9c6de085aa9c"))

      Post(invalidAddUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK

        val res = responseAs[CourseSpaceMemberResponse]

        res.success shouldEqual false
        res.error shouldEqual Some("error.unknownSpace")
      }
    }

  }

  "CourseSpaceApi#removeMember" should {

    "requires authentication" in {
      implicit val authDirective = AuthenticationDirectives.denyAll
      route = new CourseSpacesApi(dao).route

      Post(validRemoveUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return method not allowed for anything but POST" in {
      Seq(Get, Put, Delete, Patch).foreach { method =>
        method(validRemoveUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).addMember(any[UUID], any[UUID])
    }

    "return the correct response for removing a member succeeding" in {
      val request = CourseSpaceMemberRequest(UUID.fromString("679a1dda-955c-43c1-a79f-59e6f7b62670"))

      Post(validRemoveUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK

        val res = responseAs[CourseSpaceMemberResponse]

        res.success shouldEqual true
        res.error shouldEqual None
      }
    }

    "return the correct response for removing a member failing" in {
      val request = CourseSpaceMemberRequest(UUID.fromString("c735a532-420a-41e4-9a89-e14cc4d7fc4b"))

      Post(invalidRemoveUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK

        val res = responseAs[CourseSpaceMemberResponse]

        res.success shouldEqual false
        res.error shouldEqual Some("error.unknownSpace")
      }
    }

  }

  "CourseSpaceApi#setDepositPaid" should {

    "return success when a record is updated" in {
      val request = CourseSpaceDepositPaidRequest(depositPaid = true)

      Put(depositPaidUrl.format(StubCourseSpaceDao.setDepositPaidValidUuid.toString), request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK

        val res = responseAs[CourseSpaceDepositPaidResponse]

        res.success shouldEqual true
        res.error shouldBe None
      }
    }

    "return a 404 if the record was not found" in {
      val request = CourseSpaceDepositPaidRequest(depositPaid = false)

      Put(depositPaidUrl.format(StubCourseSpaceDao.setDepositPaidNotFoundUuid.toString), request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.NotFound
        responseAs[String] shouldEqual "No course space found with that UUID."
      }
    }

    "return the correct response for the request failing" in {
      val request = CourseSpaceDepositPaidRequest(depositPaid = true)

      Put(depositPaidUrl.format(StubCourseSpaceDao.setDepositPaidErrorUuid.toString), request) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.OK

        val res = responseAs[CourseSpaceDepositPaidResponse]

        res.success shouldEqual false
        res.error shouldEqual Some("error.internalServer")
      }
    }

  }

}
