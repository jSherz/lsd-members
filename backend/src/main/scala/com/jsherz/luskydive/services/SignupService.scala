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

package com.jsherz.luskydive.services

import akka.actor.{Actor, ActorLogging}
import akka.event.LoggingReceive
import spray.routing.RequestContext

object SignupService {
  /**
    * The primary method of signing up - with a name & phone number.
    * @param name The user's name (first or full acceptable)
    * @param phoneNumber The user's phone number, defaulting to local country
    */
  case class Signup(name: String, phoneNumber: Option[String])

  /**
    * Alternative method of signing up, used when a user's phone number isn't available.
    * @param name The user's name (first or full acceptable)
    * @param email The user's e-mail address
    */
  case class SignupAlt(name: String, email: Option[String])
}

/**
  * Handles new users.
  */
class SignupService(ctx: RequestContext) extends Actor with ActorLogging {
  import SignupService._

  override def receive: Actor.Receive = LoggingReceive {
    case Signup(name, phoneNumber) =>
      ctx.complete("Test")

    case SignupAlt(name, email) =>
      ctx.complete("Test alt")
  }
}
