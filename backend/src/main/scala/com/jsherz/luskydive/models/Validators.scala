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

package com.jsherz.luskydive.models

import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat

import scala.util.matching.Regex
import scalaz.{Failure, Success, Validation}

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
    * Phone number utilities.
    */
  private val parser = PhoneNumberUtil.getInstance()

  /**
    * Ensures a valid phone number was provided and parses it into the E164 formatted version.
    *
    * - 07123123123 (UK numbers)
    * - 447123123123 (Number with country code, no plus)
    * - +447123123123 (Number with country code and plus)
    *
    * @param phoneNumber
    * @return
    */
  def phoneNumber(phoneNumber: String): Validation[String, String] = {
    if (phoneNumber.trim.isEmpty) {
      Failure("error.required")
    } else {
      val parsedNumber = parser.parse(phoneNumber, defaultPhoneNumberRegion)

      if (parser.isValidNumber(parsedNumber)) {
        Success(parser.format(parsedNumber, PhoneNumberFormat.E164))
      } else {
        Failure("error.invalid")
      }
    }
  }

  /**
    * Performs a very basic check to see if an e-mail is valid.
    *
    * @return
    */
  def email(email: String): Validation[String, String] = {
    val trimmed = email.trim

    if (trimmed.isEmpty) {
      Failure("error.required")
    } else if (!(emailRegex findAllMatchIn trimmed).hasNext) {
      Failure("error.invalid")
    } else if (trimmed.length() > maxEmailLength) {
      Failure("error.emailTooLong")
    } else {
      Success(email)
    }
  }

  /**
    * Ensures the provided welcome text template is at least one character and is 480 characters or under.
    *
    * @return Valid() if 1 <= length <= 480, otherwise Invalid(translationKey)
    */
  def isWelcomeTextValid(welcomeText: String): Validation[String, String] = {
    val welcomeTextLength = welcomeText.length()

    if (welcomeTextLength == 0) {
      Failure("error.welcomeTextEmpty")
    } else if (welcomeTextLength > 480) {
      Failure("error.welcomeTextTooLong")
    } else {
      Success(welcomeText)
    }
  }

  /**
    * Ensures the provided member's name is shorter than the database field length.
    *
    * @return
    */
  def name(name: String): Validation[String, String] = {
    val trimmed = name.trim

    if (trimmed.length() > maxNameLength) {
      Failure("error.nameTooLong")
    } else if (trimmed.length == 0) {
      Failure("error.required")
    } else {
      Success(name)
    }
  }

  /**
    * Adds the field name to a failed validation.
    *
    * @param fieldName
    * @param validation
    * @tparam A
    * @tparam B
    * @return
    */
  def withFieldName[A, B](fieldName: String, validation: Validation[A, B]): Validation[(String, A), B] =
  validation.fold(error => Failure((fieldName, error)), result => Success(result))


}
