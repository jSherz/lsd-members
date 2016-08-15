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
import com.jsherz.luskydive.core.CourseSpace
import com.jsherz.luskydive.services.DatabaseService

import scala.concurrent.{ExecutionContext, Future}

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
  def createForCourse(courseUuid: UUID, numSpaces: Integer): Future[Unit]

}

/**
  * Stores and retrieves course spaces from the database.
  */
class CourseSpaceDaoImpl(protected override val databaseService: DatabaseService)(implicit val ec: ExecutionContext)
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
  override def createForCourse(courseUuid: UUID, numSpaces: Integer): Future[Unit] = {
    if (numSpaces >= CourseSpaceDaoImpl.MIN_SPACES && numSpaces <= CourseSpaceDaoImpl.MAX_SPACES) {
      db.run(DBIO.sequence(
        (1 to numSpaces).map { number =>
          val uuid = Some(Generators.randomBasedGenerator().generate())

          CourseSpaces += CourseSpace(uuid, courseUuid, number, None)
        }
      )).map(_ => ())
    } else {
      throw new IllegalArgumentException(s"Invalid number of spaces, must be between ${CourseSpaceDaoImpl.MIN_SPACES} " +
        s"and ${CourseSpaceDaoImpl.MAX_SPACES}")
    }
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
