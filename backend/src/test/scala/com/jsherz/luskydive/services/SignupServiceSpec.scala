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

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._
import com.jsherz.luskydive.routes.SignupRoutes
import MediaTypes._
import org.specs2.specification.Fragments

class SignupServiceSpec extends Specification with Specs2RouteTest with SignupRoutes {
  def actorRefFactory = system

  val reqNothing = "{}"
  val reqPhone = "{\"phoneNumber\": \"07123123123\"}"
  val reqEmail = "{\"email\": \"joe@bloggs.org\"}"
  val reqName = "{\"name\": \"Joe Bloggs\"}"
  val reqComplete = "{\"name\": \"Joe Bloggs\", \"phoneNumber\": \"07123123123\"}"
  val reqCompleteAlt = "{\"name\": \"Joe Bloggs\", \"email\": \"joe@bloggs.org\"}"

  "SignupService" should {
    val url = "/api/v1/sign-up"
    val missingFields: Seq[(String, String)] = Seq(
      (reqNothing, "The request content was malformed:\nObject is missing required member 'name'"),
      (reqPhone, "The request content was malformed:\nObject is missing required member 'phoneNumber'"),
      (reqName, "The request content was malformed:\nObject is missing required member 'name'")
    )

    "return a MethodNotAllowed error for GET requests" in {
      Get(url) ~> sealRoute(signupRoutes) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: POST"
      }
    }

    "return a UnsupportedMediaType error for requests with the wrong content type" in {
      Post(url, reqComplete) ~> sealRoute(signupRoutes) ~> check {
        status === UnsupportedMediaType
        responseAs[String] === "There was a problem with the requests Content-Type:\nExpected 'application/json'"
      }
    }

    "return a BadRequest error if the name or phone number is missing" in {
      missingFields.foldLeft(Fragments()) { (frag: Fragments, data: (String, String)) =>
        frag.append(
          (s"example ${data._1} ": ExampleDesc) ! {
            Post(url, HttpEntity(`application/json`, data._1)) ~> sealRoute(signupRoutes) ~> check {
              status === BadRequest
              responseAs[String] === data._2
            }
          }
        )
      }
    }

    "accept a complete request" in {
      Post(url, HttpEntity(`application/json`, reqComplete)) ~> sealRoute(signupRoutes) ~> check {
        status === OK
      }
    }
  }

  "SignupService (alt)" should {

    /*"return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("Say hello")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }*/

    "return a MethodNotAllowed error for GET requests" in {
      Get("/api/v1/sign-up/alt") ~> sealRoute(signupRoutes) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: POST"
      }
    }
  }
}
