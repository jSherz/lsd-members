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

package com.jsherz.luskydive.json

import java.sql.{Date, Timestamp}
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.jsherz.luskydive.core.{CommitteeMember, Course, CourseSpace, CourseWithOrganisers}
import com.jsherz.luskydive.util.{DateJsonFormat, TimestampJsonFormat, UuidJsonFormat}
import spray.json.DefaultJsonProtocol

object CoursesJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val UuidFormat = UuidJsonFormat
  implicit val DateFormat = DateJsonFormat
  implicit val TimestampFormat = TimestampJsonFormat
  implicit val CoursesListFormat = jsonFormat2(CoursesListRequest)
  implicit val CourseOrganiserFormat = jsonFormat2(CourseOrganiser)
  implicit val CourseFormat = jsonFormat5(Course)
  implicit val CourseWithOrganisersFormat = jsonFormat3(CourseWithOrganisers)
  implicit val CourseWithNumSpacesFormat = jsonFormat3(CourseWithNumSpaces)
  implicit val StrippedMemberFormat = jsonFormat3(StrippedMember)
  implicit val CourseSpaceWithMemberFormat = jsonFormat4(CourseSpaceWithMember)

}

/**
  * Used to show the courses between a set of dates (inclusive).
  */
case class CoursesListRequest(startDate: Date, endDate: Date)

/**
  * Stripped down [[CommitteeMember]] - used to only output important JSON.
  *
  * @param uuid
  * @param name
  */
case class CourseOrganiser(uuid: UUID, name: String)

/**
  * A course with some information about the spaces on it.
  *
  * @param course The course
  * @param totalSpaces Course capacity
  * @param spacesFree Number of available spaces
  */
case class CourseWithNumSpaces(course: Course, totalSpaces: Int, spacesFree: Int)

/**
  * A member stripped of most personal info.
  *
  * @param uuid
  * @param name
  * @param createdAt The date the member joined
  */
case class StrippedMember(uuid: Option[UUID], name: String, createdAt: Timestamp)

/**
  * A course space, including stripped member.
  *
  * @param uuid
  * @param courseUuid
  * @param number
  * @param member
  */
case class CourseSpaceWithMember(uuid: Option[UUID], courseUuid: UUID, number: Int, member: Option[StrippedMember])
