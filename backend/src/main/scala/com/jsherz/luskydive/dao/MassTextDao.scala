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

import java.sql.{Date, Timestamp}
import java.util.UUID

import akka.event.LoggingAdapter
import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core.{MassText, Member, TextMessage, TextMessageStatuses}
import com.jsherz.luskydive.services.DatabaseService
import com.jsherz.luskydive.util.EitherFutureExtensions._
import com.jsherz.luskydive.util.FutureError._
import com.jsherz.luskydive.util.TextMessageUtil
import scalaz.{-\/, \/}

import scala.concurrent.{ExecutionContext, Future}

/**
  * Handles recording text messages being sent to many members.
  */
trait MassTextDao {

  /**
    * Get the mass text with the given UUID.
    *
    * @param uuid
    * @return
    */
  def get(uuid: UUID): Future[String \/ Option[MassText]]

  /**
    * Get the number of members that joined between the given dates.
    *
    * @param startDate Start date (inclusive)
    * @param endDate   End date (exclusive)
    * @return
    */
  def filterCount(startDate: Date, endDate: Date): Future[String \/ Int]

  /**
    * Send out a mass text message to members that joined between the given dates.
    *
    * Use [[filterCount()]] to determine how many members would be contacted.
    *
    * @param sender    Committee member that's sending this text
    * @param startDate Start date (inclusive)
    * @param endDate   End date (exclusive)
    * @param template  Template of the message to send
    * @param createdAt Time the mass text was sent
    * @return UUID of created mass text
    */
  def send(sender: UUID, startDate: Date, endDate: Date, template: String, createdAt: Timestamp): Future[String \/ UUID]

}

class MassTextDaoImpl(protected override val databaseService: DatabaseService)
                     (
                       implicit ec: ExecutionContext,
                       log: LoggingAdapter
                     )
  extends Tables(databaseService) with MassTextDao {

  import driver.api._

  /**
    * Get the mass text with the given UUID.
    *
    * @param uuid
    * @return
    */
  override def get(uuid: UUID): Future[String \/ Option[MassText]] = {
    db.run(MassTexts.filter(_.uuid === uuid).result.headOption) withServerError
  }

  /**
    * Get the number of members that joined between the given dates.
    *
    * @param startDate Start date (inclusive)
    * @param endDate   End date (exclusive)
    * @return
    */
  override def filterCount(startDate: Date, endDate: Date): Future[\/[String, Int]] = {
    if (startDate.equals(endDate)) {
      Future.successful(-\/(MassTextDaoErrors.endDateStartDateSame))
    } else if (endDate.before(startDate)) {
      Future.successful(-\/(MassTextDaoErrors.endDateBeforeStartDate))
    } else {
      db.run(
        membersCreatedBetween(startDate, endDate).length.result
      ) withServerError
    }
  }

  /**
    * Send out a mass text message to members that joined between the given dates.
    *
    * Use [[filterCount()]] to determine how many members would be contacted.
    *
    * @param sender    Committee member that's sending this text
    * @param startDate Start date (inclusive)
    * @param endDate   End date (exclusive)
    * @param template  Template of the message to send
    * @return UUID of created mass text
    */
  override def send(sender: UUID, startDate: Date, endDate: Date, template: String, createdAt: Timestamp): Future[String \/ UUID] = {
    if (startDate.equals(endDate)) {
      Future.successful(-\/(MassTextDaoErrors.endDateStartDateSame))
    } else if (endDate.before(startDate)) {
      Future.successful(-\/(MassTextDaoErrors.endDateBeforeStartDate))
    } else {
      val futureMembers: Future[Seq[Member]] = db.run(membersCreatedBetween(startDate, endDate).result)

      futureMembers.flatMap { members =>
        if (members.isEmpty) {
          Future.successful(-\/(MassTextDaoErrors.noMembersMatched))
        } else {
          for {
            massTextUuid <- create(sender, template, createdAt).withServerError
            createTexts <- massTextUuid withFutureF { uuid =>
              val membersWithPhoneNumbers = members.filter(_.phoneNumber.isDefined)

              db.run(
                DBIO.sequence(
                  membersWithPhoneNumbers.map(member => addTextMessage(member, template, uuid, createdAt))
                )
              ) withServerError
            }
          } yield {
            massTextUuid
          }
        }
      }
    }
  }

  private def membersCreatedBetween(startDate: Date, endDate: Date) = {
    val startTimestamp = new Timestamp(startDate.getTime)
    val endTimestamp = new Timestamp(endDate.getTime)

    Members.filter { m =>
      m.createdAt >= startTimestamp &&
        m.createdAt < endTimestamp
    }
  }

  private def create(committeeMemberUuid: UUID, template: String, createdAt: Timestamp): Future[UUID] = {
    val massText = MassText(
      Generators.randomBasedGenerator.generate(),
      committeeMemberUuid,
      template,
      createdAt
    )

    db.run((MassTexts returning MassTexts.map(_.uuid)) += massText)
  }

  private def addTextMessage(member: Member, template: String, massTextUuid: UUID, createdAt: Timestamp) = {
    val message = TextMessageUtil.parseTemplate(template, member.firstName)

    val textMessage = TextMessage(
      Generators.randomBasedGenerator.generate(),
      member.uuid,
      Some(massTextUuid),
      TextMessageStatuses.Pending,
      member.phoneNumber.get,
      "PENDING", // TODO: Replace with optional type
      message,
      None,
      fromMember = false,
      createdAt,
      createdAt
    )

    (TextMessages returning TextMessages.map(_.uuid)) += textMessage
  }

}

object MassTextDaoErrors {

  val endDateBeforeStartDate = "error.endDateBeforeStartDate"

  val endDateStartDateSame = "error.endDateStartDateSame"

  val noMembersMatched = "error.noMembersMatched"

}
