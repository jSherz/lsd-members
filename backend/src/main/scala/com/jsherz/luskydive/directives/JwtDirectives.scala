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
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsMissing
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.BasicDirectives.{extractExecutionContext, extractRequest, provide}
import akka.http.scaladsl.server.directives.HeaderDirectives.optionalHeaderValueByName
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1}
import com.jsherz.luskydive.core.{CommitteeMember, Member}
import com.jsherz.luskydive.dao.{CommitteeMemberDao, MemberDao}
import com.jsherz.luskydive.services.JwtService

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

class JwtDirectives(
                     private val jwtService: JwtService,
                     private val memberDao: MemberDao,
                     private val committeeMemberDao: CommitteeMemberDao
                   )
                   (implicit val log: LoggingAdapter) {

  private val authHeader = "X-JWT"

  private val dummyChallenge = HttpChallenge("", None)

  private val emptyMember: Future[\/[String, Option[Member]]] = Future.successful(\/-(None: Option[Member]))

  private val emptyMemberWithCommittee: Future[Option[(Member, CommitteeMember)]] = Future.successful(None)

  val authenticateWithJwt: Directive1[Member] = {
    (extractExecutionContext & extractRequest & optionalHeaderValueByName(authHeader)).tflatMap { case (ec, request, maybeJwt) =>
      log.info(request.method.value + " " + request.uri)

      val uuidFromJwt = maybeJwt
        .flatMap(jwtService.verifyJwt)

      // Save for logging
      val uuid = uuidFromJwt.map(_.toString).getOrElse("** NO UUID **")

      val lookupResult = uuidFromJwt.fold(emptyMember)(memberDao.get)

      onSuccess(lookupResult)
        .map(resultToMaybeMember)
        .flatMap(maybeMemberToDirective(uuid))
    }
  }

  /**
    * Validate a user with a JWT and ensure they're an active committee member.
    */
  val authenticateCommitteeWithJwt: Directive1[(Member, CommitteeMember)] = {
    (extractExecutionContext & extractRequest & optionalHeaderValueByName(authHeader)).tflatMap { case (ec, request, maybeJwt) =>
      implicit val execContext: ExecutionContext = ec

      log.info(request.method.value + " " + request.uri)

      val uuidFromJwt = maybeJwt
        .flatMap(jwtService.verifyJwt)

      // Save for logging
      val uuid = uuidFromJwt.map(_.toString).getOrElse("** NO UUID **")

      onSuccess(
        uuidFromJwt
          .fold(emptyMember)(memberDao.get)
          .flatMap(
            _.fold(logMemberLookupError, committeeMemberForMember)
          )
      ).flatMap {
        case Some(memberWithCommittee: (Member, CommitteeMember)) => provide(memberWithCommittee)
        case None => rejectRequest[(Member, CommitteeMember)](uuid)
      }
    }
  }

  private def logMemberLookupError(error: String): Future[Option[(Member, CommitteeMember)]] = {
    log.error(s"(lookupCommitteeMember) Member lookup failed: $error")
    Future.successful(None)
  }

  private def committeeMemberForMember(maybeMember: Option[Member])(implicit ec: ExecutionContext): Future[Option[(Member, CommitteeMember)]] = {
    maybeMember.fold(emptyMemberWithCommittee) { member =>
      committeeMemberDao
        .forMember(member.uuid)
        .map {
          case Some(committeeMember: CommitteeMember) => {
            if (committeeMember.locked) {
              log.error(s"Committe member ${committeeMember.uuid} is locked - denying access")
              None
            } else {
              Some(member, committeeMember)
            }
          }
          case None =>
            log.error(s"lookupCommitteeMember failed - committee member not found for member ${member.uuid}")
            None
        }
    }
  }

  private def rejectRequest[T](uuid: String): Directive1[T] = {
    log.error(s"Login failed with UUID: $uuid")
    reject(AuthenticationFailedRejection(CredentialsMissing, dummyChallenge))
  }

  private def resultToMaybeMember(result: \/[String, Option[Member]]): Option[Member] = {
    result match {
      case \/-(maybeMember: Option[Member]) => maybeMember
      case -\/(error: String) =>
        log.error(s"Error looking up member: $error")
        None
    }
  }

  private def maybeMemberToDirective(uuid: String)(maybeMember: Option[Member]): Directive1[Member] = {
    maybeMember match {
      case Some(member: Member) => provide(member)
      case None =>
        log.error(s"JWT valid but no matching member: $uuid")
        reject(AuthenticationFailedRejection(CredentialsMissing, dummyChallenge))
    }
  }

}
