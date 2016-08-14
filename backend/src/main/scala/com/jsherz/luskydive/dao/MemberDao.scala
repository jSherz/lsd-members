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

import java.sql.{Date, Timestamp}
import java.util.UUID

import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.services.DatabaseService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Data Access Object to retrieve and store Member information.
  */
trait MemberDao {

  /**
    * Does a member exist in the DB with the given e-mail or phone number?
    *
    * @param phoneNumber
    * @param email
    * @return
    */
  def memberExists(phoneNumber: Option[String], email: Option[String]): Future[Boolean]

  /**
    * Insert a member into the database.
    *
    * NB: It will generate a UUID if one is not provided.
    *
    * @param member
    * @return UUID if creation succeeded
    */
  def create(member: Member): Future[Option[UUID]]

  /**
    * Get a member with the provided UUID.
    *
    * @param uuid
    * @return
    */
  def get(uuid: UUID): Future[Option[Member]]

}

case class MemberDaoImpl(override protected val databaseService: DatabaseService)(implicit ec: ExecutionContext)
  extends Tables(databaseService) with MemberDao {

  import driver.api._

  /**
    * Does a member exist in the DB with the given e-mail or phone number?
    *
    * @param phoneNumber
    * @param email
    * @return
    */
  override def memberExists(phoneNumber: Option[String], email: Option[String]): Future[Boolean] = {
    db.run(Members.filter(m =>
      (m.phoneNumber.isDefined && m.phoneNumber === phoneNumber) ||
        (m.email.isDefined && m.email === email)
    ).result).map(_.nonEmpty)
  }

  /**
    * Insert a member into the database.
    *
    * NB: It will generate a UUID if one is not provided.
    *
    * @param member
    * @return UUID if creation succeeded
    */
  override def create(member: Member): Future[Option[UUID]] = {
    val uuid = member.uuid.getOrElse(Generators.randomBasedGenerator().generate())

    if (member.phoneNumber.isDefined || member.email.isDefined) {
      db.run((Members returning Members.map(_.uuid)) += member.copy(uuid = Some(uuid))).map(Some(_))
    } else {
      Future(None)
    }
  }

  /**
    * Get a member with the provided UUID.
    *
    * @param uuid
    * @return
    */
  override def get(uuid: UUID): Future[Option[Member]] = {
    db.run(Members.filter(_.uuid === uuid).result.headOption)
  }

}
