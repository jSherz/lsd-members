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

import java.sql.Timestamp
import java.time.LocalDateTime

import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.jsherz.luskydive.core.{CommitteeMember, Member}
import com.jsherz.luskydive.dao.MassTextDao
import com.jsherz.luskydive.json.MassTextsJsonSupport._
import com.jsherz.luskydive.json.{MassTextSendRequest, MassTextSendResponse, TryFilterRequest, TryFilterResponse}
import com.jsherz.luskydive.util.TextMessageUtil
import scalaz.{-\/, \/-}

import scala.concurrent.ExecutionContext

/**
  * Used to send out a text message to many members.
  */
class MassTextApi(dao: MassTextDao)
                 (implicit ec: ExecutionContext, authDirective: Directive1[(Member, CommitteeMember)], log: LoggingAdapter) {

  val exampleName = "Mary"

  /**
    * Try out a filter (start and end date) to see how many members would be returned if a mass text was sent with those
    * filter options chosen.
    */
  val tryFilterRoute = (path("try-filter") & post & authDirective & entity(as[TryFilterRequest])) { (_, request) =>
    val result = dao.filterCount(request.startDate, request.endDate)

    result match {
      case \/-(futureCount) => onSuccess(futureCount) { count =>
        complete(TryFilterResponse(success = true, Some(count), None))
      }
      case -\/(error: String) => {
        log.error("Trying a filter for mass texting failed: " + error)

        complete(TryFilterResponse(success = false, None, Some(error)))
      }
    }
  }

  val sendRoute = (path("send") & post & authDirective & entity(as[MassTextSendRequest])) { (sender, request) =>
    val rendered = TextMessageUtil.parseTemplate(request.template, exampleName)

    if (request.template.isEmpty) {
      log.info("Mass text send attempt made with no template.")

      complete(MassTextSendResponse(success = false, Some(MassTextApiErrors.blankTemplate), None))
    } else if (!rendered.equals(request.expectedRendered)) {
      log.info(s"Mass text send attempt made with mismatched template / render. Template was '${request.template}', " +
        s"expected (from user) was '${request.expectedRendered}', we expected '${rendered}'.")

      complete(MassTextSendResponse(success = false, Some(MassTextApiErrors.templateRenderMismatch), None))
    } else {
      val result = dao.send(sender._1.uuid, request.startDate, request.endDate, request.template, Timestamp.valueOf(LocalDateTime.now))

      onSuccess(result) {
        case \/-(uuid) => complete(MassTextSendResponse(success = true, None, Some(uuid)))
        case -\/(error) => {
          log.error("Failed to send mass text: " + error)

          complete(MassTextSendResponse(success = false, Some(error), None))
        }
      }
    }
  }

  val route = pathPrefix("mass-texts") {
    tryFilterRoute ~
      sendRoute
  }

}

object MassTextApiErrors {

  val blankTemplate = "error.blankTemplate"

  val templateRenderMismatch = "error.templateRenderMismatch"

}
