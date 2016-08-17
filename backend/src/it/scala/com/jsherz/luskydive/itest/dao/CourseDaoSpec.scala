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
import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core.{Course, CourseStatuses, CourseWithOrganisers}
import com.jsherz.luskydive.dao.{CommitteeMemberDaoImpl, CourseDao, CourseDaoImpl, CourseSpaceDaoImpl}
import com.jsherz.luskydive.itest.util.Util
import com.jsherz.luskydive.json.{CourseCreateRequest, CourseSpaceWithMember, CourseWithNumSpaces}
import com.jsherz.luskydive.itest.util.DateUtil
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures._
import com.jsherz.luskydive.json.CoursesJsonSupport._
import org.scalatest.time.{Seconds, Span}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scalaz.{-\/, \/, \/-}

class CourseDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: CourseDao = _

  override protected def beforeAll(): Unit = {
    implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)
    val dbService = Util.setupGoldTestDb()

    val committeeMemberDao = new CommitteeMemberDaoImpl(dbService)
    val courseSpaceDao = new CourseSpaceDaoImpl(dbService)
    dao = new CourseDaoImpl(dbService, committeeMemberDao, courseSpaceDao)
  }

  "CourseDao#get" should {

    "return None when course does not exist" in {
      val course = dao.get(UUID.fromString("5a535978-324c-40aa-a658-20c7eace865e"))

      course.futureValue.isDefined shouldBe false
    }

    "return the correct course with its organiser when the course does exist (one organiser)" in {
      val course = dao.get(UUID.fromString("ed89a51d-f479-475f-ab61-6903c50b8b89"))

      whenReady(course) { c =>
        c.isDefined shouldBe true
        c shouldBe Some(Util.fixture[CourseWithOrganisers]("ed89a51d.json"))
      }
    }

    "return the correct course with its organisers when the course does exist (both organisers)" in {
      val course = dao.get(UUID.fromString("ad702bb1-0eac-41d9-b146-ea794211449a"))

      whenReady(course) { c =>
        c.isDefined shouldBe true
        c shouldBe Some(Util.fixture[CourseWithOrganisers]("ad702bb1.json"))
      }
    }
  }

  "CourseDao#find" should {

    "return nothing when no courses are found on the given dates" in {
      val courses = dao.find(DateUtil.makeDate(2015, 10, 31), DateUtil.makeDate(2016, 1, 11))

      courses.futureValue shouldBe empty
    }

    "return a list of one course if only one is found" in {
      val courses = dao.find(DateUtil.makeDate(2012, 10, 11), DateUtil.makeDate(2012, 10, 11))

      val course = courses.futureValue.head

      course shouldEqual Util.fixture[CourseWithNumSpaces]("c1756c09.json")
    }

    "return all matching courses sorted by date" in {
      val courses = dao.find(DateUtil.makeDate(2010, 1, 16), DateUtil.makeDate(2010, 2, 20))

      courses.futureValue shouldEqual Util.fixture[Vector[CourseWithNumSpaces]]("2010-01-16_2010-02-20.json")
    }

  }

  "CourseDao#spaces" should {

    "return the correct spaces for a course, including the attached member (sorted by number)" in {
      val spaces = dao.spaces(UUID.fromString("ad3f289b-ce04-428d-968c-513eaf9889b0"))

      spaces.futureValue shouldBe Util.fixture[Vector[CourseSpaceWithMember]]("ad3f289b.json")
    }

    "return nothing when no spaces are linked to a course" in {
      val spaces = dao.spaces(UUID.fromString("d3f900ad-a062-4d00-be2e-c53a846b7438"))

      spaces.futureValue shouldBe empty
    }

  }

  "CourseDao#create" should {

    "return Left(error.invalidNumSpaces) when numSpaces < 1 || > 50" in {
      val zeroSpaces = createWithFixture("numSpaces_zero.json")
      val fiftyOneSpaces = createWithFixture("numSpaces_fiftyone.json")

      zeroSpaces.futureValue shouldBe -\/("error.invalidNumSpaces")
      fiftyOneSpaces.futureValue shouldBe -\/("error.invalidNumSpaces")
    }

    "return Left(error.invalidOrganiser) when the organiser doesn't exist" in {
      val req = createWithFixture("organiser_doesnt_exist.json")

      req.futureValue shouldEqual -\/("error.invalidOrganiser")
    }

    "return Left(error.invalidSecondaryOrganiser) when the secondary organiser doesn't exist" in {
      val req = createWithFixture("secondary_organiser_doesnt_exist.json")

      req.futureValue shouldEqual -\/("error.invalidSecondaryOrganiser")
    }

    "return Left(error.invalidSecondaryOrganiser) when both of the organisers don't exist" in {
      val req = createWithFixture("both_organisers_dont_exist.json")

      req.futureValue shouldEqual -\/("error.invalidOrganiser")
    }

    "add valid courses with the correct number of spaces" in {
      val examples = Util.fixture[Seq[CourseCreateRequest]]("valid_examples.json")

      implicit val patienceConfig = PatienceConfig(scaled(Span(1, Seconds)))

      examples.foreach(req => {
        // Build a course
        val uuid = Generators.randomBasedGenerator().generate()
        val course = Course(Some(uuid), req.date, req.organiserUuid, req.secondaryOrganiserUuid, CourseStatuses.PENDING)

        // Insert it with the given num spaces
        val result = dao.create(course, req.numSpaces)
        result.futureValue shouldEqual \/-(uuid)

        // Lookup the same course
        val found = dao.get(uuid)
        val foundSpaces = dao.spaces(uuid)

        val maybeSavedCourse = found.futureValue
        maybeSavedCourse.isDefined shouldEqual true

        // Verify the course data is correct
        val savedCourse = maybeSavedCourse.get
        savedCourse.course.date shouldEqual req.date
        savedCourse.course.organiserUuid shouldEqual req.organiserUuid
        savedCourse.course.secondaryOrganiserUuid shouldEqual req.secondaryOrganiserUuid
        savedCourse.course.status shouldEqual CourseStatuses.PENDING

        // Verify the correct number of spaces were added
        val actualNumSpaces = foundSpaces.futureValue.length
        actualNumSpaces shouldEqual req.numSpaces
      })
    }

  }

  /**
    * Create a course with the given fixture's data.
    *
    * @return
    */
  private def createWithFixture(fixture: String): Future[String \/ UUID] = {
    val req = Util.fixture[CourseCreateRequest](fixture)
    val uuid = Generators.randomBasedGenerator().generate()
    val course = Course(Some(uuid), req.date, req.organiserUuid, req.secondaryOrganiserUuid, CourseStatuses.PENDING)

    dao.create(course, req.numSpaces)
  }

}
