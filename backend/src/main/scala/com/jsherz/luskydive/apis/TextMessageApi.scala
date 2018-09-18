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

package com.jsherz.luskydive.apis

import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.UUID

import akka.event.LoggingAdapter
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directive1
import akka.http.scaladsl.server.Directives.{complete, _}
import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core._
import com.jsherz.luskydive.dao.{MemberDao, TextMessageDao}
import com.jsherz.luskydive.json.TextMessageJsonSupport._

import scala.concurrent.ExecutionContext


/**
  * Handles querying of text messages & incoming messages delivered by a provider.
  */
class TextMessageApi(textMessageDao: TextMessageDao,
                     memberDao: MemberDao,
                     validApiKey: String,
                     committeeAuthDirective: Directive1[(Member, CommitteeMember)])
                    (implicit ec: ExecutionContext,
                     log: LoggingAdapter) {

  val receiveRoute = (path("receive" / Remaining) & post & formFields('To, 'From, 'Body, 'MessageSid)) {
    (apiKey: String, to: String, from: String, body: String, externalSid: String) =>
      if (apiKey.isEmpty || !validApiKey.equals(apiKey)) {
        complete(StatusCodes.Unauthorized, "Unauthorized.")
      } else {
        // Find member and, if found, insert text message
        onSuccess(memberDao.forPhoneNumber(from)) {
          case Some(member: Member) => {
            val message = buildMessage(member.uuid, to, from, body, externalSid)

            onSuccess(textMessageDao.insert(message)) { uuid =>
              log.info(s"Text message with SID $externalSid saved for member ${member.uuid}")

              val entity = HttpEntity(ContentTypes.`text/xml(UTF-8)`,
                s"""<?xml version="1.0" encoding="UTF-8"?><!-- Recorded as ${uuid.toString} --><Response></Response>"""
              )

              complete(StatusCodes.OK, entity)
            }
          }
          case None => {
            log.info(s"No member found with the phone number $from - not saving message $body.")

            complete(StatusCodes.NotFound, TextMessageApiErrors.receiveMemberNotFound)
          }
        }
      }
  }

  val receivedRoute = (path("received") & get) {
    committeeAuthDirective { _ =>
      onSuccess(textMessageDao.getReceived()) { texts =>
        complete(texts)
      }
    }
  }

  val receivedCountRoute = (path("num-received") & get) {
    committeeAuthDirective { _ =>
      onSuccess(textMessageDao.getReceivedCount()) { numReceived =>
        complete(NumReceivedMessages(numReceived))
      }
    }
  }

  val route = pathPrefix("text-messages") {
    receiveRoute ~
      receivedRoute ~
      receivedCountRoute
  }

  private def buildMessage(memberUuid: UUID, to: String, from: String, body: String, externalSid: String) = {
    val uuid = Generators.randomBasedGenerator.generate
    val now = Timestamp.valueOf(LocalDateTime.now())

    TextMessage(
      uuid,
      memberUuid,
      None,
      TextMessageStatuses.Received,
      to,
      from,
      body,
      Some(externalSid),
      fromMember = true,
      now,
      now
    )
  }

}

object TextMessageApiErrors {

  val receiveMissingParams = "Must specify To, From, Body and MessageSid request parameters."
  val receiveMemberNotFound = "Member not found with the supplied From number."

}
