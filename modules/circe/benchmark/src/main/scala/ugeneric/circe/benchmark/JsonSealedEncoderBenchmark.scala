package ugeneric.circe.benchmark

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import ugeneric.circe.UCirce
import ugeneric.circe.benchmark.model.adt.ADTBenchmarkModel

@BenchmarkMode(Array(Mode.Throughput)) // 测试方法平均执行时间
@OutputTimeUnit(TimeUnit.SECONDS)      // 输出结果的时间粒度为微秒
@State(Scope.Thread)                   // 每个测试线程一个实例
class JsonSealedEncoderBenchmark {

  import upickle.default.{ReadWriter => RW, macroRW}
  import upickle.default._

  import ugeneric.circe.benchmark.model.adt.ADTs.ADT0
  import ugeneric.circe.benchmark.model.adt.Defaults._
  import ugeneric.circe.benchmark.model.adt.Generic.ADT
  import ugeneric.circe.benchmark.model.adt.Hierarchy._
  import ugeneric.circe.benchmark.model.adt.Recursive._
  type Data = ADT[Seq[(Int, Int)], String, A, LL, ADTc, ADT0]

  import io.circe._
  import io.circe.generic.semiauto._

  val rawCirceEncoder: Encoder[Data] = {
    implicit lazy val _w2: Encoder[A]        = deriveEncoder
    implicit lazy val _w3: Encoder[B]        = deriveEncoder
    implicit lazy val _w4: Encoder[C]        = deriveEncoder
    implicit lazy val _w5: Encoder[LL]       = deriveEncoder
    implicit lazy val _w6: Encoder[Node]     = deriveEncoder
    implicit lazy val _w7: Encoder[End.type] = deriveEncoder
    implicit lazy val _w8: Encoder[ADTc]     = deriveEncoder
    implicit lazy val _w9: Encoder[ADT0]     = deriveEncoder

    deriveEncoder
  }

  implicit val upickleRW: RW[Data] = {
    implicit lazy val _w2: RW[A]        = macroRW
    implicit lazy val _w3: RW[B]        = macroRW
    implicit lazy val _w4: RW[C]        = macroRW
    implicit lazy val _w5: RW[LL]       = macroRW
    implicit lazy val _w6: RW[Node]     = macroRW
    implicit lazy val _w7: RW[End.type] = macroRW
    implicit lazy val _w8: RW[ADTc]     = macroRW
    implicit lazy val _w9: RW[ADT0]     = macroRW
    macroRW
  }

  object zsgEncoder {
    implicit val _w2: Encoder[A]        = UCirce.encodeSealed
    implicit val _w3: Encoder[B]        = UCirce.encodeCaseClass
    implicit val _w4: Encoder[C]        = UCirce.encodeCaseClass
    implicit val _w5: Encoder[LL]       = UCirce.encodeSealed
    implicit val _w6: Encoder[Node]     = UCirce.encodeCaseClass
    implicit val _w7: Encoder[End.type] = UCirce.encodeCaseObject
    implicit val _w8: Encoder[ADTc]     = UCirce.encodeCaseClass
    implicit val _w9: Encoder[ADT0]     = UCirce.encodeCaseClass
    implicit val _wData: Encoder[Data]  = UCirce.encodeCaseClass
  }

  @Benchmark
  def upickleTest = {
    import upickle.default._
    write(ADTBenchmarkModel.benchmarkSampleData)
  }

  @Benchmark
  def zsgCirceTest = {
    zsgEncoder._wData(ADTBenchmarkModel.benchmarkSampleData).noSpaces
  }

  @Benchmark
  def rawCirceTest = {
    rawCirceEncoder(ADTBenchmarkModel.benchmarkSampleData).noSpaces
  }

  @TearDown
  def after: Unit = {
    val asunaCirceStr = zsgEncoder._wData(ADTBenchmarkModel.benchmarkSampleData).noSpaces
    val rawCirceStr   = rawCirceEncoder(ADTBenchmarkModel.benchmarkSampleData).noSpaces
    val upickleStr    = write(ADTBenchmarkModel.benchmarkSampleData)(upickleRW)
    println(s"asuna 结果: ${asunaCirceStr}")
    println(s"circe 结果: ${rawCirceStr}")
    println(s"upickle 结果: ${upickleStr}")
    println(s"asunaCirceStr == rawCirceStr: ${asunaCirceStr == rawCirceStr}")
    println(s"asunaCirceStr == upickleStr: ${asunaCirceStr == upickleStr}")
  }

}
