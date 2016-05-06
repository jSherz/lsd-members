import javax.inject.Inject

import models.Member
import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import services.MembershipService

/**
  * Tests the member model.
  */
class MemberSpec @Inject() (ms: MembershipService) extends PlaySpec with OneAppPerTest {
  "Member" should {
    "not be valid if missing a name" in {
      val members = Seq(
        Member(ms, null, "07123123123", null),
        Member(ms, null, null, "joe@bloggs.com"),
        Member(ms, "", "07123123123", null),
        Member(ms, "", null, "joe@bloggs.com")
      )

      for (member <- members) {
        member.valid() mustBe false
      }
    }

    "not be valid if missing both a phone number and e-mail address" in {
      val members = Seq(
        Member(ms, "Joe Bloggs", null, null),
        Member(ms, "Joe Bloggs", "", ""),
        Member(ms, "Joe Bloggs", "7123123123", ""),
        Member(ms, "Joe Bloggs", "7123123123", null),
        Member(ms, "Joe Bloggs", "", "joe@localhost"),
        Member(ms, "Joe Bloggs", null, "joe@localhost")
      )

      for (member <- members) {
        member.valid() mustBe false
      }
    }

    "be valid if it has just a name and phone number" in {
      val members = Seq(
        Member(ms, "Joe Bloggs", "07123123123", null),
        Member(ms, "Joe Bloggs", "07123123123", "")
      )

      for (member <- members) {
        member.valid() mustBe true
      }
    }

    "be valid if it hass just a name and e-mail address" in {
      val members = Seq(
        Member(ms, "Joe Bloggs", null, "joe@bloggs.org"),
        Member(ms, "Joe Bloggs", "", "joe@bloggs.org")
      )

      for (member <- members) {
        member.valid() mustBe true
      }
    }
  }
}
