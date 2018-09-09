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
import java.util.{Calendar, UUID}

import akka.event.LoggingAdapter
import com.fasterxml.uuid.Generators
import com.jsherz.luskydive.core.{ApiKey, CommitteeMember}
import com.jsherz.luskydive.services.DatabaseService
import com.jsherz.luskydive.util.EitherFutureExtensions._
import com.jsherz.luskydive.util.FutureError._
import com.jsherz.luskydive.util.PasswordHasher

import scala.concurrent.{ExecutionContext, Future}
import scalaz.{-\/, \/, \/-}


trait AuthDao {

  /**
    * Authenticate a user, either returning the Left(error) or Right(memberUuid).
    *
    * @param apiKey
    * @param time Time at which access is required
    * @return
    */
  def authenticate(apiKey: UUID, time: Timestamp): Future[String \/ UUID]

  /**
    * Get the API key with the given UUID / key.
    *
    * @param apiKey
    * @return
    */
  def get(apiKey: UUID): Future[Option[ApiKey]]

  /**
    * Attempt to authenticate a user and generate an API key for them.
    *
    * @param email
    * @param password
    * @param time
    * @return An API key, if login succeeded
    */
  def login(email: String, password: String, time: Timestamp): Future[String \/ UUID]

}

class AuthDaoImpl(protected override val databaseService: DatabaseService)
                 (implicit ec: ExecutionContext, log: LoggingAdapter)
  extends Tables(databaseService) with AuthDao {

  import driver.api._

  /**
    * Number of hours an API key lasts for after being created or reissued.
    */
  val API_KEY_EXPIRES = 24

  /**
    * Authenticate a user, either returning the Left(error) or Right(memberUuid).
    *
    * @param apiKey
    * @param time Time at which access is required
    * @return
    */
  override def authenticate(apiKey: UUID, time: Timestamp): Future[String \/ UUID] = {
    val lookup = db.run(
      ApiKeys.filter(_.uuid === apiKey).join(CommitteeMembers).on(_.committeeMemberUuid === _.uuid).result.headOption
    )

    for {
      lookupResult <- lookup ifNone AuthDaoErrors.invalidApiKey
      validateResult <- Future.successful(lookupResult.flatMap(validateKey(time)))
      authResult <- validateResult withFutureF extendKeyExpiry(time)
    } yield authResult
  }

  /**
    * Test to see if the given API key is valid at the provided time.
    *
    * Checks that the key hasn't expired and the attached committee member hasn't been locked.
    *
    * @param time
    * @param keyAndCommittee
    * @return
    */
  private def validateKey(time: Timestamp)(keyAndCommittee: (ApiKey, CommitteeMember)): String \/ ApiKey = {
    keyAndCommittee match {
      case (apiKey, committeeMember) => {
        // Ensure key hasn't expired
        if (apiKey.expiresAt.after(time)) {
          // Ensure committee member is allowed to login
          if (!committeeMember.locked) {
            \/-(apiKey)
          } else {
            -\/(AuthDaoErrors.accountLocked)
          }
        } else {
          -\/(AuthDaoErrors.invalidApiKey)
        }
      }
    }
  }

  /**
    * Extend the given key's expiry date by the current time + X hours.
    *
    * @param time
    * @param apiKey
    * @return
    */
  private def extendKeyExpiry(time: Timestamp)(apiKey: ApiKey): Future[String \/ UUID] = {
    val newExpiry = addHoursToTimestamp(time, API_KEY_EXPIRES)

    db.run(
      ApiKeys.filter(_.uuid === apiKey.uuid).map(_.expiresAt).update(newExpiry)
    ).map {
      _ => apiKey.committeeMemberUuid
    } withServerError
  }

  /**
    * Get the API key with the given UUID / key.
    *
    * @param apiKey
    * @return
    */
  override def get(apiKey: UUID): Future[Option[ApiKey]] = {
    db.run(ApiKeys.filter(_.uuid === apiKey).result.headOption)
  }

  /**
    * Attempt to authenticate a user and generate an API key for them.
    *
    * @param email
    * @param password
    * @param time
    * @return An API key, if login succeeded
    */
  override def login(email: String, password: String, time: Timestamp): Future[String \/ UUID] = {
    for {
      committeeMember <- db.run(CommitteeMembers.filter(cm => cm.email === email).result.headOption)
      passwordCheckResult <- Future.successful(checkPasswordAndLocked(committeeMember, password))
      apiKeyResult <- passwordCheckResult withFutureF generateApiKey(time)
    } yield apiKeyResult.map(_.uuid)
  }

  /**
    * Create a new [[Timestamp]] by adding a number of hours to an existing one.
    *
    * @param time
    * @param hours
    * @return
    */
  private def addHoursToTimestamp(time: Timestamp, hours: Int): Timestamp = {
    val calendar = Calendar.getInstance()
    calendar.setTime(time)
    calendar.add(Calendar.HOUR, API_KEY_EXPIRES)

    new Timestamp(calendar.getTime.getTime)
  }

  /**
    * Ensure that:
    *
    * - maybeCommitteeMember is defined
    * - the password given is correct
    * - the committee member isn't locked
    *
    * @param maybeCommitteeMember
    * @param password
    * @return
    */
  private def checkPasswordAndLocked(maybeCommitteeMember: Option[CommitteeMember], password: String): String \/ CommitteeMember = {
    maybeCommitteeMember match {
      case Some(committeeMember) => {
        PasswordHasher.verifyPassword(password, committeeMember.password, committeeMember.salt) match {
          case true => {
            if (committeeMember.locked) {
              -\/(AuthDaoErrors.accountLocked)
            } else {
              \/-(committeeMember)
            }
          }
          case false => -\/(AuthDaoErrors.invalidEmailPass)
        }
      }
      case None => -\/(AuthDaoErrors.invalidEmailPass)
    }
  }

  /**
    * Generate a new API key that's valid for [[API_KEY_EXPIRES]] hours.
    *
    * @param time
    * @param committeeMember
    * @return
    */
  private def generateApiKey(time: Timestamp)(committeeMember: CommitteeMember): Future[String \/ ApiKey] = {
    val key = Generators.randomBasedGenerator.generate
    val createdAt = time
    val expiresAt = addHoursToTimestamp(createdAt, API_KEY_EXPIRES)

    val createdKey = ApiKey(key, committeeMember.uuid, createdAt, expiresAt)

    db.run(
      (ApiKeys += createdKey).map(_ => createdKey)
    ) withServerError
  }

}

object AuthDaoErrors {

  val invalidApiKey = "error.invalidApiKey"

  val accountLocked = "error.accountLocked"

  val internalService = "error.internalService"

  val invalidEmailPass = "error.invalidEmailPass"

}
