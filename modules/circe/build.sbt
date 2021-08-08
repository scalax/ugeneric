UGenericSettings.scalaSetting
UGenericSettings.scala_211_212_213_cross
UGenericSettings.scalaVersionSetting

libraryDependencies ++= Dependencies.zsg
libraryDependencies += Dependencies.shapeless
libraryDependencies ++= Dependencies.circe(scalaVersion.value)
libraryDependencies ++= Dependencies.scalaTest.map(_ % Test)
