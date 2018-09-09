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

import java.util.UUID

import com.jsherz.luskydive.core.CommitteeMember
import com.jsherz.luskydive.dao.{CommitteeMemberDao, CommitteeMemberDaoImpl}
import com.jsherz.luskydive.itest.util.{TestDatabase, Util}
import com.jsherz.luskydive.json.StrippedCommitteeMember
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures._
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Exercises the committee member database functionality.
  */
class CommitteeMemberDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: CommitteeMemberDao = _
  private var cleanup: () => Unit = _

  override protected def beforeAll(): Unit = {
    val TestDatabase(dbService, cleanupFn) = Util.setupGoldTestDb()
    cleanup = cleanupFn

    dao = new CommitteeMemberDaoImpl(databaseService = dbService)
  }

  override protected def afterAll(): Unit = cleanup()

  "CommitteeMemberDao#active" should {

    "return the correct committee members, sorted by name" in {
      val results = dao.active()

      whenReady(results) { r =>
        r shouldBe Util.fixture[Vector[StrippedCommitteeMember]]("active.json")
      }
    }

  }

  "CommitteeMemberDao#get" should {

    "return None when no committee member is found" in {
      val result = dao.get(UUID.fromString("b457ffa1-9a24-4da8-9d6c-07d0fb16bb80"))

      result.futureValue shouldBe None
    }

    "return the correct information when a committee member is found" in {
      val result = dao.get(UUID.fromString("956610c8-a7d6-4fd5-9e91-91013a681ef4"))

      result.futureValue shouldBe Some(Util.fixture[CommitteeMember]("956610c8.json"))
    }

  }

  "CommitteeMemberDao#forMember" should {

    "return None when no committee member is found" in {
      val result = dao.forMember(UUID.fromString("13e67105-c103-49d6-b434-19ab0524dc02"))

      result.futureValue shouldBe None
    }

    "return Some(committeeMember) when found for a member" in {
      val result = dao.forMember(UUID.fromString("5f89a942-2704-4442-9d68-f30408b51ca1"))

      result.futureValue shouldEqual Some(Util.fixture[CommitteeMember]("f5e0e6f1.json"))
    }

  }

}
