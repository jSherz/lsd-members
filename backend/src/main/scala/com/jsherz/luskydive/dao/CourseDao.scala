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
import com.jsherz.luskydive.json.{CourseOrganiser, CourseSpaceWithMember, CourseWithNumSpaces, StrippedMember}
import com.jsherz.luskydive.services.DatabaseService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Access to courses.
  */
trait CourseDao {

  /**
    * Try and find a course with the given UUID.
    *
    * @param uuid
    * @return
    */
  def get(uuid: UUID): Future[Option[CourseWithOrganisers]]

  /**
    * Attempt to find courses within the given dates (inclusive).
    *
    * @param startDate
    * @param endDate
    * @return
    */
  def find(startDate: Date, endDate: Date): Future[Seq[CourseWithNumSpaces]]

  /**
    * Get the space(s) (if any) on a course.
    *
    * @param uuid
    * @return
    */
  def spaces(uuid: UUID): Future[Seq[CourseSpaceWithMember]]

}

/**
  * Database backed access to courses.
  *
  * @param databaseService
  * @param ec
  */
class CourseDaoImpl(protected override val databaseService: DatabaseService)(implicit val ec: ExecutionContext)
  extends Tables(databaseService) with CourseDao {

  import driver.api._

  /**
    * Try and find a course with the given UUID, including finding the primary and secondary organisers.
    *
    * @param uuid
    * @return
    */
  override def get(uuid: UUID): Future[Option[CourseWithOrganisers]] = {
    val courseLookup = for {
      ((course, organiser), secondaryOrganiser) <-
        Courses.filter(_.uuid === uuid) join
          CommitteeMembers on (_.organiserUuid === _.uuid) joinLeft
          CommitteeMembers on (_._1.secondaryOrganiserUuid === _.uuid)
    } yield (
      course,
      (organiser.uuid, organiser.name),
      secondaryOrganiser.map(so => (so.uuid, so.name))
    )

    db.run(courseLookup.result.headOption).map(_.map {
      case (course, org, secOrg) => assembleCourse(course, org, secOrg)
    })
  }

  /**
    * Attempt to find courses within the given dates (inclusive).
    * @param startDate
    * @param endDate
    * @return
    */
  override def find(startDate: Date, endDate: Date): Future[Seq[CourseWithNumSpaces]] = {
    // Find the course, joining in spaces
    val lookup = (
      for {
        course <- Courses if course.date >= startDate && course.date <= endDate
        spaces <- CourseSpaces if course.uuid === spaces.courseUuid
      } yield (course, spaces)
    ).sortBy(_._1.date).groupBy(_._1)

    // Pick out the course, total # spaces & spaces that are free
    val transform = lookup.map {
      case (course, spaces) => (
        course,
        spaces.length,
        spaces.length - spaces.map(_._2.memberUuid).countDefined // calc num free
      )
    }

    db.run(transform.result.map(_.map(CourseWithNumSpaces.tupled(_))))
  }

  /**
    * Get the space(s) (if any) on a course.
    *
    * @param uuid
    * @return
    */
  override def spaces(uuid: UUID): Future[Seq[CourseSpaceWithMember]] = {
    val query = for {
      (space, member) <-
        CourseSpaces.filter(_.courseUuid === uuid) joinLeft
          Members on (_.memberUuid === _.uuid)
    } yield (space, member)

    db.run(query.sortBy(_._1.number).result).map {
      _.map {
        case (space, member) =>
          CourseSpaceWithMember(
            space.uuid, space.courseUuid, space.number,
            member.map(x => StrippedMember(x.uuid, x.name, x.createdAt))
          )
      }
    }
  }

  /**
    * Turn the raw returned course and organiser information into a class ready for serialization.
    *
    * @param course
    * @param organiser
    * @param secondaryOrganiser
    * @return
    */
  private def assembleCourse(course: Course, organiser: (UUID, String), secondaryOrganiser: Option[(UUID, String)]): CourseWithOrganisers = {
    CourseWithOrganisers(
      course,
      CourseOrganiser.tupled(organiser),
      secondaryOrganiser.map(x => CourseOrganiser.tupled(x))
    )
  }

}
