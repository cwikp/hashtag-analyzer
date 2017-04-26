name := "hashtag-analyzer"

version := "1.0"

scalaVersion := "2.12.2"

val akkaVersion = "2.5.0"
val scalaTestVersion = "3.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test")