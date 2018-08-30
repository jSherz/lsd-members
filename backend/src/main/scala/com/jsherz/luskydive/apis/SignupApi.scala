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

import akka.event.LoggingAdapter
import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.server.Directives._
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.json.{SignupAltRequest, SignupJsonSupport, SignupRequest, SignupResponse}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{Failure, Success}

/**
  * The two methods of signing up new members at a fresher's fair (phone number or e-mail).
  */
class SignupApi(private val memberDao: MemberDao)
               (implicit ec: ExecutionContext, authDirective: Directive1[Member], log: LoggingAdapter) {

  import SignupJsonSupport._

  /**
    * The primary method of signing up new members.
    *
    * Requires a name and e-mail address.
    */
  private val signupRoute = (path("sign-up") & post & authDirective & entity(as[SignupRequest])) { (_, req) =>
    req.validate() match {
      case Success(phoneNumber) => {
        val createAction = memberDao.memberExists(Some(phoneNumber), None).flatMap {
          case true => Future.successful(SignupResponse(false, Map("phoneNumber" -> "error.inUse")))
          case false => {
            val createdAt = currentTimestamp()
            val member = Member(None, req.name, None, Some(phoneNumber), None, None, None, None, false, false,
              createdAt, createdAt, None)

            memberDao.create(member).map { _ =>
              log.info(s"Signed up member with name '${req.name}' and phone number '${req.phoneNumber}'.")

              SignupResponse(true, Map.empty)
            }
          }
        }

        onSuccess(createAction) { result =>
          complete(result)
        }
      }
      case Failure(reason) => {
        log.info("Member sign-up validation error: " + reason.list.toString)

        complete(SignupResponse(false, reason.list.toList.toMap))
      }
    }
  }

  /**
    * The alternative method of signing up new members.
    *
    * Requires a name and phone number.
    */
  private val signupAltRoute = (path("sign-up" / "alt") & post & authDirective & entity(as[SignupAltRequest])) { (_, req) =>
    req.validate() match {
      case Success(_) => {
        val createAction = memberDao.memberExists(None, Some(req.email)).flatMap {
          case true => Future.successful(SignupResponse(false, Map("email" -> "error.inUse")))
          case false => {
            val createdAt = currentTimestamp()
            val member = Member(None, req.name, None, None, Some(req.email), None, None, None, false, false, createdAt,
              createdAt, None)

            memberDao.create(member).map { _ =>
              log.info(s"Signed up member with name '${req.name}' and e-mail '${req.email}'.")

              SignupResponse(true, Map.empty)
            }
          }
        }

        onSuccess(createAction) { result =>
          complete(result)
        }
      }
      case Failure(reason) => {
        log.info("Member alt sign-up validation error: " + reason.list.toString)

        complete(SignupResponse(false, reason.list.toList.toMap))
      }
    }
  }

  val route: Route = pathPrefix("members") {
    signupRoute ~ signupAltRoute
  }

  private def currentTimestamp(): Timestamp = {
    new Timestamp(new java.util.Date().getTime)
  }

}
