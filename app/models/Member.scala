package models

import java.util

import play.api.data.validation.Valid
import slick.ast.ColumnOption.{AutoInc, PrimaryKey}

object Tables extends {
  val profile = slick.driver.PostgresDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.driver.JdbcProfile
  import profile.api._

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

  class Members(tag: Tag) extends Table[Member](tag, "members") {
    def id = column[Int]("id", PrimaryKey, AutoInc)
    def name = column[String]("name")
    def phoneNumber = column[String]("phone_number")
    def email = column[String]("email")
    def * = (name, phoneNumber, email, id.?) <> (Member.tupled, Member.unapply)
  }

  val members = TableQuery[Members]
}
