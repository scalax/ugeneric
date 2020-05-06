import sbt._
import sbt.Keys._

object UGenericSettings {

  val versionSetting = Seq(version := "0.0.1-20200505SNAP1", organization := "org.scalax")

  val currentScalaVersion = "2.13.2"
  val scala_212_Version  = "2.12.11"
  val scala_211_Version  = "2.11.12"

  val scalaVersionSetting        = scalaVersion := currentScalaVersion
  val scala_212_213_cross      = crossScalaVersions := Seq(scala_212_Version, currentScalaVersion)
  val scala_211_212_213_cross = crossScalaVersions := Seq(scala_211_Version, scala_212_Version, currentScalaVersion)

  val scalaSetting = Seq(
    scalaVersionSetting,
    transitiveClassifiers := Seq("sources"),
    org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true,
    publishArtifact in packageDoc := false
  )

}
