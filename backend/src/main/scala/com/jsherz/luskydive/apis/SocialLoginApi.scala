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

import akka.event.LoggingAdapter
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.jsherz.luskydive.json.{SocialLoginRequest, SocialLoginResponse}
import com.jsherz.luskydive.services.{JwtService, SocialService}

import scala.concurrent.ExecutionContext

/**
  * Used to authenticate users with a social single-sign-on.
  */
class SocialLoginApi(private val service: SocialService, private val jwtService: JwtService)
                    (implicit ec: ExecutionContext, log: LoggingAdapter) {

  import com.jsherz.luskydive.json.SocialLoginJsonSupport._

  val socialLoginRoute: Route = (pathEnd & post & entity(as[SocialLoginRequest])) { req =>
    val parsedRequest = service.parseSignedRequest(req.signedRequest)

    parsedRequest match {
      case Some(validRequest) => complete(SocialLoginResponse(true, None, Some("JWT HERE!")))
      case None => complete(SocialLoginResponse(false, Some("Invalid signed request."), None))
    }
  }

  val getTokenRoute: Route = (path("token") & get) {
    complete(jwtService.createJwt())
  }

  val route: Route = pathPrefix("social-login") {
    socialLoginRoute ~
    getTokenRoute
  }

}
