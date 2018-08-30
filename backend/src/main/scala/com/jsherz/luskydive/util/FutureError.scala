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

import akka.event.LoggingAdapter

import scalaz.{-\/, \/, \/-}
import scala.concurrent.{ExecutionContext, Future}

object FutureError {

  implicit def futureOptionEitherConversion[T](future: Future[Option[T]])(implicit log: LoggingAdapter): FutureOptionError[T] = {
    new FutureOptionError(future)
  }

  implicit def futureEitherConversion[T](future: Future[T])(implicit log: LoggingAdapter): FutureError[T] = {
    new FutureError(future)
  }

}

/**
  * A [[Future[Option[T]] that can be combined with an error message to produce an [[String \/ T]].
  *
  * @param future Future to wrap with our custom error
  * @tparam V Type of value produced when the future executes
  */
class FutureOptionError[V](future: Future[Option[V]])(implicit val log: LoggingAdapter) {

  /**
    * If future returns None, error with errorMessage.
    *
    * If it fails, error with the generic internal server error.
    *
    * @param errorMessage
    * @param ec
    * @return
    */
  def ifNone(errorMessage: String)(implicit ec: ExecutionContext): Future[String \/ V] = {
    future.map {
      case Some(value: V @unchecked) => \/-(value)
      case None => -\/(errorMessage)
    }.recover {
      case ex: Throwable =>
        log.error(ex.getMessage)
        -\/(Errors.internalServer)
    }
  }

}

class FutureError[V](future: Future[V])(implicit val log: LoggingAdapter) {

  /**
    * Catch any [[Throwable]] caused by a [[Future]] failing and replace it with a generic error message.
    *
    * @param ec
    * @return
    */
  def withServerError()(implicit ec: ExecutionContext): Future[String \/ V] = {
    future.map {
      \/-(_)
    }.recover {
      case ex: Throwable =>
        log.error(ex.getMessage)
        -\/(Errors.internalServer)
    }
  }

}
