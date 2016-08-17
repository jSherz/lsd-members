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

package com.jsherz.luskydive.itest.dao

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util.Util
import com.jsherz.luskydive.itest.util.Util._
import com.jsherz.luskydive.services.DatabaseService
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures._

import scala.io.Source
import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.-\/

/**
  * Ensures the course space management functions correctly.
  */
class CourseSpaceDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dbService: DatabaseService = _
  private var dao: CourseSpaceDao = _
  private var courseDao: CourseDao = _

  override protected def beforeAll(): Unit = {
    implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)
    dbService = Util.setupGoldTestDb()

    dao = new CourseSpaceDaoImpl(dbService)
    courseDao = new CourseDaoImpl(dbService, new CommitteeMemberDaoImpl(dbService), dao)
  }

  "CourseSpaceDao#createForCourse" should {

    "return Left(error.invalidNumSpaces) if the number of spaces is outside of the allowed values" in {
      val testCases = Seq(
        -2141, -1, 0, 51, 52, 112391
      )

      testCases.foreach { numSpaces =>
        val result = dao.createForCourse(UUID.fromString("95ae2682-1f03-4a88-9a0e-2761b6a32141"), numSpaces)

        result.futureValue shouldEqual -\/("error.invalidNumSpaces")
      }
    }

    "return Left(error.courseAlreadySetup) if the course already has spaces" in {
      val numSpaces = 5
      val result = dao.createForCourse(UUID.fromString("3594633d-5ea8-46a9-b83e-021de2f8862f"), numSpaces)

      result.futureValue shouldEqual -\/("error.courseAlreadySetup")
    }

    "add the correct number of course spaces with no members attached" in {
      val testCases = Map(
        "ef5c13a0-8fa4-4fcd-91c3-48942ae95d06" -> 8,
        "8bc6a2e3-a403-4630-9ad2-90b3d9fa1f4b" -> 10,
        "019b7077-b994-42be-924c-015275d44b55" -> 1,
        "f920c658-62fa-44c2-aec7-2c46182ca9f3" -> 20,
        "c1c6f7ee-424f-4672-8741-2483cf83c6f4" -> 50,
        "5f83767e-96db-4c7d-a221-a38288307481" -> 7
      )

      // Create some courses with no spaces for testing
      val emptyCoursesSql = Source.fromURL(getClass.getResource("/fixtures/empty-courses.sql")).mkString

      dbService
        .db
        .createSession
        .conn
        .prepareStatement(emptyCoursesSql)
        .execute()

      // Ensure we can add spaces to each test case
      testCases.foreach {
        case (courseUuid, numSpacesToCreate) => {
          val uuid = UUID.fromString(courseUuid)

          dao.createForCourse(uuid, numSpacesToCreate).futureValue

          courseDao.spaces(uuid).futureValue.length shouldEqual numSpacesToCreate
        }
      }
    }

  }

}
