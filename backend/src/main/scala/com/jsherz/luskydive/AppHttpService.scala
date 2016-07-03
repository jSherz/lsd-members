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

package com.jsherz.luskydive

import akka.actor.{Actor, ActorContext}
import com.jsherz.luskydive.routes.SignupRoutes
import spray.routing._

/**
  * The primary application service that routes all requests.
  */
trait AppHttpService extends HttpService with SignupRoutes {
  /**
    * All application routes.
    */
  val routes =
    signupRoutes
}

/**
  * The Actor that receives all HTTP requests and routes them with the above service.
  */
class AppHttpServiceActor extends Actor with AppHttpService {

  /**
    * Used to connect this service to other Actors.
    */
  def actorRefFactory: ActorContext = context

  /**
    * Handle each HTTP request with the routes defined above.
    */
  def receive: Actor.Receive = runRoute(routes)
}
