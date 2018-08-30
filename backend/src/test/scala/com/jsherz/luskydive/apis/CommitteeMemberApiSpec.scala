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

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.jsherz.luskydive.dao.StubCommitteeMemberDao
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport._
import com.jsherz.luskydive.util.AuthenticationDirectives
import org.mockito.Mockito
import org.mockito.Mockito._
import spray.json._

/**
  * Ensures the committee member endpoints function correctly.
  */
class CommitteeMemberApiSpec extends BaseApiSpec {

  private implicit val authDirective = AuthenticationDirectives.allowAll

  private val url = "/committee-members/active"

  private var dao = Mockito.spy(new StubCommitteeMemberDao())

  private var route = new CommitteeMemberApi(dao).route

  before {
    dao = Mockito.spy(new StubCommitteeMemberDao())
    route = new CommitteeMemberApi(dao).route
  }

  "CommitteeMemberApi#active" should {

    "requires authentication" in {
      implicit val authDirective = AuthenticationDirectives.denyAll
      route = new CommitteeMemberApi(dao).route

      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "return method not allowed when used with anything other than GET" in {
      Seq(Post, Put, Delete, Patch).foreach { method =>
        method(url) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).active()
    }

    "return the correct committee members, in name order" in {
      val expected = StubCommitteeMemberDao.activeMembers

      Get(url) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[String].parseJson shouldEqual expected.toJson
      }

      verify(dao).active()
    }

  }

}
