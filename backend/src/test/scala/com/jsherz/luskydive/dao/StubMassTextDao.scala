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

import com.jsherz.luskydive.core.MassText
import com.jsherz.luskydive.util.{DateUtil, Errors}
import scalaz.{-\/, \/, \/-}

import scala.concurrent.Future


class StubMassTextDao extends MassTextDao {

  /**
    * Get the mass text with the given UUID.
    *
    * @param uuid
    * @return
    */
  override def get(uuid: UUID): Future[Option[MassText]] = {
    Future.successful {
      if (StubMassTextDao.existsMassTextUuid.equals(uuid)) {
        Some(MassText(
            UUID.fromString("b1266b65-40b2-4874-a551-854bf2e2ef26"),
            UUID.fromString("2c7eb9b0-65bb-4c5c-b9f4-7194659fed2c"),
            "Roll up, roll up {{ name }}, it's time to get yourself down to our skydiving G.I.A.G - meet you there :D " +
              "#excited #skydiving - Reply \"NOFUN\" to stop these messages.",
            Timestamp.valueOf("2012-09-25 00:00:00.000000")
          ))
      } else if (StubMassTextDao.unknownMassTextUuid.equals(uuid)) {
        None
      } else {
        throw new RuntimeException(s"unknown uuid $uuid used with stub")
      }
    }
  }

  /**
    * Get the number of members that joined between the given dates.
    *
    * @param startDate Start date (inclusive)
    * @param endDate   End date (exclusive)
    * @return
    */
  override def filterCount(startDate: Date, endDate: Date): String \/ Future[Int] = {
    if (StubMassTextDao.validStartDate.equals(startDate) && StubMassTextDao.validEndDate.equals(endDate)) {
      \/-(Future.successful(StubMassTextDao.validFilterCount))
    } else if (StubMassTextDao.serverErrorStartDate.equals(startDate) && StubMassTextDao.serverErrorEndDate.equals(endDate)) {
      -\/(Errors.internalServer)
    } else {
      throw new RuntimeException("unknown dates used with stub")
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
    * @param createdAt The time that the mass text was created
    * @return UUID of created mass text
    */
  override def send(sender: UUID, startDate: Date, endDate: Date, template: String, createdAt: Timestamp): Future[String \/ UUID] = {
    if (StubMassTextDao.validSenderTemplate.equals(template)) {
      Future.successful(\/-(StubMassTextDao.validCreatedUuid))
    } else if (StubMassTextDao.serverErrorTemplate.equals(template)) {
      Future.successful(-\/(MassTextDaoErrors.noMembersMatched))
    } else {
      throw new RuntimeException("unknown template used with stub")
    }
  }

}

object StubMassTextDao {

  val existsMassTextUuid = UUID.fromString("b1266b65-40b2-4874-a551-854bf2e2ef26")
  val unknownMassTextUuid = UUID.fromString("37360e5d-d443-47cc-a367-49e9519e1a8a")
  val serverErrorMassTextUuid = UUID.fromString("c1ff0cbb-2d96-4d47-b026-fffa89d968a2")

  val validStartDate = DateUtil.makeDate(2016, 8, 1)
  val validEndDate = DateUtil.makeDate(2016, 9, 1)
  val validFilterCount = 14811

  val serverErrorStartDate = DateUtil.makeDate(2015, 9, 1)
  val serverErrorEndDate = DateUtil.makeDate(2015, 10, 1)

  val validSenderTemplate = "Hello, {{ name }}!"
  val serverErrorTemplate = "What is the meaning of life, {{ name }}?"
  val validCreatedUuid = UUID.fromString("c754de69-d62b-44a8-bc41-db7b78d49eca")

}
