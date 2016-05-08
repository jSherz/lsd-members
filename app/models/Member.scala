package models

import java.util.HashMap

import play.api.data.validation.Valid

/**
  * The main member class, representing a person that has provided us with a phone number or e-mail address.
  *
  * @param id Record ID
  * @param name Peron's (first) name
  * @param phoneNumber UK mobile phone number
  * @param email E-mail address
  */
case class Member(id: Option[Int], name: String, phoneNumber: String, email: String) {
  /**
    * Ensures that the record has at least:
    *
    * - A non-empty name
    * - A valid phone number OR e-mail address.
    *
    * @return true if above conditions are met
    */
  def valid(): Boolean = {
    val phoneOrEmailValid = (phoneNumber != null && Validators.phoneNumberValidator(phoneNumber) == Valid) ||
      (email != null && Validators.emailValidator(email) == Valid)

    name != null && !name.trim().isEmpty && phoneOrEmailValid
  }

  /**
    * Turns the Member into a form that can be written to the Jesque job.
    *
    * @return Dictionary containing name, phone number & e-mail.
    */
  def getQueueJobVars(): HashMap[String, Object] = {
    val data = new HashMap[String, Object]()

    data.put("name", name)
    data.put("phoneNumber", if (phoneNumber == null) "" else phoneNumber)
    data.put("email", if (email == null) "" else email)

    data
  }
}
