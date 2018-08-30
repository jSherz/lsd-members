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

package com.jsherz.luskydive.services

import java.io.UnsupportedEncodingException
import java.time.Instant
import java.util.{Date, UUID}

import akka.event.LoggingAdapter
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.{JWTCreationException, JWTVerificationException}

object JwtService {

  val Issuer = "LSD"

  val ClaimUuid = "UUID"

}

trait JwtService {

  def verifyJwt(token: String): Option[UUID]

  def createJwt(uuid: UUID, issuedAt: Instant, expiresAt: Instant): String

}

class JwtServiceImpl(jwtSecret: String)(implicit log: LoggingAdapter) extends JwtService {

  private val algorithm = Algorithm.HMAC384(jwtSecret)

  /**
    * Attempt to verify a JWT and retrieve a member's UUID from it.
    *
    * @param token JWT to parse & verify
    * @return Members UUID, if successful
    */
  def verifyJwt(token: String): Option[UUID] = {
    try {
      val verifier = JWT.require(algorithm)
        .withIssuer(JwtService.Issuer)
        .build()

      val decoded = verifier.verify(token)

      val rawUuid = for {
        maybeIssuedAt <- Option(decoded.getIssuedAt)
        maybeExpiresAt <- Option(decoded.getExpiresAt)
        maybeUuid <- Option[String](decoded.getClaim(JwtService.ClaimUuid).asString())
      } yield maybeUuid

      rawUuid match {
        case Some(uuid) =>
          log.debug(s"Verified JWT - $uuid")

          try {
            Some(UUID.fromString(uuid))
          } catch {
            case ex: IllegalArgumentException =>
              log.error(s"Mangled UUID in JWT claim - ${uuid}")
              None
          }
        case _ =>
          log.error("JWT authentication failed - valid token but missing issued at, expires at or UUID claim.")
          None
      }
    } catch {
      case ex: UnsupportedOperationException => {
        val msg = "Failed to parse JWT - UTF-8 encoding not supported."
        log.error(ex, msg)
        throw new RuntimeException(msg, ex)
      }
      case ex: JWTVerificationException => {
        log.warning(s"Failed to parse JWT: ${ex.getMessage} ${ex.getCause}")

        None
      }
    }
  }

  /**
    * Create a JWT for a member.
    *
    * @param uuid      Member's UUID
    * @param issuedAt  Token valid from
    * @param expiresAt Token valid until
    * @return Token
    */
  def createJwt(uuid: UUID, issuedAt: Instant, expiresAt: Instant): String = {
    try {
      val token = JWT.create
        .withIssuer(JwtService.Issuer)
        .withClaim(JwtService.ClaimUuid, uuid.toString)
        .withIssuedAt(Date.from(issuedAt))
        .withExpiresAt(Date.from(expiresAt))
        .sign(algorithm)

      token
    } catch {
      case ex: UnsupportedEncodingException =>
        val msg = "Failed to parse JWT - UTF-8 encoding not supported."
        log.error(ex, msg)
        throw new RuntimeException(msg, ex)
      case ex: JWTCreationException =>
        val msg = s"Failed to create JWT."
        log.error(ex, msg)
        throw new RuntimeException(msg, ex)
    }
  }

}
