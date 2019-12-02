package org.scalax.kirito.circe

import io.circe.{Decoder, Encoder, Json, JsonObject}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import org.scalax.kirito.circe.encoder.PropertyApplication
import shapeless._
import shapeless.tag._

import scala.util.Try

class TestKCirceDecoder extends AnyFunSpec with Matchers {
  trait IntTag
  val IntTag = tag[IntTag]

  trait LongTag
  val LongTag = tag[LongTag]

  case class Test04(i1: String, i2: String, i3: Int @@ IntTag, i4: Long @@ LongTag)

  implicit val longTagDecoder: Decoder[Long @@ LongTag] = Decoder.decodeString.emapTry(s => Try {s.toLong.asInstanceOf[Long @@ LongTag]})
  implicit val intTagDecoder: Decoder[Int @@ IntTag] = Decoder.decodeString.emapTry(s => Try { s.toInt.asInstanceOf[Int @@ IntTag]})

  //implicit def proImplicit: PropertyApplication[Int @@ IntTag] =
    //new PropertyApplication[Int @@ IntTag]((name, t) => ("被改过的 key：" + name, Json.fromString("value 是：" + String.valueOf(t))))

  implicit val decoderTest04: Decoder[Test04] = KCirce.decodeCaseClass

  describe("A json") {
    it("should decode to a case class") {
      val test04 = Test04(i1 = "pro1", i2 = "pro2", i3 = IntTag(3), i4 = LongTag(4L))
      val jsonObj = JsonObject.fromIterable(List(("i1", Json.fromString("pro1")), ("i2", Json.fromString("pro2")),("i3", Json.fromString("3")),("i4",Json.fromString("4"))))
      println(Json.fromJsonObject(jsonObj).as(decoderTest04).getOrElse(throw new Exception("decode failed")))
      Json.fromJsonObject(jsonObj).as(decoderTest04).getOrElse(throw new Exception("decode failed")) shouldEqual test04
    }
  }
}
