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
import java.util.{Calendar, Date}

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.jsherz.luskydive.dao.{AuthDao, AuthDaoErrors}
import com.jsherz.luskydive.json.{LoginRequest, LoginResponse}
import com.jsherz.luskydive.services.JwtService
import org.slf4j.{Logger, LoggerFactory}
import scalaz.{-\/, \/-}

import scala.concurrent.ExecutionContext

/**
  * Used to authenticate users.
  */
class LoginApi(dao: AuthDao, jwtService: JwtService)(implicit ec: ExecutionContext) {

  import com.jsherz.luskydive.json.LoginJsonSupport._

  private val log: Logger = LoggerFactory.getLogger(getClass)

  /**
    * Number of hours an API key lasts for after being created or reissued.
    */
  val API_KEY_EXPIRES = 24

  val loginRoute = pathEnd {
    post {
      entity(as[LoginRequest]) { req =>
        val time = new Timestamp(new Date().getTime)
        val loginResult = dao.login(req.email, req.password)

        onSuccess(loginResult) {
          case \/-(cm) =>
            val jwt = jwtService.createJwt(cm.memberUuid, time.toInstant, addHoursToTimestamp(time, API_KEY_EXPIRES).toInstant)
            complete(LoginResponse(true, Map.empty, Some(jwt)))
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

  /**
    * Create a new [[Timestamp]] by adding a number of hours to an existing one.
    *
    * @param time
    * @param hours
    * @return
    */
  private def addHoursToTimestamp(time: Timestamp, hours: Int): Timestamp = {
    val calendar = Calendar.getInstance()
    calendar.setTime(time)
    calendar.add(Calendar.HOUR, API_KEY_EXPIRES)

    new Timestamp(calendar.getTime.getTime)
  }

}
