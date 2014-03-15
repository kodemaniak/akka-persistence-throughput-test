organization := "kodemaniak"

name := "akka-persistence-throughput-test"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.10.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.0",
  "com.typesafe.akka" %% "akka-kernel" % "2.3.0",
  "com.typesafe.akka" %% "akka-slf4j" % "2.3.0",
  "com.typesafe.akka" %% "akka-persistence-experimental" % "2.3.0",
  "ch.qos.logback" % "logback-classic" % "1.1.1"
)

scalacOptions ++= Seq("-encoding", "UTF-8", "-deprecation", "-unchecked", "-feature")

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

// Include only src/main/java in the compile configuration
unmanagedSourceDirectories in Compile := (scalaSource in Compile).value :: Nil

// Include only src/test/java in the test configuration
unmanagedSourceDirectories in Test := (scalaSource in Test).value :: Nil

fork in run := true
