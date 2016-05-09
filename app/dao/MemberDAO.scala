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
    if (email != null && phoneNumber == null) {
      db.run(Members.filter(_.email === email).exists.result)
    } else if (phoneNumber != null && email == null) {
      db.run(Members.filter(_.phoneNumber === phoneNumber).exists.result)
    } else {
      db.run(Members.filter(m => m.phoneNumber === phoneNumber || m.email === email).exists.result)
    }
  }

  /**
    * Delete all members.
    *
    * @return
    */
  def empty(): Future[Unit] = db.run(Members.delete).map { _ => () }

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
