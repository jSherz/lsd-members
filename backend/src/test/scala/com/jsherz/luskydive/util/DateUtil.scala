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

import java.sql.{Date, Timestamp}
import java.util.GregorianCalendar

import org.scalatest.{Matchers, WordSpec}

/**
  * Utility method for working with [[java.sql.Date]]s.
  */
object DateUtil {

  /**
    * Create an SQL Date with the given year, month and day.
    *
    * @param year  Year (4 digits plz)
    * @param month One indexed month
    * @param day   Day of month
    */
  def makeDate(year: Int, month: Int, day: Int): Date = {
    new Date(new GregorianCalendar(
      year,
      month - 1, // Java dates are 0 indexed
      day
    ).getTime.getTime)
  }

  /**
    * Create an SQL timestamp at midnight on the given day.
    *
    * @param year  Year (4 digits plz)
    * @param month One indexed month
    * @param day   Day of month
    * @return
    */
  def makeTimestamp(year: Int, month: Int, day: Int): Timestamp = {
    new Timestamp(makeDate(year, month, day).getTime)
  }

}

class DateUtilSpec extends WordSpec with Matchers {

  "DateUtil#makeDate" should {

    "return the correct Date, given a one indexed month" in {
      DateUtil.makeDate(2016, 1, 1) shouldEqual Date.valueOf("2016-01-01")
      DateUtil.makeDate(1998, 12, 5) shouldEqual Date.valueOf("1998-12-05")
      DateUtil.makeDate(2011, 6, 23) shouldEqual Date.valueOf("2011-06-23")
    }

  }

  "DateUtil#makeTimestamp" should {

    "return the correct Timestamp, given a one indexed month" in {
      DateUtil.makeTimestamp(2016, 1, 1) shouldEqual Timestamp.valueOf("2016-01-01 00:00:00")
      DateUtil.makeTimestamp(1998, 12, 5) shouldEqual Timestamp.valueOf("1998-12-05 00:00:00")
      DateUtil.makeTimestamp(2011, 6, 23) shouldEqual Timestamp.valueOf("2011-06-23 00:00:00")
    }

  }

}
