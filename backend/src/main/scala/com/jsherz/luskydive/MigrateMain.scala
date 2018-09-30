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

package com.jsherz.luskydive

import com.jsherz.luskydive.util.Config
import org.flywaydb.core.Flyway
import org.slf4j.bridge.SLF4JBridgeHandler
import org.slf4j.{Logger, LoggerFactory}

/**
  * The main application service that bootstraps all other components and runs the HTTP server.
  */
object MigrateMain extends App with Config {

  SLF4JBridgeHandler.install()

  private val log: Logger = LoggerFactory.getLogger(getClass)

  val flyway = new Flyway()
  flyway.setDataSource(dbUrl, dbUsername, dbPassword)
  val numMigrations = flyway.migrate()

  log.info(s"Ran $numMigrations migrations.")

}
