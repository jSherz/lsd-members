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
import scalaz.{-\/, \/-}

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

  "CourseSpaceDao#addMember" should {

    "return Left(error.unknownSpace) if the space doesn't exist" in {
      val invalidSpaces = Map(
        // Fake space -> valid member to try
        "7897f19d-475f-4c35-b433-0b0d0cfa1b83" -> "163a4596-adc9-4585-a9e3-c779bf25ad76",
        "ca86bf9a-8849-49f5-b40b-0e682b800d04" -> "2fd251e7-5be1-45a5-a4a9-bded6907b530",
        "a0dbe7f0-6a43-4252-b016-f45bed3a2748" -> "94a340d7-8058-4205-8e78-c734089fa237"
      )

      invalidSpaces.foreach { case (spaceUuid, memberUuid) =>
        val result = dao.addMember(UUID.fromString(spaceUuid), UUID.fromString(memberUuid))

        result.futureValue shouldEqual -\/("error.unknownSpace")
      }
    }

    "return Left(error.spaceNotEmpty) if a member is already occupying that space" in {
      val spacesAlreadyFilled = Map(
        // Filled UUID -> valid member UUID to try
        "0eec2389-9ac6-45c5-bcd8-7d8a7e057111" -> "6b5d1788-fcf7-4bc9-abcc-8b8f2bb6e55e",
        "7ebd6ec6-ce5d-4b07-9cc9-6a91b436a8fb" -> "e842ed32-b12a-4dca-88e7-28e618db6a57",
        "9df90473-9a27-464b-9727-ee7e5e4c5d2a" -> "93aca909-2020-45e3-b634-384b48f51b8d"
      )

      spacesAlreadyFilled.foreach { case (filledSpaceUuid, validMemberUuid) =>
        val result = dao.addMember(UUID.fromString(filledSpaceUuid), UUID.fromString(validMemberUuid))

        result.futureValue shouldEqual -\/("error.spaceNotEmpty")
      }
    }

    "return Left(error.alreadyOnCourse) if a member is already on that same course" in {
      val spacesWithMembers = Map(
        // Space UUID -> valid member already on course UUID
        "660280dd-a091-4f14-87a4-3849cea3b9c9" -> "69ed5ee1-5be2-48ec-81db-2de28fdf250e",
        "979b38af-9195-4019-b70a-ed72110f2567" -> "670bc55a-2a41-4977-8b8d-283cfa592299",
        "b551991b-74cc-4873-9c00-b77960fd1a8b" -> "dc5a6d34-6bed-448d-96f4-dc4735275776"
      )

      spacesWithMembers.foreach { case (spaceUuid, memberUuid) =>
        val result = dao.addMember(UUID.fromString(spaceUuid), UUID.fromString(memberUuid))

        result.futureValue shouldEqual -\/("error.alreadyOnCourse")
      }
    }

    "add a member to a space correctly" in {
      val spacesWithoutMembers = Map(
        // Space UUID -> valid member UUID
        "021a24eb-99b7-4f12-9f71-81ef9f07ac4d" -> "52baae26-20fe-427e-a733-154011e57db4",
        "f14e20f1-7a32-435b-8f04-9db7394a565d" -> "5da87618-b3cc-45fd-ba1d-cdf644499a93",
        "675b60eb-300b-441d-a814-9b4089576c2c" -> "fe437cee-983d-4ce3-9ed1-1690f2c5190f"
      )

      spacesWithoutMembers.foreach { case (spaceUuid, memberUuid) =>
        val result = dao.addMember(UUID.fromString(spaceUuid), UUID.fromString(memberUuid))

        result.futureValue shouldEqual \/-(UUID.fromString(spaceUuid))
      }
    }

  }

  "CourseSpaceDao#removeMember" should {

    "return Left(error.unknownSpace) if the space doesn't exist" in {
      val invalidSpaces = Map(
        // Fake space -> valid member to try
        "5d7d7847-5cf2-4110-9647-ac8d9ed5da40" -> "2c055147-6114-40aa-80bf-11530d621ee1",
        "e1450cff-e8e5-46b7-a771-3fc40fabafdf" -> "d7174ab2-d762-4ef3-9cb3-a44341338ce5",
        "37a811bd-d768-4200-875a-56ccd9a0e2f2" -> "cfec0f35-cfce-4fa6-ac24-1754c738a009"
      )

      invalidSpaces.foreach { case (spaceUuid, memberUuid) =>
        val result = dao.removeMember(UUID.fromString(spaceUuid), UUID.fromString(memberUuid))

        result.futureValue shouldEqual -\/("error.unknownSpace")
      }
    }

    "return Left(error.memberNotInSpace) if the member isn't in the provided space" in {
      val noMemberInSpaceResult =
        dao.removeMember(UUID.fromString("3b1056e8-e759-4c0c-9842-5d053b65b600"), UUID.fromString("7867a789-fc07-471d-a2c9-e0b3dcd39ad5"))
      val anotherMemberInSpaceResult =
        dao.removeMember(UUID.fromString("05ad2a39-e8fd-466d-ad2a-4d12ee33cb26"), UUID.fromString("56f4b4a7-f526-40f8-b718-994a92126d8e"))

      noMemberInSpaceResult.futureValue shouldEqual -\/("error.memberNotInSpace")
      anotherMemberInSpaceResult.futureValue shouldEqual -\/("error.memberNotInSpace")
    }

    "remove a member from a space correctly" in {
      val validExamples = Map(
        // Course UUID, Space UUID -> member UUID
        ("e29668eb-e8e3-44b0-b9ca-0f72770a576f", "ecbb17b7-7b30-4e02-aea4-adb3959744b6") ->
          "47bb44af-66ed-4800-8c3d-328779db4f3c",
        ("e29668eb-e8e3-44b0-b9ca-0f72770a576f", "bbc8b584-27c1-4cf2-8881-500ae5e89c3c") ->
          "670bc55a-2a41-4977-8b8d-283cfa592299",
        ("2ae8d372-5160-47a7-91ae-2d3b38e9b9b6", "ea17874a-804e-4b05-90ec-7d69a7c31cc4") ->
          "0b8e9518-9fbc-4dc1-80ca-841564bb6f77",
        ("4d57b508-8d4e-43c7-b772-6854a70c29d2", "19b251af-4d22-4b6d-b6f2-cb0d86cef533") ->
          "27cd5f36-555a-4601-8592-dce584d357ae"
      )

      validExamples.foreach {
        case ((rawCourseUuid, rawSpaceUuid), memberUuid) => {
          val courseUuid = UUID.fromString(rawCourseUuid)
          val spaceUuid = UUID.fromString(rawSpaceUuid)

          val result = dao.removeMember(spaceUuid, UUID.fromString(memberUuid))

          result.futureValue shouldEqual \/-(spaceUuid)

          val spaces = courseDao.spaces(courseUuid).futureValue

          val matchingSpace = spaces.find(_.uuid contains spaceUuid)

          matchingSpace shouldBe defined
          matchingSpace.foreach { space =>
            space.member shouldBe None
            space.depositPaid shouldBe false
          }
        }
      }
    }

  }

}
