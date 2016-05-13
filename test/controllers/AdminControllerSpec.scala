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

import dao.SettingsDAO
import models.{Setting, Settings}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Very basic specs for the admin controller.
  */
class AdminControllerSpec extends BaseSpec {
  /**
    * An instance of the DAO, used for testing.
    */
  private val settingsDao = app.injector.instanceOf[SettingsDAO]

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

      eventually { settingsDao.get(Settings.WelcomeText).map(_ mustEqual myWelcome) }
    }
  }
}
