/**
  * MIT License
  *
  * Copyright (c) 2016-2018 James Sherwood-Jones <james.sherwoodjones@gmail.com>
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

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.Main.{actorSystem, getClass}
import com.jsherz.luskydive.core.{CommitteeMember, Member}
import com.jsherz.luskydive.json.MemberSearchResult
import com.jsherz.luskydive.services.DatabaseService
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

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
    * @param member
    * @return UUID if creation succeeded
    */
  def create(member: Member): String \/ Future[UUID]

  /**
    * Get a member with the provided UUID.
    *
    * @param uuid
    * @return
    */
  def get(uuid: UUID): Future[Option[Member]]

  /**
    * Perform a search for members by matching on names, phone numbers and e-mails.
    *
    * @param term
    * @return
    */
  def search(term: String): String \/ Future[Seq[MemberSearchResult]]

  /**
    * Look for a member with the given phone number.
    *
    * @param phoneNumber
    * @return
    */
  def forPhoneNumber(phoneNumber: String): Future[Option[Member]]

  /**
    * Look for a member with the given social user ID.
    *
    * @param socialId
    * @return
    */
  def forSocialId(socialId: String): Future[Option[(Member, Option[CommitteeMember])]]

  /**
    * Update a member's information.
    *
    * @param member
    * @return
    */
  def update(member: Member): Future[Member]

}

class MemberDaoImpl(override protected val databaseService: DatabaseService)
                   (implicit ec: ExecutionContext)
  extends Tables(databaseService) with MemberDao {

  import driver.api._

  private val log: Logger = LoggerFactory.getLogger(getClass)

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
  override def create(member: Member): String \/ Future[UUID] = {
    if (member.phoneNumber.isDefined || member.email.isDefined) {
      \/-(db.run((Members returning Members.map(_.uuid)) += member))
    } else {
      -\/(MemberDaoErrors.noPhoneNumberOrEmail)
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

  /**
    * Perform a search for members by matching on all of the fields that are specified.
    *
    * For example, if name and phoneNumber are not None, members must match both to be included in the results.
    *
    * @param term
    * @return
    */
  override def search(term: String): String \/ Future[Seq[MemberSearchResult]] = {
    val termTrimmed = term.trim

    if (termTrimmed.length >= 3) {
      val formattedTerm = '%' + termTrimmed.toLowerCase + '%'

      val query = db.run(
        Members.filter(member =>
          // Match on any of name, phone or e-mail
          (member.firstName.toLowerCase like formattedTerm) ||
            (member.lastName.toLowerCase like formattedTerm) ||
            (member.phoneNumber like formattedTerm) ||
            (member.email.toLowerCase like formattedTerm)
        ).sortBy(m => (m.lastName, m.firstName)).result.map(_.map { rawMember =>
          MemberSearchResult(rawMember.uuid, rawMember.firstName, rawMember.lastName, rawMember.phoneNumber, rawMember.email)
        })
      )

      \/-(query)
    } else {
      -\/(MemberDaoErrors.invalidSearchTerm)
    }
  }

  /**
    * Look for a member with the given phone number.
    *
    * @param phoneNumber
    * @return
    */
  override def forPhoneNumber(phoneNumber: String): Future[Option[Member]] = {
    db.run(Members.filter(_.phoneNumber === phoneNumber).result.headOption)
  }

  /**
    * Look for a member with the given social user ID.
    *
    * @param socialId
    * @return
    */
  override def forSocialId(socialId: String): Future[Option[(Member, Option[CommitteeMember])]] = {
    val lookup = for {
      (member, committeeMember) <- Members.filter(_.socialUserId === socialId) joinLeft
        CommitteeMembers on (_.uuid === _.memberUuid)
    } yield (member, committeeMember)

    db.run(lookup.result.headOption)
  }

  /**
    * Update a member's information.
    *
    * @param member
    * @return
    */
  override def update(member: Member): Future[Member] = {
    db.run(Members.filter(_.uuid === member.uuid).update(member)).map(_ => member)
  }

}

object MemberDaoErrors {

  val invalidSearchTerm = "error.invalidSearchTerm"
  val noPhoneNumberOrEmail = "error.noPhoneNumberOrEmail"

}
