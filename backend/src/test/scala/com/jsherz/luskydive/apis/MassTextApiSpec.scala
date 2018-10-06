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

import java.sql.{Timestamp, Date}
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import com.jsherz.luskydive.dao.{MassTextDaoErrors, StubMassTextDao}
import com.jsherz.luskydive.json.MassTextsJsonSupport._
import com.jsherz.luskydive.json._
import com.jsherz.luskydive.util.{AuthenticationDirectives, Errors}
import org.mockito.Matchers.{any, anyString}
import org.mockito.Mockito._


class MassTextApiSpec extends BaseApiSpec {

  private implicit val authDirective = AuthenticationDirectives.allowAll

  private val tryFilterUrl = "/mass-texts/try-filter"
  private val sendUrl = "/mass-texts/send"

  "MassTextApi#try-filter" should {

    "requires authentication" in {
      implicit val authDirective = AuthenticationDirectives.denyAll
      val route = new MassTextApi(new StubMassTextDao()).route

      Post(tryFilterUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "returns the correct response when getting the filter count succeeds" in {
      val request = TryFilterRequest(StubMassTextDao.validStartDate, StubMassTextDao.validEndDate)
      val dao = spy(new StubMassTextDao())
      val route = new MassTextApi(dao).route

      Post(tryFilterUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[TryFilterResponse].success shouldEqual true
        responseAs[TryFilterResponse].error shouldBe None
        responseAs[TryFilterResponse].numMembers shouldEqual Some(StubMassTextDao.validFilterCount)

        verify(dao).filterCount(StubMassTextDao.validStartDate, StubMassTextDao.validEndDate)
      }
    }

    "returns the incorrect response when getting the filter count fails" in {
      val request = TryFilterRequest(StubMassTextDao.serverErrorStartDate, StubMassTextDao.serverErrorEndDate)
      val dao = spy(new StubMassTextDao())
      val route = new MassTextApi(dao).route

      Post(tryFilterUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[TryFilterResponse].success shouldEqual false
        responseAs[TryFilterResponse].error shouldEqual Some(Errors.internalServer)
        responseAs[TryFilterResponse].numMembers shouldBe None

        verify(dao).filterCount(StubMassTextDao.serverErrorStartDate, StubMassTextDao.serverErrorEndDate)
      }
    }

  }

  "MassTextApi#send" should {

    "requires authentication" in {
      implicit val authDirective = AuthenticationDirectives.denyAll
      val route = new MassTextApi(new StubMassTextDao()).route

      Post(sendUrl) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "returns the correct response when sending the message succeeds" in {
      // Dates here are irrelevant
      val request = MassTextSendRequest(StubMassTextDao.validStartDate, StubMassTextDao.validEndDate,
        StubMassTextDao.validSenderTemplate, "Hello, Mary! - Reply 'NOFUN' to stop these messages")
      val dao = spy(new StubMassTextDao())
      val route = new MassTextApi(dao).route

      Post(sendUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[MassTextSendResponse].success shouldEqual true
        responseAs[MassTextSendResponse].error shouldBe None
        responseAs[MassTextSendResponse].uuid shouldEqual Some(StubMassTextDao.validCreatedUuid)

        verify(dao).send(
          any[UUID],
          org.mockito.Matchers.eq(StubMassTextDao.validStartDate),
          org.mockito.Matchers.eq(StubMassTextDao.validEndDate),
          org.mockito.Matchers.eq("Hello, {{ name }}!"),
          any[Timestamp]
        )
      }
    }

    "returns the correct error response when sending the message fails" in {
      val request = MassTextSendRequest(StubMassTextDao.validStartDate, StubMassTextDao.validEndDate,
        StubMassTextDao.serverErrorTemplate, "What is the meaning of life, Mary? - Reply 'NOFUN' to stop these messages")
      val dao = spy(new StubMassTextDao())
      val route = new MassTextApi(dao).route

      Post(sendUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[MassTextSendResponse].success shouldEqual false
        responseAs[MassTextSendResponse].error shouldEqual Some(MassTextDaoErrors.noMembersMatched)
        responseAs[MassTextSendResponse].uuid shouldBe None

        verify(dao).send(any[UUID], any[Date], any[Date], anyString, any[Timestamp])
      }
    }

    "returns an error when the template and expected rendered version don't match" in {
      val request = MassTextSendRequest(StubMassTextDao.validStartDate, StubMassTextDao.validEndDate,
        "Hello, {{ name }}!", "Hello, Bloggs!")
      val dao = spy(new StubMassTextDao())
      val route = new MassTextApi(dao).route

      Post(sendUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[MassTextSendResponse].success shouldEqual false
        responseAs[MassTextSendResponse].error shouldEqual Some(MassTextApiErrors.templateRenderMismatch)
        responseAs[MassTextSendResponse].uuid shouldBe None

        verify(dao, never()).send(any[UUID], any[Date], any[Date], anyString, any[Timestamp])
      }
    }

    "returns an error when the template is blank" in {
      val request = MassTextSendRequest(StubMassTextDao.validStartDate, StubMassTextDao.validEndDate, "", "")
      val dao = spy(new StubMassTextDao())
      val route = new MassTextApi(dao).route

      Post(sendUrl, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[MassTextSendResponse].success shouldEqual false
        responseAs[MassTextSendResponse].error shouldEqual Some(MassTextApiErrors.blankTemplate)
        responseAs[MassTextSendResponse].uuid shouldBe None

        verify(dao, never()).send(any[UUID], any[Date], any[Date], anyString, any[Timestamp])
      }
    }

  }

}
