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

package com.jsherz.luskydive.itest.dao

import com.jsherz.luskydive.core.CommitteeMember
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util.{TestDatabase, TestUtil, Util}
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport.CommitteeMemberFormat
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import scalaz.-\/

import scala.concurrent.ExecutionContext.Implicits.global

class AuthDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  implicit val patienceConfig: PatienceConfig = TestUtil.defaultPatienceConfig
  private var dao: AuthDao = _
  private var cleanup: () => Unit = _

  "AuthDao#login" should {

    "return Left(error.invalidEmailPass) if the e-mail does not exist" in {
      val fakeEmails = Seq(
        "amorissette@yahoo.com",
        "nborer@yahoo.com",
        "irolfson@gmail.com"
      )

      fakeEmails.foreach { email =>
        val result = dao.login(email, email + "_Hunter2").futureValue

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
        val result = dao.login(email, email.reverse).futureValue

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
        val result = dao.login(email, email + "_Hunter2").futureValue

        result shouldBe a[-\/[_]]
        result.leftMap { error =>
          error shouldEqual "error.accountLocked"
        }
      }
    }

    "return Right(committee member) if the e-mail and password are valid" in {
      val result = dao.login("brittney20@robinson.info", "brittney20@robinson.info_Hunter2").futureValue

      result.isRight shouldBe true

      // Now try and use the generated API key
      result.map { cm => cm shouldEqual Util.fixture[CommitteeMember]("5c1140a2.json") }
    }

  }

  override protected def beforeAll(): Unit = {
    val TestDatabase(dbService, cleanupFn) = Util.setupGoldTestDb()
    cleanup = cleanupFn

    dao = new AuthDaoImpl(dbService)
  }

  override protected def afterAll(): Unit = cleanup()

}
