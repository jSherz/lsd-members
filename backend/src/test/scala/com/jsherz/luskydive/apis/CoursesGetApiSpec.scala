/**
  * MIT License
  *
  * Copyright (c) 2016-2018 James Sherwood-Jones <james.sherwoodjones@gmail.com>
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
import com.jsherz.luskydive.dao.StubCourseDao
import com.jsherz.luskydive.json.CoursesJsonSupport
import com.jsherz.luskydive.json.CoursesJsonSupport._
import com.jsherz.luskydive.util.AuthenticationDirectives
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{never, verify}
import spray.json._

/**
  * Ensures the find one course endpoint function correctly.
  */
class CoursesGetApiSpec extends BaseApiSpec {

  private implicit val authDirective = AuthenticationDirectives.allowAll

  private var dao = Mockito.spy(new StubCourseDao())

  private var route = new CoursesApi(dao).route

  private val baseUrl = "/courses/"

  private val validUrl =
    baseUrl + StubCourseDao.validCourseUuid.toString

  private val missingUrl =
    baseUrl + StubCourseDao.notFoundCourseUuid.toString

  before {
    dao = Mockito.spy(new StubCourseDao())
    route = new CoursesApi(dao).route
  }

  "CoursesApi#get" should {

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

      verify(dao, never()).get(any[UUID])
    }

    "return not found when the URL has no course" in {
      Get(baseUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.NotFound
      }

      verify(dao, never()).get(any[UUID])
    }

    "return not found when the course doesn't exist" in {
      Get(missingUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.NotFound
      }

      verify(dao).get(StubCourseDao.notFoundCourseUuid)
    }

    "return the correct courses" in {
      val expected = StubCourseDao.validCourse

      Get(validUrl) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[String].parseJson shouldEqual expected.toJson
      }

      verify(dao).get(any[UUID])
    }

  }

}
