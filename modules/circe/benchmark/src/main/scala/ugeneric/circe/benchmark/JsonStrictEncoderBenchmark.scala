package ugeneric.circe.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import upickle.default.{ReadWriter => RW}
import io.circe.Encoder
import ugeneric.circe.UCirce
import ugeneric.circe.benchmark.model.case_class.{Bar, BenchmarkModel, Foo}

@BenchmarkMode(Array(Mode.Throughput)) // 测试方法平均执行时间
@OutputTimeUnit(TimeUnit.SECONDS)      // 输出结果的时间粒度为微秒
@State(Scope.Thread)                   // 每个测试线程一个实例
class JsonStrictEncoderBenchmark {

  object uPickle {
    import upickle.default.macroRW
    implicit val barRW: RW[Bar] = macroRW
    implicit val fooRW: RW[Foo] = macroRW
  }

  object rawCirceEncoder {
    import io.circe.generic.semiauto._
    implicit val barEncoder: Encoder[Bar] = deriveEncoder
    implicit val fooEncoder: Encoder[Foo] = deriveEncoder
  }

  object zsgEncoder {
    implicit val barEncoder: Encoder[Bar] = UCirce.encodeCaseClass
    implicit val fooEncoder: Encoder[Foo] = UCirce.encodeCaseClass
  }

  val model: Bar = BenchmarkModel.bar

  @Benchmark
  def uPickleTest: String = {
    import upickle.default._
    write(model)(uPickle.barRW)
  }

  @Benchmark
  def zsgCirceTest: String = {
    zsgEncoder.barEncoder(model).noSpaces
  }

  @Benchmark
  def vBaseCirceTest: String = {
    rawCirceEncoder.barEncoder(model).noSpaces
  }

}
