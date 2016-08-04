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

package com.jsherz.luskydive.api

import com.jsherz.luskydive.models.Validators

/**
  * The main member class, representing a person that has provided us with a phone number or e-mail address.
  *
  * @param id          Record ID
  * @param name        Peron's (first) name
  * @param phoneNumber Phone number
  * @param email       E-mail address
  */
case class Member(id: Option[Int], name: String, phoneNumber: Option[String], email: Option[String]) {

  /**
    * Ensures that the record has at least:
    *
    * - A non-empty name
    * - A valid phone number OR e-mail address.
    *
    * @return true if above conditions are met
    */
  def valid(): Boolean = {
    val nameValid = Validators.isNameValid(name) == Valid()
    val phoneNumberValid = Validators.parsePhoneNumber(phoneNumber).isRight
    val emailValid = Validators.isEmailValid(email) == Valid()

    nameValid && (phoneNumberValid || emailValid)
  }

}
