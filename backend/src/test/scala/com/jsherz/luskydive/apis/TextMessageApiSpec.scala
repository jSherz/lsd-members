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

package com.jsherz.luskydive.apis

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.model.{ContentTypes, FormData, StatusCodes}
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.directives.BasicDirectives.provide
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import com.jsherz.luskydive.core._
import com.jsherz.luskydive.dao.{MemberDao, StubMemberDao, StubTextMessageDao, TextMessageDao}
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport.CommitteeMemberFormat
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.json.TextMessageJsonSupport.NumReceivedMessagesFormat
import com.jsherz.luskydive.util.Util
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, never, verify, when}
import org.mockito.{ArgumentCaptor, Mockito}
import scalaz.-\/

import scala.concurrent.Future


class TextMessageApiSpec extends BaseApiSpec {

  val validApiKey = "1248ytyghbjiytrfdcgvhiuojknygt6r5"
  val validReceiveUrl = s"/text-messages/receive/${validApiKey}"

  val validReceiveRequest = FormData(
    "SmsMessageSid" -> "SM11h1f5jasfh782y35hdfw235jhdfh51j", "NumMedia" -> "0", "ToCity" -> "", "FromZip" -> "",
    "SmsSid" -> "SMr1j54i1j4124ujijdfu235jkksjf914j", "FromState" -> "", "SmsStatus" -> "received",
    "FromCity" -> "", "Body" -> "Hello, world!", "FromCountry" -> "GB", "To" -> "+447123123123",
    "MessagingServiceSid" -> "MG1247yg12b7814hj8y124hjr7814jns71", "ToZip" -> "", "NumSegments" -> "1",
    "MessageSid" -> "SMf35b148y12h123412jdf87sdf7sdfhhu", "AccountSid" -> "AC123u123124214712873jaksd815hsfyu",
    "From" -> "+447881072696", "ApiVersion" -> "2010-04-01"
  )

  val memberNotFoundReceiveRequest = FormData(validReceiveRequest.fields.toMap + ("From" -> "+447810000001"))

  val noToReceiveRequest = FormData(validReceiveRequest.fields.toMap - "To")
  val noFromReceiveRequest = FormData(validReceiveRequest.fields.toMap - "From")
  val noBodyReceiveRequest = FormData(validReceiveRequest.fields.toMap - "Body")
  val noMessageSidReceiveRequest = FormData(validReceiveRequest.fields.toMap - "MessageSid")

  trait Fixtured {
    val memberDao: MemberDao = Mockito.spy(new StubMemberDao())
    val dao: TextMessageDao = Mockito.spy(new StubTextMessageDao())
    val member = Util.fixture[Member]("37b50c24.json")
    val committeeMember = Util.fixture[CommitteeMember]("956610c8.json")
    val authDirective = provide((member, committeeMember))

    val route = new TextMessageApi(dao, memberDao, validApiKey, authDirective).route
  }

  "TextMessageApi#receive" should {

    "return method not allowed when used with anything other than POST" in new Fixtured {
      Seq(Get, Put, Delete, Patch).foreach { method =>
        method(validReceiveUrl) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }
    }

    "not require the standard API key authentication" in new Fixtured {
      Post(validReceiveUrl, validReceiveRequest) ~> Route.seal(route) ~> check {
        response.status shouldBe StatusCodes.OK
      }
    }

    "reject requests without a receiving secret" in new Fixtured {
      Post("/text-messages/receive/", validReceiveRequest) ~> Route.seal(route) ~> check {
        response.status shouldBe StatusCodes.Unauthorized
        responseAs[String] shouldEqual "Unauthorized."
      }

      Post("/text-messages/receive", validReceiveRequest) ~> Route.seal(route) ~> check {
        response.status shouldBe StatusCodes.NotFound
      }
    }

    "reject requests with an invalid receiving secret" in new Fixtured {
      Post("/text-messages/receive/asdf598y7ghbjnoi9u8y7ghb", validReceiveRequest) ~> Route.seal(route) ~> check {
        response.status shouldBe StatusCodes.Unauthorized
        responseAs[String] shouldEqual "Unauthorized."
      }
    }

    "accept and save text messages with the correct information" in new Fixtured {
      Post(validReceiveUrl, validReceiveRequest) ~> route ~> check {
        val messageCaptor = ArgumentCaptor.forClass[TextMessage](TextMessage.getClass.asInstanceOf[Class[TextMessage]])
        verify(dao).insert(messageCaptor.capture())
        verify(memberDao).forPhoneNumber("+447881072696")
        verify(memberDao, never()).create(any())

        val message = messageCaptor.getAllValues.get(0)

        message.status shouldEqual TextMessageStatuses.Received
        message.toNumber shouldEqual "+447123123123"
        message.fromNumber shouldEqual StubMemberDao.forPhoneNumber
        message.externalId shouldEqual Some("SMf35b148y12h123412jdf87sdf7sdfhhu")
        message.message shouldEqual "Hello, world!"
        message.fromMember shouldBe true
        message.massTextUuid shouldBe None

        response.status shouldBe StatusCodes.OK
        response.entity.contentType shouldEqual ContentTypes.`text/xml(UTF-8)`
        responseAs[String] shouldEqual "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!-- Recorded as " + message.uuid.get + " --><Response></Response>"
      }
    }

    "return 404 if no member is found with that phone number" in new Fixtured {
      Post(validReceiveUrl, memberNotFoundReceiveRequest) ~> route ~> check {
        response.status shouldEqual StatusCodes.NotFound
        responseAs[String] shouldEqual TextMessageApiErrors.receiveMemberNotFound
      }
    }

    "return bad request if the To is missing" in new Fixtured {
      Post(validReceiveUrl, noToReceiveRequest) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

    "return bad request if the From is missing" in new Fixtured {
      Post(validReceiveUrl, noFromReceiveRequest) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

    "return bad request if the Body is missing" in new Fixtured {
      Post(validReceiveUrl, noBodyReceiveRequest) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

    "return bad request if the MessageSid is missing" in new Fixtured {
      Post(validReceiveUrl, noMessageSidReceiveRequest) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.BadRequest
      }
    }

  }

  "TextMessageApi#receivedRoute" should {

    val url = "/text-messages/received"

    "requires authentication with a JWT" in new Fixtured {
      val jwtAuthDirective = reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("", None)))
      override val route: Route = new TextMessageApi(dao, memberDao, validApiKey, jwtAuthDirective).route

      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }

      verify(dao, never()).getReceived()
    }

    "return method not allowed when used with anything other than GET" in new Fixtured {
      Seq(Post, Put, Delete, Patch).foreach { method =>
        method(url) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).getReceived()
    }

    "return the recent text messages" in new Fixtured {
      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[Vector[TextMessage]] shouldEqual Util.fixture[Vector[TextMessage]]("get_received.json")
      }
    }

    "return an error when getting the recent text messages fails" in new Fixtured {
      override val dao = mock(classOf[TextMessageDao])
      when(dao.getReceived()).thenReturn(Future.successful(-\/("failed to get them")))

      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
      }
    }

  }

  "TextMessageApi#receivedCountRoute" should {

    val url = "/text-messages/num-received"

    "requires authentication with a JWT" in new Fixtured {
      val jwtAuthDirective = reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("", None)))
      override val route: Route = new TextMessageApi(dao, memberDao, validApiKey, jwtAuthDirective).route

      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }

      verify(dao, never()).getReceivedCount()
    }

    "return method not allowed when used with anything other than GET" in new Fixtured {
      Seq(Post, Put, Delete, Patch).foreach { method =>
        method(url) ~> Route.seal(route) ~> check {
          response.status shouldEqual StatusCodes.MethodNotAllowed
        }
      }

      verify(dao, never()).getReceivedCount()
    }

    "return the recent text messages" in new Fixtured {
      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[NumReceivedMessages] shouldEqual NumReceivedMessages(3)
      }
    }

    "return an error when getting the recent text messages fails" in new Fixtured {
      override val dao = mock(classOf[TextMessageDao])
      when(dao.getReceivedCount()).thenReturn(Future.successful(-\/("failed to get them")))

      Get(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.InternalServerError
      }
    }

  }

}
