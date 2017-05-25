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

package com.jsherz.luskydive.apis

import java.util.UUID

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive1, Route}
import com.jsherz.luskydive.core.{Member, PackingListItem}
import com.jsherz.luskydive.dao.PackingListItemDao
import com.jsherz.luskydive.json.PackingListJsonSupport._
import com.jsherz.luskydive.json.StrippedPackingListItem

import scalaz.{-\/, \/-}


class PackingListApi(private val authDirective: Directive1[Member],
                     private val dao: PackingListItemDao)
                    (implicit val log: LoggingAdapter) {

  private val getRoute: Route = (get & pathEnd & authDirective) { member =>
    onSuccess(dao.getOrDefault(member.uuid.get)) {
      case \/-(packingListItem: PackingListItem) => complete(stripPackingList(packingListItem))
      case -\/(error: String) =>
        log.error(s"Failed to load packing list for ${member.uuid}: $error")
        complete(StatusCodes.InternalServerError)
    }
  }

  private val upsertRoute: Route = (put & pathEnd & authDirective & entity(as[StrippedPackingListItem])) { (member, packingListItem) =>
    onSuccess(dao.upsert(addUuidToPackingList(member.uuid.get, packingListItem))) {
      case \/-(_) => complete(StatusCodes.OK)
      case -\/(error: String) =>
        log.error(s"Failed to update packing list for ${member.uuid}: $error")
        complete(StatusCodes.InternalServerError)
    }
  }

  val route: Route = pathPrefix("packing-list") {
    getRoute ~
      upsertRoute
  }

  private def stripPackingList(packingListItem: PackingListItem) = {
    StrippedPackingListItem(
      packingListItem.warmClothes,
      packingListItem.sleepingBag,
      packingListItem.sturdyShoes,
      packingListItem.cash,
      packingListItem.toiletries
    )
  }

  private def addUuidToPackingList(uuid: UUID, packingListItem: StrippedPackingListItem) = {
    PackingListItem(
      uuid,
      packingListItem.warmClothes,
      packingListItem.sleepingBag,
      packingListItem.sturdyShoes,
      packingListItem.cash,
      packingListItem.toiletries
    )
  }

}
