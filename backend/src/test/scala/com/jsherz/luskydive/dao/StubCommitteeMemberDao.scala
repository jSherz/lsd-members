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

package com.jsherz.luskydive.dao

import java.util.UUID

import com.jsherz.luskydive.core.CommitteeMember
import com.jsherz.luskydive.util.Util
import com.jsherz.luskydive.json.StrippedCommitteeMember
import com.jsherz.luskydive.json.CommitteeMembersJsonSupport._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * An implementation of [[CommitteeMemberDao]] that returns canned values.
  */
class StubCommitteeMemberDao extends CommitteeMemberDao {

  /**
    * Get active committee members, sorted by name.
    *
    * @return
    */
  override def active(): Future[Seq[StrippedCommitteeMember]] = {
    Future(StubCommitteeMemberDao.activeMembers)
  }

  /**
    * Get a committee member with the given UUID.
    *
    * @param uuid
    * @return
    */
  override def get(uuid: UUID): Future[Option[CommitteeMember]] = {
    if (StubCommitteeMemberDao.foundMember.uuid.get.equals(uuid)) {
      Future(Some(StubCommitteeMemberDao.foundMember))
    } else if (StubCommitteeMemberDao.notFoundMemberUuid.equals(uuid)) {
      Future(None)
    } else {
      throw new RuntimeException("Unknown UUID used with stub")
    }
  }

}

object StubCommitteeMemberDao {

  val activeMembers = Util.fixture[Seq[StrippedCommitteeMember]]("active.json")

  val foundMember = Util.fixture[CommitteeMember]("0d717b7.json")
  val notFoundMemberUuid = UUID.fromString("e0048a33-0624-4855-844c-91419c29c380")

}
