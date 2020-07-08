package ugeneric.circe.benchmark.model.case_class

object BenchmarkModel {
  val foo = Foo(
    i1 = 404,
    i2 = "i2",
    i3 = "i3",
    i4 = "i4",
    i5 = "i5",
    i6 = "i6",
    i7 = "i7",
    i8 = "i8",
    i9 = "i9",
    i10 = "i10",
    i11 = "i11",
    i12 = "i12"
  )

  val bar = Bar(i1 = BenchmarkModel.foo)
}
