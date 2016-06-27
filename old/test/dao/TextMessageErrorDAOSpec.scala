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

import dao.{MemberDAO, TextMessageDAO, TextMessageErrorDAO}
import models.{Member, TextMessage, TextMessageError}
import org.scalatest.Matchers._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Tests the TextMessageDAO.
  */
class TextMessageErrorDAOSpec extends BaseSpec {

  // DAOs, used for creating fixtures

  private val textMessageErrorDao = app.injector.instanceOf[TextMessageErrorDAO]
  private val textMessageDao = app.injector.instanceOf[TextMessageDAO]
  private val memberDao = app.injector.instanceOf[MemberDAO]

  // Fixtures

  private val testMember = Member(None, "Billy No-bugs", Some("07123123123"), None)

  private val testMessageA = TextMessage(None, 0, "07123123123", "07123123123",
    Some(new Timestamp(61423903920000L)), Some("ms-123124781247"), 0, "Test message")
  private val testMessageB = TextMessage(None, 0, "07123123123", "447123123123",
    Some(new Timestamp(61392281520000L)), Some("ms-123823985911"), 0, "Another test message")

  private val testMessages = Seq(testMessageA, testMessageB)

  private val testErrorA = TextMessageError(None, 0, new Timestamp(61392281520000L), "I made an oops!")
  private val testErrorB = TextMessageError(None, 0, new Timestamp(61718237120000L), "Computer said no :(")
  private val testErrorC = TextMessageError(None, 0, new Timestamp(61247121823700L), "YOU HAVE RUN OUT OF MONEY :O")

  private val testErrorD = TextMessageError(None, 0, new Timestamp(61247121823700L), "I belong to message B!")

  private val testErrors = Seq(testErrorA, testErrorB, testErrorC)

  /**
    * Inserts the given test text message into the database with the provided errors.
    *
    * @param textMessage Message
    * @param errors Errors for the message
    */
  private def insertTestData(textMessage: TextMessage, errors: Seq[TextMessageError]): (TextMessage, Seq[TextMessageError]) = {
    val memberId = memberDao.insert(testMember).futureValue
    val messageWithMemberId = textMessage.copy(memberId = memberId)

    val textMessageId = textMessageDao.insert(messageWithMemberId).futureValue
    val textMessageWithId = messageWithMemberId.copy(id = Some(textMessageId))

    (textMessageWithId, errors.map { error =>
      val errorWithTextMessage = error.copy(textMessageId = textMessageId)
      val errorId = textMessageErrorDao.insert(errorWithTextMessage).futureValue

      errorWithTextMessage.copy(id = Some(errorId))
    })
  }

  /**
    * Inserts testMessageA with 3 errors.
    *
    * @return The message and its errors
    */
  private def insertTestMessageA(): (TextMessage, Seq[TextMessageError]) = {
    insertTestData(testMessageA, testErrors)
  }

  /**
    * Inserts testMessageB with 1 error.
    *
    * @return The message and its error
    */
  private def insertTestMessageB(): (TextMessage, Seq[TextMessageError]) = {
    insertTestData(testMessageB, Seq(testErrorD))
  }

  "TextMessageErrorDAO" should {
    "not return any errors if none are present" in {
      textMessageErrorDao.all().futureValue shouldBe empty
    }

    "return all stored errors" in {
      textMessageErrorDao.all().futureValue shouldBe empty

      val (_, errors) = insertTestMessageA()

      textMessageErrorDao.all().futureValue shouldEqual errors
    }

    "return None if an error wasn't found" in {
      textMessageErrorDao.get(0).futureValue shouldBe None
    }

    "return Some(error) if an error was found with the provided ID" in {
      val (_, errors) = insertTestMessageA()

      textMessageErrorDao.get(errors(0).id.get).futureValue shouldBe Some(errors(0))
    }

    "insert an error and return its ID" in {
      val (_, errors) = insertTestMessageA()

      errors.map { error =>
        error.id mustNot be(None)
      }
    }

    "return all of the errors for a text messages" in {
      val (messageA, messageAErrors) = insertTestMessageA()
      val (messageB, messageBErrors) = insertTestMessageB()

      textMessageErrorDao.forTextMessage(messageA).futureValue shouldEqual messageAErrors
      textMessageErrorDao.forTextMessage(messageB).futureValue shouldEqual messageBErrors

      messageAErrors.map { error =>
        textMessageErrorDao.forTextMessage(messageB).futureValue shouldNot contain(error)
      }
    }

    "delete all errors correctly" in {
      insertTestMessageA()
      insertTestMessageB()

      textMessageErrorDao.all().futureValue shouldNot be(empty)

      textMessageErrorDao.empty().futureValue

      textMessageErrorDao.all().futureValue should be(empty)
    }
  }
}
