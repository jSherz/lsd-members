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
import java.time.LocalDateTime
import java.util.Date

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.jsherz.luskydive.dao.{AuthDao, AuthDaoErrors}
import com.jsherz.luskydive.json.{LoginRequest, LoginResponse}

import scala.concurrent.ExecutionContext
import scalaz.{-\/, \/-}

/**
  * Used to authenticate users.
  */
class LoginApi(private val dao: AuthDao)(implicit ec: ExecutionContext, log: LoggingAdapter) {

  import com.jsherz.luskydive.json.LoginJsonSupport._

  val loginRoute = pathEnd {
    post {
      entity(as[LoginRequest]) { req =>
        val time = new Timestamp(new Date().getTime)
        val loginResult = dao.login(req.email, req.password, time)

        onSuccess(loginResult) {
          case \/-(uuid) => complete(LoginResponse(true, Map.empty, Some(uuid)))
          case -\/(error) => {
            if (AuthDaoErrors.invalidEmailPass.equals(error)) {
              log.info(s"Login failed - invalid e-mail '${req.email}' or password.")

              complete(LoginResponse(false, Map("password" -> AuthDaoErrors.invalidEmailPass), None))
            } else if (AuthDaoErrors.accountLocked.equals(error)) {
              log.info(s"Login failed - account '${req.email}' is locked.")

              complete(LoginResponse(false, Map("email" -> AuthDaoErrors.accountLocked), None))
            } else {
              log.error("Login failed: " + error)

              complete(StatusCodes.InternalServerError, error)
            }
          }
        }
      }
    }
  }

  val route: Route = pathPrefix("login") {
    loginRoute
  }

}
