/**
  * MIT License
  *
  * Copyright (c) 2016-2018 James Sherwood-Jones <james.sherwoodjones@gmail.com>
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

import com.jsherz.luskydive.core.FBSignedRequest
import com.jsherz.luskydive.util.{BaseSpec, FbClientFactory}
import com.restfb.FacebookClient.AccessToken
import com.restfb.exception.{FacebookJsonMappingException, FacebookSignedRequestParsingException}
import com.restfb.types.User
import com.restfb.{FacebookClient, Parameter}
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito.{mock, when}
import org.mockito.invocation.InvocationOnMock

import scala.util.Random

class SocialServiceSpec extends BaseSpec {

  private val READY_WAIT_TIME_MS = 25
  private val MAX_WAIT_ATTEMPTS = 10
  private val SLOW_CLIENT_DELAY_MS = 10 * 1000

  private def waitForReady(socialService: SocialService, attempts: Int = 0): Unit = {
    if (!socialService.ready) {
      Thread.sleep(READY_WAIT_TIME_MS)

      if (attempts > MAX_WAIT_ATTEMPTS) {
        throw new IllegalStateException(s"SocialService not ready after waiting ${READY_WAIT_TIME_MS * attempts}ms")
      } else {
        waitForReady(socialService, attempts + 1)
      }
    }
  }

  "SocialService#parseSignedRequest" should {

    "catch FacebookExceptions and return None" in {
      val fb = mock(classOf[FacebookClient])
      when(fb.obtainAppAccessToken(anyString, anyString)).thenReturn(AccessToken.fromQueryString("?access_token=blah"))
      when(fb.parseSignedRequest(any, any, any)).thenThrow(new FacebookSignedRequestParsingException("Boaty McBoatface"))

      val service = new SocialServiceImpl(factoryFor(fb), "BLAHBLAHBLAH", "SSSHHHSECRET", "https://localhost")

      waitForReady(service)

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

      waitForReady(service)

      service.parseSignedRequest(dummyRequest) shouldBe Some(expectedSignedRequest)
    }

    "return None when the FB client is not ready" in {
      val fb = mock(classOf[FbClientFactory])
      when(fb.forAppIdAndSecret(anyString, anyString)).thenAnswer((_: InvocationOnMock) => {
        Thread.sleep(SLOW_CLIENT_DELAY_MS)
        AccessToken.fromQueryString("?access_token=should_be_too_slow")
      })

      val service = new SocialServiceImpl(fb, "BLAHBLAHBLAH", "SSSHHHSECRET", "https://localhost")

      // Wait long enough for normal init but not long enough for our dummy delay
      Thread.sleep(READY_WAIT_TIME_MS)

      service.ready shouldBe false
      service.parseSignedRequest("blah") shouldBe None
    }

    "return None when the FB client setup fails" in {
      val fb = mock(classOf[FbClientFactory])
      when(fb.forAppIdAndSecret(anyString, anyString)).thenThrow(new RuntimeException("failz"))

      val service = new SocialServiceImpl(fb, "BLAHBLAHBLAH", "SSSHHHSECRET", "https://localhost")

      // Wait long enough for normal init but not long enough for our dummy delay
      Thread.sleep(READY_WAIT_TIME_MS)

      service.ready shouldBe false
      service.parseSignedRequest("blah") shouldBe None
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

      waitForReady(service)

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

      waitForReady(service)

      val result = service.getUserAccessToken("wooooooooooot")
      result.isRight shouldBe true
      result.map {
        _.getAccessToken shouldBe "thecakeisalie"
      }
    }

    "return None when the FB client is not ready" in {
      val fb = mock(classOf[FbClientFactory])
      when(fb.forAppIdAndSecret(anyString, anyString)).thenAnswer((_: InvocationOnMock) => {
        Thread.sleep(SLOW_CLIENT_DELAY_MS)
        AccessToken.fromQueryString("?access_token=should_be_too_slow")
      })

      val service = new SocialServiceImpl(fb, "BLAHBLAHBLAH", "SSSHHHSECRET", "https://localhost")

      // Wait long enough for normal init but not long enough for our dummy delay
      Thread.sleep(READY_WAIT_TIME_MS)

      service.ready shouldBe false
      service.getUserAccessToken("wooooooooooot")
    }

    "return None when the FB client setup fails" in {
      val fb = mock(classOf[FbClientFactory])
      when(fb.forAppIdAndSecret(anyString, anyString)).thenThrow(new RuntimeException("failz"))

      val service = new SocialServiceImpl(fb, "BLAHBLAHBLAH", "SSSHHHSECRET", "https://localhost")

      // Wait long enough for normal init but not long enough for our dummy delay
      Thread.sleep(READY_WAIT_TIME_MS)

      service.ready shouldBe false
      service.getUserAccessToken("wooooooooooot")
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

      waitForReady(service)

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

      waitForReady(service)

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
