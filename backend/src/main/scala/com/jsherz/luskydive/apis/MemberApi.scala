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
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.jsherz.luskydive.core.{CommitteeMember, Member, TextMessage}
import com.jsherz.luskydive.dao.{MemberDao, MemberDaoErrors, TextMessageDao}
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.json.MemberSearchRequest
import org.slf4j.{Logger, LoggerFactory}
import scalaz.{-\/, \/-}

/**
  * Used to retrieve member information.
  */
class MemberApi(memberDao: MemberDao, textMessageDao: TextMessageDao)
               (implicit authDirective: Directive1[(Member, CommitteeMember)]) {

  private val log: Logger = LoggerFactory.getLogger(getClass)

  val searchRoute = path("search") {
    post {
      authDirective { _ =>
        entity(as[MemberSearchRequest]) { req =>
          if (req.searchTerm.trim.length >= 3) {
            val searchResult = memberDao.search(req.searchTerm)

            searchResult match {
              case \/-(futureResults) => onSuccess(futureResults) {
                complete(_)
              }
              case -\/(error) => {
                log.error("Failed to perform member search" + error)

                complete(StatusCodes.InternalServerError -> error)
              }
            }
          } else {
            log.info(s"Search request too small: '${req.searchTerm}'")

            complete(StatusCodes.BadRequest -> MemberDaoErrors.invalidSearchTerm)
          }
        }
      }
    }
  }

  val getRoute = (path(JavaUUID) & get & authDirective) { (memberUuid, _) =>
    val lookup = memberDao.get(memberUuid)

    onSuccess(lookup) {
      case Some(member: Member) => complete(member)
      case None => {
        log.info(s"Could not get member with UUID '${memberUuid}'")

        complete(StatusCodes.NotFound -> "Member not found")
      }
    }
  }

  val updateRoute = (path(JavaUUID) & put & authDirective & entity(as[Member])) { (memberUuid, _, member) =>
    if (member.uuid == memberUuid) {
      onSuccess(memberDao.update(member)) { returnedMember =>
        complete(returnedMember)
      }
    } else {
      complete(StatusCodes.BadRequest -> MemberApiErrors.uuidUrlBodyMismatch)
    }
  }

  val textMessagesRoute = (path(JavaUUID / "text-messages") & get & authDirective) { (memberUuid, _) =>
    onSuccess(textMessageDao.forMember(memberUuid)) { messages: Seq[TextMessage] =>
      complete(messages)
    }
  }

  val createRoute = (path("create") & post & authDirective & entity(as[Member])) { (_, member) =>
    val createResult = memberDao.create(member)

    createResult match {
      case \/-(futureUuid) => onSuccess(futureUuid) {
        complete(_)
      }
      case -\/(error) => complete(StatusCodes.InternalServerError -> error)
    }
  }

  val route: Route = pathPrefix("members") {
    searchRoute ~
      getRoute ~
      textMessagesRoute ~
      updateRoute ~
      createRoute
  }

}

object MemberApiErrors {

  val uuidUrlBodyMismatch = "error.uuidUrlBodyMismatch"

}
