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

import java.util.concurrent.TimeUnit

import Helpers._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.{Futures, ScalaFutures}
import org.scalatestplus.play.{HtmlUnitFactory, OneBrowserPerSuite, OneServerPerSuite, PlaySpec}
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * A base class for running tests. Ensure the database is clean before every test.
  */
class BaseSpec extends PlaySpec with OneServerPerSuite with OneBrowserPerSuite with HtmlUnitFactory with BeforeAndAfterEach
  with Futures with ScalaFutures {
  /**
    * The number of seconds that cleaning the database can take.
    */
  private val DB_CLEAN_TIMEOUT = 30

  /**
    * A duration that represents the maximum amount of time cleaning the database can take.
    */
  private val DB_CLEAN_MAX_DURATION =  Duration.create(DB_CLEAN_TIMEOUT, TimeUnit.SECONDS)

  /**
    * Override the default application so that we can customise it below.
    */
  implicit override lazy val app = createTestApplication()

  /**
    * Clean the database before every spec / test.
    */
  override def beforeEach: Unit =
    Await.result(cleanDatabase(app.injector.instanceOf[DatabaseConfigProvider]), DB_CLEAN_MAX_DURATION)
}
