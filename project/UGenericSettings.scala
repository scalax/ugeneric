import sbt._
import sbt.Keys._

object UGenericSettings {

  val versionSetting = Seq(version := "0.0.1-20200430SNAP1", organization := "org.scalax")

  val currentScalaVersion = "2.13.2"
  val scala_2_12_Version  = "2.12.11"

  val scalaVersionSetting = scalaVersion := currentScalaVersion

  val scalaSetting = Seq(
    scalaVersionSetting,
    crossScalaVersions := Seq(scala_2_12_Version, currentScalaVersion),
    transitiveClassifiers := Seq("sources"),
    org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true,
    publishArtifact in packageDoc := false
  )

}
