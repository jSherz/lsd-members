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

import akka.http.scaladsl.model.StatusCodes
import com.jsherz.luskydive.dao.{MemberDao, MemberDaoErrors}

import scala.concurrent.ExecutionContext
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import com.jsherz.luskydive.json.MemberSearchRequest
import com.jsherz.luskydive.services.Cors.cors

import scalaz.{-\/, \/-}

/**
  * Used to retrieve member information.
  */
class MemberApi(private val memberDao: MemberDao)
               (implicit ec: ExecutionContext, authDirective: Directive1[UUID]) {

  import com.jsherz.luskydive.json.MemberJsonSupport._

  val searchRoute = path("search") {
    cors {
      post {
        authDirective { _ =>
          entity(as[MemberSearchRequest]) { req =>
            if (req.searchTerm.trim.length >= 3) {
              onSuccess(memberDao.search(req.searchTerm)) {
                case \/-(results) => complete(results)
                case -\/(error) => complete(StatusCodes.InternalServerError, error)
              }
            } else {
              complete(StatusCodes.BadRequest, MemberDaoErrors.invalidSearchTerm)
            }
          }
        }
      }
    }
  }

  val route: Route = pathPrefix("members") {
    searchRoute
  }

}
