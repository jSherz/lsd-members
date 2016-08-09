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

import java.sql.Timestamp

import org.scalatest.{Matchers, WordSpec}
import spray.json.{DeserializationException, JsArray, JsNull, JsNumber, JsObject, JsString}

/**
  * Ensures timestamps are properly (de)serialized by [[TimestampJsonFormat]].
  */
class TimestampJsonFormatSpec extends WordSpec with Matchers {

  private val testTimestamps: Map[JsString, Timestamp] = Map(
    JsString("2016-08-09 08:36:32.469") -> Timestamp.valueOf("2016-08-09 08:36:32.469"),
    JsString("2016-12-09 08:36:44.821897658") -> Timestamp.valueOf("2016-12-09 08:36:44.821897658"),
    JsString("1999-08-20 08:36:44.0") -> Timestamp.valueOf("1999-08-20 08:36:44")
  )

  "TimestampJsonFormat" should {

    "serialize timestamps correctly" in {
      testTimestamps.foreach { case (jsString, timestamp) =>
        TimestampJsonFormat.write(timestamp) shouldEqual jsString
      }
    }

    "deserialize dates correctly" in {
      testTimestamps.foreach { case (jsString, timestamp) =>
        TimestampJsonFormat.read(jsString) shouldEqual timestamp
      }
    }

    "throw an exception if not given a JsString" in {
      a[DeserializationException] should be thrownBy TimestampJsonFormat.read(JsObject())
      a[DeserializationException] should be thrownBy TimestampJsonFormat.read(JsArray())
      a[DeserializationException] should be thrownBy TimestampJsonFormat.read(JsNumber(0))
      a[DeserializationException] should be thrownBy TimestampJsonFormat.read(JsNull)
    }

  }

}
