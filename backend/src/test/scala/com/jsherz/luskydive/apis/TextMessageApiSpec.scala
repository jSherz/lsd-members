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

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.dao.StubTextMessageDao
import org.scalatest.{Matchers, WordSpec}


class TextMessageApiSpec extends WordSpec with Matchers with ScalatestRouteTest {

  trait Fixtured {
    val validApiKey = "1248ytyghbjiytrfdcgvhiuojknygt6r5"

    val dao = new StubTextMessageDao()
    val route = new TextMessageApi(dao, validApiKey).route
  }

  "TextMessageApi#receive" should {

    "not require the standard API key authentication" in new Fixtured {
      Post("/text-messages/receive/" + validApiKey) ~> Route.seal(route) ~> check {
        response.status shouldBe StatusCodes.OK
      }
    }

    "reject requests without a receiving secret" in new Fixtured {
      Post("/text-messages/receive/") ~> Route.seal(route) ~> check {
        response.status shouldBe StatusCodes.Unauthorized
        responseAs[String] shouldEqual "Unauthorized."
      }

      Post("/text-messages/receive") ~> Route.seal(route) ~> check {
        response.status shouldBe StatusCodes.NotFound
      }
    }

    "reject requests with an invalid receiving secret" in new Fixtured {
      Post("/text-messages/receive/asdf598y7ghbjnoi9u8y7ghb") ~> Route.seal(route) ~> check {
        response.status shouldBe StatusCodes.Unauthorized
        responseAs[String] shouldEqual "Unauthorized."
      }
    }

    "accept and save text messages with the correct information" is pending

  }

}
