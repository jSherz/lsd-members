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

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.PathMatchers.JavaUUID
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import com.jsherz.luskydive.dao.CourseDAO
import com.jsherz.luskydive.json.{CoursesJsonSupport, CoursesListRequest}

import scala.concurrent.ExecutionContext

/**
  * Used to retrieve and store course information.
  */
class CoursesApi(private val courseDao: CourseDAO)(implicit ec: ExecutionContext) {

  import CoursesJsonSupport._

  /**
    * Shows the courses that are
    */
  val listRoute = pathEnd {
    post {
      entity(as[CoursesListRequest]) { req =>
        if (req.endDate.before(req.startDate)) {
          complete(StatusCodes.BadRequest, "endDate must be after startDate")
        } else {
          complete(courseDao.find(req.startDate, req.endDate))
        }
      }
    }
  }

  val getRoute = path(JavaUUID) { uuid =>
    get {
      onSuccess(courseDao.get(uuid)) {
        case Some(course) => complete(course)
        case None => complete(StatusCodes.NotFound, "No course found with that UUID")
      }
    }
  }

  val route: Route = pathPrefix("courses") {
    listRoute ~
    getRoute
  }

}