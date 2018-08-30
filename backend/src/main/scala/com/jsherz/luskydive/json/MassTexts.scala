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

import java.sql.Date
import java.util.UUID

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.jsherz.luskydive.core.{MassText, TextMessage}
import com.jsherz.luskydive.util.{DateJsonFormat, TimestampJsonFormat, UuidJsonFormat}
import spray.json.DefaultJsonProtocol


case class TryFilterRequest(startDate: Date, endDate: Date)

case class TryFilterResponse(success: Boolean, numMembers: Option[Int], error: Option[String])

case class MassTextSendRequest(startDate: Date, endDate: Date, template: String, expectedRendered: String)

case class MassTextSendResponse(success: Boolean, error: Option[String], uuid: Option[UUID])

object MassTextsJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {

  implicit val DateFormat = DateJsonFormat
  implicit val UuidFormat = UuidJsonFormat
  implicit val TimestampFormat = TimestampJsonFormat
  implicit val tryFilterRequestFormat = jsonFormat2(TryFilterRequest)
  implicit val tryFilterResponseFormat = jsonFormat3(TryFilterResponse)
  implicit val massTextFormat = jsonFormat4(MassText)
  implicit val textMessageFormat = jsonFormat11(TextMessage)
  implicit val massTextSendRequestFormat = jsonFormat4(MassTextSendRequest)
  implicit val massTextSendResponseFormat = jsonFormat3(MassTextSendResponse)

}
