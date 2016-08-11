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

package com.jsherz.luskydive.itest

import com.jsherz.luskydive.Main._
import com.jsherz.luskydive.services.DatabaseService

import scala.io.{Codec, Source}

/**
  * Ensures the database is setup correctly before each test.
  */
object Util {

  /**
    * Load the golden database SQL file into our test database.
    */
  def setupGoldTestDb(): DatabaseService = {
    implicit val codec = Codec.UTF8
    val goldenSql = Source.fromURL(getClass.getResource("/test-golden-v1.sql")).mkString

    val service = new DatabaseService(dbUrl, dbUsername, dbPassword)

    service
      .db
      .createSession
      .conn
      .prepareStatement(goldenSql)
      .execute()

    service
  }

}
