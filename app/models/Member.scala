package models

import java.util
import java.util.Map

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

  /**
    * Turns the Member into a form that can be written to the Jesque job.
    *
    * @return Dictionary containing name, phone number & e-mail.
    */
  def getQueueJobVars(): util.HashMap[String, Object] = {
    val data = new util.HashMap[String, Object]()

    data.put("name", name)
    data.put("phoneNumber", if (phoneNumber == null) "" else phoneNumber)
    data.put("email", if (email == null) "" else email)

    data
  }
}

