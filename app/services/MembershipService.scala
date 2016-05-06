package services

import javax.inject._

import net.greghaines.jesque.ConfigBuilder
import play.api.inject.ApplicationLifecycle
import models.Member
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError, Constraints}

import scala.util.matching.Regex

/**
  * Handles the creation of members.
  */
@Singleton
class MembershipService @Inject() (appLifecycle: ApplicationLifecycle) {
  /**
    * A basic regex for UK mobile numbers. Source: http://stackoverflow.com/a/16405304/373230
    */
  private val phoneNumberRegex: Regex = "^(07[\\d]{8,12}|\\+?447[\\d]{7,11})$".r

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

  /**
    * Create a new member with the given information. Ensures that either a phone number or e-mail address was provided.
    *
    * @param member Raw member data (from the form)
    * @return Either an error message or the same member record that was provided
    */
  def signup(member: Member): Either[String, Member] = {
    Right(member)
  }

  /**
    * The Jesque configuration.
    */
  val config = new ConfigBuilder().build()
}
