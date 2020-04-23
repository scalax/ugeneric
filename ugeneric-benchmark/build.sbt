scalaVersion := "2.13.1"

val circeVersion = "0.12.2"

val circeDependencies = ("io.circe" %% "circe-derivation" % "0.12.0-M7") +: Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

val upickleDependencies = Seq("com.lihaoyi" %% "upickle" % "0.8.0")

libraryDependencies ++= circeDependencies
libraryDependencies ++= upickleDependencies

enablePlugins(JmhPlugin)
