package com.jsherz.lsdintro

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
        Member(None, null, "07123123123", null),
        Member(None, null, null, "joe@bloggs.com"),
        Member(None, "", "07123123123", null),
        Member(None, "", null, "joe@bloggs.com")
      )

      for (member <- members) {
        member.valid() mustBe false
      }
    }

    "not be valid if missing both a phone number and e-mail address" in {
      val members = Seq(
        Member(None, "Joe Bloggs", null, null),
        Member(None, "Joe Bloggs", "", ""),
        Member(None, "Joe Bloggs", "7123123123", ""),
        Member(None, "Joe Bloggs", "7123123123", null),
        Member(None, "Joe Bloggs", "", "joe@localhost"),
        Member(None, "Joe Bloggs", null, "joe@localhost")
      )

      for (member <- members) {
        member.valid() mustBe false
      }
    }

    "be valid if it has just a name and phone number" in {
      val members = Seq(
        Member(None, "Joe Bloggs", "07123123123", null),
        Member(None, "Joe Bloggs", "07123123123", "")
      )

      for (member <- members) {
        member.valid() mustBe true
      }
    }

    "be valid if it hass just a name and e-mail address" in {
      val members = Seq(
        Member(None, "Joe Bloggs", null, "joe@bloggs.org"),
        Member(None, "Joe Bloggs", "", "joe@bloggs.org")
      )

      for (member <- members) {
        member.valid() mustBe true
      }
    }
  }
}
