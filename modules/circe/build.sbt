Dependencies.fsg
libraryDependencies += Dependencies.shapeless

libraryDependencies ++= Dependencies.circe(scalaVersion.value)

UGenericSettings.scalaSetting

libraryDependencies ++= Dependencies.scalaTest.map(_ % Test)

resolvers += Resolver.bintrayRepo("djx314", "maven")
libraryDependencies += "net.scalax" %% "poi-collection" % "0.4.0-M8"
