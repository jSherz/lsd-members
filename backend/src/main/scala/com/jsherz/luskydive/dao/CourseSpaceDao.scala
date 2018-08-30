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

import akka.event.LoggingAdapter
import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core.CourseSpace
import com.jsherz.luskydive.services.DatabaseService
import com.jsherz.luskydive.util.Errors
import com.jsherz.luskydive.util.FutureError

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}

/**
  * The available slots on a course.
  */
trait CourseSpaceDao {

  /**
    * Create the given number of spaces and attach them to a course.
    *
    * Assumes the course does not already have spaces attached.
    *
    * @param courseUuid
    * @param numSpaces
    */
  def createForCourse(courseUuid: UUID, numSpaces: Integer): Future[String \/ Unit]

  /**
    * Add a member to this course space. Will fail if the space already has a member.
    *
    * @param spaceUuid
    * @param memberUuid
    * @return Error or UUID of the member that was added
    */
  def addMember(spaceUuid: UUID, memberUuid: UUID): Future[String \/ UUID]

  /**
    * Removes the provided member from this course space. Will fail if the provided member isn't on that space.
    *
    * @param spaceUuid
    * @param memberUuid
    * @return Error or UUID of the member that was removed
    */
  def removeMember(spaceUuid: UUID, memberUuid: UUID): Future[String \/ UUID]

  /**
    * Sets the deposit on a space to be paid or unpaid.
    *
    * @param spaceUuid
    * @param depositPaid
    * @return
    */
  def setDepositPaid(spaceUuid: UUID, depositPaid: Boolean): Future[String \/ Int]

}

/**
  * Stores and retrieves course spaces from the database.
  */
class CourseSpaceDaoImpl(protected override val databaseService: DatabaseService)
                        (implicit val ec: ExecutionContext, implicit val log: LoggingAdapter)
  extends Tables(databaseService) with CourseSpaceDao {

  import driver.api._

  /**
    * Create the given number of spaces and attach them to a course.
    *
    * Assumes the course does not already have spaces attached.
    *
    * @param courseUuid
    * @param numSpaces
    */
  override def createForCourse(courseUuid: UUID, numSpaces: Integer): Future[String \/ Unit] = {
    if (numSpaces >= CourseSpaceDaoImpl.MIN_SPACES && numSpaces <= CourseSpaceDaoImpl.MAX_SPACES) {
      // Prepare a DBIO action to add the spaces
      val createSpacesAction = DBIO.sequence(
        (1 to numSpaces).map { number =>
          val uuid = Some(Generators.randomBasedGenerator().generate())

          CourseSpaces += CourseSpace(uuid, courseUuid, number, None, depositPaid = false)
        }
      )

      (
        for {
          existingSpaces <- db.run(CourseSpaces.filter(_.courseUuid === courseUuid).length.result)
          if existingSpaces == 0
          createResult <- db.run(createSpacesAction).map(_ => ())
        } yield \/-(createResult)
        ).recover {
        case _: NoSuchElementException => -\/(CourseSpaceDaoErrors.courseAlreadySetup)
        case _ => -\/(Errors.internalServer)
      }
    } else {
      Future.successful(-\/(CourseSpaceDaoErrors.invalidNumSpaces))
    }
  }

  /**
    * Add a member to this course space. Will fail if the space already has a member.
    *
    * @param uuid
    * @param memberUuid
    * @return Error or UUID of the member that was added
    */
  override def addMember(uuid: UUID, memberUuid: UUID): Future[String \/ UUID] = {
    get(uuid).flatMap {
      case Some(space) => {
        // Check the space isn't already full
        if (space.memberUuid.isDefined) {
          Future.successful(-\/(CourseSpaceDaoErrors.spaceNotEmpty))
        } else {
          // Ensure the member isn't already on this course
          val courseLookup = db.run(CourseSpaces.filter(foundSpace =>
            foundSpace.courseUuid === space.courseUuid &&
              foundSpace.memberUuid === memberUuid
          ).result.headOption)

          courseLookup.flatMap {
            case Some(_: CourseSpace) => Future.successful(-\/(CourseSpaceDaoErrors.alreadyOnCourse))
            case _ => {
              db.run(CourseSpaces.filter(_.uuid === uuid)
                .map(_.memberUuid)
                .update(Some(memberUuid))
              ).map(_ => \/-(uuid))
            }
          }
        }
      }
      case None => Future.successful(-\/(CourseSpaceDaoErrors.unknownSpace))
    }
  }

  /**
    * Removes the provided member from this course space. Will fail if the provided member isn't on that space.
    *
    * @param uuid
    * @param memberUuid
    * @return Error or UUID of the member that was removed
    */
  override def removeMember(uuid: UUID, memberUuid: UUID): Future[String \/ UUID] = {
    get(uuid).flatMap {
      case Some(space) => {
        // Check space has the correct member
        if (space.memberUuid.contains(memberUuid)) {
          db.run(CourseSpaces.filter(_.uuid === uuid)
            .map(space => (space.memberUuid, space.depositPaid))
            .update(None, false) // Deposit can't be paid with no-one in space
          ).map(_ => \/-(uuid))
        } else {
          Future.successful(-\/(CourseSpaceDaoErrors.memberNotInSpace))
        }
      }
      case None => Future.successful(-\/(CourseSpaceDaoErrors.unknownSpace))
    }
  }

  /**
    * Lookup a space by UUID. Private as no tests for it yet.
    *
    * @param uuid
    * @return
    */
  private def get(uuid: UUID): Future[Option[CourseSpace]] = {
    db.run(CourseSpaces.filter(_.uuid === uuid).result.headOption)
  }

  /**
    * Sets the deposit on a space to be paid or unpaid.
    *
    * @param spaceUuid
    * @param depositPaid
    * @return
    */
  override def setDepositPaid(spaceUuid: UUID, depositPaid: Boolean): Future[String \/ Int] = {
    new FutureError(db.run(
      CourseSpaces.filter(_.uuid === spaceUuid)
        .map(_.depositPaid)
        .update(depositPaid)
    )) withServerError
  }

}

object CourseSpaceDaoImpl {

  /**
    * Minimum number of spaces that a course can have.
    */
  val MIN_SPACES: Integer = 1

  /**
    * Maximum number of spaces that a course can have.
    */
  val MAX_SPACES: Integer = 50

}

object CourseSpaceDaoErrors {

  val internalServer = "error.internalServer"

  val invalidNumSpaces = "error.invalidNumSpaces"

  val courseAlreadySetup = "error.courseAlreadySetup"

  val unknownSpace = "error.unknownSpace"

  val spaceNotEmpty = "error.spaceNotEmpty"

  val alreadyOnCourse = "error.alreadyOnCourse"

  val memberNotInSpace = "error.memberNotInSpace"

}
