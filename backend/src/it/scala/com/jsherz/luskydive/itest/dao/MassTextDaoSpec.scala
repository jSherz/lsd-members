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

package com.jsherz.luskydive.itest.dao

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.jsherz.luskydive.core.{MassText, TextMessage}
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util.Util
import com.jsherz.luskydive.itest.util.DateUtil
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.time.{Seconds, Span}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import com.jsherz.luskydive.json.MassTextsJsonSupport._

import scala.concurrent.ExecutionContext.Implicits.global
import scalaz.{-\/, \/-}

class MassTextDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: MassTextDao = _
  private var textMessageDao: TextMessageDao = _

  implicit val patienceConfig = PatienceConfig(scaled(Span(1, Seconds)))

  override protected def beforeAll(): Unit = {
    implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)
    val dbService = Util.setupGoldTestDb()

    dao = new MassTextDaoImpl(dbService)
    textMessageDao = new TextMessageDaoImpl(dbService)
  }

  "MassTextDao#get" should {

    "return None if the given UUID does not exist" in {
      val result = dao.get(UUID.fromString("318edeb0-f8cd-49fe-a581-1d03252f390e")).futureValue

      result shouldEqual \/-(None)
    }

    "return Some(massText) if the given UUID matches a mass text" in {
      val result = dao.get(UUID.fromString("0e02581c-df85-4200-bedd-55a667740486")).futureValue

      result.isRight shouldBe true
      result.map {
        _ shouldEqual Some(Util.fixture[MassText]("0e02581c.json"))
      }
    }

  }

  "MassTextDao#filterCount" should {

    "return 0 if no members are matched between the given dates" in {
      val result = dao.filterCount(DateUtil.makeDate(1850, 1, 1), DateUtil.makeDate(1851, 1, 1)).futureValue

      result.isRight shouldBe true
      result.map { count =>
        count shouldEqual 0
      }
    }

    "return the correct number of members, filtered correctly" in {
      val examples = Map(
        (DateUtil.makeDate(2008, 12, 13), DateUtil.makeDate(2010, 4, 12)) -> 156,
        (DateUtil.makeDate(2009, 11, 16), DateUtil.makeDate(2009, 11, 17)) -> 2
      )

      examples.foreach { case (dates, expectedCount) =>
        val result = dao.filterCount(dates._1, dates._2).futureValue

        result.isRight shouldBe true
        result.map { count =>
          count shouldEqual expectedCount
        }
      }
    }

    "return an error if the end date is before the start date" in {
      val result = dao.filterCount(DateUtil.makeDate(2010, 5, 4), DateUtil.makeDate(2010, 4, 4)).futureValue

      result.isLeft shouldBe true
      result.map { error =>
        error shouldEqual MassTextDaoErrors.endDateBeforeStartDate
      }
    }

    "return an error if the end date and start date are the same" in {
      val result = dao.filterCount(DateUtil.makeDate(2015, 12, 20), DateUtil.makeDate(2015, 12, 20)).futureValue

      result.isLeft shouldBe true
      result.map { error =>
        error shouldEqual MassTextDaoErrors.endDateStartDateSame
      }
    }

  }

}
