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

package com.jsherz.luskydive.core

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.UUID

import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class FBSignedRequestSpec extends WordSpec with Matchers {

  "FBSignedRequest" should {

    "handle a null expiry & issued at dates correctly" in {
      val request = new FBSignedRequest(randomId, randomOauth, FBSignedRequest.nullLong(), FBSignedRequest.nullLong())

      request.getExpires shouldBe None
      request.getIssuedAt shouldBe None
    }

    "correctly parse an expiry date" in {
      val request = new FBSignedRequest(randomId, randomOauth, 1493833025L, FBSignedRequest.nullLong())

      request.getExpires shouldBe toInstant("2017-05-03T17:37:05.000Z")
    }

    "correctly parse an issued at date" in {
      val request = new FBSignedRequest(randomId, randomOauth, FBSignedRequest.nullLong(), 1492833025L)

      request.getIssuedAt shouldBe toInstant("2017-04-22T03:50:25.000Z")
    }

  }

  private def randomId: String = {
    (Random.nextInt(1000000000) + 1000000000).toString
  }

  private def randomOauth: String = {
    UUID.randomUUID().toString
  }

  private def toInstant(dateStr: String): Option[Instant] = {
    Some(Instant.from(DateTimeFormatter.ISO_INSTANT.parse(dateStr)))
  }

}
