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
import com.jsherz.luskydive.core.{TextMessage, TextMessageStatuses}
import com.jsherz.luskydive.services.DatabaseService
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}


trait TextMessageDao {

  def all(): Future[Seq[TextMessage]]

  def get(uuid: UUID): Future[Option[TextMessage]]

  def insert(textMessage: TextMessage): Future[UUID]

  def forMember(memberUuid: UUID): Future[Seq[TextMessage]]

  def update(textMessage: TextMessage): Future[Int]

  def forMassText(massTextUuid: UUID): Future[Seq[TextMessage]]

  def toSend(): Future[Seq[TextMessage]]

  def getReceived(): Future[Seq[TextMessage]]

  def getReceivedCount(): Future[Int]

}

/**
  * Used to create and access text messages stored in the database.
  */
class TextMessageDaoImpl(protected override val databaseService: DatabaseService)
                        (implicit ec: ExecutionContext)
  extends Tables(databaseService) with TextMessageDao {

  import driver.api._

  private val log: Logger = LoggerFactory.getLogger(getClass)

  /**
    * Get all text messages that will or have been sent, ordered by the date they were last updated, descending.
    *
    * @return Left(error) or Right(messages)
    */
  def all(): Future[Seq[TextMessage]] = {
    db.run(TextMessages.sortBy(_.updatedAt.desc).result)
  }

  /**
    * Get the text message with the given UUID.
    *
    * @param uuid
    * @return Left(error) or Right(Some(message) if found, otherwise None)
    */
  def get(uuid: UUID): Future[Option[TextMessage]] = {
    db.run(TextMessages.filter(_.uuid === uuid).result.headOption)
  }

  /**
    * Insert a new text message into the database.
    *
    * @param textMessage
    * @return
    */
  def insert(textMessage: TextMessage): Future[UUID] = {
    db.run((TextMessages returning TextMessages.map(_.uuid)) += textMessage)
  }

  /**
    * Get all text messages sent to a member.
    *
    * @param memberUuid
    * @return
    */
  def forMember(memberUuid: UUID): Future[Seq[TextMessage]] = {
    db.run(TextMessages.filter(_.memberUuid === memberUuid).sortBy(_.updatedAt.desc).result)
  }

  /**
    * Update the provided text message.
    *
    * @param textMessage Text message to update (matched on UUID)
    * @return Left(error) or Right(num rows updated)
    */
  def update(textMessage: TextMessage): Future[Int] = {
    db.run(TextMessages.filter(_.uuid === textMessage.uuid).update(textMessage))
  }

  /**
    * Get all of the text messages that were sent for a mass text.
    *
    * @param massTextUuid
    * @return
    */
  override def forMassText(massTextUuid: UUID): Future[Seq[TextMessage]] = {
    db.run(
      TextMessages
        .filter(_.massTextUuid === massTextUuid)
        .sortBy(_.updatedAt.desc)
        .result
    )
  }

  /**
    * Get all text messages that are waiting to be sent.
    *
    * @return
    */
  override def toSend(): Future[Seq[TextMessage]] = {
    db.run(
      TextMessages
        .filter(_.status === TextMessageStatuses.Pending)
        .sortBy(m => (m.createdAt.asc, m.updatedAt.asc))
        .result
    )
  }

  /**
    * Get all text messages that have been received but have not yet been replied to.
    *
    * @return
    */
  override def getReceived(): Future[Seq[TextMessage]] = {
    db.run(
      TextMessages
        .filter(_.status === TextMessageStatuses.Received)
        .sortBy(_.createdAt.asc)
        .result
    )
  }

  /**
    * Get the number of text messages that have been received but not yet replied to.
    *
    * @return
    */
  override def getReceivedCount(): Future[Int] = {
    db.run(
      TextMessages
        .filter(_.status === TextMessageStatuses.Received)
        .length
        .result
    )
  }

}
