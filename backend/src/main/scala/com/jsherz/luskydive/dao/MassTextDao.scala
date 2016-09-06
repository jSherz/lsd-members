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

package com.jsherz.luskydive.dao

import java.sql.{Date, Timestamp}

import akka.event.LoggingAdapter
import com.jsherz.luskydive.services.DatabaseService
import com.jsherz.luskydive.util.FutureError._

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/}

/**
  * Handles recording text messages being sent to many members.
  */
trait MassTextDao {

  /**
    * Get the number of members that joined between the given dates.
    *
    * @param startDate Start date (inclusive)
    * @param endDate   End date (exclusive)
    * @return
    */
  def filterCount(startDate: Date, endDate: Date): Future[String \/ Int]

}

class MassTextDaoImpl(protected override val databaseService: DatabaseService)
                     (
                       implicit val ec: ExecutionContext,
                       implicit val log: LoggingAdapter
                     )
  extends Tables(databaseService) with MassTextDao {

  import driver.api._

  /**
    * Get the number of members that joined between the given dates.
    *
    * @param startDate Start date (inclusive)
    * @param endDate   End date (exclusive)
    * @return
    */
  override def filterCount(startDate: Date, endDate: Date): Future[\/[String, Int]] = {
    if (startDate.equals(endDate)) {
      Future.successful(-\/(MassTextDaoErrors.endDateStartDateSame))
    } else if (endDate.before(startDate)) {
      Future.successful(-\/(MassTextDaoErrors.endDateBeforeStartDate))
    } else {
      val startTimestamp = new Timestamp(startDate.getTime)
      val endTimestamp = new Timestamp(endDate.getTime)

      db.run(
        Members.filter { m =>
          m.createdAt >= startTimestamp &&
            m.createdAt < endTimestamp
        }.countDistinct.result
      ) withServerError
    }
  }

}

object MassTextDaoErrors {

  val endDateBeforeStartDate = "error.endDateBeforeStartDate"

  val endDateStartDateSame = "error.endDateStartDateSame"

}
