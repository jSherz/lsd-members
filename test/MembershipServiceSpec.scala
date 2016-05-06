import javax.inject.Inject

import org.scalatestplus.play._
import play.api.data.validation.{Invalid, Valid}
import services.MembershipService

/**
  * Defines the behaviour of the MembershipService class.
  */
class MembershipServiceSpec @Inject() (ms: MembershipService) extends PlaySpec with OneAppPerTest {
  val validNumbers = Seq(
    "07123123123", "07123614534", "07235753823", "447235898341", "447725837138", "447346881482", "+447458389482",
    "+447154712381", "+447123123123"
  )

  val invalidNumbers = Seq(
    "071231", "071231231", "0712312312", "07123614534", "072357538231", "0723575382312", "0723575382312123", "4472358",
    "4472358983", "44723589834", "447725837138", "4473468814821", "44734688148212", "44734688148212123", "+4474583",
    "+4474583894", "+44745838948", "+447154712381", "+4471231231231", "+44712312312312", "+44712312312312123",
    "7123123123", "01123123123", "4407123123123", "+4407123123123"
  )

  val validEmails = Seq(
    "goose@goat.org", "leeroy@jenkins.wow.amaze.org"
  )

  val invalidEmails = Seq(
    "fake@localhost", "something@fishy-net"
  )

  "MembershipService" should {
    "accept valid phone numbers" in {
      for (validNumber <- validNumbers) {
        ms.phoneNumberValidator.apply(validNumber) mustBe Valid
      }
    }

    "reject invalid phone numbers" in {
      for (invalidNumber <- invalidNumbers) {
        ms.phoneNumberValidator.apply(invalidNumber) mustBe Invalid
      }
    }

    "accept valid e-mail addresses" in {
      for (validEmail <- validEmails) {
          ms.emailValidator.apply(validEmail) mustBe Valid
      }
    }

    "reject invalid e-mail addresses" in {
      for (invalidEmail <- invalidEmails) {
        ms.emailValidator.apply(invalidEmail) mustBe Invalid
      }
    }
  }
}
