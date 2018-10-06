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

import scalaz.{-\/, \/, \/-}

import scala.concurrent.Future

/**
  * Fake course space DAO.
  */
class StubCourseSpaceDao extends CourseSpaceDao {

  /**
    * Create the given number of spaces and attach them to a course.
    *
    * Assumes the course does not already have spaces attached.
    *
    * @param courseUuid
    * @param numSpaces
    */
  override def createForCourse(courseUuid: UUID, numSpaces: Integer): Future[\/[String, Unit]] = ???

  /**
    * Add a member to this course space. Will fail if the space already has a member.
    *
    * @param spaceUuid
    * @param memberUuid
    * @return Error or UUID of the member that was added
    */
  override def addMember(spaceUuid: UUID, memberUuid: UUID): Future[\/[String, UUID]] = {
    fakeImpl(spaceUuid)
  }

  /**
    * Removes the provided member from this course space. Will fail if the provided member isn't on that space.
    *
    * @param spaceUuid
    * @param memberUuid
    * @return Error or UUID of the member that was removed
    */
  override def removeMember(spaceUuid: UUID, memberUuid: UUID): Future[\/[String, UUID]] = {
    fakeImpl(spaceUuid)
  }

  private def fakeImpl(spaceUuid: UUID): Future[\/[String, UUID]] = {
    if (StubCourseSpaceDao.validSpaceUuid.equals(spaceUuid)) {
      Future.successful(\/-(spaceUuid))
    } else if (StubCourseSpaceDao.invalidSpaceUuid.equals(spaceUuid)) {
      Future.successful(-\/(CourseSpaceDaoErrors.unknownSpace))
    } else {
      throw new RuntimeException(s"unknown UUID $spaceUuid used with stub")
    }
  }

  /**
    * Sets the deposit on a space to be paid or unpaid.
    *
    * @param spaceUuid
    * @param depositPaid
    * @return
    */
  override def setDepositPaid(spaceUuid: UUID, depositPaid: Boolean): Future[Int] = {
    Future.successful {
      if (StubCourseSpaceDao.setDepositPaidValidUuid.equals(spaceUuid)) {
        1
      } else if (StubCourseSpaceDao.setDepositPaidNotFoundUuid.equals(spaceUuid)) {
        0
      } else {
        throw new RuntimeException(s"unknown uuid $spaceUuid used with stub")
      }
    }
  }

}

object StubCourseSpaceDao {

  val validSpaceUuid = UUID.fromString("a7dabd00-8f84-4ec3-b99f-de9ab95498ae")

  val invalidSpaceUuid = UUID.fromString("f17f83d6-c8a2-4748-bda7-2e925ca11e52")

  val setDepositPaidValidUuid = UUID.fromString("205f67c0-ef8d-46dd-ae51-062c65b1a0a2")
  val setDepositPaidNotFoundUuid = UUID.fromString("4efc3e26-6565-493d-94f0-eaba00a19043")
  val setDepositPaidErrorUuid = UUID.fromString("1348bc47-975c-45fc-a1b2-80b7ae9786b9")

}
