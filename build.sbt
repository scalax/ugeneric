val currentScalaVersion = "2.13.0"
val scala_2_12_Version  = "2.12.10"

scalaVersion := scala_2_12_Version
crossScalaVersions := Seq(scala_2_12_Version, currentScalaVersion)

transitiveClassifiers := Seq("sources")

resolvers += Resolver.bintrayRepo("scalax", "asuna")

libraryDependencies ++= Seq("org.scalax" %% "asuna-macros"      % "0.0.2-SNAP20191113.1")
libraryDependencies ++= Seq("org.scalax" %% "asuna-scala-tuple" % "0.0.2-SNAP20191113.1")
libraryDependencies += "com.chuusai" %% "shapeless" % "2.3.3"

val circeVersion = "0.12.2"
val circeDependencies = ("io.circe" %% "circe-derivation" % "0.12.0-M7") +: Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

libraryDependencies ++= circeDependencies

val scalaTestVersion = "3.1.0-RC3"
libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % scalaTestVersion)
