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

package com.jsherz.luskydive.itest.dao

import java.util.UUID

import akka.event.LoggingAdapter
import com.jsherz.luskydive.core.PackingListItem
import com.jsherz.luskydive.dao.{PackingListItemDao, PackingListItemDaoImpl}
import com.jsherz.luskydive.itest.util.{NullLogger, TestDatabase, Util}
import org.scalatest.concurrent.ScalaFutures._
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}
import scalaz.\/-

import scala.concurrent.ExecutionContext.Implicits.global


class PackingListItemDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: PackingListItemDao = _
  private var cleanup: () => Unit = _

  override protected def beforeAll(): Unit = {
    implicit val log: LoggingAdapter = new NullLogger

    val TestDatabase(dbService, cleanupFn) = Util.setupGoldTestDb()
    cleanup = cleanupFn

    dao = new PackingListItemDaoImpl(databaseService = dbService)
  }

  override protected def afterAll(): Unit = cleanup()

  "PackingListItemDao#getOrDefault" should {

    "return an empty record when no packing list data is present" in {
      val memberUuid = UUID.fromString("d2807349-5bae-48b4-9e41-9f886fb7f53f")

      val result = dao.getOrDefault(memberUuid)

      result.futureValue shouldEqual \/-(PackingListItem(memberUuid, warmClothes = false, sleepingBag = false,
        sturdyShoes = false, cash = false, toiletries = false))
    }

    "return the stored information for a member" in {
      val result = dao.getOrDefault(UUID.fromString("29b8cb2f-c3fb-4730-8abc-6d81b74fbf4f"))

      result.futureValue shouldEqual \/-(PackingListItem(
        UUID.fromString("29b8cb2f-c3fb-4730-8abc-6d81b74fbf4f"),
        warmClothes = true,
        sleepingBag = true,
        sturdyShoes = false,
        cash = false,
        toiletries = true
      ))
    }

  }

  "PackingListItemDao#upsert" should {

    "create a new entry if one does not exist" in {
      val uuid = UUID.fromString("4cae160a-9e79-4651-950b-954ab51f2964")

      val item = PackingListItem(
        uuid,
        warmClothes = true,
        sleepingBag = false,
        sturdyShoes = true,
        cash = false,
        toiletries = false
      )

      // Empty (doesn't exist)
      dao.getOrDefault(uuid).futureValue shouldEqual \/-(PackingListItem(
        uuid,
        warmClothes = false,
        sleepingBag = false,
        sturdyShoes = false,
        cash = false,
        toiletries = false
      ))

      dao.upsert(item).futureValue shouldEqual \/-(1)

      dao.getOrDefault(uuid).futureValue shouldEqual \/-(item)
    }

    "updates an existing entry" in {
      val uuid = UUID.fromString("0bb9831c-ef3b-4cf6-bbed-5a58c559d5ed")

      val item = PackingListItem(
        uuid,
        warmClothes = true,
        sleepingBag = false,
        sturdyShoes = true,
        cash = true,
        toiletries = false
      )

      dao.getOrDefault(uuid).futureValue shouldEqual \/-(item)

      val updated = PackingListItem(
        uuid,
        warmClothes = false,
        sleepingBag = true,
        sturdyShoes = false,
        cash = false,
        toiletries = true
      )

      dao.upsert(updated).futureValue shouldEqual \/-(1)

      dao.getOrDefault(uuid).futureValue shouldEqual \/-(updated)
    }

  }

}
