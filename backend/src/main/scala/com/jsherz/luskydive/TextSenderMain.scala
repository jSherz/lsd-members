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

package com.jsherz.luskydive

import java.time.{Duration, Instant}

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.stream.ActorMaterializer
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.services.{DatabaseService, TextSendingService}
import com.jsherz.luskydive.util.Config
import com.twilio.http.TwilioRestClient
import org.flywaydb.core.Flyway

import scala.io.{Codec, Source}

/**
  * Runs as a daemon to send SMS' to members.
  */
object TextSenderMain extends App with Config {

  val bootStarted = Instant.now()


  implicit val actorSystem = ActorSystem()
  implicit val executor = actorSystem.dispatcher
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer = ActorMaterializer()

  // What is an application without an ASCII-art banner?
  implicit val codec = Codec.UTF8
  log.info(Source.fromURL(getClass.getResource("/banner.txt")).mkString)

  log.info("Connecting to DB")

  val databaseService = new DatabaseService(dbUrl, dbUsername, dbPassword)

  log.info("Running migrations")

  // Automatically run migrations
  val flyway = new Flyway()
  flyway.setDataSource(dbUrl, dbUsername, dbPassword)
  flyway.migrate()

  log.info("Creating text sending service")

  // val massTextDao = new MassTextDaoImpl(databaseService)
  val textMessageDao = new TextMessageDaoImpl(databaseService)

  val twilioClient = new TwilioRestClient.Builder(twilioAccountSid, twilioAuthToken).build()

  val bootTime = Duration.between(bootStarted, Instant.now())
  log.info(s"Startup took ${bootTime.toMillis} ms")

  val textSendingService = new TextSendingService(textMessageDao, twilioClient, twilioMessagingServiceSid)

}
