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

package com.jsherz.luskydive.util

import scalaz.{-\/, \/}

import scala.concurrent.{ExecutionContext, Future}

object EitherFutureExtensions {

  implicit def toEitherWithError[X](either: String \/ X): EitherWithError[X] = {
    new EitherWithError(either)
  }

}

/**
  * Either a [[String]] error, or an [[X]].
  *
  * @param either
  * @tparam X
  */
class EitherWithError[X](either: String \/ X) {

  /**
    * If the wrapped [[\/]] is Right, return Right(f(rightVal)), otherwise return Future(Left(leftVal).
    *
    * @param f
    * @return
    */
  def withFutureF[V](f: X => Future[String \/ V])(implicit ec: ExecutionContext): Future[String \/ V] = {
    either.fold(error => Future.successful(-\/(error)), f(_))
  }

}
