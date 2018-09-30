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

import java.time.{Duration, Instant}

import akka.actor.ActorSystem
import akka.event.{Logging, LoggingAdapter}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.jsherz.luskydive.dao._
import com.jsherz.luskydive.services._
import com.jsherz.luskydive.util.{Config, FbClientFactoryImpl}
import com.restfb.Version
import org.slf4j.bridge.SLF4JBridgeHandler

import scala.concurrent.ExecutionContextExecutor

/**
  * The main application service that bootstraps all other components and runs the HTTP server.
  */
object Main extends App with Config {

  val bootStarted = Instant.now()

  SLF4JBridgeHandler.install()

  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val executor: ExecutionContextExecutor = actorSystem.dispatcher
  val log: LoggingAdapter = Logging(actorSystem, getClass)
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val actorBootTime = Duration.between(bootStarted, Instant.now())
  log.info(s"Actor system startup took ${actorBootTime.toMillis} ms")

  val databaseService = new DatabaseService(dbUrl, dbUsername, dbPassword)

  val memberDao = new MemberDaoImpl(databaseService)
  val committeeMemberDao = new CommitteeMemberDaoImpl(databaseService)
  val courseSpaceDao = new CourseSpaceDaoImpl(databaseService)
  val courseDao = new CourseDaoImpl(databaseService, committeeMemberDao, courseSpaceDao)
  val authDao = new AuthDaoImpl(databaseService)
  val massTextDao = new MassTextDaoImpl(databaseService)
  val textMessageDao = new TextMessageDaoImpl(databaseService)
  val packingListDao = new PackingListItemDaoImpl(databaseService)

  val fbClientFactory = new FbClientFactoryImpl(Version.VERSION_2_9)
  val socialService = new SocialServiceImpl(fbClientFactory, fbAppId, fbSecret, loginReturnUrl)
  val jwtService = new JwtServiceImpl(jwtSecret)

  val httpService = new HttpService(memberDao, courseDao, committeeMemberDao, courseSpaceDao, authDao, massTextDao,
    textMessageDao, textMessageReceiveApiKey, socialService, jwtService, packingListDao)

  Http().bindAndHandle(httpService.routes, interface, port)

  val bootTime = Duration.between(bootStarted, Instant.now())
  log.info(s"Startup took ${bootTime.toMillis} ms")

}
