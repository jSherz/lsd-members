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

import models.Validators
import org.scalatestplus.play._
import play.api.data.validation.{Invalid, Valid, ValidationError}

/**
  * Defines the behaviour of the MembershipService class.
  */
class ValidatorsSpec extends PlaySpec with OneAppPerTest {
  /**
    * Generate a (long) string of something.
    *
    * @param length Total length to create
    * @param thing Character to fill the string with, by default a space
    * @return
    */
  private def stringOfThings(length: Int, thing: String = " "): String = {
    (1 to length).map(_ => thing).foldLeft("")((a, b) => a ++ b)
  }

  private val validNumbers = Seq(
    "07123123123", "07123614534", "07235753823", "447235898341", "447725837138", "447346881482", "+447458389482",
    "+447154712381", "+447123123123"
  )

  private val invalidNumbers = Seq(
    "071231", "071231231", "0712312312", "072357538231", "0723575382312", "0723575382312123", "4472358",
    "4472358983", "44723589834", "4473468814821", "44734688148212", "44734688148212123", "+4474583",
    "+4474583894", "+44745838948", "+4471231231231", "+44712312312312", "+44712312312312123",
    "7123123123", "01123123123", "4407123123123", "+4407123123123"
  )

  private val validEmails = Seq(
    "goose@goat.org", "leeroy@jenkins.wow.amaze.org"
  )

  private val invalidEmails = Seq(
    "fake@localhost", "something@fishy-net"
  )

  "Validators" should {
    "make long strings of spaces correctly" in {
      stringOfThings(1) mustEqual " "
      stringOfThings(2) mustEqual "  "
      stringOfThings(10) mustEqual "          "
      stringOfThings(10, "a") mustEqual "aaaaaaaaaa"
    }

    "accept valid phone numbers" in {
      for (validNumber <- validNumbers) {
        Validators.phoneNumberValidator.apply(validNumber) mustBe Valid
      }
    }

    "reject invalid phone numbers" in {
      for (invalidNumber <- invalidNumbers) {
        Validators.phoneNumberValidator.apply(invalidNumber) mustBe Invalid(List(ValidationError(List("error.invalidPhoneNumber"))))
      }
    }

    "accept valid e-mail addresses" in {
      for (validEmail <- validEmails) {
        Validators.emailValidator.apply(validEmail) mustBe Valid
      }
    }

    "reject invalid e-mail addresses" in {
      for (invalidEmail <- invalidEmails) {
        Validators.emailValidator.apply(invalidEmail) mustBe Invalid(List(ValidationError(List("error.invalidEmail"))))
      }
    }

    "allow any valid welcome message" in {
      val exampleMessages = Seq(
        "1", "Hello! :)", stringOfThings(480)
      )

      exampleMessages.map { message =>
        Validators.welcomeTextValidator.apply(message) mustBe Valid
      }
    }

    "reject empty welcome messages" in {
      Validators.welcomeTextValidator.apply("") mustBe Invalid(List(ValidationError(List("error.welcomeTextEmpty"))))
    }

    "reject overly long welcome messages" in {
      val maxFieldLength = 480

      val overlyLongMessages = Seq(
        stringOfThings(maxFieldLength + 1), stringOfThings(maxFieldLength + 2), stringOfThings(maxFieldLength * 10)
      )

      overlyLongMessages.map { message =>
        Validators.welcomeTextValidator.apply(message) mustBe Invalid(List(ValidationError(List("error.welcomeTextTooLong"))))
      }
    }

    "reject overly long names" in {
      val maxFieldLength = 50

      val overlyLongNames = Seq(
        stringOfThings(maxFieldLength + 1), stringOfThings(maxFieldLength + 2), stringOfThings(maxFieldLength * 10)
      )

      overlyLongNames.map { message =>
        Validators.nameValidator.apply(message) mustBe Invalid(List(ValidationError(List("error.nameTooLong"))))
      }
    }

    "reject overly long e-mails" in {
      // Max of 255 - 15 characters of dummy e-mail text used below
      val maxFieldLength = 255 - 15

      val overlyLongEmails = Seq(
        s"ema${stringOfThings(maxFieldLength + 1)}il@email.com",
        s"ema${stringOfThings(maxFieldLength + 2)}il@email.com",
        s"ema${stringOfThings(maxFieldLength * 10)}il@email.com"
      )

      overlyLongEmails.map { message =>
        Validators.emailValidator.apply(message) mustBe Invalid(List(ValidationError(List("error.emailTooLong"))))
      }
    }
  }
}
