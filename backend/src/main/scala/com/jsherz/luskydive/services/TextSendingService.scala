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

package com.jsherz.luskydive.services

import java.util.concurrent.TimeUnit

import akka.event.LoggingAdapter
import com.jsherz.luskydive.core.TextMessageStatuses
import com.jsherz.luskydive.dao.TextMessageDao
import com.twilio.`type`.PhoneNumber
import com.twilio.http.TwilioRestClient
import com.twilio.rest.api.v2010.account.Message

import scala.concurrent.{Await, Awaitable}
import scala.concurrent.duration.Duration
import scalaz.{-\/, \/-}

/**
  * Finds texts to send, sends them. Repeats.
  */
class TextSendingService(val textMessageDao: TextMessageDao,
                         val twilioClient: TwilioRestClient,
                         val messagingServiceSid: String)
                        (implicit log: LoggingAdapter) {

  val errorWaitTime = 10000
  val okWaitTime = 1000
  val idleWaitTime = 30000
  val messageCreationWaitTime = 1000

  while (true) {

    log.info("Starting sending process...")

    val textsToSend = sync(textMessageDao.toSend())

    textsToSend match {
      case \/-(texts) => {
        log.info(s"Got ${texts.length} texts to send.")

        for (text <- texts) {
          try {
            log.info(s"Attempting to send text ${text.uuid.get} to ${text.toNumber}")

            val message = Message.create(
              new PhoneNumber(text.toNumber),
              messagingServiceSid,
              text.message
            )

            val result = message.execute(twilioClient)

            log.info(s"Sent message - ${result.getSid}")

            sync(textMessageDao.update(text.copy(
              externalId = Some(result.getSid),
              status = TextMessageStatuses.Sent
            )))

            log.info(s"Updated text message ${text.uuid.get} to have status Sent")

            try {
              log.info("Trying to get message from number (sleeping first)")

              Thread.sleep(messageCreationWaitTime)

              val messageResult = Message.fetch(result.getSid).execute(twilioClient)

              //noinspection ScalaStyle
              if (messageResult != null) {
                log.info("Found message to update our record with.")

                sync(textMessageDao.update(text.copy(
                  externalId = Some(result.getSid),
                  status = TextMessageStatuses.Sent,
                  fromNumber = messageResult.getFrom.toString
                )))

                log.info(s"Updated our records with from number ${messageResult.getFrom}")
              } else {
                log.error(s"Could not find message with ${result.getSid} to get from number")
              }
            } catch {
              case ex: Exception => {
                log.error(ex, "Failed to get from number for message.")
              }
            }

            log.info("Sleeping...")
            Thread.sleep(okWaitTime)
          } catch {
            case ex: Exception => {
              log.error(ex, "Failed to send text.")

              sync(textMessageDao.update(text.copy(
                status = TextMessageStatuses.Error
              )))

              log.info(s"Updated text message ${text.uuid.get} to have status Error.")
            }
          }
        }
      }
      case -\/(error) => {
        log.error("Failed to get texts to send: " + error)
        Thread.sleep(errorWaitTime)
      }
    }

    log.info("All done - snoozing!")
    Thread.sleep(idleWaitTime)
  }

  private def sync[T](await: Awaitable[T]): T = {
    Await.result(await, Duration(1, TimeUnit.SECONDS))
  }

}
