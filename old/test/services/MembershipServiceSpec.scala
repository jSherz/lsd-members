import dao.MemberDAO
import models.Member
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import services.MembershipService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

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

/**
  * Tests the member creation service.
  */
class MembershipServiceSpec extends BaseSpec with MockitoSugar {

  val realService = app.injector.instanceOf[MembershipService]

  val testMember = Member(None, "Stephanie Bloggs", Some("07123123123"), None)

  "MembershipService" should {
    "return a generic error if the member is not valid" in {
      val invalidMember = Member(None, "Stephanie Bloggs", None, None)

      realService.signup(invalidMember).futureValue mustBe Left("error.generic")
    }

    "return the correct error if a member already exists with the given phone number or e-mail" in {
      val memberDao = mock[MemberDAO]
      when(memberDao.exists(testMember.phoneNumber, testMember.email)).thenReturn(Future(true))
      val mockedService = new MembershipService(memberDao)

      mockedService.signup(testMember).futureValue mustBe Left("error.memberExists")
    }

    "return the new member (with ID) if one was created successfully" in {
      val memberWithId = testMember.copy(id = Some(5))

      val memberDao = mock[MemberDAO]
      when(memberDao.exists(testMember.phoneNumber, testMember.email)).thenReturn(Future(false))
      when(memberDao.insert(testMember)).thenReturn(Future(memberWithId.id.get))
      val mockedService = new MembershipService(memberDao)

      mockedService.signup(testMember).futureValue mustBe Right(memberWithId)
    }

    "returns a generic error if an exception occurred while trying to find the member" in {
      val memberDao = mock[MemberDAO]
      when(memberDao.exists(testMember.phoneNumber, testMember.email)).thenReturn(Future { throw new RuntimeException("") })
      val mockedService = new MembershipService(memberDao)

      mockedService.signup(testMember).futureValue mustBe Left("error.generic")
    }

    "returns a generic error if an exception occurred while trying to add the member" in {
      val memberDao = mock[MemberDAO]
      when(memberDao.exists(testMember.phoneNumber, testMember.email)).thenReturn(Future(false))
      when(memberDao.insert(testMember)).thenReturn(Future { throw new RuntimeException("") })
      val mockedService = new MembershipService(memberDao)

      mockedService.signup(testMember).futureValue mustBe Left("error.generic")
    }
  }

}
