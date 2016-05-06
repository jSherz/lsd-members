import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._
import org.scalatest.Matchers._

/**
 * add your integration spec here.
 * An integration test will fire up a whole play application in a real (or headless) browser
 */
class IntegrationSpec extends PlaySpec with OneServerPerTest with OneBrowserPerTest with HtmlUnitFactory {

  "Application" should {
    "show the main sign-up form as the homepage" in {
      go to ("http://localhost:" + port)
      pageSource must include ("Leeds University Skydivers")
      pageSource must include ("Phone number")
    }

    "direct the user to the alternative form when the 'Alt' button is clicked" in {
      go to ("http://localhost:" + port)
      pageSource must include ("Phone number")

      click on find(linkText("Alt")).value

      eventually { pageSource must include ("E-mail address") }
    }

    "direct the user to the main form when the 'Main' button is clicked" in {
      go to (s"http://localhost:${port}/alt")
      pageSource must include ("E-mail address")

      click on find(linkText("Main")).value

      eventually { pageSource must include ("Phone number") }
    }

    "highlight the correct menu button for each form" in {
      go to ("http://localhost:" + port)
      find(cssSelector("nav .active")).value.text must equal("Main")

      go to (s"http://localhost:${port}/alt")
      eventually { find(cssSelector("nav .active")).value.text must equal("Alt") }
    }

    "highlight the correct menu button for each form when submitted" in {
      go to ("http://localhost:" + port)
      click on find(cssSelector("button[type=submit]")).value
      find(cssSelector("nav .active")).value.text must equal("Main")

      go to (s"http://localhost:${port}/alt")
      click on find(cssSelector("button[type=submit]")).value
      eventually { find(cssSelector("nav .active")).value.text must equal("Alt") }
    }
  }
}
