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

import java.sql.Timestamp
import java.util.UUID

import com.jsherz.luskydive.core.{MassText, TextMessage}
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util._
import com.jsherz.luskydive.json.MassTextsJsonSupport._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import scalaz.-\/

import scala.concurrent.ExecutionContext.Implicits.global

class MassTextDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: MassTextDao = _
  private var textMessageDao: TextMessageDao = _
  private var cleanup: () => Unit = _

  implicit val patienceConfig: PatienceConfig = TestUtil.defaultPatienceConfig

  override protected def beforeAll(): Unit = {
    val TestDatabase(dbService, cleanupFn) = Util.setupGoldTestDb()
    cleanup = cleanupFn

    dao = new MassTextDaoImpl(dbService)
    textMessageDao = new TextMessageDaoImpl(dbService)
  }

  override protected def afterAll(): Unit = cleanup()

  "MassTextDao#get" should {

    "return None if the given UUID does not exist" in {
      val result = dao.get(UUID.fromString("318edeb0-f8cd-49fe-a581-1d03252f390e")).futureValue

      result shouldEqual None
    }

    "return Some(massText) if the given UUID matches a mass text" in {
      val result = dao.get(UUID.fromString("0e02581c-df85-4200-bedd-55a667740486")).futureValue

      result shouldEqual Some(Util.fixture[MassText]("0e02581c.json"))
    }

  }

  "MassTextDao#filterCount" should {

    "return 0 if no members are matched between the given dates" in {
      val result = dao.filterCount(DateUtil.makeDate(1850, 1, 1), DateUtil.makeDate(1851, 1, 1))

      result.isRight shouldBe true
      result.foreach(_.futureValue shouldEqual 0)
    }

    "return the correct number of members, filtered correctly" in {
      val examples = Map(
        (DateUtil.makeDate(2008, 12, 13), DateUtil.makeDate(2010, 4, 12)) -> 174,
        (DateUtil.makeDate(2009, 11, 16), DateUtil.makeDate(2009, 11, 17)) -> 2
      )

      examples.foreach { case (dates, expectedCount) =>
        val result = dao.filterCount(dates._1, dates._2)

        result.isRight shouldBe true
        result.foreach(_.futureValue shouldEqual expectedCount)
      }
    }

    "return an error if the end date is before the start date" in {
      val result = dao.filterCount(DateUtil.makeDate(2010, 5, 4), DateUtil.makeDate(2010, 4, 4))

      result.leftMap(_ shouldEqual MassTextDaoErrors.endDateBeforeStartDate)
    }

    "return an error if the end date and start date are the same" in {
      val result = dao.filterCount(DateUtil.makeDate(2015, 12, 20), DateUtil.makeDate(2015, 12, 20))

      result.leftMap(_ shouldEqual MassTextDaoErrors.endDateStartDateSame)
    }

  }

  "MassTextDao#send" should {

    "refuse to send a message to no-one" in {
      val result = dao.send(
        UUID.fromString("73d7b6d0-5e38-4b31-9142-8a7264a51507"),
        DateUtil.makeDate(1990, 1, 1),
        DateUtil.makeDate(1991, 1, 1),
        "This is a test message. Goodbye {{ name }}!",
        Timestamp.valueOf("2016-09-09 22:37:00")
      ).futureValue

      result shouldEqual -\/(MassTextDaoErrors.noMembersMatched)
    }

    "return an error if the end date is before the start date" in {
      val result = dao.send(
        UUID.fromString("d24da23c-e4d0-494d-829e-e6e367ede02a"),
        DateUtil.makeDate(2016, 9, 25),
        DateUtil.makeDate(2016, 9, 24),
        "Ello {{ name }} - how are things?",
        Timestamp.valueOf("2016-09-09 21:19:45")
      ).futureValue

      result shouldEqual -\/(MassTextDaoErrors.endDateBeforeStartDate)
    }

    "return an error if the end date and start date are the same" in {
      val result = dao.send(
        UUID.fromString("73a7a27e-afd0-4d6f-b7fe-1c01c941ee37"),
        DateUtil.makeDate(2015, 10, 4),
        DateUtil.makeDate(2015, 10, 4),
        "{{ name }}... {{ name }}? {{ name }}???? Is this thing on?",
        Timestamp.valueOf("2016-09-09 22:18:50")
      ).futureValue

      result shouldEqual -\/(MassTextDaoErrors.endDateStartDateSame)
    }

    "add a mass text record and text message for each matching member" in {
      val createdAt = Timestamp.valueOf("2016-09-09 21:50:55")
      val result = dao.send(
        UUID.fromString("ddc4ca00-cd7f-4192-a165-2c7dd42cae6d"),
        DateUtil.makeDate(2015, 1, 1),
        DateUtil.makeDate(2015, 2, 1),
        "Hello, {{ name }}. How are you?!",
        createdAt
      ).futureValue

      result.isRight shouldBe true
      result.map { uuid =>
        val createdMassText = dao.get(uuid).futureValue

        createdMassText.isDefined shouldBe true

        createdMassText.foreach { mt =>
          mt.uuid shouldEqual uuid
          mt.committeeMemberUuid shouldEqual UUID.fromString("ddc4ca00-cd7f-4192-a165-2c7dd42cae6d")
          mt.template shouldEqual "Hello, {{ name }}. How are you?!"
        }

        val messages = textMessageDao.forMassText(uuid).futureValue

        messages.size shouldEqual 12

        // Load in our expected text messages and ensure each one was made
        val expectedMessages = Util.fixture[Vector[TextMessage]]("mass_text_creates.json")

        expectedMessages.foreach { expected =>
          val maybeMessage = messages.find(_.toNumber == expected.toNumber)

          maybeMessage.isDefined shouldBe true

          maybeMessage.foreach { m =>
            m.createdAt shouldEqual expected.createdAt
            m.externalId shouldEqual expected.externalId
            m.fromNumber shouldEqual expected.fromNumber
            m.massTextUuid shouldEqual Some(uuid)
            m.memberUuid shouldEqual expected.memberUuid
            m.message shouldEqual expected.message
            m.status shouldEqual expected.status
            m.updatedAt shouldEqual expected.updatedAt
            m.fromMember shouldEqual expected.fromMember
          }
        }
      }
    }

  }

}
