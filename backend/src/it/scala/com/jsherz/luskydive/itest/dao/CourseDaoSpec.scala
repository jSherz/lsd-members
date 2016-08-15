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

import com.jsherz.luskydive.core.CourseWithOrganisers
import com.jsherz.luskydive.dao.{CourseDao, CourseDaoImpl}
import com.jsherz.luskydive.itest.util.Util
import com.jsherz.luskydive.json.{CourseCreateRequest, CourseSpaceWithMember, CourseWithNumSpaces}
import com.jsherz.luskydive.itest.util.DateUtil
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures._
import com.jsherz.luskydive.json.CoursesJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CourseDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: CourseDao = _

  override protected def beforeAll(): Unit = {
    val dbService = Util.setupGoldTestDb()

    dao = new CourseDaoImpl(databaseService = dbService)
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

      zeroSpaces.futureValue shouldEqual Left("error.invalidNumSpaces")
      fiftyOneSpaces.futureValue shouldEqual Left("error.invalidNumSpaces")
    }

    "return Left(error.invalidOrganiser) when one of the organisers (or both) doesn't exist" in {
      val organiserDoesntExist = createWithFixture("organiser_doesnt_exist.json")
      val secondaryOrganiserDoesntExist = createWithFixture("secondary_organiser_does_exist.json")
      val bothOrganisersDontExist = createWithFixture("both_organisers_dont_exist.json")

      Seq(organiserDoesntExist, secondaryOrganiserDoesntExist, bothOrganisersDontExist).foreach(req =>
        req.futureValue shouldEqual Left("error.invalidOrganiser")
      )
    }

    "return Right(UUID) when a course is created successfully" in {
      val examples = Util.fixture[Seq[CourseCreateRequest]]("valid_examples.json")

      examples.foreach(req => {
        val result = dao.create(req.date, req.organiserUuid, req.secondaryOrganiserUuid, req.numSpaces)
        result.futureValue shouldBe 'right

        val uuid = result.futureValue.right.get
        val found = dao.get(uuid)
        val foundSpaces = dao.spaces(uuid)

        val maybeSavedCourse = found.futureValue
        maybeSavedCourse.isDefined shouldEqual true

        val savedCourse = maybeSavedCourse.get
        savedCourse.course.date shouldEqual req.date
        savedCourse.course.organiserUuid shouldEqual req.organiserUuid
        savedCourse.course.secondaryOrganiserUuid shouldEqual req.secondaryOrganiserUuid

        val actualNumSpaces = foundSpaces.futureValue.length
        actualNumSpaces shouldEqual numSpaces
      })
    }

  }

  /**
    * Create a course with the given fixture's data.
    *
    * @return
    */
  private def createWithFixture(fixture: String): Future[Either[String, UUID]] = {
    val req = Util.fixture[CourseCreateRequest](fixture)
    dao.create(req.date, req.organiserUuid, req.secondaryOrganiserUuid, req.numSpaces)
  }

}
