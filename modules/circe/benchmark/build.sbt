UGenericSettings.scalaSetting
UGenericSettings.scalaVersionSetting
libraryDependencies ++= Dependencies.upickle(scalaVersion = scalaVersion.value)
enablePlugins(JmhPlugin)
name := "circe-benchmark"
