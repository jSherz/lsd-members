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

import java.util.UUID

import com.jsherz.luskydive.core.CommitteeMember
import com.jsherz.luskydive.dao.{CommitteeMemberDao, CommitteeMemberDaoImpl}
import com.jsherz.luskydive.itest.util.Util
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

  override protected def beforeAll(): Unit = {
    val dbService = Util.setupGoldTestDb()

    dao = new CommitteeMemberDaoImpl(databaseService = dbService)
  }

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
      val result = dao.get(UUID.fromString("0d6717b7-530c-418b-9b97-dffbe972de87"))

      result.futureValue shouldBe Some(Util.fixture[CommitteeMember]("0d6717b7.json"))
    }

  }

}
