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

import java.sql.Timestamp
import java.util.UUID

import com.jsherz.luskydive.core.ApiKey

import scala.concurrent.Future
import scalaz.{-\/, \/, \/-}
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Returns canned responses simulating the real [[AuthDaoImpl]].
  */
class StubAuthDao extends AuthDao {

  /**
    * Authenticate a user, either returning the Left(error) or Right(memberUuid).
    *
    * @param apiKey
    * @param time Time at which access is required
    * @return
    */
  override def authenticate(apiKey: UUID, time: Timestamp): Future[\/[String, UUID]] = {
    if (apiKey.equals(StubAuthDao.validApiKey)) {
      Future.successful(\/-(StubAuthDao.validMemberUuid))
    } else if (apiKey.equals(StubAuthDao.invalidApiKey)) {
      Future.successful(-\/(AuthDaoErrors.invalidApiKey))
    } else {
      throw new RuntimeException("unknown value used with stub")
    }
  }

  /**
    * Get the API key with the given UUID / key.
    *
    * @param apiKey
    * @return
    */
  override def get(apiKey: UUID): Future[Option[ApiKey]] = {
    if (apiKey.equals(StubAuthDao.validApiKey)) {
      Future.successful(Some(StubAuthDao.exampleApiKey))
    } else if (apiKey.equals(StubAuthDao.invalidApiKey)) {
      Future.successful(None)
    } else {
      throw new RuntimeException("unknown value used with stub")
    }
  }

  /**
    * Attempt to authenticate a user and generate an API key for them.
    *
    * @param email
    * @param password
    * @param time
    * @return An API key, if login succeeded
    */
  override def login(email: String, password: String, time: Timestamp): Future[\/[String, UUID]] = {
    if (email == StubAuthDao.validEmail && password == StubAuthDao.validPassword) {
      Future.successful(\/-(StubAuthDao.validApiKey))
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

  val validApiKey = UUID.fromString("dfbb4f63-8082-4d4e-820e-46835223478b")
  val validMemberUuid = UUID.fromString("1fddbe40-cf0f-48d3-bd23-b564691001e5")

  val exampleApiKey = ApiKey(validApiKey, UUID.fromString("18cb4209-df83-4202-94fb-e6a2f7f92c8d"),
    Timestamp.valueOf("2017-04-09 10:15:09.141"), Timestamp.valueOf("2017-04-10 10:15:09.141"))

  val invalidApiKey = UUID.fromString("2bbd5d19-37f0-4801-b1f5-cf3c043b117f")

  val validEmail = "trainseveryday@gmail.com"
  val validPassword = "1jj1j18wfjjjaa"

  val invalidEmail = "foot@bathsemporiums.zorg"
  val invalidPassword = "88888888eight"

  val accountLockedEmail = "fun@fun.fun"
  val accountLockedPassword = "LockStock00"

}
