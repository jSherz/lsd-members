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

import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives.{complete, get, path}
import akka.http.scaladsl.server.{Directive1, Route}
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.json.CoursesJsonSupport._
import com.jsherz.luskydive.json.StrippedMember

class CurrentMemberApi(private val authDirective: Directive1[Member],
                       private val memberDao: MemberDao)
                      (implicit val log: LoggingAdapter) {

  val basicInfoRoute: Route = path("me") {
    (get & authDirective) { member =>
      complete(StrippedMember(
        uuid = member.uuid,
        firstName = member.firstName,
        lastName = member.lastName,
        createdAt = member.createdAt
      ))
    }
  }

  val route: Route = {
    basicInfoRoute
  }

}
