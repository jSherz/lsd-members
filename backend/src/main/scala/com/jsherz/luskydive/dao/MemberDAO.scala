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

import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.services.DatabaseService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Data Access Object to retrieve and store Member information.
  */
trait MemberDAO {

  /**
    * Does a member exist in the DB with the given e-mail or phone number?
    *
    * @param phoneNumber
    * @param email
    * @return
    */
  def memberExists(phoneNumber: Option[String], email: Option[String]): Future[Boolean]

  /**
    * Create a new member.
    *
    * @param name
    * @param phoneNumber
    * @param email
    * @return
    */
  def create(name: String, phoneNumber: Option[String], email: Option[String]): Future[Int]

}

case class MemberDAOImpl(override protected val databaseService: DatabaseService)(implicit ec: ExecutionContext)
  extends Tables(databaseService) with MemberDAO {

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
    * Create a new member.
    *
    * @param name
    * @param phoneNumber
    * @param email
    * @return
    */
  override def create(name: String, phoneNumber: Option[String], email: Option[String]): Future[Int] =
    db.run((Members returning Members.map(_.id)) += Member(None, name, phoneNumber, email))

}
