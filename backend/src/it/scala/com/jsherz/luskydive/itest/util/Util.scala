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

package com.jsherz.luskydive.itest.util

import java.nio.file.Paths

import com.jsherz.luskydive.Main._
import com.jsherz.luskydive.services.DatabaseService
import org.flywaydb.core.Flyway

import scala.io.{Codec, Source}

/**
  * Ensures the database is setup correctly before each test.
  */
object Util {

  import spray.json._

  implicit val codec: Codec = Codec.UTF8

  /**
    * Wipe the test database, run migrations and then load fresh test data.
    */
  def setupGoldTestDb(): DatabaseService = {
    implicit val codec = Codec.UTF8

    val cleanupSql = Source.fromURL(getClass.getResource("/clean-test-db.sql")).mkString
    val goldenSql = Source.fromURL(getClass.getResource("/test-data.sql")).mkString

    val service = new DatabaseService(dbUrl, dbUsername, dbPassword)

    service
      .db
      .createSession
      .conn
      .prepareStatement(cleanupSql)
      .execute()

    val flyway = new Flyway()
    flyway.setDataSource(dbUrl, dbUsername, dbPassword)
    flyway.migrate()

    service
      .db
      .createSession
      .conn
      .prepareStatement(goldenSql)
      .execute()

    service
  }

  /**
    * Parse a JSON fixture into an object.
    *
    * The path to the fixture is resolved as:
    *
    * res/fixtures/T/path
    *
    * e.g.
    *
    * res/fixtures/CourseWithOrganisers/ed89a51d.json
    *
    * @param path
    * @tparam T
    * @return
    */
  def fixture[T: JsonReader](path: String)(implicit jsonFormat: JsonFormat[T], t: reflect.Manifest[T]): T = {
    val fullPath = Paths.get("/fixtures", t.toString.split("\\.").last.replace("]", ""), path).toString
    val resourceUrl = getClass.getResource(fullPath)
    val raw = Source.fromURL(resourceUrl).mkString

    if (raw == null) {
      throw new RuntimeException(s"Cannot find fixture '${fullPath}'")
    }

    raw.parseJson.convertTo[T]
  }

}
