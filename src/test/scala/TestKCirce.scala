package org.scalax.kirito.circe

import io.circe.{Encoder, Json}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import org.scalax.kirito.circe.encoder.PropertyApplication
import shapeless._
import shapeless.tag._

class TestKCirce extends AnyFunSpec with Matchers {
  trait IntTag
  val IntTag = tag[IntTag]

  trait LongTag
  val LongTag = tag[LongTag]

  case class Test04(i1: String, i2: String, i3: Int @@ IntTag, i4: Long @@ LongTag)

  implicit val longTagEncoder: Encoder[Long@@LongTag] = Encoder.instance(s => Json.fromString("长整型值是：" + String.valueOf(s)))

  implicit def proImplicit: PropertyApplication[Int @@ IntTag] = new PropertyApplication[Int @@ IntTag] {
    override def toProperty(name: String, t: Int @@ IntTag): (String, Json) = ("被改过的 key：" + name, Json.fromString("value 是：" + String.valueOf(t)))
  }
  implicit val encoderTest04: Encoder[Test04] = KCirce.encodeCaseClass

  describe("A case class") {
    it("should encode to a json") {
      val test04 = Test04(i1 = "pro1", i2 = "pro2", i3 = IntTag(3), i4 = LongTag(4L))
      println(test04.asJson.noSpaces)
    }
  }
}
