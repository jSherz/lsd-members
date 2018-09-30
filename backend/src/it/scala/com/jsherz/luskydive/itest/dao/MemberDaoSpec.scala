/**
  * MIT License
  *
  * Copyright (c) 2016-2018 James Sherwood-Jones <james.sherwoodjones@gmail.com>
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

import java.sql.Timestamp
import java.util.UUID

import com.jsherz.luskydive.core.{CommitteeMember, Member}
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util.{DateUtil, TestDatabase, Util}
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport.CommitteeMemberFormat
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.json.MemberSearchResult
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import scalaz.-\/

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Ensures the member DAO works correctly with the golden test DB.
  */
class MemberDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: MemberDao = _
  private var cleanup: () => Unit = _

  override protected def beforeAll(): Unit = {
    val TestDatabase(dbService, cleanupFn) = Util.setupGoldTestDb()
    cleanup = cleanupFn

    dao = new MemberDaoImpl(databaseService = dbService)
  }

  override protected def afterAll(): Unit = cleanup()

  "MemberDao#memberExists" should {

    "return false when no phone number or e-mail is given" in {
      val exists = dao.memberExists(None, None)

      exists.futureValue shouldBe false
    }

    "return false when the phone number does not match any member" in {
      val exists = dao.memberExists(Some("+44777111888"), None)

      exists.futureValue shouldBe false
    }

    "return false when the e-mail does not match any member" in {
      val exists = dao.memberExists(None, Some("goats@matthews-mosley.org"))

      exists.futureValue shouldBe false
    }

    "return false when neither the phone number nor e-mail matches any member" in {
      val exists = dao.memberExists(Some("+44700100200"), Some("destiny821337@gmail.com"))

      exists.futureValue shouldBe false
    }

    "return true when matching phone number is given" in {
      val exists = dao.memberExists(Some("+447155728581"), None)

      exists.futureValue shouldBe true
    }

    "return true when matching e-mail is given" in {
      val exists = dao.memberExists(None, Some("nelsonbryan@matthews-mosley.org"))

      exists.futureValue shouldBe true
    }

    "return true when matching phone number and e-mail are given" in {
      val exists = dao.memberExists(Some("+447156850760"), Some("destiny82@gmail.com"))

      exists.futureValue shouldBe true
    }

    "return true when phone number and e-mail match but are for different members" in {
      val exists = dao.memberExists(Some("+447793403999"), Some("gescobar@lawson-petty.biz"))

      exists.futureValue shouldBe true
    }

  }

  "MemberDao#create" should {

    "return Left(error) if no phone number or e-mail was supplied" in {
      val createdAt = Timestamp.valueOf("2016-08-13 21:13:37.10101")
      val updatedAt = Timestamp.valueOf("2016-08-13 21:14:10.00101")

      val member = Member(UUID.fromString("2fb4b6bd-9567-42cf-b466-2124c02a49f6"), "Tegan", Some("Harper"), None, None, None, None, None, false, false, createdAt,
        updatedAt, None)
      val result = dao.create(member)

      result shouldBe -\/(MemberDaoErrors.noPhoneNumberOrEmail)
    }

    "generate a valid UUID (if not supplied) and inserts a member with the correct information" in {
      val createdAt = Timestamp.valueOf("2009-01-20 10:19:59.10101")
      val updatedAt = Timestamp.valueOf("2009-01-21 18:10:10.123814")

      val member = Member(UUID.fromString("31a43b3a-b4a7-4b79-b22d-75e517ab2e0f"), "Keira", Some("Rahman"), Some("+447916149532"), Some("KeiraRahman@armyspy.com"),
        Some(DateUtil.makeDate(2011, 5, 9)), None, None, false, false, createdAt, updatedAt, None)

      val result = dao.create(member)

      result.isRight shouldBe true

      result.map { futureUuid =>
        val uuid = futureUuid.futureValue
        val foundMember = dao.get(uuid)
        foundMember.futureValue shouldBe Some(member.copy(uuid = uuid))
      }
    }

    "uses the supplied UUID (if provided) and inserts a member with the correct information" in {
      val createdAt = Timestamp.valueOf("2009-01-20 10:19:59.10101")
      val updatedAt = Timestamp.valueOf("2009-01-21 18:10:10.812728")

      val member = Member(UUID.fromString("da53db07-72b5-40e5-88a6-caa4e3d41403"), "Spencer", Some("Burton"),
        Some("+447938921821"), Some("sburton@theburtons.xyz"), None, None, None, false, false, createdAt, updatedAt,
        None)

      val result = dao.create(member)

      result.isRight shouldBe true

      result.map { futureUuid =>
        val uuid = futureUuid.futureValue
        val foundMember = dao.get(uuid)
        foundMember.futureValue shouldBe Some(member)
      }
    }

  }

  "MemberDao#get" should {

    "return None if the member was not found" in {
      val member = dao.get(UUID.fromString("d8418dda-aed4-4e58-9dc1-38a8ea720120"))

      member.futureValue shouldBe None
    }

    "return Some(member) if a valid UUID was given" in {
      val member = dao.get(UUID.fromString("0d0fa940-6d3f-45f9-9be0-07b08ec4e240"))

      member.futureValue shouldEqual Some(Util.fixture[Member]("0d0fa940.json"))
    }

  }

  "MemberDao#search" should {

    "return Left(error.invalidSearchTerm) if the search term is blank or under three characters" in {
      val testCases = Seq("", "a", "Ja", "  ", "  Br  ")

      testCases.foreach { example =>
        dao.search(example) shouldEqual -\/("error.invalidSearchTerm")
      }
    }

    "not return anything when no fields are a match" in {
      val testCases = Seq("Brucey", "+451182", "freestyler")

      testCases.foreach { example =>
        val result = dao.search(example)

        result.isRight shouldBe true
        result.foreach(_.futureValue shouldEqual Seq.empty)
      }
    }

    "return a match on any of name, phone number or e-mail" in {
      val testCases = Map(
        "Bruce" -> Util.fixture[Vector[MemberSearchResult]]("bruce.json"),
        "+447189154" -> Util.fixture[Vector[MemberSearchResult]]("plus447189154.json"),
        "alexandra" -> Util.fixture[Vector[MemberSearchResult]]("alexandra.json")
      )

      testCases.foreach {
        case (term, expected) => {
          val result = dao.search(term)
          result.isRight shouldBe true
          result.foreach(_.futureValue shouldEqual expected)
        }
      }
    }

    "return a match on any of name or e-mail (case insensitive)" in {
      val testCases = Map(
        "BrUcE" -> Util.fixture[Vector[MemberSearchResult]]("bruce.json"),
        "aleXANdra" -> Util.fixture[Vector[MemberSearchResult]]("alexandra.json")
      )

      testCases.foreach {
        case (term, expected) => {
          val result = dao.search(term)

          result.isRight shouldBe true
          result.foreach(_.futureValue shouldEqual expected)
        }
      }
    }

  }

  "MemberDao#forPhoneNumber" should {

    "return Right(Some(member)) if a member exists with the phone number" in {
      val member = dao.forPhoneNumber("+447526188767").futureValue

      member shouldEqual Some(Util.fixture[Member]("e5b8abf1.json"))
    }

    "return Right(None) if a member isn't found with the given phone number" in {
      val member = dao.forPhoneNumber("+447888888888").futureValue

      member shouldEqual None
    }

  }

  "MemberDao#forSocialId" should {

    "return the member if one exists with the given social ID (no committee record)" in {
      val member = dao.forSocialId("1231415124123").futureValue

      member shouldEqual Some((Util.fixture[Member]("0ac54c4b.json"), None))
    }

    "return the member & committee record if one exists with the given social ID" in {
      val member = dao.forSocialId("000111000111").futureValue

      val expectedMember = Util.fixture[Member]("ddc4ca00.json")
      val expectedCommitteeMember = Util.fixture[CommitteeMember]("f59c7cd7.json")

      member shouldEqual Some((expectedMember, Some(expectedCommitteeMember)))
    }

    "return Right(None) if a member isn't found with the given social ID" in {
      val member = dao.forSocialId("145781284798").futureValue

      member shouldEqual None
    }

  }

  "MemberDao#update" should {

    "update a member correctly" in {
      val memberWithDiffInfo = Util.fixture[Member]("update_diff_info.json")

      val result = dao.update(memberWithDiffInfo).futureValue

      result shouldEqual memberWithDiffInfo

      val doubleCheck = dao.get(memberWithDiffInfo.uuid).futureValue

      doubleCheck shouldEqual Some(memberWithDiffInfo)
    }

  }

}
