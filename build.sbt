name := "reactive-tweets"

version := "1.0"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % "2.4.8",
  "de.heikoseeberger" %% "akka-http-circe" % "1.7.0"
)

mainClass := Some("dragisak.workday.Main")
