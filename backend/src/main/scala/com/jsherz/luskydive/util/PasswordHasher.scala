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

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import java.util.Base64
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
  * Password utility functions.
  */
object PasswordHasher {

  val NUM_ITERATIONS = 25000

  val KEY_LENGTH = 512

  val KEY_ALGO = "PBKDF2WithHmacSHA512"

  /**
    * Based on the OWASP Java guide (https://www.owasp.org/index.php/Hashing_Java).
    *
    * @param password
    * @param salt
    * @return
    */
  def hashPassword(password: String, salt: String): String = {
    val saltBytes = Base64.getDecoder.decode(salt)

    try {
      val skf = SecretKeyFactory.getInstance(KEY_ALGO)
      val spec = new PBEKeySpec(password.toCharArray, saltBytes, NUM_ITERATIONS, KEY_LENGTH)
      val key = skf.generateSecret(spec)

      val withTwoEquals = Base64.getEncoder.encodeToString(key.getEncoded)

      withTwoEquals.substring(0, withTwoEquals.length - 1)
    } catch {
      case ex @ (_: NoSuchAlgorithmException | _: InvalidKeySpecException) =>
        throw new RuntimeException(ex);
    }
  }

  /**
    * Check that a password matches the given hash in constant time.
    *
    * @param password
    * @param salt
    * @param correctHash
    * @return
    */
  def verifyPassword(password: String, correctHash: String, salt: String): Boolean = {
    var matches = true

    val hash = hashPassword(password, salt)

    if (hash.length != correctHash.length) {
      matches = false
    }

    (hash.toCharArray zip correctHash.toCharArray).foreach { case (a: Char, b: Char) =>
      if (a != b) {
        matches = false
      }
    }

    matches
  }

}
