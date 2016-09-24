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

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import com.jsherz.luskydive.dao.{MemberDao, MemberDaoErrors, TextMessageDao}

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.jsherz.luskydive.core.{Member, TextMessage}
import com.jsherz.luskydive.json.MemberSearchRequest
import com.jsherz.luskydive.json.MemberJsonSupport._

import scalaz.{-\/, \/-}

/**
  * Used to retrieve member information.
  */
class MemberApi(private val memberDao: MemberDao,
                private val textMessageDao: TextMessageDao)
               (implicit ec: ExecutionContext, authDirective: Directive1[UUID], log: LoggingAdapter) {

  val searchRoute = path("search") {
    post {
      authDirective { _ =>
        entity(as[MemberSearchRequest]) { req =>
          if (req.searchTerm.trim.length >= 3) {
            onSuccess(memberDao.search(req.searchTerm)) {
              case \/-(results) => complete(results)
              case -\/(error) => {
                log.error("Failed to perform member search" + error)

                complete(StatusCodes.InternalServerError, error)
              }
            }
          } else {
            log.info(s"Search request too small: '${req.searchTerm}'")

            complete(StatusCodes.BadRequest, MemberDaoErrors.invalidSearchTerm)
          }
        }
      }
    }
  }

  val getRoute = (path(JavaUUID) & get & authDirective) { (memberUuid, _) =>
    val lookup = memberDao.get(memberUuid)

    onSuccess(lookup) {
      case \/-(maybeMember: Option[Member]) => {
        maybeMember match {
          case Some(member: Member) => complete(member)
          case None => {
            log.info(s"Could not get member with UUID '${memberUuid}'")

            complete(StatusCodes.NotFound, "Member not found")
          }
        }
      }
      case -\/(error) => complete(StatusCodes.InternalServerError, error)
    }
  }

  val textMessagesRoute = (path(JavaUUID / "text-messages") & get & authDirective) { (memberUuid, _) =>
    onSuccess(textMessageDao.forMember(memberUuid)) {
      case \/-(textMessages: Seq[TextMessage]) => complete(textMessages)
      case -\/(error) => complete(StatusCodes.InternalServerError, error)
    }
  }

  val route: Route = pathPrefix("members") {
    searchRoute ~
      getRoute ~
      textMessagesRoute
  }

}
