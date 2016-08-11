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

package com.jsherz.luskydive.itest

import org.scalatest.{Matchers, WordSpec}

class CourseDaoSpec extends WordSpec with Matchers {

  "CourseDao" should {

    "do a thing" in {
      assert(true)
    }

    // get -> return None when course does not exist

    // get -> return Some(correct course with num spaces) when course does exist (one organiser)

    // get -> return Some(correct course with num spaces) when course does exist (both organisers)

    // find -> return Seq() when no courses exist between given dates

    // find -> return Seq(course) when one course is found

    // find -> return Seq(course, course) when multiple courses are found

    // spaces -> return correct spaces, including member information (if present)

    // spaces -> return Seq() for unknown course uuid

  }

}
