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

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.directives.BasicDirectives.provide
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.json.CoursesJsonSupport.StrippedMemberFormat
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.json.StrippedMember
import com.jsherz.luskydive.util.Util
import org.mockito.Mockito.mock

class CurrentMemberApiSpec extends BaseApiSpec {

  "CurrentMemberApi#me" should {

    "return an error if the authentication fails" in {
      val memberDao = mock(classOf[MemberDao])
      val authDirective = reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("", None)))

      val api = new CurrentMemberApi(authDirective, memberDao)

      Get("/me") ~> addHeader("X-JWT", "asdfasf") ~> Route.seal(api.route) ~> check {
        response.status shouldBe StatusCodes.Unauthorized
      }
    }

    "return a stripped down version of the member's info" in {
      val member = Util.fixture[Member]("6066143f.json")
      val authDirective = provide(member)
      val memberDao = mock(classOf[MemberDao])

      val api = new CurrentMemberApi(authDirective, memberDao)

      Get("/me") ~> addHeader("X-JWT", "123123123") ~> Route.seal(api.route) ~> check {
        response.status shouldBe StatusCodes.OK
        responseAs[StrippedMember] shouldEqual StrippedMember(
          uuid = member.uuid,
          firstName = member.firstName,
          lastName = member.lastName,
          createdAt = member.createdAt
        )
      }
    }

  }

}
