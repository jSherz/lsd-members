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

import akka.http.scaladsl.server.Directives._
import com.jsherz.luskydive.core.{SignupAltRequest, SignupRequest, SignupResponse}
import com.jsherz.luskydive.dao.MemberDAO

import scala.concurrent.ExecutionContext

/**
  * The two methods of signing up new members at a fresher's fair (phone number or e-mail).
  */
class SignupAPI(private val memberDao: MemberDAO)(implicit ec: ExecutionContext) {

  import com.jsherz.luskydive.core.SignupJsonSupport._

  /**
    * The primary method of signing up new members.
    *
    * Requires a name and e-mail address.
    */
  private val signupRoute = path("sign-up") {
    entity(as[SignupRequest]) { req =>
      post {
        complete {
          SignupResponse(false, Map.empty)
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
    entity(as[SignupAltRequest]) { req =>
      post {
        complete {
          SignupResponse(false, Map.empty)
        }
      }
    }
  }

  val route = pathPrefix("members") {
    signupRoute ~ signupAltRoute
  }

}
