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

import akka.event.LoggingAdapter
import com.jsherz.luskydive.core.TextMessage
import com.jsherz.luskydive.services.DatabaseService
import com.jsherz.luskydive.util.FutureError._

import scala.concurrent.{ExecutionContext, Future}
import scalaz.\/


trait TextMessageDao {

  def all(): Future[String \/ Seq[TextMessage]]

  def get(uuid: UUID): Future[String \/ Option[TextMessage]]

  def insert(textMessage: TextMessage): Future[String \/ UUID]

  def forMember(memberUuid: UUID): Future[String \/ Seq[TextMessage]]

  def update(textMessage: TextMessage): Future[String \/ Int]

}

/**
  * Used to create and access text messages stored in the database.
  */
class TextMessageDaoImpl(protected override val databaseService: DatabaseService)
                        (implicit val ec: ExecutionContext, implicit val log: LoggingAdapter)
  extends Tables(databaseService) with TextMessageDao {

  import driver.api._

  /**
    * Get all text messages that will or have been sent, ordered by the date they were last updated, descending.
    *
    * @return Left(error) or Right(messages)
    */
  def all(): Future[String \/ Seq[TextMessage]] = {
    db.run(TextMessages.sortBy(_.updatedAt.desc).result) withServerError
  }

  /**
    * Get the text message with the given UUID.
    *
    * @param uuid
    * @return Left(error) or Right(Some(message) if found, otherwise None)
    */
  def get(uuid: UUID): Future[String \/ Option[TextMessage]] = {
    db.run(TextMessages.filter(_.uuid === uuid).result.headOption) withServerError
  }

  /**
    * Insert a new text message into the database.
    *
    * @param textMessage
    * @return
    */
  def insert(textMessage: TextMessage): Future[String \/ UUID] = {
    db.run((TextMessages returning TextMessages.map(_.uuid)) += textMessage) withServerError
  }

  /**
    * Get all text messages sent to a member.
    *
    * @param memberUuid
    * @return
    */
  def forMember(memberUuid: UUID): Future[String \/ Seq[TextMessage]] = {
    db.run(TextMessages.filter(_.memberUuid === memberUuid).result) withServerError
  }

  /**
    * Update the provided text message.
    *
    * @param textMessage Text message to update (matched on UUID)
    * @return Left(error) or Right(num rows updated)
    */
  def update(textMessage: TextMessage): Future[String \/ Int] = {
    db.run(TextMessages.filter(_.uuid === textMessage.uuid).update(textMessage)) withServerError
  }

}
