package dao

import javax.inject.Inject

import models.Member
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by james on 08/05/16.
  */
class MemberDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider) extends HasDatabaseConfigProvider[JdbcProfile] {
  import driver.api._

  private val Members = TableQuery[MembersTable]

  /**
    * Get all of the configured members.
    *
    * @return
    */
  def all(): Future[Seq[Member]] = db.run(Members.result)

  /**
    * Add a new member to the database.
    *
    * @param member
    * @return
    */
  def insert(member: Member): Future[Unit] = db.run(Members += member).map { _ => () }

  /**
    * Check if a member exists with the given phone number OR e-mail address.
    *
    * @param phoneNumber Mobile number to lookup
    * @param email E-mail address to lookup
    * @return
    */
  def exists(phoneNumber: String, email: String): Future[Boolean] = {
    db.run(Members.filter(m => m.phoneNumber === phoneNumber || m.email === email).exists.result)
  }

  /**
    * The Slick mapping for the Member object.
    * @param tag
    */
  private class MembersTable(tag: Tag) extends Table[Member](tag, "members") {

    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def name = column[String]("name")

    def phoneNumber = column[String]("phone_number")

    def email = column[String]("email")

    def * = (id.?, name, phoneNumber, email) <> (Member.tupled, Member.unapply)

  }

}
