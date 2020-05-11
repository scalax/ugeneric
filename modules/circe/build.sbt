UGenericSettings.scalaSetting
UGenericSettings.scala_211_212_213_cross
UGenericSettings.scala211VersionSetting

Dependencies.fsg
libraryDependencies += Dependencies.shapeless

libraryDependencies ++= Dependencies.circe(scalaVersion.value)

libraryDependencies ++= Dependencies.scalaTest.map(_ % Test)

//resolvers += Resolver.bintrayRepo("djx314", "maven")
//libraryDependencies += "net.scalax" %% "poi-collection" % "0.4.0-M8"
