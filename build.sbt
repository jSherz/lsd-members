name := """play-scala"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "com.twilio.sdk" % "twilio-java-sdk" % "3.4.5",
  "net.greghaines" % "jesque" % "2.1.1",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "com.typesafe.slick" %% "slick-codegen" % "3.1.1",
  "org.postgresql" % "postgresql" % "9.4.1208"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"
