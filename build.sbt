val currentScalaVersion = "2.13.0"
val scala_2_12_Version  = "2.12.10"

scalaVersion := currentScalaVersion
crossScalaVersions := Seq(scala_2_12_Version, currentScalaVersion)

resolvers += Resolver.bintrayRepo("scalax", "asuna")

libraryDependencies ++= Seq("org.scalax" %% "asuna-macros" % "0.0.2-SNAP20191109.1")
libraryDependencies ++= Seq("org.scalax" %% "asuna-scala-tuple" % "0.0.2-SNAP20191109.1")
libraryDependencies ++= Seq("org.scalax" %% "asuna" % "0.0.2-SNAP20191109.1")
libraryDependencies ++= Seq("org.scalax" %% "asuna-testkit" % "0.0.2-SNAP20191109.1")