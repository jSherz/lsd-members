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

import dao.{MemberDAO, TextMessageDAO}
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

  /**
    * An instance of the member DAO, used to create members that messages will be associated with.
    */
  private val memberDao = app.injector.instanceOf[MemberDAO]

  private val testMember = Member(None, "Billy No-bugs", Some("07123123123"), None)

  private val anotherTestMember = Member(None, "Another Member", Some("07000111000"), None)

  private val testMessageA = TextMessage(None, 0, "07123123123", "07123123123",
    new Timestamp(61423903920000L), 0, "Test message")

  private val testMessageB = TextMessage(None, 0, "07123123123", "447123123123",
    new Timestamp(61392281520000L), 0, "Another test message")

  private val testMessageC = TextMessage(None, 0, "07123123123", "+447123123123",
    new Timestamp(61360745520000L), 0, "Are you still reading test messages?")

  private val testMessageD = TextMessage(None, 0, "07123123123", "+447000111000",
    new Timestamp(61360745520000L), 0, "Hello Another Member!")

  private val testMessages = Seq(testMessageA, testMessageB, testMessageC)

  "TextMessageDAO" should {
    "not return any text messages if none are present" in {
      textMessageDao.all().futureValue shouldBe empty
    }

    "return all stored text messages" in {
      textMessageDao.all().futureValue shouldBe empty

      val memberId = memberDao.insert(testMember).futureValue

      val messagesWithIds = testMessages.map { message =>
        val messageWithMemberId = message.copy(memberId = memberId)

        message.copy(
          id = Some(textMessageDao.insert(messageWithMemberId).futureValue),
          memberId = memberId
        )
      }

      textMessageDao.all().futureValue shouldEqual messagesWithIds
    }

    "return None if a text message wasn't found" in {
      textMessageDao.get(0).futureValue shouldBe None
    }

    "return Some(message) if a text was found with the provided ID" in {
      val memberId = memberDao.insert(testMember).futureValue
      val messageId = textMessageDao.insert(testMessageA.copy(memberId = memberId)).futureValue

      val testMessageWithCorrectIds = testMessageA.copy(
        id = Some(messageId),
        memberId = memberId
      )

      textMessageDao.get(messageId).futureValue shouldBe Some(testMessageWithCorrectIds)
    }

    "insert a text message and return its ID" in {
      val memberId = memberDao.insert(testMember).futureValue

      testMessages.map { message =>
        val messageWithMemberId = message.copy(memberId = memberId)
        val messageId = textMessageDao.insert(messageWithMemberId).futureValue

        textMessageDao.get(messageId).futureValue shouldEqual Some(messageWithMemberId.copy(id = Some(messageId)))
      }
    }

    "return all of the text messages for a member" in {
      val memberId = memberDao.insert(testMember).futureValue
      val anotherMemberId = memberDao.insert(anotherTestMember).futureValue

      val memberWithId = testMember.copy(id = Some(memberId))

      textMessageDao.forMember(memberWithId).futureValue shouldBe empty

      val messagesWithIds = testMessages.map { message =>
        val messageWithMemberId = message.copy(memberId = memberId)
        val messageId = textMessageDao.insert(messageWithMemberId).futureValue

        messageWithMemberId.copy(id = Some(messageId))
      }

      // Add another member's message to ensure only the correct message(s) for each member are returned
      val otherTestMessage = testMessageD.copy(memberId = anotherMemberId)
      val otherMessageId = textMessageDao.insert(otherTestMessage).futureValue
      val otherMessageWithIds = otherTestMessage.copy(id = Some(otherMessageId))

      textMessageDao.forMember(memberWithId).futureValue shouldEqual messagesWithIds
      textMessageDao.forMember(memberWithId).futureValue shouldNot contain(otherMessageWithIds)
    }

    "update a text message correctly" in {
      val memberId = memberDao.insert(testMember).futureValue
      val messageWithMemberId = testMessageA.copy(memberId = memberId)
      val messageId = textMessageDao.insert(messageWithMemberId).futureValue
      val messageWithIds = messageWithMemberId.copy(id = Some(messageId))

      textMessageDao.get(messageId).futureValue shouldBe Some(messageWithIds)

      val modifiedMessage = TextMessage(Some(messageId), memberId, "07000111000", "07000555000",
        new Timestamp(0L), 99, "OMG! Test message")

      textMessageDao.update(modifiedMessage).futureValue

      textMessageDao.get(messageId).futureValue shouldBe Some(modifiedMessage)
    }

    "delete all messages correctly" in {
      val memberId = memberDao.insert(testMember).futureValue

      testMessages.map { message =>
        val messageWithMemberId = message.copy(memberId = memberId)
        textMessageDao.insert(messageWithMemberId).futureValue
      }

      textMessageDao.all().futureValue shouldNot be(empty)

      textMessageDao.empty().futureValue

      textMessageDao.all().futureValue shouldBe empty
    }
  }
}
