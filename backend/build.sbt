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

import sbt.Keys._
import wartremover.WartRemover.autoImport._

val scalaV = "2.11.8"
val akkaV = "2.5.0"
val akkaHttpV = "10.0.5"
val scaldiV = "0.5.7"
val metricsV = "3.1.0"

lazy val commonSettings = Seq(
  organization  := "com.jsherz",
  name          := "luskydive",
  version       := "0.0.0.1",

  scalaVersion  := scalaV,
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")
)

lazy val root = (project in file("."))
  .configs(Runtime, Test, IntegrationTest)
  .settings(commonSettings: _*)
  .settings(Defaults.itSettings: _*)
  .settings(
    parallelExecution in IntegrationTest := false,

    Revolver.settings,

    wartremoverErrors ++= Warts.all,

    mainClass in (Compile, run) := Some("com.jsherz.luskydive.Main"),
    mainClass in assembly := Some("com.jsherz.luskydive.Main"),

    libraryDependencies ++= {
      Seq(
        // Utils
        "org.scalaz" %% "scalaz-core" % "7.2.4",

        // Web framework
        "com.typesafe.akka" %% "akka-actor"                        % akkaV,
        "com.typesafe.akka" %% "akka-remote"                       % akkaV,
        "com.typesafe.akka" %% "akka-slf4j"                        % akkaV,
        "com.typesafe.akka" %% "akka-testkit"                      % akkaV,
        "com.typesafe.akka" %% "akka-http-core"                    % akkaHttpV,
        "com.typesafe.akka" %% "akka-http-testkit"                 % akkaHttpV,
        "com.typesafe.akka" %% "akka-http-spray-json"              % akkaHttpV,
        "ch.megard" %% "akka-http-cors" % "0.2.1",

        // Logging
        "ch.qos.logback" % "logback-classic" % "1.1.3",
        "org.slf4j"      % "slf4j-api"       % "1.7.21",

        // DB
        "org.postgresql"     %  "postgresql"  % "9.4.1208",
        "org.flywaydb"       %  "flyway-core" % "4.0.3",
        "com.typesafe.slick" %% "slick"       % "3.1.1",
        "com.zaxxer"         %  "HikariCP"    % "2.4.7",

        // Phone number parsing
        "com.googlecode.libphonenumber" % "libphonenumber" % "7.4.1",

        // Sending & receiving SMS
        "com.twilio.sdk" % "twilio" % "7.0.0-rc-27",

        // Testing frameworks
        "org.scalactic" %% "scalactic"    % "2.2.6",
        "org.scalatest" %% "scalatest"    % "2.2.6"   % "it,test",
        "org.mockito"   %  "mockito-core" % "1.10.19" % "it,test",

        // Utils
        "com.fasterxml.uuid" % "java-uuid-generator" % "3.1.3",

        // Misc (version conflicts)
        "org.scala-lang"         %  "scala-reflect"  % scalaV,
        "org.scala-lang"         %  "scala-compiler" % scalaV,
        "org.scala-lang"         %  "scala-library"  % scalaV,
        "org.scala-lang.modules" %% "scala-xml"      % "1.0.4"
      )
    }
  )
