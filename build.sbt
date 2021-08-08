val rootFile       = file(".")
val modulesFile    = rootFile / "modules"
val circeFile      = modulesFile / "circe"
val circe          = project in circeFile
val circeBenchmark = (project in circeFile / "benchmark").dependsOn(circe).aggregate(circe)
val slick          = project in modulesFile / "slick"
// val poi            = (project in (file(".") / "modules" / "poi")).dependsOn(circe)

UGenericSettings.scalaVersionSetting

addCommandAlias("jmh1", "circeBenchmark/jmh:run -i 120 -wi 120 -f 1 -t 1 ugeneric.circe.benchmark.JsonStrictEncoderBenchmark.*")
addCommandAlias("jmh2", "circeBenchmark/jmh:run -i 120 -wi 120 -f 1 -t 1 ugeneric.circe.benchmark.JsonNonStrictEncoderBenchmark.*")
addCommandAlias("jmh3", "circeBenchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 ugeneric.circe.benchmark.JsonSealedEncoderBenchmark.*")

addCommandAlias("fmt", "all scalafmtSbt scalafmt test:scalafmt")

val ugeneric = (project in rootFile).dependsOn(circe, slick).aggregate(circe, slick)
