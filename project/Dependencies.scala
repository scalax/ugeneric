import sbt._
import sbt.Keys._

object Dependencies {

  val zsgVersion = "0.0.4-SNAP2020101801"
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
    "com.typesafe.slick" %% "slick"         % slickVersion,
    "com.typesafe.slick" %% "slick-codegen" % slickVersion,
    "com.h2database"      % "h2"            % "1.4.200"
  )

  val cpoi = List("net.scalax" %% "poi-collection" % "0.4.0-M8")

  val shapeless = "com.chuusai" %% "shapeless" % "2.3.3"

  val circe_2_11_version = "0.11.2"
  val circe_2_13_version = "0.13.0"
  def circe(scalaVersionM: String) = CrossVersion.partialVersion(scalaVersionM) match {
    case Some((2, 11)) =>
      List(
        "io.circe" %% "circe-derivation" % "0.11.0-M3",
        "io.circe" %% "circe-core"       % circe_2_11_version,
        "io.circe" %% "circe-generic"    % circe_2_11_version,
        "io.circe" %% "circe-parser"     % circe_2_11_version
      )
    case Some((2, _)) =>
      List(
        "io.circe" %% "circe-derivation" % "0.13.0-M4",
        "io.circe" %% "circe-core"       % circe_2_13_version,
        "io.circe" %% "circe-generic"    % circe_2_13_version,
        "io.circe" %% "circe-parser"     % circe_2_13_version
      )
    case _ => List.empty
  }

  val scalaTestVersion = "3.1.0"
  val scalaTest        = List("org.scalatest" %% "scalatest" % scalaTestVersion)

  def upickle(scalaVersion: String) = CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, i)) if i == 12 || i == 13 => List("com.lihaoyi" %% "upickle" % "0.8.0")
    case _                                  => List.empty
  }

}
