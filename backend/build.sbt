organization  := "com.jsherz"

version       := "0.1"

scalaVersion  := "2.11.6"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    // Spray framework
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "io.spray"            %%  "spray-json"    % "1.3.2",

    // Akka actors
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",

    // TODO: Remove old testing
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",

    // Database interaction
    "com.typesafe.slick" %% "slick"      % "3.1.1",
    "org.postgresql"     %  "postgresql" % "9.4.1208",

    // Phone number parsing
    "com.googlecode.libphonenumber" % "libphonenumber" % "7.4.1",

    // Testing frameworks
    "org.scalactic" %% "scalactic" % "2.2.6",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  )
}

Revolver.settings
