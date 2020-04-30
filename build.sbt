val circe          = (project in file("./modules/circe"))
val circeBenchmark = (project in file("./modules/circe/benchmark")).dependsOn(circe).aggregate(circe)
val slick          = (project in file("./modules/slick"))

UGenericSettings.scalaVersionSetting

addCommandAlias("jmh1", "circeBenchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 .*Test01.*")
addCommandAlias("jmh2", "circeBenchmark/jmh:run -i 3 -wi 3 -f 1 -t 1 .*Test02.*")
