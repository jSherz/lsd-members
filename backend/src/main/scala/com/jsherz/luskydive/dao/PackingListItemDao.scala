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

package com.jsherz.luskydive.dao

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import com.jsherz.luskydive.core.PackingListItem
import com.jsherz.luskydive.services.DatabaseService
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{ExecutionContext, Future}


trait PackingListItemDao {

  def getOrDefault(uuid: UUID): Future[PackingListItem]

  def upsert(packingListItem: PackingListItem): Future[Int]

}

class PackingListItemDaoImpl(override protected val databaseService: DatabaseService)
                            (implicit ec: ExecutionContext)
  extends Tables(databaseService) with PackingListItemDao {

  import driver.api._

  private val log: Logger = LoggerFactory.getLogger(getClass)

  override def getOrDefault(uuid: UUID): Future[PackingListItem] = {
    db.run(
      PackingListItems.filter(_.uuid === uuid)
        .result
        .headOption
        .map(packingListOrDefault(uuid))
    )
  }

  private def packingListOrDefault(uuid: UUID)(packingListItem: Option[PackingListItem]) = {
    packingListItem.fold(defaultPackingListFor(uuid))(identity)
  }

  private def defaultPackingListFor(uuid: UUID): PackingListItem = {
    PackingListItem(uuid, warmClothes = false, sleepingBag = false, sturdyShoes = false, cash = false,
      toiletries = false)
  }

  override def upsert(packingListItem: PackingListItem): Future[Int] = {
    db.run(PackingListItems.insertOrUpdate(packingListItem))
  }

}
