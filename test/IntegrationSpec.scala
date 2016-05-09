import org.scalatestplus.play._
import play.api.{Play, Application}
import play.api.test._
import play.api.test.Helpers._
import org.scalatest.Matchers._
import org.scalatest.TestData
import Helpers._

/**
 * Integration tests with a real (or headless) browser.
 */
class IntegrationSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory {

  implicit override lazy val app = createTestApplication()

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

    "display errors for every field if nothing was entered" in {
      go to ("http://localhost:" + port)
      click on find(cssSelector("button[type=submit]")).value

      eventually {
        find(cssSelector("#name_field .error")).value.text must include ("This field is required")
        find(cssSelector("#phoneNumber_field .error")).value.text must include ("This field is required")
      }
    }

    "display an error if no name was entered" in {
      go to ("http://localhost:" + port)
      click on find("phoneNumber").value
      enter("07123123123")
      click on find(cssSelector("button[type=submit]")).value

      eventually { find(cssSelector("#name_field .error")).value.text must include ("This field is required") }
    }

    "display an error if no mobile phone was entered" in {
      go to ("http://localhost:" + port)
      click on find("name").value
      enter("Joe Bloggs")
      click on find(cssSelector("button[type=submit]")).value

      eventually { find(cssSelector("#phoneNumber_field .error")).value.text must include ("This field is required") }
    }

    "display an error if no e-mail address was entered (alternative form)" in {
      go to (s"http://localhost:${port}/alt")
      click on find("name").value
      enter("Joe Bloggs")
      click on find(cssSelector("button[type=submit]")).value

      eventually { find(cssSelector("#email_field .error")).value.text must include ("This field is required") }
    }

    "display an error if an invalid mobile phone number was entered" in {
      go to ("http://localhost:" + port)
      click on find("name").value
      enter("Joe Bloggs")
      click on find("phoneNumber").value
      enter("4407123123123")
      click on find(cssSelector("button[type=submit]")).value

      eventually { find(cssSelector("#phoneNumber_field .error")).value.text must include ("Invalid mobile number") }
    }

    "display an error if an invalid e-mail address was entered (alternative form)" in {
      go to (s"http://localhost:${port}/alt")
      click on find("name").value
      enter("Joe Bloggs")
      click on find("email").value
      enter("bloggs@localhost")
      click on find(cssSelector("button[type=submit]")).value

      eventually { find(cssSelector("#email_field .error")).value.text must include ("Invalid e-mail address") }
    }

    "redirect the user to the thank you page if a valid name and phone number were entered" in {
      go to ("http://localhost:" + port)
      click on find("name").value
      enter("Joe Bloggs")
      click on find("phoneNumber").value
      enter("07123123123")
      click on find(cssSelector("button[type=submit]")).value

      eventually { pageSource should include ("Thank you!") }
    }

    "redirect the user to the thank you page if a valid name and e-mail were entered (alternative form)" in {
      go to (s"http://localhost:${port}/alt")
      click on find("name").value
      enter("Joe Bloggs")
      click on find("email").value
      enter("bloggs@localhost.com")
      click on find(cssSelector("button[type=submit]")).value

      eventually { pageSource should include ("Thank you!") }
    }
  }
}
