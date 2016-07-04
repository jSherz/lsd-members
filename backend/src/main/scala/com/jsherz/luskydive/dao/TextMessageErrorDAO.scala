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

import com.jsherz.luskydive.models.{TextMessage, TextMessageError}
import slick.backend.DatabaseConfig
import slick.driver.JdbcProfile

import scala.concurrent.Future

/**
  * Used to create and access text message errors stored in the database.
  */
class TextMessageErrorDAO(override protected val dbConfig: DatabaseConfig[JdbcProfile]) extends Tables(dbConfig) {
  import driver.api._

  /**
    * Get all of the text message errors that have been recorded.
    *
    * @return
    */
  def all(): Future[Seq[TextMessageError]] = db.run(TextMessageErrors.result)

  /**
    * Get the text message error with the given ID.
    *
    * @param id ID to search with
    * @return Some(error) if found, otherwise None
    */
  def get(id: Int): Future[Option[TextMessageError]] = db.run(TextMessageErrors.filter(_.id === id).result.headOption)

  /**
    * Insert a new text message error into the database.
    *
    * @param textMessageError Text message error to add
    * @return
    */
  def insert(textMessageError: TextMessageError): Future[Int] =
    db.run((TextMessageErrors returning TextMessageErrors.map(_.id)) += textMessageError)

  /**
    * Get all errors for a text message.
    *
    * @param textMessage Message used to find errors
    * @return
    */
  def forTextMessage(textMessage: TextMessage): Future[Seq[TextMessageError]] =
    db.run(TextMessageErrors.filter(_.textMessageId === textMessage.id).result)

  /**
    * Deletes all errors. Only used for testing.
    *
    * @return
    */
  def empty(): Future[Int] = db.run(TextMessageErrors.delete)
}
