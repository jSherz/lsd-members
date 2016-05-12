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

import models.{Member, TextMessage}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Used to create and access text messages stored in the database.
  */
class TextMessageDAO @Inject() (override protected val dbConfigProvider: DatabaseConfigProvider) extends Tables(dbConfigProvider) {
  import driver.api._

  /**
    * Get all text messages that will or have been sent.
    *
    * @return
    */
  def all(): Future[Seq[TextMessage]] = db.run(TextMessages.result)

  /**
    * Get the text message with the given ID.
    *
    * @param id ID to search with
    * @return Some(message) if found, otherwise None
    */
  def get(id: Int): Future[Option[TextMessage]] = db.run(TextMessages.filter(_.id === id).result.headOption)

  /**
    * Insert a new text message into the database.
    *
    * @param textMessage Text message to add
    * @return
    */
  def insert(textMessage: TextMessage): Future[Unit] = db.run(TextMessages += textMessage).map { _ => () }

  /**
    * Get all text messages sent to a member.
    *
    * @param member Member to lookup messages by
    * @return
    */
  def forMember(member: Member): Future[Seq[TextMessage]] = db.run(TextMessages.filter(_.memberId === member.id).result)

  /**
    * Update the provided text message.
    *
    * @param textMessage Text message to update
    * @return
    */
  def update(textMessage: TextMessage): Future[Int] = db.run(TextMessages.filter(_.id === textMessage.id).update(textMessage))
}
