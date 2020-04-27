package org.scalax.kirito.circe

import io.circe.Decoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import org.scalax.kirito.circe.decoder.DecodeCaseClassApplication
import shapeless._
import shapeless.tag._

import scala.util.Try

class TestKCirceDecoder extends AnyFunSpec with Matchers {
  trait IntTag
  val IntTag = tag[IntTag]

  trait LongTag
  val LongTag = tag[LongTag]

  case class Test04(i1: String, i2: String, i3: Int @@ IntTag, i4: Long @@ LongTag)
  case class Test04Compare(i1: String, i2: String, i3: String, `修改过的 key：i4`: Long)

  implicit val intTagDecoder: Decoder[Int @@ IntTag] = Decoder.decodeString.emapTry(s => Try { s.toInt.asInstanceOf[Int @@ IntTag] })
  implicit val longTagDecoder: DecodeCaseClassApplication[Long @@ LongTag] = new DecodeCaseClassApplication(
    (name, h) => h.get("修改过的 key：i4")(Decoder.decodeLong).map(s => s.asInstanceOf[Long @@ LongTag])
  )

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
