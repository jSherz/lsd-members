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

package com.jsherz.luskydive.core

import java.sql.Timestamp
import java.util.UUID

/**
  * A text message that will be or has been sent to a member.
  */
case class TextMessage(
                        uuid: Option[UUID],
                        memberUuid: UUID,
                        massTextUuid: Option[UUID],
                        status: Short,
                        toNumber: String,
                        fromNumber: String,
                        message: String,
                        // When the message is sent, this holds the external service's ID for the message
                        // e.g. Twilio's "msid"
                        externalId: Option[String],
                        fromMember: Boolean,
                        createdAt: Timestamp,
                        updatedAt: Timestamp
                      )

object TextMessageStatuses {

  val Pending: Short = 0
  val Sent: Short = 1
  val Error: Short = 2
  val Received: Short = 3

}
