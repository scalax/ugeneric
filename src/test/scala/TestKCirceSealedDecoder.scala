package org.scalax.kirito.circe

import io.circe.Decoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._

class TestKCirceSealedDecoder extends AnyFunSpec with Matchers {

  sealed trait ParentTrait
  case class Test01(i1: String, i2: Int)  extends ParentTrait
  case object Test02                      extends ParentTrait
  case class Test03(i1: String, i2: Long) extends ParentTrait

  val decodeParentTrait: Decoder[ParentTrait] = {
    implicit val decodeTest01: Decoder[Test01]      = KCirce.decodeCaseClass
    implicit val decodeTest02: Decoder[Test02.type] = Decoder.instance(_ => Right(Test02))
    implicit val decodeTest03: Decoder[Test03]      = KCirce.decodeCaseClass
    KCirce.decodeSealed
  }

  describe("A json") {
    it("should decode to a simple sealed trait") {
      val test01 = Test01(i1 = "pro1", i2 = 2)
      val test03 = Test03(i1 = "pro1-test03", i2 = 4)

      val test01Json = {
        import io.circe.generic.auto._
        (test01: ParentTrait).asJson
      }
      val test02Json = {
        import io.circe.generic.auto._
        (Test02: ParentTrait).asJson
      }
      val test03Json = {
        import io.circe.generic.auto._
        (test03: ParentTrait).asJson
      }
      test01Json.as(decodeParentTrait).getOrElse(throw new Exception("decode failed")) shouldEqual test01
      test02Json.as(decodeParentTrait).getOrElse(throw new Exception("decode failed")) shouldEqual Test02
      test03Json.as(decodeParentTrait).getOrElse(throw new Exception("decode failed")) shouldEqual test03
    }
  }
}
