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
  * Ensures the text message sending utilities work correctly.
  */
class TextMessageUtilSpec extends WordSpec with Matchers {

  "TextMessageUtil#parseTemplate" should {

    "not match any tags that aren't name" in {
      val result = TextMessageUtil.parseTemplate("{{ names }}", "Jonathan")

      result shouldEqual "{{ names }}" + TextMessageUtil.optOut
    }

    "replace the name tag multiple times" in {
      val result = TextMessageUtil.parseTemplate("Hello, {{ name }}. Your name is {{ name}}!", "Michelle")

      result shouldEqual "Hello, Michelle. Your name is Michelle!" + TextMessageUtil.optOut
    }

    "replace tags with spaces around the word 'name'" in {
      val examples = Seq("{{name}}", "{{  name  }}", "{{name }}", "{{ name}}")

      examples.foreach { example =>
        val result = TextMessageUtil.parseTemplate(example, "Sharon")

        result shouldEqual "Sharon" + TextMessageUtil.optOut
      }
    }

  }

}
