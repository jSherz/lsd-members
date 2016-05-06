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
    * Test if the provided string is a valid UK mobile number in one of the following formats:
    *
    * - 07123123123
    * - 447123123123
    * - +447123123123
    *
    * @param phoneNumber Number to test
    * @return true if it's valid
    */
  def validatePhoneNumber(phoneNumber: String) = {
    (phoneNumberRegex findAllMatchIn phoneNumber).hasNext
  }

  /**
    * A Play Framework compatible validator to check that a form field value is a valid phone number.
    * @return
    */
  def phoneNumberValidator: Constraint[String] = Constraint[String]("constraint.required") { phoneNumber =>
    if (phoneNumber == null) Invalid(ValidationError("error.required"))
    else if (phoneNumber.trim.isEmpty) Invalid(ValidationError("error.required"))
    else if (!validatePhoneNumber(phoneNumber)) Invalid(ValidationError("error.invalidPhoneNumber"))
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
