import org.scalatestplus.play._
import play.api.test._
import play.api.test.Helpers._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends PlaySpec with OneAppPerTest {
  "Routes" should {
    "send 404 on a bad request" in  {
      route(app, FakeRequest(GET, "/boum")).map(status(_)) mustBe Some(NOT_FOUND)
    }
  }

  "MembershipService" should {
    "render the main sign-up form on the homepage" in {
      val home = route(app, FakeRequest(GET, "/")).get

      status(home) mustBe OK
      contentType(home) mustBe Some("text/html")
      contentAsString(home) must include ("Leeds University Skydivers")
      contentAsString(home) must include ("Phone number")
    }

    "render the alternative sign-up form correctly" in {
      val alt = route(app, FakeRequest(GET, "/alt")).get

      status(alt) mustBe OK
      contentType(alt) mustBe Some("text/html")
      contentAsString(alt) must include ("Leeds University Skydivers")
      contentAsString(alt) must include ("E-mail address")
    }
  }
}
