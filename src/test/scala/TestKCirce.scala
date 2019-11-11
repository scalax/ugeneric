package org.scalax.kirito.circe

import io.circe.{Encoder, Json}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import org.scalax.kirito.circe.encoder.PropertyApplication
import shapeless.tag._

class TestKCirce extends AnyFunSpec with Matchers {
  trait IntTag
  val IntTag = apply[IntTag]
  case class Test04(i1: String, i2: String, i3: Int @@ IntTag, i4: Long)

  implicit def proImplicit: PropertyApplication[Int @@ IntTag] = new PropertyApplication[Int @@ IntTag] {
    override def toProperty(name: String, t: Int @@ IntTag): (String, Json) = ("被改过的 key：" + name, Json.fromString("value 是：" + String.valueOf(t)))
  }
  implicit val encoderTest04: Encoder[Test04] = KCirce.encodeCaseClass

  describe("A case class") {
    it("should encode to a json") {
      val test04 = Test04(i1 = "pro1", i2 = "pro2", i3 = IntTag(3), i4 = 4L)
      println(test04.asJson.noSpaces)
    }
  }
}
