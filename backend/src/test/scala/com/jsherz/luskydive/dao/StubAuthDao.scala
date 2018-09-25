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

import com.jsherz.luskydive.core.CommitteeMember
import com.jsherz.luskydive.itest.util.Util
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport.CommitteeMemberFormat
import scalaz.{-\/, \/, \/-}

import scala.concurrent.Future

/**
  * Returns canned responses simulating the real [[AuthDaoImpl]].
  */
class StubAuthDao extends AuthDao {

  override def login(email: String, password: String): Future[String \/ CommitteeMember] = {
    if (email == StubAuthDao.validEmail && password == StubAuthDao.validPassword) {
      Future.successful(\/-(StubAuthDao.validCommitteeMember))
    } else if (email == StubAuthDao.invalidEmail && password == StubAuthDao.invalidPassword) {
      Future.successful(-\/(AuthDaoErrors.invalidEmailPass))
    } else if (email == StubAuthDao.accountLockedEmail && password == StubAuthDao.accountLockedPassword) {
      Future.successful(-\/(AuthDaoErrors.accountLocked))
    } else {
      throw new RuntimeException("unknown value used with stub")
    }
  }

}

object StubAuthDao {

  val validCommitteeMember = Util.fixture[CommitteeMember]("f59c7cd7.json")
  val validEmail = "trainseveryday@gmail.com"
  val validPassword = "1jj1j18wfjjjaa"

  val invalidEmail = "foot@bathsemporiums.zorg"
  val invalidPassword = "88888888eight"

  val accountLockedEmail = "fun@fun.fun"
  val accountLockedPassword = "LockStock00"

}
