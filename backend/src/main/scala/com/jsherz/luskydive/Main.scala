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
import java.util.logging.{LogManager, Logger}

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.services._
import com.jsherz.luskydive.util.{Config, FbClientFactoryImpl}
import com.restfb.{DefaultFacebookClient, Version}
import org.flywaydb.core.Flyway
import org.slf4j.bridge.SLF4JBridgeHandler

import scala.io.{Codec, Source}

/**
  * The main application service that bootstraps all other components and runs the HTTP server.
  */
object Main extends App with Config {

  val bootStarted = Instant.now()


  implicit val actorSystem = ActorSystem()
  implicit val executor = actorSystem.dispatcher
  implicit val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer = ActorMaterializer()

  // What is an application without an ASCII-art banner?
  log.info(Source.fromURL(getClass.getResource("/banner.txt"))(Codec.UTF8).mkString)

  SLF4JBridgeHandler.install()

  log.info("SLF4J bridge created")

  val databaseService = new DatabaseService(dbUrl, dbUsername, dbPassword)

  log.info("Checking for and running migrations")

  // Automatically run migrations
  val flyway = new Flyway()
  flyway.setDataSource(dbUrl, dbUsername, dbPassword)
  flyway.migrate()

  log.info("Migrations complete")

  val memberDao = new MemberDaoImpl(databaseService)
  val committeeMemberDao = new CommitteeMemberDaoImpl(databaseService)
  val courseSpaceDao = new CourseSpaceDaoImpl(databaseService)
  val courseDao = new CourseDaoImpl(databaseService, committeeMemberDao, courseSpaceDao)
  val authDao = new AuthDaoImpl(databaseService)
  val massTextDao = new MassTextDaoImpl(databaseService)
  val textMessageDao = new TextMessageDaoImpl(databaseService)

  val fbClientFactory = new FbClientFactoryImpl(Version.VERSION_2_9)
  val socialService = new SocialServiceImpl(fbClientFactory, fbAppId, fbSecret, loginReturnUrl)
  val jwtService = new JwtServiceImpl(jwtSecret)

  val httpService = new HttpService(memberDao, courseDao, committeeMemberDao, courseSpaceDao, authDao, massTextDao,
    textMessageDao, textMessageReceiveApiKey, socialService, jwtService)

  Http().bindAndHandle(httpService.routes, interface, port)

  val bootTime = Duration.between(bootStarted, Instant.now())
  log.info(s"Startup took ${bootTime.toMillis} ms")

}
