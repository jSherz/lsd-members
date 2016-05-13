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
  val validNumbers = Seq(
    "07123123123", "07123614534", "07235753823", "447235898341", "447725837138", "447346881482", "+447458389482",
    "+447154712381", "+447123123123"
  )

  val invalidNumbers = Seq(
    "071231", "071231231", "0712312312", "072357538231", "0723575382312", "0723575382312123", "4472358",
    "4472358983", "44723589834", "4473468814821", "44734688148212", "44734688148212123", "+4474583",
    "+4474583894", "+44745838948", "+4471231231231", "+44712312312312", "+44712312312312123",
    "7123123123", "01123123123", "4407123123123", "+4407123123123"
  )

  val validEmails = Seq(
    "goose@goat.org", "leeroy@jenkins.wow.amaze.org"
  )

  val invalidEmails = Seq(
    "fake@localhost", "something@fishy-net"
  )

  "Validators" should {
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
        "1", "Hello! :)", (1 to 480).map(_ => " ").foldLeft("")((a, b) => a ++ b)
      )

      exampleMessages.map { message =>
        Validators.welcomeTextValidator.apply(message) mustBe Valid
      }
    }

    "reject empty welcome messages" in {
      Validators.welcomeTextValidator.apply("") mustBe Invalid(List(ValidationError(List("error.welcomeTextEmpty"))))
    }

    "reject overly long welcome messages" in {
      val overlyLongMessages = Seq(
        (1 to 481).map(_ => " ").foldLeft("")((a, b) => a ++ b),
        (1 to 482).map(_ => " ").foldLeft("")((a, b) => a ++ b),
        (1 to 1000).map(_ => " ").foldLeft("")((a, b) => a ++ b)
      )

      overlyLongMessages.map { message =>
        Validators.welcomeTextValidator.apply(message) mustBe Invalid(List(ValidationError(List("error.welcomeTextTooLong"))))
      }
    }
  }
}
