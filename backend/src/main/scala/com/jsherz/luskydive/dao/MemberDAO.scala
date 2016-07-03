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

package com.jsherz.luskydive.dao

import com.jsherz.luskydive.models.Member
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Used to access members stored in the database.
  */
class MemberDAO(override protected val dbConfig: DatabaseConfig[JdbcProfile]) extends Tables(dbConfig) {
  /**
    * Get all of the configured members.
    *
    * @return
    */
  def all(): Future[Seq[Member]] = db.run(Members.result)

  /**
    * Get the member with the given ID.
    *
    * @param id ID to search with
    * @return Some(member) if found, otherwise None
    */
  def get(id: Int): Future[Option[Member]] = db.run(Members.filter(_.id === id).result.headOption)

  /**
    * Add a new member to the database.
    *
    * @param member Member to add
    * @return The member's ID
    */
  def insert(member: Member): Future[Int] = db.run((Members returning Members.map(_.id)) += member)

  /**
    * Check if a member exists with the given phone number OR e-mail address.
    *
    * @param phoneNumber Mobile number to lookup
    * @param email E-mail address to lookup
    * @return
    */
  def exists(phoneNumber: Option[String], email: Option[String]): Future[Boolean] = {
    if (email.isDefined && phoneNumber.isEmpty) {
      db.run(Members.filter(_.email === email).exists.result)
    } else if (phoneNumber.isDefined && email.isEmpty) {
      db.run(Members.filter(_.phoneNumber === phoneNumber).exists.result)
    } else {
      db.run(Members.filter(m: Member =>
        m.phoneNumber === phoneNumber && m.email === email).exists.result)
    }
  }

  /**
    * Delete all members.
    *
    * @return
    */
  def empty(): Future[Unit] = db.run(Members.delete).map { _ => () }
}
