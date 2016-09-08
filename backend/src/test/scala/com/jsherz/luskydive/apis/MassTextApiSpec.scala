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
import com.jsherz.luskydive.dao.StubMassTextDao
import com.jsherz.luskydive.json.{TryFilterRequest, TryFilterResponse}
import com.jsherz.luskydive.util.{AuthenticationDirectives, Errors}
import org.mockito.Mockito._
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import com.jsherz.luskydive.json.MassTextsJsonSupport._


class MassTextApiSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfter {

  private implicit val authDirective = AuthenticationDirectives.allowAll

  private val url = "/mass-texts/try-filter"

  "MassTextApi#try-filter" should {

    "requires authentication" in {
      implicit val authDirective = AuthenticationDirectives.denyAll
      val route = new MassTextApi(new StubMassTextDao()).route

      Post(url) ~> Route.seal(route) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

    "returns the correct response when getting the filter count succeeds" in {
      val request = TryFilterRequest(StubMassTextDao.validStartDate, StubMassTextDao.validEndDate)
      val dao = spy(new StubMassTextDao())
      val route = new MassTextApi(dao).route

      Post(url, request) ~> route ~> check {
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

      Post(url, request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[TryFilterResponse].success shouldEqual false
        responseAs[TryFilterResponse].error shouldEqual Some(Errors.internalServer)
        responseAs[TryFilterResponse].numMembers shouldBe None

        verify(dao).filterCount(StubMassTextDao.serverErrorStartDate, StubMassTextDao.serverErrorEndDate)
      }
    }

  }

}
