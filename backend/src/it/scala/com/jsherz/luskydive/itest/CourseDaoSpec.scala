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

package com.jsherz.luskydive.itest

import java.sql.{Date, Timestamp}
import java.util.UUID

import com.jsherz.luskydive.core.{CommitteeMember, Course, CourseWithOrganisers}
import com.jsherz.luskydive.dao.{CourseDAO, CourseDAOImpl, StubCourseDao}
import com.jsherz.luskydive.json.CourseOrganiser
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global
import org.scalatest.concurrent.ScalaFutures._

class CourseDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: CourseDAO = new StubCourseDao() // Used only to setup dao variable - real version used later

  override protected def beforeAll(): Unit = {
    val dbService = Util.setupGoldTestDb()

    dao = new CourseDAOImpl(databaseService = dbService)
  }

  "CourseDao" should {

    "return None when course does not exist" in {
      val course = dao.get(UUID.fromString("5a535978-324c-40aa-a658-20c7eace865e"))

      course.futureValue.isDefined shouldBe false
    }

    "return the correct course with its organiser when the course does exist (one organiser)" in {
      val course = dao.get(UUID.fromString("ed89a51d-f479-475f-ab61-6903c50b8b89"))

      whenReady(course) { c =>
        c.isDefined shouldBe true

        c shouldBe Some(CourseWithOrganisers(
          Course(
            Some(UUID.fromString("ed89a51d-f479-475f-ab61-6903c50b8b89")),
            Date.valueOf("2015-10-25"),
            UUID.fromString("18cb4209-df83-4202-94fb-e6a2f7f92c8d"),
            None,
            1),

          CourseOrganiser(UUID.fromString("18cb4209-df83-4202-94fb-e6a2f7f92c8d"), "Benjamin Johnson"),

          None
        ))
      }
    }

    "return the correct course with its organisers when the course does exist (both organisers)" in {
      val course = dao.get(UUID.fromString("ad702bb1-0eac-41d9-b146-ea794211449a"))

      whenReady(course) { c =>
        c.isDefined shouldBe true

        c shouldBe Some(CourseWithOrganisers(
          Course(
            Some(UUID.fromString("ad702bb1-0eac-41d9-b146-ea794211449a")),
            Date.valueOf("2009-10-16"),
            UUID.fromString("2bb4ccd7-927a-4e5d-9456-40e5dcee3d34"),
            Some(UUID.fromString("756bf336-e3c7-47d3-bd14-00dbfff302cf")),
            1),

          CourseOrganiser(UUID.fromString("2bb4ccd7-927a-4e5d-9456-40e5dcee3d34"), "Valerie Barker MD"),

          Some(CourseOrganiser(UUID.fromString("756bf336-e3c7-47d3-bd14-00dbfff302cf"), "Jessica Schmidt"))
        ))
      }
    }

    // find -> return Seq() when no courses exist between given dates

    // find -> return Seq(course) when one course is found

    // find -> return Seq(course, course) when multiple courses are found

    // spaces -> return correct spaces, including member information (if present)

    // spaces -> return Seq() for unknown course uuid

  }

}
