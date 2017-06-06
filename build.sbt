name := "hashtag-analyzer"

version := "1.0"

scalaVersion := "2.11.8"

val akkaVersion = "2.4.16"
val scalaTestVersion = "3.0.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" % "akka-actor_2.11" % akkaVersion,
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
)

libraryDependencies += "com.quantifind" % "wisp_2.11" % "0.0.4"
libraryDependencies += "com.danielasfregola" % "twitter4s_2.11" % "5.1"
