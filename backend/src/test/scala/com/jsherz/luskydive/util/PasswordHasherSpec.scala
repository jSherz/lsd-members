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

package com.jsherz.luskydive.util

import org.scalatest.{Matchers, WordSpec}

/**
  * Ensures passwords are hashed and checked correctly.
  */
class PasswordHasherSpec extends WordSpec with Matchers {

  "PasswordHasher#hashPassword" should {

    "return the correct hash" in {
      val examples = Map(
        "maryjames@gmail.com_Hunter2" -> (
          "lz5VSmAjYpxZFnxFlsub2NcpS5SQmHd4uj8JMtn3YcIHR7suYauAEJ46Z8Z6W+t52SGAbFjLwusrtS6cKmLu1w=",
          "bm0N4TwHYGwtBcCYU+o9x/g/hzAGAOAcA4Bwbk3pfQ8="
        ),
        "gregory33@hotmail.com_Hunter2" -> (
          "d/pSUTYujZOzznhtHSUE6dzL4rQZ7e6x01RqYRFX8E+Lt7G3aX29LyWq+DxgPSA45rPBPxzxeAPqnFqGEA7wzg=",
          "MqZ0zrl37j0HYCzlPKeUUsoZQ+j9fw/BOKe0dm6tVeo="
        ),
        "miranda05@walker-snyder.info_Hunter2" -> (
          "gfVp7W32rXGgvyCaKBoGjpMGXY95nxpKBSJ3ecVeW+qZVtJCqLrKPa0SczUGrUJr+O+Srl3XiJnmCyOFAF6vuQ=",
          "cg4BwFhLCSEkxNjbe48R4ryXUiol5Nx7DyGEsJYSQug="
        )
      )

      examples.foreach { case (password, hashAndSalt) =>
        PasswordHasher.hashPassword(password, hashAndSalt._2) shouldEqual hashAndSalt._1
      }
    }

  }

  "PasswordHasher#verifyPassword" should {

    "not accept incorrect passwords" in {
      val incorrectExamples = Map(
        "" -> (
          "exqgKWjV+XysTcyUDasLgCS0QqaFB1LAXTx7KBSIP0TvFZdurFhdTwKQN4NB86zhZYdhV6OzAVQSG4+9tjcsgA=",
          "xDhnjBGitPbe+x9jLOWcszZmTEmpVSplrNU6h7B2jpE="
        ),
        "S3CR3T" -> (
          "BN9YOQiylM8GnYZsCrWP84dOZUd5jrHd4VRmv/FTL8txSW9VGxKgrEXyr60rv2waYHuZ1ee9lrcXy0AF9IB3tA=",
          "Ped8r9X6Pyck5Pw/Z8yZU0qJEcK4d+5dK6UUIgTAeM8="
        ),
        "Passw0rd123" -> (
          "hwwYAFdt5yD/Y9VHoJg6WhMzswh1uhYe5W1SUE8EfEfdYR06MrVAK7bxXbiN9hIn08Z46o0iUVZfxa8Nbo2H9w=",
          "IGQsJeS8l/J+713LWctZK2Ws9R6jlFLqfe89Z4yx1to="
        )
      )

      incorrectExamples.foreach { case (password, hashAndSalt) =>
        PasswordHasher.verifyPassword(password, hashAndSalt._1, hashAndSalt._2) shouldBe false
      }
    }

    "accept valid passwords" in {
      val validExamples = Map(
        "sarah22@holt.biz_Hunter2" -> (
          "u9S7shooXdu4i6aa4/8FYO454PV3t5Nf4PFtWUkxiNv0aYPs9Lhs4IpTwHacdcDN/OAIrUcaZGWabEqadSSVkA=",
          "rxWidG4NIcT4HyMEAMAYozQmZOw9p9T637tXylkLAQA="
        ),
        "jensensusan@rodriguez.org_Hunter2" -> (
          "0jquTqj2juVUyAPsqIusSvF1aPaDaaFJd57Ul0aaxEfVbnE6+3qADK40A8vBX7qgCVBWLj7cUgwEj8bbI7wtAw=",
          "RqhVivG+N+b8nxPiXOs9xxjDuBdi7B1jrBWidI5x7h0="
        ),
        "ahubbard@robinson.com_Hunter2" -> (
          "b71lrYOMUtdAwIqoEM6E9rGV9HgoiaQYB4yPeCWiAwswydyjv07xCgr5lAGPTkeOnd046AWlyWBu3Q+NdC5f5Q=",
          "f29NaQ1BqHVujbG2llJqTam1dm5N6T3nXGsNAWAsRcg="
        )
      )

      validExamples.foreach { case (password, hashAndSalt) =>
        PasswordHasher.verifyPassword(password, hashAndSalt._1, hashAndSalt._2) shouldBe true
      }
    }

  }

}
