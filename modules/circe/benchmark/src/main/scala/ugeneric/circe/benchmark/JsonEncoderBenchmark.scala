package ugeneric.circe.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import upickle.default.{ReadWriter => RW}
import io.circe.Encoder
import ugeneric.circe.UCirce
import ugeneric.circe.benchmark.model.{Bar, Foo, Model1}

@BenchmarkMode(Array(Mode.Throughput)) // 测试方法平均执行时间
@OutputTimeUnit(TimeUnit.SECONDS)      // 输出结果的时间粒度为微秒
@State(Scope.Thread)                   // 每个测试线程一个实例
class JsonEncoderBenchmark {

  object uPickle {
    import upickle.default.macroRW
    implicit val rw1: RW[Bar] = macroRW
    implicit val rw2: RW[Foo] = macroRW
  }

  object rawCirceEncoder {
    import io.circe.generic.semiauto._
    implicit val a1: Encoder[Bar] = deriveEncoder
    implicit val a2: Encoder[Foo] = deriveEncoder

  }

  object zsgEncoder {
    implicit val a1: Encoder[Bar] = UCirce.encodeCaseClass
    implicit val a2: Encoder[Foo] = UCirce.encodeCaseClass
  }

  val model: Bar = Model1.bar

  @Benchmark
  def uPickleTest: String = {
    import upickle.default._
    write(model)(uPickle.rw1)
  }

  @Benchmark
  def zsgCirceTest: String = {
    zsgEncoder.a1(model).noSpaces
  }

  @Benchmark
  def rawCirceTest: String = {
    rawCirceEncoder.a1(model).noSpaces
  }

}
