package org.scalax.kirito.circe

import io.circe.Decoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import org.scalax.kirito.circe.decoder.DecodeCaseClassApplication
import shapeless._
import shapeless.tag._

import scala.util.Try

class TestKCirceSealedDecoder extends AnyFunSpec with Matchers {
  trait IntTag
  val IntTag = tag[IntTag]

  trait LongTag
  val LongTag = tag[LongTag]

  sealed trait ParentTrait
  case class Test01(i1: String, i2: Int)
  case object Test02
  case class Test03(i1: String, i2: Long)

  implicit val decoderTest04: Decoder[Test04] = KCirce.decodeCaseClass

  describe("A json") {
    it("should decode to a case class") {
      val test04 = Test04(i1 = "pro1", i2 = "pro2", i3 = IntTag(3), i4 = LongTag(4L))
      val jsonObj = {
        import io.circe.generic.auto._
        Test04Compare(i1 = "pro1", i2 = "pro2", i3 = "3", `修改过的 key：i4` = 4L).asJson
      }
      jsonObj.as(decoderTest04).getOrElse(throw new Exception("decode failed")) shouldEqual test04
    }
  }
}
