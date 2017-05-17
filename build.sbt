name := "hashtag-analyzer"

version := "1.0"

scalaVersion := "2.12.2"

val akkaVersion = "2.4.18"
val scalaTestVersion = "3.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.12" % "2.4.18",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "com.danielasfregola" %% "twitter4s" % "5.1")