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

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.core.FBSignedRequest
import com.restfb.FacebookClient
import com.restfb.exception.FacebookSignedRequestParsingException
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class SocialServiceSpec extends WordSpec with Matchers with ScalatestRouteTest {

  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  "SocialService" should {

    "catch FacebookExceptions and return None" in {
      val fb = mock(classOf[FacebookClient])
      when(fb.parseSignedRequest(any, any, any)).thenThrow(new FacebookSignedRequestParsingException("Boaty McBoatface"))

      val service = new SocialServiceImpl(fb, "BLAHBLAHBLAH", "SSSHHHSECRET")

      service.parseSignedRequest("foobar") shouldBe None
    }

    "return Some(req) when parsing succeeds" in {
      val dummyRequest = "0010011010101001001"
      val dummySecret = "WATERMELONADDICTION"
      val expectedSignedRequest = new FBSignedRequest("666000666", UUID.randomUUID().toString, Random.nextLong(),
        Random.nextLong())

      val fb = mock(classOf[FacebookClient])
      when(fb.parseSignedRequest(dummyRequest, dummySecret, classOf[FBSignedRequest]))
        .thenReturn(expectedSignedRequest)

      val service = new SocialServiceImpl(fb, "BLAHBLAHBLAH", dummySecret)

      service.parseSignedRequest(dummyRequest) shouldBe Some(expectedSignedRequest)
    }

  }

}
