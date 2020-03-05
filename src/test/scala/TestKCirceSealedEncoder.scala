package org.scalax.kirito.circe

import io.circe.Encoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._

class TestKCirceSealedEncoder extends AnyFunSpec with Matchers {

  sealed trait ParentTrait
  case class Test01(i1: String, i2: Int)  extends ParentTrait
  case object Test02                      extends ParentTrait
  case class Test03(i1: String, i2: Long) extends ParentTrait

  val encodeParentTrait: Encoder[ParentTrait] = {
    implicit val decodeTest01: Encoder[Test01]      = KCirce.encodeCaseClassWithTable(EmptyTable.value)
    implicit val decodeTest02: Encoder[Test02.type] = KCirce.encodeCaseObject
    implicit val decodeTest03: Encoder[Test03]      = KCirce.encodeCaseClassWithTable(EmptyTable.value)
    KCirce.encodeSealed
  }

  describe("A case class") {
    it("should encode to a json") {
      val test01: ParentTrait = Test01(i1 = "pro1", i2 = 2)
      val test02: ParentTrait = Test02
      val test03: ParentTrait = Test03(i1 = "pro1-test03", i2 = 4)

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
      test01.asJson(encodeParentTrait) shouldEqual test01Json
      test02.asJson(encodeParentTrait) shouldEqual test02Json
      test03.asJson(encodeParentTrait) shouldEqual test03Json
    }
  }
}
