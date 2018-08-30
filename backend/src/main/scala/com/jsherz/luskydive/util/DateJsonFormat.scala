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

import java.sql.Date
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

/**
  * A formatter that converts JSON values to and from [[java.sql.Date]]s.
  */
object DateJsonFormat extends RootJsonFormat[Date] {

  private val formatter = DateTimeFormatter.ISO_DATE

  /**
    * We accept and read in dates in the following format:
    *
    * YYYY-MM-DD
    *
    * Where MM is a one indexed month.
    *
    * @param json
    * @return
    */
  override def read(json: JsValue): Date = json match {
    case JsString(rawDate) => Date.valueOf(LocalDate.from(formatter.parse(rawDate)))
    case _ => throw new DeserializationException("Invalid date format - use YYYY-MM-DD")
  }

  /**
    * We write out dates into the following format:
    *
    * YYYY-MM-DD
    *
    * @param date
    * @return
    */
  override def write(date: Date): JsValue = {
    JsString(formatter.format(date.toLocalDate))
  }

}
