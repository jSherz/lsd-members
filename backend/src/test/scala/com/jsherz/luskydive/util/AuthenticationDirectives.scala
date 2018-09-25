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

package com.jsherz.luskydive.util

import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.directives.BasicDirectives.provide
import akka.http.scaladsl.server.directives.RouteDirectives._
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1}
import com.jsherz.luskydive.core.{CommitteeMember, Member}
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport.CommitteeMemberFormat
import com.jsherz.luskydive.json.MemberJsonSupport._

/**
  * Some dummy directives that can be used to test APIs that require authentication.
  */
object AuthenticationDirectives {

  /**
    * We don't use a "challenge" (as with HTTP Basic Auth) so provide an empty one.
    */
  private val dummyChallenge = HttpChallenge("", None)

  private val member = Util.fixture[Member]("6066143f.json")

  private val committeeMember = Util.fixture[CommitteeMember]("956610c8.json")

  /**
    * Allows any request, regardless of API key header.
    */
  val allowAll: Directive1[(Member, CommitteeMember)] = {
    provide(member, committeeMember)
  }
  /**
    * Rejects any request, regardless of API key header.
    */
  val denyAll: Directive1[(Member, CommitteeMember)] = {
    reject(AuthenticationFailedRejection(CredentialsRejected, dummyChallenge))
  }

}
