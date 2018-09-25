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

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.JavaUUID
import akka.http.scaladsl.server.{Directive1, Route}
import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core.{CommitteeMember, Course, CourseStatuses, Member}
import com.jsherz.luskydive.dao.CourseDao
import com.jsherz.luskydive.json.{CourseCreateRequest, CourseCreateResponse, CoursesJsonSupport, CoursesListRequest}
import scalaz.{-\/, \/-}

import scala.concurrent.ExecutionContext

/**
  * Used to retrieve and store course information.
  */
class CoursesApi(courseDao: CourseDao)
                (implicit ec: ExecutionContext, authDirective: Directive1[(Member, CommitteeMember)], log: LoggingAdapter) {

  import CoursesJsonSupport._

  /**
    * Shows the courses that are
    */
  val listRoute = pathEnd {
    get {
      authDirective { _ =>
        entity(as[CoursesListRequest]) { req =>
          if (req.endDate.before(req.startDate)) {
            log.info("Bad course list request - endDate before startDate")

            complete(StatusCodes.BadRequest, "endDate must be after startDate")
          } else {
            complete(courseDao.find(req.startDate, req.endDate))
          }
        }
      }
    }
  }

  val spacesRoute = path(JavaUUID / "spaces") { uuid =>
    get {
      authDirective { _ =>
        onSuccess(courseDao.spaces(uuid)) {
          complete(_)
        }
      }
    }
  }

  val getRoute = path(JavaUUID) { uuid =>
    get {
      authDirective { _ =>
        onSuccess(courseDao.get(uuid)) {
          case Some(course) => complete(course)
          case None => {
            log.info(s"Could not get course with UUID ${uuid} - not found.")

            complete(StatusCodes.NotFound, "No course found with that UUID")
          }
        }
      }
    }
  }

  val createRoute = path("create") {
    post {
      authDirective { _ =>
        entity(as[CourseCreateRequest]) { req =>
          val (course, numSpaces) = createRequestToDaoFormat(req)

          onSuccess(courseDao.create(course, numSpaces)) {
            case \/-(courseUuid) => complete(CourseCreateResponse(true, None, Some(courseUuid)))
            case -\/(error) => {
              log.error("Failed to create course: " + error)

              complete(CourseCreateResponse(false, Some(error), None))
            }
          }
        }
      }
    }
  }

  val route: Route = pathPrefix("courses") {
    listRoute ~
      spacesRoute ~
      getRoute ~
      createRoute
  }

  /**
    * Generate a UUID for a course and build the objects required by the DAO.
    *
    * @param req
    * @return
    */
  private def createRequestToDaoFormat(req: CourseCreateRequest): (Course, Integer) = {
    val uuid = Generators.randomBasedGenerator().generate()

    (
      Course(uuid, req.date, req.organiserUuid, req.secondaryOrganiserUuid, CourseStatuses.PENDING),
      req.numSpaces
    )
  }

}
