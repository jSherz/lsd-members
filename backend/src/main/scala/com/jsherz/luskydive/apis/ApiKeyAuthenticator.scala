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
import java.util.{Date, UUID}

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.{CredentialsMissing, CredentialsRejected}
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Directive1}
import akka.http.scaladsl.server.directives._
import com.jsherz.luskydive.dao.AuthDao

import scalaz.{-\/, \/-}

/**
  * Provides a directive that will authenticate users with their API key.
  */
class ApiKeyAuthenticator(authDao: AuthDao)
                         (implicit log: LoggingAdapter) {

  import BasicDirectives._
  import HeaderDirectives._
  import FutureDirectives._
  import RouteDirectives._

  /**
    * Lowercase name of the header that will provide the API key.
    */
  private val headerName = "api-key"

  /**
    * We don't use a "challenge" (as with HTTP Basic Auth) so provide an empty one.
    */
  private val dummyChallenge = HttpChallenge("", None)

  /**
    * Authenticate a request by API key.
    *
    * @return The currently logged in member's UUID
    */
  val authenticateWithApiKey: Directive1[UUID] = {
    extractExecutionContext.flatMap { implicit ec ⇒
      extractRequest.flatMap { request =>
        log.info(request.method.value + " " + request.uri)

        extractApiKey.flatMap {
          case Some(apiKey) =>
            val time = new Timestamp(new Date().getTime)

            onSuccess(authDao.authenticate(apiKey, time)).flatMap {
              case \/-(memberUuid) => provide(memberUuid)
              case -\/(_) => {
                log.info("Auth failed - invalid API key: " + apiKey)

                reject(AuthenticationFailedRejection(CredentialsRejected, dummyChallenge))
              }
            }
          case None => {
            log.info("Auth failed - no API key.")

            reject(AuthenticationFailedRejection(CredentialsMissing, dummyChallenge))
          }
        }
      }
    }
  }

  /**
    * Extract the "api-key" header from a request.
    *
    * @return
    */
  private def extractApiKey: Directive1[Option[UUID]] = {
    optionalHeaderValueByName(headerName).map(parseUuid)
  }

  /**
    * Attempt to parse a UUID.
    *
    * @param maybeUuid
    * @return
    */
  private def parseUuid(maybeUuid: Option[String]): Option[UUID] = {
    maybeUuid match {
      case Some(rawUuid: String) =>
        try {
          Some(UUID.fromString(rawUuid))
        } catch {
          case _: IllegalArgumentException => None
        }
      case None => None
    }
  }

}
