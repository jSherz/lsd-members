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

import com.jsherz.luskydive.api.Member
import com.jsherz.luskydive.dao.StubMemberDAO
import org.scalatest.{FunSpec, Matchers}

/**
  * Ensures the main sign-up endpoint functions correctly.
  */
class SignupResourceSpec extends FunSpec with Matchers {

  private val dao = new StubMemberDAO()
  private val resource = new SignupResource(dao)

  describe("SignupResource") {
    it("should return success with no errors if a valid username & e-mail are given") {
      val member = memberWith("Tyler Davey", None, Some("TylerDavey@jourrapide.com"))

      val result = resource.signup(member)

      result.success shouldEqual true
      result.errors shouldBe empty
    }

    it("should return success with no errors if a valid username & phone number are given") {
      val member = memberWith("Toby Howard", Some("07918323440"), None)

      val result = resource.signup(member)

      result.success shouldEqual true
      result.errors shouldBe empty
    }

    it("should return failed with an error if no name is given") {
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

    it("should return failed with an error if a blank name (only spaces) is given") {
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

    it("should return failed with an appropriate error if the phone number is in use") {
      val member = memberWith("Declan Clark", StubMemberDAO.existsPhoneNumber, None)

      val result = resource.signup(member)

      result.success shouldEqual false
      result.errors should contain("phoneNumber")
      result.errors.get("phoneNumber") shouldEqual "A member has already signed up with that phone number"
    }

    it("should return failed with an appropriate error if the e-mail is in use") {
      val member = memberWith("Nicole Howarth", None, StubMemberDAO.existsEmail)

      val result = resource.signup(member)

      result.success shouldEqual false
      result.errors should contain("email")
      result.errors.get("email") shouldEqual "A member has already signed up with that e-mail address"
    }

    it("should return failed with an appropriate error if the e-mail is invalid") {
      val member = memberWith("Caitlin Chamberlain", None, Some("definitely-not-valid.com"))

      val result = resource.signup(member)

      result.success shouldEqual false
      result.errors should contain("email")
      result.errors.get("email") shouldEqual "Invalid e-mail address"
    }

    it("should return failed with an appropriate error if the phone number is invalid") {
      val member = memberWith("Aidan Carter", Some("07123123"), None)

      val result = resource.signup(member)

      result.success shouldEqual false
      result.errors should contain("phoneNumber")
      result.errors.get("phoneNumber") shouldEqual "Invalid phone number"
    }
  }

  private def memberWith(name: String, phoneNumber: Option[String], email: Option[String]): Member = {
    Member(None, name, phoneNumber, email)
  }

}
