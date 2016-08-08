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

package com.jsherz.luskydive.util

import java.sql.Date

import org.scalatest.{Matchers, WordSpec}
import spray.json.JsString

/**
  * Ensures dates are properly (de)serialized by [[DateJsonFormat]].
  */
class DateJsonFormatSpec extends WordSpec with Matchers {

  private val testDates: Map[JsString, Date] = Map(
    JsString("2014-01-28") -> DateUtil.makeDate(2014, 1, 28),
    JsString("2014-12-19") -> DateUtil.makeDate(2014, 12, 19),
    JsString("2014-01-01") -> DateUtil.makeDate(2014, 1, 1),
    JsString("2009-05-20") -> DateUtil.makeDate(2009, 5, 20),
    JsString("1999-09-07") -> DateUtil.makeDate(1999, 9, 7)
  )

  "DateJsonFormat" should {

    "serialize dates correctly" in {
      testDates.foreach { case (jsString, date) =>
        DateJsonFormat.write(date) shouldEqual jsString
      }
    }

    "deserialize dates correctly" in {
      testDates.foreach { case (jsString, date) =>
        DateJsonFormat.read(jsString) shouldEqual date
      }
    }

  }

}
