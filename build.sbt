name := """aura-voice"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "com.google.code.gson" % "gson" % "2.3",
  "com.googlecode.json-simple" % "json-simple" % "1.1.1",
  "org.apache.httpcomponents" % "httpclient" % "4.3.6",
  "postgresql" % "postgresql" % "9.1-901-1.jdbc4"
)
