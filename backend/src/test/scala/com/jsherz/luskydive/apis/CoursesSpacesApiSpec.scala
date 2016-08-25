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

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.core.CourseSpace
import com.jsherz.luskydive.dao.StubCourseDao
import com.jsherz.luskydive.json.{CourseSpaceWithMember, CoursesJsonSupport}
import com.jsherz.luskydive.util.AuthenticationDirectives
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{never, verify}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

import scala.concurrent.ExecutionContext

/**
  * Ensures the course spaces endpoint function correctly.
  */
class CoursesSpacesApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  import CoursesJsonSupport._
  import spray.json._

  private implicit val authDirective = AuthenticationDirectives.allowAll

  private var dao = Mockito.spy(new StubCourseDao())

  private var route = new CoursesApi(dao).route

  private val baseUrl = "/courses/%s/spaces"

  private val validUrl =
    baseUrl.format(StubCourseDao.validCourseUuid.toString)

  private val missingUrl =
    baseUrl.format(StubCourseDao.notFoundCourseUuid.toString)

  private val noCourseUrl =
    baseUrl.format("")

  before {
    dao = Mockito.spy(new StubCourseDao())
    route = new CoursesApi(dao).route
  }

  "CoursesApi#spaces" should {

    "requires authentication" in {
      implicit val authDirective = AuthenticationDirectives.denyAll
      route = new CoursesApi(dao).route

      Get(validUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return method not allowed when used with anything other than GET" in {
      Seq(Post, Put, Delete, Patch).foreach { method =>
        method(validUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).spaces(any[UUID])
    }

    "return not found when the URL has no course" in {
      Get(noCourseUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.NotFound
      }

      verify(dao, never()).spaces(any[UUID])
    }

    "return an empty list when the course doesn't exist" in {
      Get(missingUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[String].parseJson shouldEqual Seq[CourseSpaceWithMember]().toJson
      }

      verify(dao).spaces(StubCourseDao.notFoundCourseUuid)
    }

    "return the correct course spaces" in {
      val expected = StubCourseDao.validCourseSpaces

      Get(validUrl) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[String].parseJson shouldEqual expected.toJson
      }

      verify(dao).spaces(any[UUID])
    }

  }

}
