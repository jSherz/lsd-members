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

import javax.inject.Inject

import models.Member
import org.scalatestplus.play._
import services.MembershipService

/**
  * Tests the member model.
  */
class MemberSpec @Inject() (ms: MembershipService) extends PlaySpec with OneAppPerTest {
  "Member" should {
    "not be valid if missing a name" in {
      val members = Seq(
        Member(None, null, Some("07123123123"), None),
        Member(None, null, None, Some("joe@bloggs.com")),
        Member(None, "", Some("07123123123"), None),
        Member(None, "", None, Some("joe@bloggs.com"))
      )

      for (member <- members) {
        member.valid() mustBe false
      }
    }

    "not be valid if missing both a phone number and e-mail address" in {
      val members = Seq(
        Member(None, "Joe Bloggs", None, None),
        Member(None, "Joe Bloggs", Some(""), Some("")),
        Member(None, "Joe Bloggs", Some("7123123123"), Some("")),
        Member(None, "Joe Bloggs", Some("7123123123"), None),
        Member(None, "Joe Bloggs", Some(""), Some("joe@localhost")),
        Member(None, "Joe Bloggs", None, Some("joe@localhost"))
      )

      for (member <- members) {
        member.valid() mustBe false
      }
    }

    "be valid if it has just a name and phone number" in {
      val members = Seq(
        Member(None, "Joe Bloggs", Some("07123123123"), None),
        Member(None, "Joe Bloggs", Some("07123123123"), Some(""))
      )

      for (member <- members) {
        member.valid() mustBe true
      }
    }

    "be valid if it hass just a name and e-mail address" in {
      val members = Seq(
        Member(None, "Joe Bloggs", None, Some("joe@bloggs.org")),
        Member(None, "Joe Bloggs", Some(""), Some("joe@bloggs.org"))
      )

      for (member <- members) {
        member.valid() mustBe true
      }
    }
  }
}
