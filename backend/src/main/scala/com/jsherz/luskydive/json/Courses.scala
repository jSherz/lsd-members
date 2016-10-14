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
import com.jsherz.luskydive.core.{Course, CourseWithOrganisers}
import com.jsherz.luskydive.util.{DateJsonFormat, TimestampJsonFormat, UuidJsonFormat}
import spray.json.DefaultJsonProtocol

object CoursesJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val UuidFormat = UuidJsonFormat
  implicit val DateFormat = DateJsonFormat
  implicit val TimestampFormat = TimestampJsonFormat
  implicit val CoursesListFormat = jsonFormat2(CoursesListRequest)
  implicit val CourseOrganiserFormat = jsonFormat2(StrippedCommitteeMember)
  implicit val CourseFormat = jsonFormat5(Course)
  implicit val CourseWithOrganisersFormat = jsonFormat3(CourseWithOrganisers)
  implicit val CourseWithNumSpacesFormat = jsonFormat3(CourseWithNumSpaces)
  implicit val StrippedMemberFormat = jsonFormat4(StrippedMember)
  implicit val CourseSpaceWithMemberFormat = jsonFormat5(CourseSpaceWithMember)
  implicit val CourseCreateRequestFormat = jsonFormat4(CourseCreateRequest)
  implicit val CourseCreateResponseFormat = jsonFormat3(CourseCreateResponse)

}

/**
  * Used to show the courses between a set of dates (inclusive).
  */
case class CoursesListRequest(startDate: Date, endDate: Date)

/**
  * A course with some information about the spaces on it.
  *
  * @param course      The course
  * @param totalSpaces Course capacity
  * @param spacesFree  Number of available spaces
  */
case class CourseWithNumSpaces(course: Course, totalSpaces: Int, spacesFree: Int)

/**
  * A member stripped of most personal info.
  *
  * @param uuid
  * @param firstName
  * @param lastName
  * @param createdAt The date the member joined
  */
case class StrippedMember(uuid: Option[UUID], firstName: String, lastName: Option[String], createdAt: Timestamp)

/**
  * A course space, including stripped member.
  *
  * @param uuid
  * @param courseUuid
  * @param number
  * @param member
  */
case class CourseSpaceWithMember(uuid: Option[UUID], courseUuid: UUID, number: Int, depositPaid: Boolean,
                                 member: Option[StrippedMember])

/**
  * A request to create a new course.
  *
  * @param date
  * @param organiserUuid
  * @param secondaryOrganiserUuid
  * @param numSpaces
  */
case class CourseCreateRequest(date: Date, organiserUuid: UUID, secondaryOrganiserUuid: Option[UUID], numSpaces: Int)

/**
  * A response to a request to create a course.
  *
  * @param success Did creating the course succeed?
  * @param error   The error that caused creating the course to fail, if applicable
  * @param uuid    The created course's UUID, if successful
  */
case class CourseCreateResponse(success: Boolean, error: Option[String], uuid: Option[UUID])
