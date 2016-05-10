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

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Integration tests with a real (or headless) browser.
  */
class MemberDAOSpec extends BaseSpec {
  /**
    * An instance of the DAO, used for testing.
    */
  private val memberDao = app.injector.instanceOf[MemberDAO]

  "MemberDAO" should {
    "return no members if none are configured" in {
      memberDao.all().map(_ shouldBe empty)
    }

    "returns all configured members" in {
      val members = Seq(
        Member(None, "Bob", "07123123123", null),
        Member(None, "Dave", "07123123123", null),
        Member(None, "Mary", null, "mary@had-a-little-lamb.com"),
        Member(None, "Stephanie", null, "steph@codes.world")
      )

      members.map { memberDao.insert(_) }

      memberDao.all().map(_ shouldBe members)
    }

    "insert a new member correctly" in {
      val sandy = Member(None, "Sandra", null, "sandra@ntlworld.com")

      memberDao.all().map(_ shouldBe empty)
      memberDao.insert(sandy)
      memberDao.all().map(_ shouldBe Seq(sandy))
    }

    "detect if a member exists correctly (by e-mail)" in {
      var mauriceMail = "maurice@madagascar.io"
      val maurice = Member(None, "Maurice", null, mauriceMail)

      memberDao.all().map(_ shouldBe empty)
      memberDao.exists(null, mauriceMail).map(_ shouldBe false)
      memberDao.insert(maurice)
      memberDao.exists(null, mauriceMail).map(_ shouldBe true)
    }

    "detect if a member exists correctly (by phone number)" in {
      var mauriceMobile = "07000001337"
      val maurice = Member(None, "Maurice", mauriceMobile, null)

      memberDao.all().map(_ shouldBe empty)
      memberDao.exists(mauriceMobile, null).map(_ shouldBe false)
      memberDao.insert(maurice)
      memberDao.exists(mauriceMobile, null).map(_ shouldBe true)
    }

    "detect if a member exists correctly (by e-mail and phone number)" in {
      var mauriceMobile = "07000001337"
      var mauriceMail = "maurice@madagascar.io"
      val maurice = Member(None, "Maurice", mauriceMobile, mauriceMail)

      memberDao.all().map(_ shouldBe empty)
      memberDao.exists(mauriceMobile, mauriceMail).map(_ shouldBe false)
      memberDao.insert(maurice)
      memberDao.exists(mauriceMobile, mauriceMail).map(_ shouldBe true)

      val notMauriceMobile = "07123123123"
      val notMauriceMail = "not@maurice.org"
      val notMaurice = Member(None, "Not Maurice", notMauriceMobile, notMauriceMail)
      memberDao.insert(notMaurice)

      memberDao.exists(mauriceMobile, notMauriceMail).map(_ shouldBe false)
      memberDao.exists(notMauriceMobile, mauriceMail).map(_ shouldBe false)
    }

    "empty the members table correctly" in {
      val members = Seq(
        Member(None, "Bob", "07123123123", null),
        Member(None, "Dave", "07123123123", null)
      )

      members.map { memberDao.insert(_) }

      memberDao.all().map(_ shouldNot be(empty))
      memberDao.empty()
      memberDao.all().map(_ shouldBe empty)
    }
  }
}
