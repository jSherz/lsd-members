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

import java.sql.Timestamp

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

/**
  * (de)serializes JSON to / from [[java.sql.Timestamp]]s.
  */
object TimestampJsonFormat extends RootJsonFormat[Timestamp] {

  /**
    * Read a timestamp in the format:
    *
    * JsString(yyyy-mm-dd hh:mm:ss[.fffffffff])
    *
    * @param json
    * @return
    */
  override def read(json: JsValue): Timestamp = json match {
    case JsString(rawTimestamp) => Timestamp.valueOf(rawTimestamp)
    case _ => throw new DeserializationException("invalid timestamp format, must be yyyy-mm-dd hh:mm:ss[.fffffffff]")
  }

  /**
    * Write out a timestamp into the following format:
    *
    * yyyy-mm-dd hh:mm:ss.fffffffff
    *
    * @param source
    * @return
    */
  override def write(source: Timestamp): JsValue = {
    JsString(source.toString)
  }

}
