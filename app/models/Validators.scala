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

package models

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.matching.Regex

/**
  * Created by james on 07/05/16.
  */
object Validators {
  /**
    * A basic regex for UK mobile numbers. Source: http://stackoverflow.com/a/16405304/373230
    */
  private val phoneNumberRegex: Regex = "^(07[\\d]{9}|\\+?447[\\d]{9})$".r

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
    * A constraint validator to check that a form field value is a valid UK mobile number in one of the formats below.
    *
    * - 07123123123
    * - 447123123123
    * - +447123123123
    *
    * @return Valid if a valid UK mobile number, Invalid if not
    */
  def phoneNumberValidator: Constraint[String] = Constraint[String]("constraint.phoneNumber") { phoneNumber =>
    if (phoneNumber == None || phoneNumber.trim.isEmpty) {
      Invalid(ValidationError("error.required"))
    } else if (!(phoneNumberRegex findAllMatchIn phoneNumber).hasNext) {
      Invalid(ValidationError("error.invalidPhoneNumber"))
    } else {
      Valid
    }
  }

  /**
    * A constraint validator to do a very basic check if an e-mail is valid.
    *
    * @return Valid if the e-mail looks roughly valid, Invalid if not
    */
  def emailValidator: Constraint[String] = Constraint[String]("constraint.email") { email =>
    if (email == None || email.trim.isEmpty) {
      Invalid(ValidationError("error.required"))
    } else if (!(emailRegex findAllMatchIn email).hasNext) {
      Invalid(ValidationError("error.invalidEmail"))
    } else if (email.length() > maxEmailLength) {
      Invalid(ValidationError("error.emailTooLong"))
    } else {
      Valid
    }
  }

  /**
    * Ensures the provided welcome text template is at least one character and is 480 characters or under.
    *
    * @return Valid if 1 <= length <= 480, otherwise Invalid
    */
  def welcomeTextValidator: Constraint[String] = Constraint[String]("constraint.welcomeText") { welcomeText =>
    val welcomeTextLength = welcomeText.length()

    if (welcomeTextLength == 0) {
      Invalid(ValidationError("error.welcomeTextEmpty"))
    } else if (welcomeTextLength > 480) {
      Invalid(ValidationError("error.welcomeTextTooLong"))
    } else {
      Valid
    }
  }

  /**
    * Ensures the provided member's name is shorter than the database field length.
    *
    * @return Valid if <= maxNameLength, otherwise Invalid
    */
  def nameValidator: Constraint[String] = Constraint[String]("constraint.name") { name =>
    if (name.length() > maxNameLength) {
      Invalid(ValidationError("error.nameTooLong"))
    } else {
      Valid
    }
  }
}
