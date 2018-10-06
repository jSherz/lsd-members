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

import sbt.Keys._
import wartremover.WartRemover.autoImport._

val scalaV = "2.12.6"
val akkaV = "2.5.16"
val akkaHttpV = "10.1.4"
val scaldiV = "0.5.8"

lazy val commonSettings = Seq(
  organization := "com.jsherz",
  name := "luskydive",
  version := "0.0.0.1",

  scalaVersion := scalaV,
  scalacOptions := Seq(
    "-unchecked",
    "-deprecation",
    "-encoding", "utf8",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-explaintypes",
    "-feature",
    "-Xfatal-warnings",
    "-Ywarn-dead-code",
    "-Ywarn-unused:_",
    "-Xlint:_"
  )
)

lazy val root = (project in file("."))
  .configs(Runtime, Test, IntegrationTest)
  .settings(commonSettings: _*)
  .settings(Defaults.itSettings: _*)
  .settings(
    Revolver.settings,

    wartremoverErrors ++= Warts.all,

    mainClass in(Compile, run) := Some("com.jsherz.luskydive.Main"),
    mainClass in assembly := Some("com.jsherz.luskydive.Main"),

    libraryDependencies ++= {
      Seq(
        // Utils
        "org.scalaz" %% "scalaz-core" % "7.2.26",

        // Web framework
        "com.typesafe.akka" %% "akka-actor" % akkaV,
        "com.typesafe.akka" %% "akka-remote" % akkaV,
        "com.typesafe.akka" %% "akka-slf4j" % akkaV,
        "com.typesafe.akka" %% "akka-testkit" % akkaV,
        "com.typesafe.akka" %% "akka-http-core" % akkaHttpV,
        "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpV,
        "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpV,
        "ch.megard" %% "akka-http-cors" % "0.3.0",

        // Authentication
        "com.auth0" % "java-jwt" % "3.4.0",
        "com.restfb" % "restfb" % "1.40.1",

        // Logging
        "ch.qos.logback" % "logback-classic" % "1.2.3",
        "org.slf4j" % "slf4j-api" % "1.7.21",
        "org.slf4j" % "jul-to-slf4j" % "1.7.25",
        "net.logstash.logback" % "logstash-logback-encoder" % "5.2",

        // DB
        "org.postgresql" % "postgresql" % "42.2.5",
        "org.flywaydb" % "flyway-core" % "5.1.4",
        "com.typesafe.slick" %% "slick" % "3.2.3",
        "com.zaxxer" % "HikariCP" % "2.7.8",

        // Phone number parsing
        "com.googlecode.libphonenumber" % "libphonenumber" % "7.4.1",

        // Sending & receiving SMS
        "com.twilio.sdk" % "twilio" % "7.0.0-rc-27",

        // Testing frameworks
        "org.scalactic" %% "scalactic" % "3.0.5",
        "org.scalatest" %% "scalatest" % "3.0.5" % "it,test",
        "org.mockito" % "mockito-core" % "1.10.19" % "it,test",

        // Utils
        "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.3",

        // Misc (version conflicts)
        "org.scala-lang" % "scala-reflect" % scalaV,
        "org.scala-lang" % "scala-compiler" % scalaV,
        "org.scala-lang" % "scala-library" % scalaV,
        "org.scala-lang.modules" %% "scala-xml" % "1.0.4"
      )
    }
  )
