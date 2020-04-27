val currentScalaVersion = "2.13.1"
val scala_2_12_Version  = "2.12.10"

scalaVersion := currentScalaVersion
crossScalaVersions := Seq(scala_2_12_Version, currentScalaVersion)

transitiveClassifiers := Seq("sources")
org.scalafmt.sbt.ScalafmtPlugin.autoImport.scalafmtOnCompile := true

val circe     = (project in file("./modules/circe"))
val benchmark = (project in file("./ugeneric-benchmark")).dependsOn(circe).aggregate(circe)

addCommandAlias("jmh1", "benchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 .*Test01.*")
addCommandAlias("jmh2", "benchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 .*Test02.*")
