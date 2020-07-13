import sbt._
import sbt.Keys._

object Dependencies {

  val zsgVersion = "0.0.4-SNAP2020071301"
  val zsg = Seq(
    resolvers += Resolver.bintrayRepo("scalax", "zsg"),
    libraryDependencies ++= List(
      "org.scalax" %% "zsg-macros" % zsgVersion,
      "org.scalax" %% "zsg-debug"  % zsgVersion // ,
      // "org.scalax" %% "zsg-scala-tuple" % zsgVersion
    )
  )

  val slickVersion = "3.3.2"
  val slick = List(
    "com.typesafe.slick" %% "slick"               % slickVersion,
    "com.typesafe.slick" %% "slick-codegen"       % slickVersion,
    "mysql"              % "mysql-connector-java" % "8.0.17"
  )

  val cpoi = List("net.scalax" %% "poi-collection" % "0.4.0-M8")

  val shapeless = "com.chuusai" %% "shapeless" % "2.3.3"

  def circe(scalaVersionM: String) =
    if (scalaVersionM startsWith "2.11.") {
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

  val scalaTestVersion = "3.1.0"
  val scalaTest        = List("org.scalatest" %% "scalatest" % scalaTestVersion)

  def upickle(scalaVersion: String) =
    if (scalaVersion.startsWith("2.12") || scalaVersion.startsWith("2.13")) {
      Seq("com.lihaoyi" %% "upickle" % "0.8.0")
    } else {
      List.empty
    }

}
