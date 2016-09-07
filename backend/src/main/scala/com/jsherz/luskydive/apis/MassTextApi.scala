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

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.jsherz.luskydive.dao.MassTextDao
import com.jsherz.luskydive.json.MassTextsJsonSupport._
import com.jsherz.luskydive.json.{TryFilterRequest, TryFilterResponse}

import scala.concurrent.ExecutionContext
import scalaz.{-\/, \/-}

/**
  * Used to send out a text message to many members.
  */
class MassTextApi(private val dao: MassTextDao)
                 (implicit ec: ExecutionContext, authDirective: Directive1[UUID]) {

  /**
    * Try out a filter (start and end date) to see how many members would be returned if a mass text was sent with those
    * filter options chosen.
    */
  val tryFilterRoute = (path("try-filter") & post & authDirective & entity(as[TryFilterRequest])) { (_, request) =>
    val result = dao.filterCount(request.startDate, request.endDate)

    onSuccess(result) {
      case \/-(count: Int) => complete(TryFilterResponse(success = true, Some(count), None))
      case -\/(error: String) => complete(TryFilterResponse(success = false, None, Some(error)))
    }
  }

  val route = pathPrefix("mass-texts") {
    tryFilterRoute
  }

}
