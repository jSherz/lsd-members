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

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.PathMatchers.JavaUUID
import akka.http.scaladsl.server._
import com.jsherz.luskydive.dao.{CourseDao, CourseSpaceDao, CourseSpaceDaoErrors}
import com.jsherz.luskydive.json._

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}
import scalaz.{-\/, \/-}

/**
  * Used to retrieve and store course space information.
  */
class CourseSpacesApi(dao: CourseSpaceDao)
                     (implicit ec: ExecutionContext,
                      authDirective: Directive1[UUID],
                      log: LoggingAdapter) {

  import com.jsherz.luskydive.json.CourseSpacesJsonSupport._

  val addMemberRoute = path(JavaUUID / "add-member") { uuid =>
    post {
      authDirective { _ =>
        entity(as[CourseSpaceMemberRequest]) { req =>
          onSuccess(dao.addMember(uuid, req.memberUuid)) {
            case \/-(uuid) => complete(CourseSpaceMemberResponse(true, None))
            case -\/(error) => complete(CourseSpaceMemberResponse(false, Some(error)))
          }
        }
      }
    }
  }

  val removeMemberRoute = path(JavaUUID / "remove-member") { uuid =>
    post {
      authDirective { _ =>
        entity(as[CourseSpaceMemberRequest]) { req =>
          onSuccess(dao.removeMember(uuid, req.memberUuid)) {
            case \/-(uuid) => complete(CourseSpaceMemberResponse(true, None))
            case -\/(error) => complete(CourseSpaceMemberResponse(false, Some(error)))
          }
        }
      }
    }
  }

  val setDepositPaidRoute = path(JavaUUID / "deposit-paid") { uuid =>
    (put & authDirective & entity(as[CourseSpaceDepositPaidRequest])) { (_, req) =>
      onSuccess(dao.setDepositPaid(uuid, req.depositPaid)) {
        case \/-(numRecordsUpdated) => {
          if (numRecordsUpdated == 1) {
            log.info(s"Updated course space ${uuid} to deposit paid ${req.depositPaid}")

            complete(CourseSpaceDepositPaidResponse(success = true, None))
          } else {
            log.warning(s"Received request to update course space ${uuid} that doesn't exist.")

            complete(StatusCodes.NotFound, "No course space found with that UUID.")
          }
        }
        case -\/(error) => {
          log.error("Failed to update course space: " + error)

          complete(CourseSpaceDepositPaidResponse(success = false, Some(error)))
        }
      }
    }
  }

  val route: Route = pathPrefix("course-spaces") {
    addMemberRoute ~
      removeMemberRoute ~
      setDepositPaidRoute
  }

}
