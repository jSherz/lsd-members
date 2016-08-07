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

package com.jsherz.luskydive.resources

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.apis.SignupAPI
import com.jsherz.luskydive.core.{SignupAltRequest, SignupResponse}
import com.jsherz.luskydive.dao.StubMemberDAO
import org.scalatest.{Matchers, WordSpec}

/**
  * Ensures the main sign-up endpoint functions correctly.
  */
class SignupAPISpec extends WordSpec with Matchers with ScalatestRouteTest {

  private val dao = new StubMemberDAO()
  private val route = new SignupAPI(dao).route

  import com.jsherz.luskydive.core.SignupJsonSupport._

  "SignupAPI" should {

    "return success with no errors if a valid username & e-mail are given" in {
      val request = SignupAltRequest("Tyler Davey", "TylerDavey@jourrapide.com")
      val expected = SignupResponse(false, Map.empty)

      Post("/members/sign-up/alt", request) ~> route ~> check {
        response.status shouldEqual StatusCodes.OK
        responseAs[SignupResponse].success shouldBe true
        responseAs[SignupResponse].errors shouldBe empty
      }

    }

    /*

    "return success with no errors if a valid username & phone number are given" in {
      val request = SignupRequest("Toby Howard", "07918323440")

      Post("/api/v1/members/sign-up", request) ~> route ~> check {
      val result = resource.signup(member)

      result.success shouldEqual true
      result.errors shouldBe empty
    }

    "return failed with an error if no name is given" in {
      val memberP = memberWith("", Some("07049215717"), None)
      val memberE = memberWith("", None, Some("LiamHurst@jourrapide.com"))

      val resultP = resource.signup(memberP)

      resultP.success shouldEqual false
      resultP.errors should contain("name")
      resultP.errors.get("name") shouldEqual "Please enter a name"

      val resultE = resource.signup(memberE)

      resultE.success shouldEqual false
      resultE.errors should contain("name")
      resultE.errors.get("name") shouldEqual "Please enter a name"
    }

    "return failed with an error if a blank name (only spaces) is given" in {
      val memberP = memberWith("      ", Some("07856216259"), None)
      val memberE = memberWith("      ", None, Some("LucasJordan@rhyta.com"))

      val resultP = resource.signup(memberP)

      resultP.success shouldEqual false
      resultP.errors should contain("name")
      resultP.errors.get("name") shouldEqual "Please enter a name"

      val resultE = resource.signup(memberE)

      resultE.success shouldEqual false
      resultE.errors should contain("name")
      resultE.errors.get("name") shouldEqual "Please enter a name"
    }

    "return failed with an appropriate error if the phone number is in use" in {
      val member = memberWith("Declan Clark", StubMemberDAO.existsPhoneNumber, None)

      val result = resource.signup(member)

      result.success shouldEqual false
      result.errors should contain("phoneNumber")
      result.errors.get("phoneNumber") shouldEqual "A member has already signed up with that phone number"
    }

    "return failed with an appropriate error if the e-mail is in use" in {
      val member = memberWith("Nicole Howarth", None, StubMemberDAO.existsEmail)

      val result = resource.signup(member)

      result.success shouldEqual false
      result.errors should contain("email")
      result.errors.get("email") shouldEqual "A member has already signed up with that e-mail address"
    }

    "return failed with an appropriate error if the e-mail is invalid" in {
      val member = memberWith("Caitlin Chamberlain", None, Some("definitely-not-valid.com"))

      val result = resource.signup(member)

      result.success shouldEqual false
      result.errors should contain("email")
      result.errors.get("email") shouldEqual "Invalid e-mail address"
    }

    "return failed with an appropriate error if the phone number is invalid" in {
      val member = memberWith("Aidan Carter", Some("07123123"), None)

      val result = resource.signup(member)

      result.success shouldEqual false
      result.errors should contain("phoneNumber")
      result.errors.get("phoneNumber") shouldEqual "Invalid phone number"
    }*/
  }

}
