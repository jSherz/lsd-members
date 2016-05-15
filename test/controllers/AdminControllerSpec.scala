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

import java.sql.Timestamp

import dao.{MemberDAO, SettingsDAO, TextMessageDAO}
import models.{Member, Setting, Settings, TextMessage}
import play.api.Logger
import play.api.test.FakeRequest
import play.api.test.Helpers._
import slick.dbio.DBIOAction

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Very basic specs for the admin controller.
  */
class AdminControllerSpec extends BaseSpec {
  /**
    * An instance of the Settings DAO, used for testing.
    */
  private val settingsDao = app.injector.instanceOf[SettingsDAO]

  /**
    * An instance of the Member DAO, used for testing.
    */
  private val memberDao = app.injector.instanceOf[MemberDAO]

  /**
    * An instance of the Text Message DAO, used for testing.
    */
  private val textMessageDao = app.injector.instanceOf[TextMessageDAO]

  trait membersAndMessages {
    // A member and their texts
    val member = Member(None, "Joe Bloggs", Some("07123123123"), None)

    val messageA = TextMessage(None, 0, "07123123123", "LUU Skydive", new Timestamp(61423903920000L), 0, "Test message")
    val messageB = TextMessage(None, 0, "07123123123", "LUU Skydive", new Timestamp(61423903920000L), 0, "Further message")
    val messageC = TextMessage(None, 0, "07123123123", "LUU Skydive", new Timestamp(61423903920000L), 0, "Final info text")

    val textMessagesForUser = Seq(messageA, messageB, messageC)

    val otherMember = Member(None, "Some Person", Some("07010101010"), None)

    // Another member, used to ensure that messages are only associated with the correct person
    val textMessageNotForUser =
      TextMessage(None, 0, "07123123123", "LUU Skydive", new Timestamp(61423903920000L), 0, "Private for another user")

    val setup = for {
      memberId <- memberDao.insert(member)
      otherMemberId <- memberDao.insert(otherMember)
      c <- textMessageDao.insert(messageA.copy(memberId = memberId))
      d <- textMessageDao.insert(messageB.copy(memberId = memberId))
      e <- textMessageDao.insert(messageC.copy(memberId = memberId))
      f <- textMessageDao.insert(textMessageNotForUser.copy(memberId = otherMemberId))
    } yield memberId
  }

  "AdminController" should {
    "show the dashboard as the homepage" in {
      go to (s"http://localhost:${port}/admin")
      pageSource must include ("Stats")
      pageSource must include ("Member search")
      pageSource must include ("Settings")
    }

    "direct the user to the dashboard when the 'Main' button is clicked" in {
      go to (s"http://localhost:${port}/admin")

      click on find(linkText("Main")).value

      eventually { pageSource must include ("Stats") }
    }

    "clicking on the 'Welcome text' button takes the user to the relevant settings page" in {
      go to (s"http://localhost:${port}/admin")

      click on find(linkText("Welcome text")).value

      eventually { pageSource must include ("welcome text can be up to 480 characters") }
    }

    "clicking on the 'Settings' button takes the user to the relevant settings page" in {
      go to (s"http://localhost:${port}/admin")

      click on find(linkText("Settings")).value

      eventually { pageSource must include ("welcome text can be up to 480 characters") }
    }

    "highlight the correct menu button for each page" in {
      go to (s"http://localhost:${port}/admin")
      find(cssSelector("nav .active")).value.text must equal("Main")

      go to (s"http://localhost:${port}/admin/settings")
      eventually { find(cssSelector("nav .active")).value.text must equal("Settings") }
    }

    "show the default welcome text template if one is not set" in {
      settingsDao.empty().map { numRemoved =>
        go to (s"http://localhost:${port}/admin/settings")

        eventually { pageSource must include ("Hello, @@name@@, this is an example text!") }
      }
    }

    "show the configured welcome text template if one is set" in {
      val exampleTemplate = "Hello, World! Your name is @@name@@ :O"

      settingsDao.put(Setting(Settings.WelcomeText, exampleTemplate)).map { numAdded =>
        go to (s"http://localhost:${port}/admin/settings")

        eventually { pageSource must include (exampleTemplate) }
      }
    }

    "show an error if a user attempts to save a blank welcome text" in {
      go to (s"http://localhost:${port}/admin/settings")

      click on find("welcomeText").value
      enter("")
      click on find(cssSelector("button[type=submit]")).value

      eventually {
        pageSource must include ("You must enter a value for the welcome message")
      }
    }

    "show an error if a user attempts to save a welcome text template that's over 480 characters" in {
      go to (s"http://localhost:${port}/admin/settings")

      click on find("welcomeText").value
      enter((1 to 481).map(_ => " ").foldLeft("")((a, b) => a ++ b))
      click on find(cssSelector("button[type=submit]")).value

      eventually { pageSource must include ("must not exceed 480") }
    }

    "updates the welcome text template if a valid one was entered" in {
      val myWelcome = "This is a test! Isn't that amazing, @@name@@?"
      go to (s"http://localhost:${port}/admin/settings")

      click on find("welcomeText").value
      enter(myWelcome)
      click on find(cssSelector("button[type=submit]")).value

      eventually { settingsDao.get(Settings.WelcomeText).futureValue.get.value mustEqual myWelcome }
    }

    "show an error message when trying to view a user that doesn't exist" in {
      val memberView = route(app, FakeRequest(GET, "/admin/members/0")).get

      status(memberView) mustBe NOT_FOUND
      contentAsString(memberView) must include ("Member not found")
    }

    "show the user's details when viewing a member that exists" in {
      val member = Member(None, "Joe Bloggs", Some("07123123123"), None)
      val memberId = memberDao.insert(member).futureValue

      val memberView = route(app, FakeRequest(GET, s"/admin/members/${memberId}")).get

      status(memberView) mustBe OK
      contentType(memberView) mustBe Some("text/html")
      contentAsString(memberView) must include("Joe Bloggs")
    }

    "show any text messages sent to a member when viewing their details" in {
      new membersAndMessages {
        val memberId = setup.futureValue

        go to (s"http://localhost:${port}/admin/members/${memberId}")

        eventually {
          textMessagesForUser.map { message =>
            pageSource must include (message.message)
          }

          pageSource mustNot include (textMessageNotForUser.message)
        }
      }
    }
  }
}
