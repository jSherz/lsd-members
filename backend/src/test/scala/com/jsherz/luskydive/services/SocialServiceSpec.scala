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
import com.jsherz.luskydive.util.FbClientFactory
import com.restfb.FacebookClient.AccessToken
import com.restfb.exception.{FacebookJsonMappingException, FacebookSignedRequestParsingException}
import com.restfb.scope.ScopeBuilder
import com.restfb.types.User
import com.restfb.{FacebookClient, Parameter}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.{any, anyString, eq => meq}
import org.mockito.Mockito.{mock, when}
import org.scalatest.{Matchers, WordSpec}

import scala.util.Random

class SocialServiceSpec extends WordSpec with Matchers with ScalatestRouteTest {

  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  "SocialService#parseSignedRequest" should {

    "catch FacebookExceptions and return None" in {
      val fb = mock(classOf[FacebookClient])
      when(fb.obtainAppAccessToken(anyString, anyString)).thenReturn(AccessToken.fromQueryString("?access_token=blah"))
      when(fb.parseSignedRequest(any, any, any)).thenThrow(new FacebookSignedRequestParsingException("Boaty McBoatface"))

      val service = new SocialServiceImpl(factoryFor(fb), "BLAHBLAHBLAH", "SSSHHHSECRET", "https://localhost")

      service.parseSignedRequest("foobar") shouldBe None
    }

    "return Some(req) when parsing succeeds" in {
      val dummyRequest = "0010011010101001001"
      val dummySecret = "WATERMELONADDICTION"
      val expectedSignedRequest = dummySignedRequest()

      val fb = mock(classOf[FacebookClient])
      when(fb.obtainAppAccessToken(anyString, anyString)).thenReturn(AccessToken.fromQueryString("?access_token=blah"))
      when(fb.parseSignedRequest(dummyRequest, dummySecret, classOf[FBSignedRequest]))
        .thenReturn(expectedSignedRequest)

      val service = new SocialServiceImpl(factoryFor(fb), "BLAHBLAHBLAH", dummySecret, "http://127.0.0.1")

      service.parseSignedRequest(dummyRequest) shouldBe Some(expectedSignedRequest)
    }

  }

  "SocialService#getUserAccessToken" should {

    "return Left(error) when getting the access token fails" in {
      val dummyRequest = "0010011010101001001"
      val dummySecret = "WATERMELONADDICTION"
      val expectedSignedRequest = dummySignedRequest()

      val fb = mock(classOf[FacebookClient])
      when(fb.obtainAppAccessToken(anyString, anyString)).thenReturn(AccessToken.fromQueryString("?access_token=blah"))
      when(fb.parseSignedRequest(dummyRequest, dummySecret, classOf[FBSignedRequest]))
        .thenReturn(expectedSignedRequest)

      when(fb.obtainUserAccessToken(anyString, anyString, anyString, anyString))
        .thenThrow(new FacebookJsonMappingException("failed2parse"))

      val service = new SocialServiceImpl(factoryFor(fb), "APPID", "APPSECRET", "http://localhost")

      val result = service.getUserAccessToken("my-verification-code")
      result.isLeft shouldBe true
      result.leftMap {
        _ shouldBe "failed2parse"
      }
    }

    "return the returned access token when given a valid request" in {
      val dummyRequest = "0010011010101001001"
      val dummySecret = "WATERMELONADDICTION"
      val expectedSignedRequest = dummySignedRequest()

      val appId = UUID.randomUUID().toString
      val appSecret = UUID.randomUUID().toString
      val redirectUrl = "http://local-dev.leedsskydivers.com/" + UUID.randomUUID()

      val fb = mock(classOf[FacebookClient])
      when(fb.obtainAppAccessToken(anyString, anyString)).thenReturn(AccessToken.fromQueryString("?access_token=blah"))
      when(fb.parseSignedRequest(dummyRequest, dummySecret, classOf[FBSignedRequest]))
        .thenReturn(expectedSignedRequest)

      when(fb.obtainUserAccessToken(appId, appSecret, redirectUrl, "wooooooooooot"))
        .thenReturn(AccessToken.fromQueryString("?access_token=thecakeisalie"))

      val service = new SocialServiceImpl(factoryFor(fb), appId, appSecret, redirectUrl)

      val result = service.getUserAccessToken("wooooooooooot")
      result.isRight shouldBe true
      result.map {
        _.getAccessToken shouldBe "thecakeisalie"
      }
    }

  }

  "SocialService#getUserForAccessToken" should {

    "return Left(error) when getting the user's info fails" in {
      val dummyRequest = "0010011010101001001"
      val dummySecret = "WATERMELONADDICTION"
      val expectedSignedRequest = dummySignedRequest()

      val fb = mock(classOf[FacebookClient])
      when(fb.obtainAppAccessToken(anyString, anyString)).thenReturn(AccessToken.fromQueryString("?access_token=blah"))
      when(fb.parseSignedRequest(dummyRequest, dummySecret, classOf[FBSignedRequest]))
        .thenReturn(expectedSignedRequest)

      val userAuthdFb = mock(classOf[FacebookClient])
      when(userAuthdFb.fetchObject("/me", classOf[User], Parameter.`with`("fields", "id,first_name,last_name,name,email")))
        .thenThrow(new FacebookJsonMappingException("no user 4 u mwuhahahaha"))

      val service = new SocialServiceImpl(factoryFor(fb, Some(userAuthdFb)), "APPID", "APPSECRET", "http://localhost")

      val result = service.getUserForAccessToken("access-token-123")
      result.isLeft shouldBe true
      result.leftMap {
        _ shouldBe "no user 4 u mwuhahahaha"
      }
    }

    "request the user with the correct fields and return it" in {
      val dummyRequest = "0010011010101001001"
      val dummySecret = "WATERMELONADDICTION"
      val expectedSignedRequest = dummySignedRequest()

      val fb = mock(classOf[FacebookClient])
      when(fb.obtainAppAccessToken(anyString, anyString)).thenReturn(AccessToken.fromQueryString("?access_token=blah"))
      when(fb.parseSignedRequest(dummyRequest, dummySecret, classOf[FBSignedRequest]))
        .thenReturn(expectedSignedRequest)

      val expectedUser = new User()
      expectedUser.setName(UUID.randomUUID().toString)

      val userAuthdFb = mock(classOf[FacebookClient])
      when(userAuthdFb.fetchObject("/me", classOf[User], Parameter.`with`("fields", "id,first_name,last_name,name,email")))
        .thenReturn(expectedUser)

      val service = new SocialServiceImpl(factoryFor(fb, Some(userAuthdFb)), "APPID", "APPSECRET", "http://localhost")

      val result = service.getUserForAccessToken("access-token-123")
      result.isRight shouldBe true
      result.map {
        _ shouldBe expectedUser
      }
    }

  }

  private def dummySignedRequest() =
    new FBSignedRequest("666000666", UUID.randomUUID().toString, Random.nextLong(), Random.nextLong())

  private def factoryFor(defaultClient: FacebookClient,
                         clientForAccessToken: Option[FacebookClient] = None): FbClientFactory = {
    val factory = mock(classOf[FbClientFactory])
    when(factory.forAppIdAndSecret(anyString, anyString)).thenReturn(defaultClient)

    clientForAccessToken.map {
      when(factory.forAccessToken(anyString)).thenReturn(_)
    }

    factory
  }

}
