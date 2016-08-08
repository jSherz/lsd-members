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
import com.jsherz.luskydive.core.CoursesListRequest
import com.jsherz.luskydive.dao.StubCourseDao
import com.jsherz.luskydive.util.DateUtil
import org.mockito.Matchers.any
import org.mockito.Mockito
import org.mockito.Mockito.{never, verify}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}

/**
  * Ensures the courses endpoints function correctly.
  */
class CoursesListApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  import com.jsherz.luskydive.core.CoursesJsonSupport._
  import spray.json._

  private val dao = Mockito.spy(new StubCourseDao())

  private val route = new CoursesApi(dao).route

  private val url = "/courses"

  "Courses list API" should {

    "return method not allowed when used with anything other than POST" in {
      Seq(Get, Put, Delete, Patch).foreach { method =>
        method(url) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).find(any[Date], any[Date])
    }

    "return bad request when the end date is before the start date" in {
      val invalid = CoursesListRequest(DateUtil.makeDate(2016, 8, 8), DateUtil.makeDate(2016, 8, 7))

      Post(url, invalid) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
        responseAs[String] shouldEqual "endDate must be after startDate"
      }

      verify(dao, never()).find(any[Date], any[Date])
    }

    "return bad request if one (or both) of the dates is missing" in {
      val missingDates = Seq(
        """{}""",
        """{"startDate":"2015-01-08"}""",
        """{"endDate":"2015-01-08"}"""
      )

      missingDates.foreach { date =>
        Post(url, HttpEntity(ContentTypes.`application/json`, date)) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.BadRequest
        }
      }

      verify(dao, never()).find(any[Date], any[Date])
    }

    "return unsupported media type for non-JSON content types" in {
      val unsupportedContentTypes = Seq(
        ContentTypes.`text/plain(UTF-8)`,
        ContentTypes.`text/xml(UTF-8)`,
        ContentTypes.`text/csv(UTF-8)`
      )

      unsupportedContentTypes.foreach { contentType =>
        Post(url, HttpEntity(contentType, "blah")) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.UnsupportedMediaType
        }
      }

      verify(dao, never()).find(any[Date], any[Date])
    }

    "return the correct courses" in {
      val startDate = DateUtil.makeDate(2016, 8, 1)
      val endDate = DateUtil.makeDate(2016, 8, 31)

      val request = CoursesListRequest(startDate, endDate)

      val expected = StubCourseDao.courses

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[String].parseJson shouldEqual expected.toJson
      }

      verify(dao).find(startDate, endDate)
    }

  }

}
