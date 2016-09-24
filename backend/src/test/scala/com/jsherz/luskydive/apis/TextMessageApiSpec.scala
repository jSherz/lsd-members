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

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.model.headers.HttpEncodings
import akka.http.scaladsl.model.{ContentTypes, FormData, StatusCodes, Uri}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.core.{TextMessage, TextMessageStatuses}
import com.jsherz.luskydive.dao.{MemberDao, StubMemberDao, StubTextMessageDao, TextMessageDao}
import org.mockito.{ArgumentCaptor, Mockito}
import org.mockito.Mockito.{never, verify}
import org.mockito.Matchers.any
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.ExecutionContext


class TextMessageApiSpec extends WordSpec with Matchers with ScalatestRouteTest {

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
    implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

    val memberDao: MemberDao = Mockito.spy(new StubMemberDao())
    val dao: TextMessageDao = Mockito.spy(new StubTextMessageDao())
    val route = new TextMessageApi(dao, memberDao, validApiKey).route
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

}
