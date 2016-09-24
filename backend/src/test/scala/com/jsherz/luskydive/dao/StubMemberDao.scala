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
import com.jsherz.luskydive.util.Util
import com.jsherz.luskydive.json.MemberSearchResult
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.util.Errors

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
  override def get(uuid: UUID): Future[String \/ Option[Member]] = {
    Future.successful {
      if (StubMemberDao.getExistsUuid.equals(uuid)) {
        \/-(Some(StubMemberDao.getExistsMember))
      } else if (StubMemberDao.getNotFoundUuid.equals(uuid)) {
        \/-(None)
      } else if (StubMemberDao.getErrorUuid.equals(uuid)) {
        -\/(Errors.internalServer)
      } else {
        throw new RuntimeException("unknown uuid used with stub")
      }
    }
  }

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

  /**
    * Look for a member with the given phone number.
    *
    * @param phoneNumber
    * @return
    */
  override def forPhoneNumber(phoneNumber: String): Future[String \/ Option[Member]] = {
    Future.successful {
      if (StubMemberDao.forPhoneNumber.equals(phoneNumber)) {
        \/-(Some(StubMemberDao.forPhoneNumberMember))
      } else if (StubMemberDao.forPhoneNumberNotFound.equals(phoneNumber)) {
        \/-(None)
      } else if (StubMemberDao.forPhoneNumberError.equals(phoneNumber)) {
        -\/(Errors.internalServer)
      } else {
        throw new RuntimeException("unknown phone number used with stub")
      }
    }
  }

}

object StubMemberDao {

  val existsPhoneNumber = "+447835798240"

  val existsPhoneNumberFormatted = "+447835798240"

  val existsEmail = "IsaacBurgess@jourrapide.com"

  val searchResults = Util.fixture[Seq[MemberSearchResult]]("example.json")

  val getExistsUuid = UUID.fromString("6d9db71d-d910-4993-bdff-d5301664d5b2")
  val getExistsMember = Util.fixture[Member]("6d9db71d.json")
  val getErrorUuid = UUID.fromString("6c4b9c7f-0e08-42b9-bc99-4dd72d099497")
  val getNotFoundUuid = UUID.fromString("323f8275-f5c8-407a-adc2-d7a0ddb420a1")

  val forPhoneNumber = "+447881072696"
  val forPhoneNumberNotFound = "+447810000001"
  val forPhoneNumberError = "+447111999111"
  val forPhoneNumberMember = Util.fixture[Member]("a70dba8b.json")

}
