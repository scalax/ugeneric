val circe          = (project in (file(".") / "modules" / "circe"))
val circeBenchmark = (project in (file(".") / "modules" / "circe" / "benchmark")).dependsOn(circe).aggregate(circe)
val slick          = (project in (file(".") / "modules" / "slick"))
val poi            = (project in (file(".") / "modules" / "poi")).dependsOn(circe)

UGenericSettings.scalaVersionSetting

addCommandAlias("jmh1", "circeBenchmark/jmh:run -i 30 -wi 30 -f 1 -t 1 ugeneric.circe.benchmark.JsonStrictEncoderBenchmark.*")
addCommandAlias("jmh2", "circeBenchmark/jmh:run -i 150 -wi 150 -f 1 -t 1 ugeneric.circe.benchmark.JsonNonStrictEncoderBenchmark.*")
addCommandAlias("jmh3", "circeBenchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 ugeneric.circe.benchmark.JsonSealedEncoderBenchmark.*")

val ugeneric = (project in file(".")).dependsOn(circe, slick, poi).aggregate(circe, slick, poi)
