package models

import play.api.data.validation.Valid
import services.MembershipService

/**
  * Represents the information of a standard member.
  */
case class Member (ms: MembershipService, name: String, phoneNumber: String, email: String) {
  /**
    * Ensures that the record has at least:
    *
    * - A non-empty name
    * - A valid phone number OR e-mail address.
    *
    * @return true if above conditions are met
    */
  def valid(): Boolean = {
    val phoneOrEmailValid = (phoneNumber != null && ms.phoneNumberValidator(phoneNumber) == Valid) ||
      (email != null && ms.emailValidator(email) == Valid)

    name != null && !name.trim().isEmpty && phoneOrEmailValid
  }
}
