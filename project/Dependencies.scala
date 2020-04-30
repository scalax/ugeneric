import sbt._
import sbt.Keys._

object Dependencies {

  val fsg = Seq(
    resolvers += Resolver.bintrayRepo("scalax", "asuna"),
    libraryDependencies ++= List(
      "org.scalax" %% "asuna-macros"      % "0.0.3-20200428SNAP7",
      "org.scalax" %% "asuna-scala-tuple" % "0.0.3-20200428SNAP7"
    )
  )

  val slickVersion = "3.3.2"
  val slick = List(
    "com.typesafe.slick" %% "slick"               % slickVersion,
    "com.typesafe.slick" %% "slick-codegen"       % slickVersion,
    "mysql"              % "mysql-connector-java" % "8.0.17"
  )

  val shapeless = "com.chuusai" %% "shapeless" % "2.3.3"

  val circeDependencies: Def.Setting[Seq[ModuleID]] = libraryDependencies ++= {
    if (scalaVersion.value startsWith "2.11.") {
      ("io.circe" %% "circe-derivation" % "0.11.0-M3") :: List(
        "io.circe" %% "circe-core",
        "io.circe" %% "circe-generic",
        "io.circe" %% "circe-parser"
      ).map(_ % "0.11.2")
    } else {
      ("io.circe" %% "circe-derivation" % "0.13.0-M4") :: List(
        "io.circe" %% "circe-core",
        "io.circe" %% "circe-generic",
        "io.circe" %% "circe-parser"
      ).map(_ % "0.13.0")
    }
  }

  val scalaTestVersion = "3.1.0"
  val scalaTest        = List("org.scalatest" %% "scalatest" % scalaTestVersion)

  val upickle = Seq("com.lihaoyi" %% "upickle" % "0.8.0")

}
