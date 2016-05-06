import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends PlaySpec with OneServerPerTest with OneBrowserPerTest with HtmlUnitFactory {

  "Application" should {

    "show the main sign-up form as the homepage" in {
      go to ("http://localhost:" + port)
      pageSource must include ("Freshers Sign-up")
      pageSource must include ("Phone number")
    }
  }
}
