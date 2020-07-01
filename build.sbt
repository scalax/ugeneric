val circe          = (project in file("./modules/circe"))
val circeBenchmark = (project in file("./modules/circe/benchmark")).dependsOn(circe).aggregate(circe)
val slick          = (project in file("./modules/slick"))

UGenericSettings.scalaVersionSetting

<<<<<<< HEAD
addCommandAlias("jmh1", "circeBenchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 ugeneric.circe.benchmark.JsonEncoderBenchmark.*")
addCommandAlias("jmh2", "circeBenchmark/jmh:run -i 20 -wi 20 -f 1 -t 1 ugeneric.circe.benchmark.JsonNonStrictEncoderBenchmark.*")
=======
addCommandAlias("jmh1", "circeBenchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 ugeneric.circe.benchmark.JsonStrictEncoderBenchmark.*")
addCommandAlias("jmh2", "circeBenchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 ugeneric.circe.benchmark.JsonNonStrictEncoderBenchmark.*")
>>>>>>> 2d9a9b97b30771d24ceacd8319a198d13bbde0a4
addCommandAlias("jmh3", "circeBenchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 .*Test02.*")

val ugeneric = (project in file(".")).dependsOn(circe, slick).aggregate(circe, slick)
