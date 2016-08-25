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

import java.sql.Timestamp
import java.util.UUID

import akka.http.scaladsl.server.{Directive1, Route}
import akka.http.scaladsl.server.Directives._
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.dao.MemberDao
import com.jsherz.luskydive.json.{SignupAltRequest, SignupJsonSupport, SignupRequest, SignupResponse}
import com.jsherz.luskydive.services.Cors.cors

import scala.concurrent.ExecutionContext
import scalaz.{Failure, Success}

/**
  * The two methods of signing up new members at a fresher's fair (phone number or e-mail).
  */
class SignupApi(private val memberDao: MemberDao)
               (implicit ec: ExecutionContext, authDirective: Directive1[UUID]) {

  import SignupJsonSupport._

  /**
    * The primary method of signing up new members.
    *
    * Requires a name and e-mail address.
    */
  private val signupRoute = path("sign-up") {
    cors {
      post {
        authDirective { _ =>
          entity(as[SignupRequest]) { req =>
            complete {
              req.validate() match {
                case Success(phoneNumber) => {
                  memberDao.memberExists(Some(phoneNumber), None).map {
                    case true => SignupResponse(false, Map("phoneNumber" -> "error.inUse"))
                    case false => {
                      val createdAt = currentTimestamp
                      memberDao.create(Member(None, req.name, Some(phoneNumber), None, None, createdAt, createdAt))

                      SignupResponse(true, Map.empty)
                    }
                  }
                }
                case Failure(reason) => SignupResponse(false, reason.list.toList.toMap)
              }
            }
          }
        }
      }
    }
  }

  /**
    * The alternative method of signing up new members.
    *
    * Requires a name and phone number.
    */
  private val signupAltRoute = path("sign-up" / "alt") {
    cors {
      post {
        authDirective { _ =>
          entity(as[SignupAltRequest]) { req =>
            complete {
              req.validate() match {
                case Success(_) => {
                  memberDao.memberExists(None, Some(req.email)).map {
                    case true => SignupResponse(false, Map("email" -> "error.inUse"))
                    case false => {
                      val createdAt = currentTimestamp
                      memberDao.create(Member(None, req.name, None, Some(req.email), None, createdAt, createdAt))

                      SignupResponse(true, Map.empty)
                    }
                  }
                }
                case Failure(reason) => SignupResponse(false, reason.list.toList.toMap)
              }
            }
          }
        }
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
