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

import java.sql.Date
import java.util.UUID

import com.jsherz.luskydive.core.{Course, CourseWithOrganisers}
import com.jsherz.luskydive.json.{CourseSpaceWithMember, CourseWithNumSpaces}
import com.jsherz.luskydive.util.Util
import com.jsherz.luskydive.json.CoursesJsonSupport._

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

/**
  * A [[CourseDao]] that returns canned responses.
  */
class StubCourseDao()(implicit ec: ExecutionContext) extends CourseDao {

  /**
    * Try and find a course with the given UUID.
    *
    * @param uuid
    * @return
    */
  override def get(uuid: UUID): Future[Option[CourseWithOrganisers]] = {
    if (StubCourseDao.validCourseUuid.equals(uuid)) {
      Future.successful(Some(StubCourseDao.validCourse))
    } else if (StubCourseDao.notFoundCourseUuid.equals(uuid)) {
      Future.successful(None)
    } else {
      throw new RuntimeException("unknown uuid used with stub")
    }
  }

  /**
    * Attempt to find courses within the given dates (inclusive).
    *
    * @param startDate
    * @param endDate
    * @return
    */
  override def find(startDate: Date, endDate: Date): Future[Seq[CourseWithNumSpaces]] = {
    Future.successful(StubCourseDao.coursesWithNumSpaces)
  }

  /**
    * Get the space(s) (if any) on a course.
    *
    * @param uuid
    * @return
    */
  override def spaces(uuid: UUID): Future[Seq[CourseSpaceWithMember]] = {
    if (StubCourseDao.validCourseUuid.equals(uuid)) {
      Future.successful(StubCourseDao.validCourseSpaces)
    } else if (StubCourseDao.notFoundCourseUuid.equals(uuid)) {
      Future.successful(Seq())
    } else {
      throw new RuntimeException("unknown uuid used with stub")
    }
  }

  /**
    * Create a course on the given date and add numSpaces to it.
    *
    * @param course
    * @param numSpaces
    * @return
    */
  override def create(course: Course, numSpaces: Int): Future[String \/ UUID] = {
    Future.successful {
      if (numSpaces >= CourseSpaceDaoImpl.MIN_SPACES && numSpaces <= CourseSpaceDaoImpl.MAX_SPACES) {
        \/-(course.uuid)
      } else {
        -\/("error.invalidNumSpaces")
      }
    }
  }

}

object StubCourseDao {

  val validCourse = Util.fixture[CourseWithOrganisers]("aaf47dc8.json")

  val validCourseUuid = validCourse.course.uuid

  val validCourseSpaces = Util.fixture[Seq[CourseSpaceWithMember]]("aaf47dc8.json")

  val notFoundCourseUuid = UUID.fromString("f309d4ca-c8b2-44ac-8380-678bb7bcc3cb")

  val courses: Seq[Course] = Seq()

  val coursesWithNumSpaces: Seq[CourseWithNumSpaces] = Seq()

}
