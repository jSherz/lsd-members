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

package com.jsherz.luskydive.models

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber
import com.jsherz.luskydive.api.{Invalid, Valid, ValidationResult}

import scala.util.matching.Regex

/**
  * Created by james on 07/05/16.
  */
object Validators {
  /**
    * Catch the worst e-mail mistakes and leave the rest to the mail server.
    */
  private val emailRegex: Regex = "^.+@.+\\..+$".r

  /**
    * The longest a member's name can be.
    */
  private val maxNameLength: Int = 50

  /**
    * The longest a member's e-mail address can be.
    */
  private val maxEmailLength: Int = 255

  /**
    * Assume phone numbers are from the given region if they're not prefixed with a country code.
    */
  private val defaultPhoneNumberRegion = "GB"

  /**
    * Ensures a valid phone number was provided and parses it into a <code>PhoneNumber</code>
    * so that we can get the E164 formatted version.
    *
    * - 07123123123 (UK numbers)
    * - 447123123123 (Number with country code, no plus)
    * - +447123123123 (Number with country code and plus)
    *
    * @return Left(translationKey) if invalid or Right(number) if not
    */
  def parsePhoneNumber(maybePhoneNumber: Option[String]): Either[String, PhoneNumber] = {
    maybePhoneNumber.fold(Left("error.required"): Either[String, PhoneNumber]) { phoneNumber =>
      phoneNumber.trim.isEmpty match {
        case true => Left("error.required")
        case false =>
          val parser = PhoneNumberUtil.getInstance()
          val parsedNumber = parser.parse(phoneNumber, defaultPhoneNumberRegion)

          parser.isValidNumber(parsedNumber) match {
            case true => Right(parsedNumber)
            case false => Left("error.invalidPhoneNumber")
          }
      }
    }
  }

  /**
    * Performs a very basic check to see if an e-mail is valid.
    *
    * @return Valid() if the e-mail looks roughly valid, Invalid(translationKey) if not
    */
  def isEmailValid(maybeEmail: Option[String]): ValidationResult = {
    maybeEmail.fold(Invalid("error.required"): ValidationResult) { email =>
      val trimmed = email.trim

      if (trimmed.isEmpty) {
        Invalid("error.required")
      } else if (!(emailRegex findAllMatchIn trimmed).hasNext) {
        Invalid("error.invalidEmail")
      } else if (trimmed.length() > maxEmailLength) {
        Invalid("error.emailTooLong")
      } else {
        Valid()
      }
    }
  }

  /**
    * Ensures the provided welcome text template is at least one character and is 480 characters or under.
    *
    * @return Valid() if 1 <= length <= 480, otherwise Invalid(translationKey)
    */
  def isWelcomeTextValid(welcomeText: String): ValidationResult = {
    val welcomeTextLength = welcomeText.length()

    if (welcomeTextLength == 0) {
      Invalid("error.welcomeTextEmpty")
    } else if (welcomeTextLength > 480) {
      Invalid("error.welcomeTextTooLong")
    } else {
      Valid()
    }
  }

  /**
    * Ensures the provided member's name is shorter than the database field length.
    *
    * @return Valid() if <= maxNameLength, otherwise Invalid(translationKey)
    */
  def isNameValid(name: String): ValidationResult = {
    if (name.length() > maxNameLength) {
      Invalid("error.nameTooLong")
    } else {
      Valid()
    }
  }
}
