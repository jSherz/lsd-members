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

import org.scalatest.{Matchers, WordSpec}


class TextMessageDaoSpec extends WordSpec with Matchers {

  "TextMessageDao#all" should {

    "return text messages in the correct order" is (pending)

  }

  "TextMessageDao#get" should {

    "return None if no message was found" is (pending)

    "return Some(message) if a message was found" is (pending)

  }

  "TextMessageDao#insert" should {

    "add the message with the correct information" is (pending)

    "add messages with no associated mass text" is (pending)

    "add messages with no associated external ID" is (pending)

  }

  "TextMessageDao#forMember" should {

    "return the correct messages, in order, for a member" is (pending)

    "return an empty list when the member does not exist" is (pending)

  }

  "TextMessageDao#update" should {

    "update the correct record and only that record" is (pending)

  }

}
