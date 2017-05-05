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

package com.jsherz.luskydive.services

import java.util.UUID

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.jsherz.luskydive.util.Util
import org.scalatest.{Matchers, WordSpec}

class JwtServiceSpec extends WordSpec with Matchers with ScalatestRouteTest {

  implicit val log: LoggingAdapter = Logging(ActorSystem(), getClass)

  val jwtService = new JwtServiceImpl("apples")

  "JwtService" should {

    "reject tokens that aren't signed" in {
      val jwt = fixture("unsigned.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject expired tokens" in {
      val jwt = fixture("expired.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject tokens signed with the wrong algorithm" in {
      val jwt = fixture("wrong_algo.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject tokens signed with the wrong secret" in {
      val jwt = fixture("wrong_secret.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject tokens that are just plain WRONG" in {
      val jwt = fixture("just_plain_wrong.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject tokens with no UUID claim" in {
      val jwt = fixture("no_uuid_claim.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject tokens with a mangled UUID claim" in {
      val jwt = fixture("mangled_uuid.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject tokens that aren't valid yet" in {
      val jwt = fixture("not_valid_yet.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject tokens without a valid from date" in {
      val jwt = fixture("no_valid_from.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "reject tokens without an expiry date" in {
      val jwt = fixture("no_expiry_date.txt")

      jwtService.verifyJwt(jwt) shouldBe None
    }

    "accept a valid token and parse the correct UUID" in {
      val cases = Seq(
        "2ad8c26a-b866-4da0-88ad-7004738cf7c3",
        "8679eb24-94fe-481a-865c-5f47f5cda361",
        "256572a9-c469-42b7-a731-121f9d23a7f4"
      )

      cases.foreach { (uuid) =>
        val jwt = fixture(uuid + ".txt")

        jwtService.verifyJwt(jwt) shouldBe Some(UUID.fromString(uuid))
      }
    }

  }

  private def fixture(name: String): String = {
    Util.rawFixture("JWTs", name).trim
  }

}
