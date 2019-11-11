package org.scalax.kirito.circe

import io.circe.Encoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._

class TestKCirce extends AnyFunSpec with Matchers {
  case class Test04(i1: String, i2: String, i3: Int, i4: Long)
  implicit val encoderTest04: Encoder[Test04] = KCirce.encodeCaseClass

  describe("A case class") {
    it("should encode to a json") {
      val test04 = Test04(i1 = "pro1", i2 = "pro2", i3 = 3, i4 = 4L)
      println(test04.asJson.noSpaces)
    }
  }
}
