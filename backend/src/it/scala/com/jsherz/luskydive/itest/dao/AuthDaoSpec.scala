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

package com.jsherz.luskydive.itest.dao

import java.sql.Timestamp
import java.util.UUID

import akka.event.LoggingAdapter
import com.jsherz.luskydive.core.ApiKey
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util.Util
import com.jsherz.luskydive.util.NullLogger
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import scalaz.{-\/, \/-}

import scala.concurrent.ExecutionContext.Implicits.global

class AuthDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: AuthDao = _

  implicit val patienceConfig: PatienceConfig = Util.defaultPatienceConfig

  override protected def beforeAll(): Unit = {
    implicit val log: LoggingAdapter = new NullLogger
    val dbService = Util.setupGoldTestDb()

    dao = new AuthDaoImpl(dbService)
  }

  "AuthDao#get" should {

    "return None when the API key is not found" in {
      val result = dao.get(UUID.fromString("4a24248d-9510-45bd-8d7d-0426b560c5b6"))

      result.futureValue shouldEqual None
    }

    "return Some(apiKey) when the key is valid" in {
      val result = dao.get(UUID.fromString("1dad5374-26fd-43de-8872-4dd5626d4a97"))

      val expected = ApiKey(UUID.fromString("1dad5374-26fd-43de-8872-4dd5626d4a97"),
        UUID.fromString("75f2bfee-8859-46a8-8c08-956c2162f5c3"),
        Timestamp.valueOf("2011-05-22 10:25:53.000000"),
        Timestamp.valueOf("2011-05-22 11:33:51.000000"))

      result.futureValue shouldEqual Some(expected)
    }

  }

  "AuthDao#authenticate" should {

    "return Right(memberUuid) and update the expiry date for a valid (not expired) API key" in {
      val uuid = UUID.fromString("6a494b6a-9e11-43f1-ae1b-e03139ad80ac")
      val result = dao.authenticate(uuid, testTime)

      result.futureValue shouldEqual \/-(UUID.fromString("52abe905-4d2c-4f38-819c-fc6b5d6b851f"))

      val apiKey = dao.get(uuid).futureValue
      val expectedExpiry = Timestamp.valueOf("2016-08-22 21:02:10")

      apiKey.get.expiresAt shouldEqual expectedExpiry
    }

    "return Left(error.invalidApiKey) if the API key has expired" in {
      val result = dao.authenticate(UUID.fromString("e10de0cb-5a18-4515-9a16-c03b27f04ce0"), testTime)

      result.futureValue shouldEqual -\/("error.invalidApiKey")
    }

    "return Left(error.invalidApiKey) if no API key was found" in {
      val result = dao.authenticate(UUID.fromString("2d25afd4-5e96-4cc1-8255-c077da329092"), testTime)

      result.futureValue shouldEqual -\/("error.invalidApiKey")
    }

    "return Left(error.accountLocked) if the API key is valid, but the account is locked" in {
      val result = dao.authenticate(UUID.fromString("487a2930-8e6a-41a5-bcc0-b7fd7f2421e4"), testTime)

      result.futureValue shouldEqual -\/("error.accountLocked")
    }

  }

  "AuthDao#login" should {

    "return Left(error.invalidEmailPass) if the e-mail does not exist" in {
      val fakeEmails = Seq(
        "amorissette@yahoo.com",
        "nborer@yahoo.com",
        "irolfson@gmail.com"
      )

      fakeEmails.foreach { email =>
        val result = dao.login(email, email + "_Hunter2", testTime).futureValue

        result shouldBe a[-\/[_]]
        result.leftMap { error =>
          error shouldEqual "error.invalidEmailPass"
        }
      }
    }

    "return Left(error.invalidEmailPass) if the e-mail is valid but the password is wrong" in {
      val validEmails = Seq(
        "davisjohn@hodge-davis.com",
        "butlerlawrence@hotmail.com",
        "jlambert@gutierrez-lester.com"
      )

      validEmails.foreach { email =>
        val result = dao.login(email, email.reverse, testTime).futureValue

        result shouldBe a[-\/[_]]
        result.leftMap { error =>
          error shouldEqual "error.invalidEmailPass"
        }
      }
    }

    "return Left(error.accountLocked) if the e-mail and password are valid but the account is locked" in {
      val lockedAccounts = Seq(
        "solisomar@gmail.com",
        "probertson@hotmail.com",
        "zamorajennifer@hotmail.com"
      )

      lockedAccounts.foreach { email =>
        val result = dao.login(email, email + "_Hunter2", testTime).futureValue

        result shouldBe a[-\/[_]]
        result.leftMap { error =>
          error shouldEqual "error.accountLocked"
        }
      }
    }

    "return Right(validUuid) if the e-mail and password are valid" in {
      val validExamples = Map(
        "5c1140a2-2fc4-48fb-b4c2-75a9399094c9" -> "brittney20@robinson.info",
        "338fa63a-0486-469c-ba3f-737c0e8aafef" -> "crystalsmith@yahoo.com",
        "ade89632-2ccd-4b9f-93f1-44f5adf9704c" -> "heather17@yahoo.com"
      )

      validExamples.foreach { case (memberUuid, email) =>
        val result = dao.login(email, email + "_Hunter2", testTime).futureValue

        result shouldBe a[\/-[_]]

        // Now try and use the generated API key
        result.map { apiKey =>
          val authResult = dao.authenticate(apiKey, testTime).futureValue

          authResult.isRight shouldEqual true
          authResult.getOrElse(badUuid) shouldEqual UUID.fromString(memberUuid)
        }
      }
    }

  }

  private val testTime = {
    Timestamp.valueOf("2016-08-21 21:02:10")
  }

  private val badUuid = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")

}
