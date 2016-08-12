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
import java.sql.Date
import java.util.UUID

import com.jsherz.luskydive.core.{Course, CourseSpace, CourseWithOrganisers}
import com.jsherz.luskydive.fixtures.CoursesWithOrganisers
import com.jsherz.luskydive.json.{CourseSpaceWithMember, CourseWithNumSpaces}

import scala.concurrent.{ExecutionContext, Future}

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
      Future(Some(CoursesWithOrganisers.courseWithOrganisersA))
    } else if (StubCourseDao.notFoundCourseUuid.equals(uuid)) {
      Future(None)
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
  override def find(startDate: Date, endDate: Date): Future[Seq[CourseWithNumSpaces]] =
    Future(StubCourseDao.coursesWithNumSpaces)

  /**
    * Get the space(s) (if any) on a course.
    *
    * @param uuid
    * @return
    */
  override def spaces(uuid: UUID): Future[Seq[CourseSpaceWithMember]] = Future(Seq())

}

object StubCourseDao {

  val validCourseUuid = CoursesWithOrganisers.courseWithOrganisersA.course.uuid.get

  val validCourse = CoursesWithOrganisers.courseWithOrganisersA

  val notFoundCourseUuid = UUID.fromString("f309d4ca-c8b2-44ac-8380-678bb7bcc3cb")

  val courses: Seq[Course] = Seq()

  val coursesWithNumSpaces: Seq[CourseWithNumSpaces] = Seq()

}
