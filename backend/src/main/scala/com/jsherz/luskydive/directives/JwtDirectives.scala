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

package com.jsherz.luskydive.directives

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.BasicDirectives.provide
import akka.http.scaladsl.server.directives.HeaderDirectives.optionalHeaderValueByName
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1}
import com.jsherz.luskydive.core.{CommitteeMember, Member}
import com.jsherz.luskydive.dao.{CommitteeMemberDao, MemberDao}
import com.jsherz.luskydive.services.JwtService
import scalaz.{-\/, \/-}

class JwtDirectives(
                     jwtService: JwtService,
                     memberDao: MemberDao,
                     committeeMemberDao: CommitteeMemberDao
                   )
                   (implicit log: LoggingAdapter) {

  private val authHeader = "Authorization"
  private val dummyChallenge = HttpChallenge("", None)

  /**
    * Validate a user with a JWT and ensure they're an active committee member.
    */
  def authenticateCommitteeWithJwt: Directive1[(Member, CommitteeMember)] = {
    authenticateWithJwt.flatMap { member =>
      onSuccess(committeeMemberDao.get(member.uuid)).flatMap {
        case Some(committeeMember) =>
          if (committeeMember.locked) {
            log.error(s"member ${member.uuid} attempted to authenticate but was locked")
            badCreds[(Member, CommitteeMember)]
          } else {
            log.debug(s"member ${member.uuid} authenticated as a committee member")
            provide((member, committeeMember))
          }
        case None =>
          log.error(s"access attempted for member ${member.uuid} but not a committee member")
          badCreds[(Member, CommitteeMember)]
      }
    }
  }

  def authenticateWithJwt: Directive1[Member] = {
    optionalHeaderValueByName(authHeader).flatMap { maybeJwt: Option[String] =>
      val verifiedUuid = for {
        jwt <- maybeJwt.map(s => s.replace("Bearer ", ""))
        uuid <- jwtService.verifyJwt(jwt)
      } yield uuid

      verifiedUuid.map { uuid =>
        onSuccess(memberDao.get(uuid)).flatMap { maybeMember: Option[Member] =>
          maybeMember match {
            case Some(m: Member) =>
              log.debug(s"member ${m.uuid} authenticated")
              provide(m)
            case None =>
              log.error(s"no member found for UUID $uuid")
              badCreds[Member]
          }
        }
      }.getOrElse(credsMissing[Member])
    }
  }

  private def credsMissing[T]: Directive1[T] =
    reject(AuthenticationFailedRejection(CredentialsMissing, dummyChallenge))

  private def badCreds[T]: Directive1[T] =
    reject(AuthenticationFailedRejection(CredentialsRejected, dummyChallenge))
}
