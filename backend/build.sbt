organization  := "com.jsherz"

name          := "luskydive"

version       := "0.0.0.1"

scalaVersion  := "2.11.8"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  Seq(
    // Web framework
    "com.datasift.dropwizard.scala" %% "dropwizard-scala-core" % "0.9.3-2",

    // Database interaction
    "com.datasift.dropwizard.scala" %% "dropwizard-scala-jdbi" % "0.9.3-1",
    "org.postgresql"                %  "postgresql"            % "9.4.1208",

    // Phone number parsing
    "com.googlecode.libphonenumber" % "libphonenumber" % "7.4.1",

    // Testing frameworks
    "org.scalactic" %% "scalactic" % "2.2.6",
    "org.scalatest" %% "scalatest" % "2.2.6" % "test",

    // Misc
    "org.scala-lang.modules" %% "scala-xml" % "1.0.4"
  )
}

Revolver.settings
