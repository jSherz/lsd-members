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

/**
  * Very basic specs for the admin controller.
  */
class AdminControllerSpec extends BaseSpec {
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
  }
}
