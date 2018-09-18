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

import akka.event.LoggingAdapter
import com.jsherz.luskydive.core.TextMessage
import com.jsherz.luskydive.dao.TextMessageDaoImpl
import com.jsherz.luskydive.itest.util.{NullLogger, TestDatabase, TestUtil, Util}
import com.jsherz.luskydive.json.TextMessageJsonSupport._
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global


class TextMessageDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {
  implicit val log: LoggingAdapter = new NullLogger
  val TestDatabase(dbService, cleanup) = Util.setupGoldTestDb()
  val dao = new TextMessageDaoImpl(dbService)

  override protected def afterAll: Unit = cleanup()

  "TextMessageDao#all" should {

    "return text messages in the correct order" in {
      implicit val patienceConfig: PatienceConfig = TestUtil.defaultPatienceConfig

      val messages = dao.all().futureValue

      val expectedMessages = Util.fixture[Vector[TextMessage]]("all.json")

      messages.length shouldEqual expectedMessages.length

      messages.zip(expectedMessages).foreach { case (actual, expected) =>
        textMessagesShouldBeSameBarUuids(actual, expected)
      }
    }

  }

  "TextMessageDao#get" should {

    "return None if no message was found" in {
      val result = dao.get(UUID.fromString("0deabe93-58da-448a-84ca-e6da03a07b66")).futureValue

      result shouldBe None
    }

    "return Some(message) if a message was found" in {
      val result = dao.get(UUID.fromString("4f8ee40d-6721-4c70-b7c2-e966498947ab")).futureValue

      result shouldBe Some(Util.fixture[TextMessage]("4f8ee40d.json"))
    }

  }

  "TextMessageDao#insert" should {

    "add the message with the correct information" in {
      val text = Util.fixture[TextMessage]("valid_example.json")
      val uuid = dao.insert(text).futureValue

      uuid shouldEqual text.uuid

      val created = dao.get(uuid).futureValue
      created shouldEqual Some(text)
    }

    "add messages with no associated mass text" in {
      val text = Util.fixture[TextMessage]("valid_no_mass_text.json")
      val uuid = dao.insert(text).futureValue

      uuid shouldEqual text.uuid

      val created = dao.get(uuid).futureValue
      created shouldEqual Some(text)
    }

    "add messages with no associated external ID" in {
      val text = Util.fixture[TextMessage]("valid_no_external_id.json")
      val uuid = dao.insert(text).futureValue

      uuid shouldEqual text.uuid

      val created = dao.get(uuid).futureValue
      created shouldEqual Some(text)
    }

  }

  "TextMessageDao#forMember" should {

    "return the correct messages, in order, for a member" in {
      val member = UUID.fromString("d99d8680-296a-40db-9788-b182ce3a6935")
      val result = dao.forMember(member).futureValue

      result shouldEqual Util.fixture[Vector[TextMessage]]("erik.json")
    }

    "return an empty list when the member does not exist" in {
      val nonExistentMember = UUID.fromString("e3186d50-41ec-4c34-be95-edf808eeea40")
      val result = dao.forMember(nonExistentMember).futureValue

      result shouldBe empty
    }

  }

  "TextMessageDao#update" should {

    "update a text message correctly" in {
      val messageUuid = UUID.fromString("ac0d77ec-00af-4d4a-9636-67fa1d824d34")
      val before = dao.get(messageUuid).futureValue

      before shouldEqual Some(Util.fixture[TextMessage]("update_before.json"))

      val expected = Util.fixture[TextMessage]("update_after.json")
      val after = dao.update(expected).futureValue

      after shouldBe 1

      val doubleCheck = dao.get(messageUuid).futureValue
      doubleCheck shouldEqual Some(expected)
    }

  }

  "TextMessageDao#forMassText" should {

    "return an empty list for a text that doesn't exist" in {
      val result = dao.forMassText(UUID.fromString("9fd555aa-6f51-4989-8355-5ac6bf726a96")).futureValue

      result shouldEqual Seq.empty
    }

    "return the correct mass texts, ordered by updated at desc" in {
      val result = dao.forMassText(UUID.fromString("1503f286-f1d6-4908-9fef-c96cee1ba1a8")).futureValue

      result shouldEqual Util.fixture[Vector[TextMessage]]("for_mass_text.json")
    }

  }

  "TextMessageDao#toSend" should {

    "return all text messages that are pending being sent" in {
      val result = dao.toSend().futureValue
      val expectedMessages = Util.fixture[Vector[TextMessage]]("to_send.json")

      result.zip(expectedMessages).foreach { case (actual, expected) =>
        textMessagesShouldBeSameBarUuids(actual, expected)
      }
    }

  }

  "TextMessageDao#getReceived" should {

    "return all text messages with a state of received in created date ascending" in {
      val result = dao.getReceived().futureValue
      val expectedMessages = Util.fixture[Vector[TextMessage]]("get_received.json")

      result shouldEqual expectedMessages
    }

  }

  "TextMessageDao#getReceivedCount" should {

    "return the number of text messages with the received state" in {
      val result = dao.getReceivedCount().futureValue

      result shouldEqual 3
    }

  }

  /**
    * Compare messages, discarding the UUIDs (but ensuring the mass text UUID is either present or not present on both).
    *
    * @param actual
    * @param expected
    * @return
    */
  private def textMessagesShouldBeSameBarUuids(actual: TextMessage, expected: TextMessage): Unit = {
    actual.massTextUuid.isDefined shouldEqual expected.massTextUuid.isDefined
    actual.copy(uuid = expected.uuid, massTextUuid = expected.massTextUuid) shouldEqual expected
  }

}
