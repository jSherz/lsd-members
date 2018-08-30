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

package com.jsherz.luskydive.util

import java.util.UUID

import org.scalatest.{Matchers, WordSpec}
import spray.json.{DeserializationException, JsArray, JsNull, JsNumber, JsObject, JsString}

/**
  * Exercises [[UuidJsonFormat]].
  */
class UuidJsonFormatSpec extends WordSpec with Matchers {

  "UuidJsonFormat" should {

    val testUuids: Map[JsString, UUID] = Map(
      JsString("8a72449f-ff2d-4add-b716-f4fa0a686ef6") -> UUID.fromString("8a72449f-ff2d-4add-b716-f4fa0a686ef6"),
      JsString("64eebea5-9428-4788-a381-4e15db9ac6a3") -> UUID.fromString("64eebea5-9428-4788-a381-4e15db9ac6a3"),
      JsString("940a1ce4-7bfd-4cb7-a570-55c5af5b5245") -> UUID.fromString("940a1ce4-7bfd-4cb7-a570-55c5af5b5245"),
      JsString("ab4f72d7-7ffd-4a8e-a673-2ff91e2e4bc7") -> UUID.fromString("ab4f72d7-7ffd-4a8e-a673-2ff91e2e4bc7"),
      JsString("18f0b0c9-4bab-4114-bf1e-f8df9819af37") -> UUID.fromString("18f0b0c9-4bab-4114-bf1e-f8df9819af37")
    )

    "serialize UUIDs correctly" in {
      testUuids.foreach { case (jsString, uuid) =>
        UuidJsonFormat.write(uuid) shouldEqual jsString
      }
    }

    "deserialize UUIDs correctly" in {
      testUuids.foreach { case (jsString, uuid) =>
        UuidJsonFormat.read(jsString) shouldEqual uuid
      }
    }

    "throw an exception if not given a JsString" in {
      a[DeserializationException] should be thrownBy UuidJsonFormat.read(JsObject())
      a[DeserializationException] should be thrownBy UuidJsonFormat.read(JsArray())
      a[DeserializationException] should be thrownBy UuidJsonFormat.read(JsNumber(0))
      a[DeserializationException] should be thrownBy UuidJsonFormat.read(JsNull)
    }

  }

}
