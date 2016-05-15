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

import java.sql.Timestamp

import dao.TextMessageDAO
import models.{Member, TextMessage}
import org.scalatest.Matchers._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Tests the SettingsDAO.
  */
class TextMessageDAOSpec extends BaseSpec {
  /**
    * An instance of the DAO, used for testing.
    */
  private val textMessageDao = app.injector.instanceOf[TextMessageDAO]

  private val testMember = Member(Some(1), "Billy No-bugs", Some("07123123123"), None)

  private val anotherTestMember = Member(Some(2), "Another Member", Some("07000111000"), None)

  private val testMessageA = TextMessage(Some(1), testMember.id.get, "07123123123", "07123123123",
    new Timestamp(61423903920000L), 0, "Test message")

  private val testMessageB = TextMessage(Some(101), testMember.id.get, "07123123123", "447123123123",
    new Timestamp(61392281520000L), 0, "Another test message")

  private val testMessageC = TextMessage(Some(10101), testMember.id.get, "07123123123", "+447123123123",
    new Timestamp(61360745520000L), 0, "Are you still reading test messages?")

  private val testMessageD = TextMessage(Some(5), anotherTestMember.id.get, "07123123123", "+447000111000",
    new Timestamp(61360745520000L), 0, "Hello Another Member!")

  private val testMessages = Seq(testMessageA, testMessageB, testMessageC)

  "TextMessageDAO" should {
    "not return any text messages if none are present" in {
      textMessageDao.all().map(_ shouldBe empty)
    }

    "return all stored text messages" in {
      textMessageDao.all().map(_ shouldBe empty)
      testMessages.map { textMessageDao.insert(_) }
      textMessageDao.all().map(_ shouldEqual testMessages)
    }

    "return None if a text message wasn't found" in {
      textMessageDao.get(testMessageA.id.get).map(_ shouldBe None)
    }

    "return Some(message) if a text was found with the provided ID" in {
      textMessageDao.insert(testMessageA)
      textMessageDao.get(testMessageA.id.get).map(_ shouldBe Some(testMessageA))
    }

    "insert a text message and return its ID" in {
      testMessages.map { message =>
        textMessageDao.insert(message).map { messageId =>
          textMessageDao.get(messageId).map(_ shouldEqual message)
        }
      }
    }

    "return all of the text messages for a member" in {
      textMessageDao.forMember(testMember).map(_ shouldBe empty)
      testMessages.map { textMessageDao.insert(_) }
      textMessageDao.insert(testMessageD) // For another member

      textMessageDao.forMember(testMember).map(_ shouldEqual testMessages)
      textMessageDao.forMember(testMember).map(_ shouldNot contain(testMessageD))
    }

    "update a text message correctly" in {
      textMessageDao.insert(testMessageA)
      textMessageDao.get(testMessageA.id.get).map(_ shouldBe Some(testMessageA))

      val modifiedMessage = TextMessage(Some(1), 123, "07000111000", "07000555000",
        new Timestamp(0L), 99, "OMG! Test message")

      textMessageDao.update(modifiedMessage)

      textMessageDao.get(testMessageA.id.get).map(_ shouldBe Some(modifiedMessage))
    }

    "delete all messages correctly" in {
      textMessageDao.insert(testMessageA).futureValue
      textMessageDao.all().futureValue shouldNot be(empty)

      textMessageDao.empty()

      textMessageDao.all().futureValue shouldBe empty
    }
  }
}
