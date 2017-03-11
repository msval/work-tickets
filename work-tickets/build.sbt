name := """work-tickets"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.7",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test
)

