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

package com.jsherz.luskydive.routes

import akka.actor.{ActorRef, Props}
import com.jsherz.luskydive.models.Member
import com.jsherz.luskydive.services.SignupService
import spray.http.MediaTypes._
import spray.routing.{HttpService, RequestContext}

/**
  * Routes for user sign-up.
  */
trait SignupRoutes extends HttpService {

  import com.jsherz.luskydive.services.SignupService._
  import com.jsherz.luskydive.models.MemberJsonSupport._

  def signupService(ctx: RequestContext): ActorRef = {
    actorRefFactory.actorOf(Props(classOf[SignupService], ctx))
  }

  val signupRoutes =
    pathPrefix("api" / "v1") {
      path("sign-up") {
        post {
          entity(as[Member]) { member =>
            respondWithMediaType(`application/json`) { ctx =>
              signupService(ctx) ! Signup(member.name, member.phoneNumber)
            }
          }
        }
      } ~
      path("sign-up" / "alt") {
        post {
          entity(as[Member]) { member =>
            respondWithMediaType(`application/json`) { ctx =>
              signupService(ctx) ! SignupAlt(member.name, member.email)
            }
          }
        }
      }
    }
}
