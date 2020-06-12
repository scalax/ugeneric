package ugeneric.circe.encoder.benchmark.model

object Model1 {
  val foo = Foo(
    i1 = 404,
    i2 = "init string",
    i3 = "init string",
    i4 = "init string",
    i5 = "init string",
    i6 = "init string",
    i7 = "init string",
    i8 = "init string",
    i9 = "init string",
    i10 = "init string",
    i11 = "init string",
    i12 = "2333"
  )

  val bar = Bar(i1 = Model1.foo)
}
