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

import com.jsherz.luskydive.core.Member
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util.Util
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import org.scalatest.concurrent.ScalaFutures._
import com.jsherz.luskydive.json.MemberJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Ensures the member DAO works correctly with the golden test DB.
  */
class MemberDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: MemberDao = _

  override protected def beforeAll(): Unit = {
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

    "return None if creating the member failed" in {
      val result = dao.create("Tegan Harper", None, None)

      result.futureValue shouldBe None
    }

    "generates a valid UUID and inserts a member with the correct information" in {
      val member = Member(None, "Keira Rahman", Some("+447916149532"), Some("KeiraRahman@armyspy.com"))

      val futureResult = dao.create(member.name, member.phoneNumber, member.email)
      val result = futureResult.futureValue

      result.isDefined shouldBe true

      val foundMember = dao.get(result.get)
      foundMember.futureValue shouldBe Some(member.copy(uuid = Some(result.get)))
    }

    "generates a different UUID with each creation" in {
      val memberA = Member(None, "Alisha Stevens", Some("+447985203839"), Some("AlishaStevens@yahoo.com"))
      val memberB = Member(None, "Hollie Hammond", Some("+447885929137"), Some("x_x_hollie_x_x@fanmail.com"))
      val memberC = Member(None, "Kayleigh Barker", Some("+447043025413"), Some("kay_kay100101@hotmail.co.uk"))

      val memberAUuid = dao.create(memberA.name, memberA.phoneNumber, memberA.email).futureValue
      val memberBUuid = dao.create(memberB.name, memberB.phoneNumber, memberB.email).futureValue
      val memberCUuid = dao.create(memberC.name, memberC.phoneNumber, memberC.email).futureValue

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

}
