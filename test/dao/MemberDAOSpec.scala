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

import dao.MemberDAO
import models.Member
import org.scalatest.Matchers._

/**
  * Integration tests for the member data access object.
  */
class MemberDAOSpec extends BaseSpec {
  /**
    * An instance of the DAO, used for testing.
    */
  private val memberDao = app.injector.instanceOf[MemberDAO]

  "MemberDAO" should {
    "return no members if none are configured" in {
      memberDao.all().futureValue shouldBe empty
    }

    "return all configured members" in {
      val members = Seq(
        Member(None, "Bob", Some("07123123123"), None),
        Member(None, "Dave", Some("07123123123"), None),
        Member(None, "Mary", None, Some("mary@had-a-little-lamb.com")),
        Member(None, "Stephanie", None, Some("steph@codes.world"))
      )

      val membersWithIds: Seq[Member] = members.map { member =>
        member.copy(id = Some(memberDao.insert(member).futureValue))
      }

      memberDao.all().futureValue shouldBe membersWithIds
    }

    "return Some(member) if one exists with the given ID" in {
      val sandy = Member(None, "Sandra", None, Some("sandra@ntlworld.com"))
      val memberId = memberDao.insert(sandy).futureValue

      memberDao.get(memberId).futureValue shouldBe Some(sandy.copy(id = Some(memberId)))
    }

    "return None if a member wasn't found with the given ID" in {
      memberDao.get(0).futureValue shouldBe None
    }

    "insert a new member correctly" in {
      val sandy = Member(None, "Sandra", None, Some("sandra@ntlworld.com"))

      memberDao.all().futureValue shouldBe empty
      val memberId = memberDao.insert(sandy).futureValue
      memberDao.all().futureValue should contain(sandy.copy(id = Some(memberId)))
    }

    "detect if a member exists correctly (by e-mail)" in {
      val mauriceMail = Some("maurice@madagascar.io")
      val maurice = Member(None, "Maurice", None, mauriceMail)

      memberDao.all().futureValue shouldBe empty
      memberDao.exists(None, mauriceMail).futureValue shouldBe false
      memberDao.insert(maurice).futureValue
      memberDao.exists(None, mauriceMail).futureValue shouldBe true
    }

    "detect if a member exists correctly (by phone number)" in {
      val mauriceMobile = Some("07000001337")
      val maurice = Member(None, "Maurice", mauriceMobile, None)

      memberDao.all().futureValue shouldBe empty
      memberDao.exists(mauriceMobile, None).futureValue shouldBe false
      memberDao.insert(maurice).futureValue
      memberDao.exists(mauriceMobile, None).futureValue shouldBe true
    }

    "detect if a member exists correctly (by e-mail and phone number)" in {
      val mauriceMobile = Some("07000001337")
      val mauriceMail = Some("maurice@madagascar.io")
      val maurice = Member(None, "Maurice", mauriceMobile, mauriceMail)

      memberDao.all().futureValue shouldBe empty
      memberDao.exists(mauriceMobile, mauriceMail).futureValue shouldBe false
      memberDao.insert(maurice).futureValue
      memberDao.exists(mauriceMobile, mauriceMail).futureValue shouldBe true

      val notMauriceMobile = Some("07123123123")
      val notMauriceMail = Some("not@maurice.org")
      val notMaurice = Member(None, "Not Maurice", notMauriceMobile, notMauriceMail)

      memberDao.exists(notMauriceMobile, notMauriceMail).futureValue shouldBe false
      memberDao.insert(notMaurice).futureValue
      memberDao.exists(notMauriceMobile, notMauriceMail).futureValue shouldBe true

      memberDao.exists(mauriceMobile, notMauriceMail).futureValue shouldBe false
      memberDao.exists(notMauriceMobile, mauriceMail).futureValue shouldBe false
    }

    "empty the members table correctly" in {
      val members = Seq(
        Member(None, "Bob", Some("07123123123"), None),
        Member(None, "Dave", Some("07123123123"), None)
      )

      members.map { memberDao.insert(_).futureValue }

      memberDao.all().futureValue shouldNot be(empty)
      memberDao.empty().futureValue
      memberDao.all().futureValue shouldBe empty
    }
  }
}
