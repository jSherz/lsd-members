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
import com.jsherz.luskydive.core.TextMessage
import com.jsherz.luskydive.util.Util
import com.jsherz.luskydive.json.TextMessageJsonSupport._

import scala.concurrent.Future
import scalaz.{-\/, \/, \/-}

/**
  * Created by james on 22/09/2016.
  */
class StubTextMessageDao extends TextMessageDao {

  override def all(): Future[\/[String, Seq[TextMessage]]] = ???

  override def get(uuid: UUID): Future[\/[String, Option[TextMessage]]] = ???

  override def insert(textMessage: TextMessage): Future[\/[String, UUID]] = {
    Future.successful(\/-(textMessage.uuid.get))
  }

  override def forMember(memberUuid: UUID): Future[String \/ Seq[TextMessage]] = {
    Future.successful {
      if (StubTextMessageDao.forMemberUuid.equals(memberUuid)) {
        \/-(StubTextMessageDao.forMember)
      } else if (StubTextMessageDao.forMemberNotFoundUuid.equals(memberUuid)) {
        \/-(Seq.empty)
      } else if (StubTextMessageDao.forMemberErrorUuid.equals(memberUuid)) {
        -\/("error.internalServer")
      } else {
        throw new RuntimeException("unknown uuid used with stub")
      }
    }
  }

  override def update(textMessage: TextMessage): Future[\/[String, Int]] = ???

  override def forMassText(massTextUuid: UUID): Future[\/[String, Seq[TextMessage]]] = ???

}

object StubTextMessageDao {

  val forMemberUuid = UUID.fromString("d99d8680-296a-40db-9788-b182ce3a6935")
  val forMember = Util.fixture[Vector[TextMessage]]("for_member.json")
  val forMemberNotFoundUuid = UUID.fromString("98ab3642-923a-4d51-ba06-63c2c2981587")
  val forMemberErrorUuid = UUID.fromString("605cfaed-ce58-4449-9a4d-b4a87b206a21")

}
