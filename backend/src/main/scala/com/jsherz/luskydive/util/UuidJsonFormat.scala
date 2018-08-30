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

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

/**
  * Used to (de)serialize [[UUID]]s.
  */
object UuidJsonFormat extends RootJsonFormat[UUID] {

  /**
    * Take a raw UUID string and parse it into a UUID.
    *
    * @param json
    * @return
    */
  override def read(json: JsValue): UUID = json match {
    case JsString(rawUuid) => UUID.fromString(rawUuid)
    case _ => throw new DeserializationException("UUID must be a string in the format 98774829-b6a5-4299-b87c-d6038bacaf8e")
  }

  /**
    * Produces a JsString in the following format:
    *
    * 98774829-b6a5-4299-b87c-d6038bacaf8e
    *
    * @param obj
    * @return
    */
  override def write(obj: UUID): JsValue = {
    JsString(obj.toString)
  }

}
