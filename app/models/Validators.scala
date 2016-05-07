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
    * A constraint validator to check that a form field value is a valid UK mobile number in one of the formats below.
    *
    * - 07123123123
    * - 447123123123
    * - +447123123123
    *
    * @return Valid if a valid UK mobile number, Invalid if not
    */
  def phoneNumberValidator: Constraint[String] = Constraint[String]("constraint.required") { phoneNumber =>
    if (phoneNumber == null) Invalid(ValidationError("error.required"))
    else if (phoneNumber.trim.isEmpty) Invalid(ValidationError("error.required"))
    else if (!(phoneNumberRegex findAllMatchIn phoneNumber).hasNext) Invalid(ValidationError("error.invalidPhoneNumber"))
    else Valid
  }

  /**
    * A constraint validator to do a very basic check if an e-mail is valid.
    *
    * @return Valid if the e-mail looks roughly valid, Invalid if not
    */
  def emailValidator: Constraint[String] = Constraint[String]("constraint.required") { email =>
    if (email == null) Invalid(ValidationError("error.required"))
    else if (email.trim.isEmpty) Invalid(ValidationError("error.required"))
    else if (!(emailRegex findAllMatchIn email).hasNext) Invalid(ValidationError("error.invalidEmail"))
    else Valid
  }
}
