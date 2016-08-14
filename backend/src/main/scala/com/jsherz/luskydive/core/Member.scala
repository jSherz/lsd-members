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

import java.sql.{Date, Timestamp}
import java.util.UUID

/**
  * The main member class, representing a person that has provided us with a phone number or e-mail address.
  *
  * @param uuid        Unique ID
  * @param name        Peron's (first) name
  * @param phoneNumber Phone number
  * @param email       E-mail address
  * @param lastJump    The date the member last jumped, if applicable
  * @param createdAt   When the member was created
  * @param updatedAt   When the member's record was last updated
  */
case class Member(uuid: Option[UUID], name: String, phoneNumber: Option[String], email: Option[String], lastJump: Option[Date],
                  createdAt: Timestamp, updatedAt: Timestamp)
