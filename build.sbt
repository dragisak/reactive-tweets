name := "reactive-tweets"

version := "1.0"

scalaVersion := "2.11.8"

val playVersion = "2.5.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-ws" % playVersion,
  "com.typesafe.play" %% "play-json" % playVersion
)


mainClass := Some("dragisak.workday.Main")
