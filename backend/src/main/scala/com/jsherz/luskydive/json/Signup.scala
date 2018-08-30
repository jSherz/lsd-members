/**
  * MIT License
  *
  * Copyright (c) 2016-2018 James Sherwood-Jones <james.sherwoodjones@gmail.com>
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

package com.jsherz.luskydive.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.jsherz.luskydive.models.Validators
import spray.json.DefaultJsonProtocol

import scalaz.Scalaz._
import scalaz._

/**
  * JSON (de)serialization support.
  */
object SignupJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val SignupRequestFormat = jsonFormat2(SignupRequest)
  implicit val SignupAltRequestFormat = jsonFormat2(SignupAltRequest)
  implicit val SignupResponseFormat = jsonFormat2(SignupResponse)

}

/**
  * Standard method of a member signing up.
  */
case class SignupRequest(name: String, phoneNumber: String) {

  /**
    * Ensures the given request is valid.
    *
    * @return If valid, returns the E164 formatted phone number
    */
  def validate(): ValidationNel[(String, String), String] = {
    val nameValid = Validators.withFieldName("name", Validators.name(name))
    val phoneNumberValid = Validators.withFieldName("phoneNumber", Validators.phoneNumber(phoneNumber))

    (nameValid.toValidationNel |@| phoneNumberValid.toValidationNel) {
      case (_, formattedPhoneNumber) => formattedPhoneNumber
    }
  }

}

/**
  * Alternative method of a member signing up.
  */
case class SignupAltRequest(name: String, email: String) {

  /**
    * Ensure the given request is valid.
    *
    * @return
    */
  def validate(): ValidationNel[(String, String), Unit] = {
    val nameValid = Validators.withFieldName("name", Validators.name(name))
    val emailValid = Validators.withFieldName("email", Validators.email(email))

    (nameValid.toValidationNel |@| emailValid.toValidationNel) {
      case _ => ()
    }
  }

}

/**
  * A response to a sign-up request.
  *
  * @param success Did signing up succeed?
  * @param errors  Any errors that were reported as a field -> error mapping
  */
case class SignupResponse(success: Boolean, errors: Map[String, String])
