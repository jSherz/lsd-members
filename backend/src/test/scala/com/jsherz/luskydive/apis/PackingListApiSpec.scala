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

import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.HttpChallenge
import akka.http.scaladsl.server.AuthenticationFailedRejection.CredentialsRejected
import akka.http.scaladsl.server.directives.BasicDirectives.provide
import akka.http.scaladsl.server.directives.RouteDirectives.reject
import akka.http.scaladsl.server.{AuthenticationFailedRejection, Route}
import com.jsherz.luskydive.core.{Member, PackingListItem}
import com.jsherz.luskydive.dao.PackingListItemDao
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.json.PackingListJsonSupport.{packingListItemFormat, strippedPackingListItemFormat}
import com.jsherz.luskydive.json.StrippedPackingListItem
import com.jsherz.luskydive.util.Util
import org.mockito.Matchers.any
import org.mockito.Mockito.{mock, verify, when}
import scalaz.\/-

import scala.concurrent.Future


class PackingListApiSpec extends BaseApiSpec {

  private val member = Util.fixture[Member]("6066143f.json")
  private val authDirective = provide(member)
  private var dao = mock(classOf[PackingListItemDao])
  private var api: Route = new PackingListApi(authDirective, dao).route

  before {
    dao = mock(classOf[PackingListItemDao])
    api = new PackingListApi(authDirective, dao).route

    when(dao.getOrDefault(UUID.fromString("6066143f-4bb9-41cc-ba22-ce41126752bf")))
      .thenReturn(Future.successful(\/-(
        Util.fixture[PackingListItem]("example.json")
      )))

    when(dao.upsert(any(classOf[PackingListItem])))
      .thenReturn(Future.successful(\/-(1)))
  }

  "PackingListApi#GET" should {

    "returns the member's packing list" in {
      Get("/packing-list") ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[StrippedPackingListItem] shouldEqual Util.fixture[StrippedPackingListItem]("example.json")
      }
    }

    "requires authentication" in {
      val authDirective = reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("", None)))

      api = new PackingListApi(authDirective, dao).route

      Get("/packing-list") ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

  }

  "PackingListApi#PUT" should {

    "saves the member's packing list" in {
      val expected = Util.fixture[PackingListItem]("example.json")
      val request = Util.fixture[StrippedPackingListItem]("example.json")

      Put("/packing-list", request) ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.OK

        verify(dao).upsert(expected)
      }
    }

    "requires authentication" in {
      val authDirective = reject(AuthenticationFailedRejection(CredentialsRejected, HttpChallenge("", None)))

      api = new PackingListApi(authDirective, dao).route

      Put("/packing-list") ~> Route.seal(api) ~> check {
        response.status shouldEqual StatusCodes.Unauthorized
      }
    }

  }

}
