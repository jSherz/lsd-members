package models

import java.util

import play.api.data.validation.Valid

import slick.driver.PostgresDriver.api._

/**
  * The case to SQL mappings for Slick.
  */
object CaseClassMappings extends App {

  /**
    * The main member class, representing a person that has provided us with a phone number or e-mail address.
    *
    * @param name Peron's (first) name
    * @param phoneNumber UK mobile phone number
    * @param email E-mail address
    * @param id Record ID, if saved in DB
    */
  case class Member(name: String, phoneNumber: String, email: String, id: Option[Int] = None) {
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
    def getQueueJobVars(): util.HashMap[String, Object] = {
      val data = new util.HashMap[String, Object]()

      data.put("name", name)
      data.put("phoneNumber", if (phoneNumber == null) "" else phoneNumber)
      data.put("email", if (email == null) "" else email)

      data
    }
  }

  /**
    * The Slick mapping for Member instances.
    *
    * @param tag
    */
  class Members(tag: Tag) extends Table[Member](tag, "members") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    def phoneNumber = column[String]("phone_number")
    def email = column[String]("email")
    def * = (name, phoneNumber, email, id.?) <> (Member.tupled, Member.unapply)
  }

  /**
    * The object used to perform queries on Members in the database.
    */
  val members = TableQuery[Members]
}
