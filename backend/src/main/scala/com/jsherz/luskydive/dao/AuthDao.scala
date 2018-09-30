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
import com.jsherz.luskydive.services.DatabaseService
import com.jsherz.luskydive.util.PasswordHasher
import scalaz.{-\/, \/, \/-}

import scala.concurrent.{ExecutionContext, Future}


trait AuthDao {

  def login(email: String, password: String): Future[String \/ CommitteeMember]

}

class AuthDaoImpl(protected override val databaseService: DatabaseService)
                 (implicit ec: ExecutionContext)
  extends Tables(databaseService) with AuthDao {

  import driver.api._

  /**
    * Attempt to authenticate and return their
    */
  override def login(email: String, password: String): Future[String \/ CommitteeMember] = {
    for {
      committeeMember <- db.run(CommitteeMembers.filter(cm => cm.email === email).result.headOption)
      passwordCheckResult = checkPasswordAndLocked(committeeMember, password)
    } yield passwordCheckResult match {
      case \/-(cm) => \/-(cm)
      case -\/(error) => -\/(error)
    }
  }

  /**
    * Ensure that:
    *
    * - maybeCommitteeMember is defined
    * - the password given is correct
    * - the committee member isn't locked
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

}

object AuthDaoErrors {

  val invalidApiKey = "error.invalidApiKey"

  val accountLocked = "error.accountLocked"

  val internalService = "error.internalService"

  val invalidEmailPass = "error.invalidEmailPass"

}
