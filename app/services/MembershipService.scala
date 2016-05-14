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

package services

import javax.inject._

import dao.MemberDAO
import models.Member
import net.greghaines.jesque.client.ClientImpl
import net.greghaines.jesque.{ConfigBuilder, Job}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
  * Handles the creation of members.
  */
@Singleton
class MembershipService @Inject() (memberDao: MemberDAO) {
  /**
    * Create a new member with the given information. Ensures that either a phone number or e-mail address was provided.
    *
    * @param member Raw member data (from the form)
    * @return Either an error message or the same member record that was provided
    */
  def signup(member: Member): Future[Either[String, Member]] = {
    if (member.valid()) {
      // Attempt to find a member by the phone number or e-mail given
      val memberExists = memberDao.exists(member.phoneNumber, member.email)

      memberExists.map { exists =>
        if (exists) {
          Left("error.memberExists")
        } else {
          memberDao.insert(member).map { memberId =>
            val args = new java.util.ArrayList[Int]()
            args.add(memberId)

            val job = new Job(Queues.SIGNUP_ACTION, args)

            val client = new ClientImpl(config)
            client.enqueue(Queues.SIGNUP, job)
            client.end()
          }

          Right(member)
        }
      }
    } else {
      Future(Left("error.generic"))
    }
  }

  /**
    * The Jesque configuration.
    */
  private val config = new ConfigBuilder().build()
}
