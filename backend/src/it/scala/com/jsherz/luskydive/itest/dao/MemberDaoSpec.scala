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

import java.sql.Timestamp
import java.util.UUID

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util.{DateUtil, Util}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures._
import com.jsherz.luskydive.json.MemberJsonSupport._
import com.jsherz.luskydive.json.MemberSearchResult

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.{-\/, \/-}

/**
  * Ensures the member DAO works correctly with the golden test DB.
  */
class MemberDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: MemberDao = _

  override protected def beforeAll(): Unit = {
    implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

    val dbService = Util.setupGoldTestDb()

    dao = new MemberDaoImpl(databaseService = dbService)
  }

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

    "return None if no phone number or e-mail was supplied" in {
      val createdAt = Timestamp.valueOf("2016-08-13 21:13:37.10101")
      val updatedAt = Timestamp.valueOf("2016-08-13 21:14:10.00101")

      val member = Member(None, "Tegan", Some("Harper"), None, None, None, None, None, false, false, createdAt, updatedAt)
      val result = dao.create(member)

      result.futureValue shouldBe None
    }

    "generates a valid UUID (if not supplied) and inserts a member with the correct information" in {
      val createdAt = Timestamp.valueOf("2009-01-20 10:19:59.10101")
      val updatedAt = Timestamp.valueOf("2009-01-21 18:10:10.123814")

      val member = Member(None, "Keira", Some("Rahman"), Some("+447916149532"), Some("KeiraRahman@armyspy.com"),
        Some(DateUtil.makeDate(2011, 5, 9)), None, None, false, false, createdAt, updatedAt)

      val futureResult = dao.create(member)
      val result = futureResult.futureValue

      result.isDefined shouldBe true

      val foundMember = dao.get(result.get)
      foundMember.futureValue shouldBe Some(member.copy(uuid = Some(result.get)))
    }

    "uses the supplied UUID (if provided) and inserts a member with the correct information" in {
      val createdAt = Timestamp.valueOf("2009-01-20 10:19:59.10101")
      val updatedAt = Timestamp.valueOf("2009-01-21 18:10:10.812728")

      val member = Member(Some(UUID.fromString("da53db07-72b5-40e5-88a6-caa4e3d41403")), "Spencer", Some("Burton"),
        Some("+447938921821"), Some("sburton@theburtons.xyz"), None, None, None, false, false, createdAt, updatedAt)

      val futureResult = dao.create(member)
      val result = futureResult.futureValue

      result.isDefined shouldBe true

      val foundMember = dao.get(result.get)
      foundMember.futureValue shouldBe Some(member)
    }

    "generates a different UUID with each creation" in {
      val createdAt = Timestamp.valueOf("2016-08-14 12:13:00")
      val updatedAt = Timestamp.valueOf("2016-08-14 12:13:01")
      val memberA = Member(None, "Alisha", Some("Stevens"), Some("+447985203839"), Some("AlishaStevens@yahoo.com"),
        None, None, None, false, false, createdAt, updatedAt)
      val memberB = Member(None, "Hollie", Some("Hammond"), Some("+447885929137"), Some("x_x_hollie_x_x@fanmail.com"),
        None, None, None, false, false, createdAt, updatedAt)
      val memberC = Member(None, "Kayleigh", Some("Barker"), Some("+447043025413"), Some("kay_kay100101@hotmail.co.uk"),
        None, None, None, false, false, createdAt, updatedAt)

      val memberAUuid = dao.create(memberA).futureValue
      val memberBUuid = dao.create(memberB).futureValue
      val memberCUuid = dao.create(memberC).futureValue

      memberAUuid shouldNot be(None)
      memberBUuid shouldNot be(None)
      memberCUuid shouldNot be(None)

      memberAUuid shouldNot equal(memberBUuid)
      memberAUuid shouldNot equal(memberCUuid)

      memberBUuid shouldNot equal(memberAUuid)
      memberBUuid shouldNot equal(memberCUuid)

      memberCUuid shouldNot equal(memberAUuid)
      memberCUuid shouldNot equal(memberBUuid)
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
        dao.search(example).futureValue shouldEqual -\/("error.invalidSearchTerm")
      }
    }

    "not return anything when no fields are a match" in {
      val testCases = Seq("Brucey", "+451182", "freestyler")

      testCases.foreach { example =>
        dao.search(example).futureValue shouldEqual \/-(Seq.empty)
      }
    }

    "return a match on any of name, phone number or e-mail" in {
      val testCases = Map(
        "Bruce" -> Util.fixture[Vector[MemberSearchResult]]("bruce.json"),
        "+447189154" -> Util.fixture[Vector[MemberSearchResult]]("plus447189154.json"),
        "alexandra" -> Util.fixture[Vector[MemberSearchResult]]("alexandra.json")
      )

      testCases.foreach {
        case (term, expected) => dao.search(term).futureValue shouldEqual \/-(expected)
      }
    }

    "return a match on any of name or e-mail (case insensitive)" in {
      val testCases = Map(
        "BrUcE" -> Util.fixture[Vector[MemberSearchResult]]("bruce.json"),
        "aleXANdra" -> Util.fixture[Vector[MemberSearchResult]]("alexandra.json")
      )

      testCases.foreach {
        case (term, expected) => dao.search(term).futureValue shouldEqual \/-(expected)
      }
    }

  }

}
