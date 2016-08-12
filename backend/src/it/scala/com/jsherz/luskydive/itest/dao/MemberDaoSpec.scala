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

import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.itest.util.Util
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Ensures the member DAO works correctly with the golden test DB.
  */
class MemberDaoSpec extends WordSpec with Matchers with BeforeAndAfterAll {

  private var dao: MemberDao = _

  override protected def beforeAll(): Unit = {
    val dbService = Util.setupGoldTestDb()

    dao = new MemberDaoImpl(databaseService = dbService)
  }

  "MemberDao#memberExists" should {

    "return false when no phone number or e-mail is given" in {
      val exists = dao.memberExists(None, None)

      exists shouldBe false
    }

    "return true when matching phone number is given" in {
      val exists = dao.memberExists(Some("+447155728581"), None)

      exists shouldBe true
    }

    "return true when matching e-mail is given" in {
      val exists = dao.memberExists(None, Some("nelsonbryan@matthews-mosley.org"))

      exists shouldBe true
    }

    "returns true when matching phone number and e-mail are given" in {
      val exists = dao.memberExists(Some("+447156850760"), Some("destiny82@gmail.com"))

      exists shouldBe true
    }

  }

  "MemberDao#create" should {

    // create -> throws exception if no phone number or e-mail given?

    // create -> generates valid uuid and inserts member (lookup /w returned uuid, confirm info)

    // create -> uuid is different every time

  }

}
