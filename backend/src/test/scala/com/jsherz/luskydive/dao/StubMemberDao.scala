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

import java.util.UUID

import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.itest.util.Util
import com.jsherz.luskydive.json.MemberSearchResult
import com.jsherz.luskydive.json.MemberJsonSupport._

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

/**
  * A testing implementation of the MemberDAO that responds to a set of pre-defined inputs.
  */
class StubMemberDao()(implicit val ec: ExecutionContext) extends MemberDao {

  /**
    * Does a member exist in the DB with the given e-mail or phone number?
    *
    * @param phoneNumber
    * @param email
    * @return
    */
  override def memberExists(phoneNumber: Option[String], email: Option[String]): Future[Boolean] = {
    Future(phoneNumber.contains(StubMemberDao.existsPhoneNumberFormatted) || email.contains(StubMemberDao.existsEmail))
  }

  /**
    * Create a new member.
    *
    * @param member
    * @return
    */
  override def create(member: Member): Future[Option[UUID]] = {
    Future(Some(Generators.randomBasedGenerator().generate()))
  }

  /**
    * Get a member with the provided UUID.
    *
    * @param uuid
    * @return
    */
  override def get(uuid: UUID): Future[Option[Member]] = Future(None)

  /**
    * Perform a search for members by matching on names, phone numbers and e-mails.
    *
    * @param term
    * @return
    */
  override def search(term: String): Future[\/[String, Seq[MemberSearchResult]]] = {
    Future {
      if (term.trim.length >= 3) {
        \/-(StubMemberDao.searchResults)
      } else {
        -\/(MemberDaoErrors.invalidSearchTerm)
      }
    }
  }

}

object StubMemberDao {

  val existsPhoneNumber = "+447835798240"

  val existsPhoneNumberFormatted = "+447835798240"

  val existsEmail = "IsaacBurgess@jourrapide.com"

  val searchResults = Util.fixture[Seq[MemberSearchResult]]("example.json")

}
