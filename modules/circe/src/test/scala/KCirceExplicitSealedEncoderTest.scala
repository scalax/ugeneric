package org.scalax.ugeneric.circe

import io.circe.Encoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import org.scalax.ugeneric.circe.enImpl2.ParentTest04
import org.scalax.ugeneric.circe.encoder.common.sealed_trait.EncodeSealedApplication

package enImpl1 {
  sealed trait ParentTrait1
  case class Test01(i1: String, i2: Int)    extends ParentTrait1
  case object Test02                        extends ParentTrait1
  case class Test03(i1: String, i2: String) extends ParentTrait1
}

package enImpl2 {
  sealed trait ParentTrait2
  case class Test01(i1: String, i2: Int)        extends ParentTrait2
  case object Test02                            extends ParentTrait2
  case class ParentTest03(i1: String, i2: Long) extends ParentTrait2
  case class ParentTest04(i1: String, i2: String)
}

class UCirceExplicitSealedEncoderTest extends AnyFunSpec with Matchers {

  val encodeParentTrait: Encoder[enImpl2.ParentTrait2] = {
    implicit val encodeTest01: Encoder[enImpl2.Test01]       = UCirce.encodeCaseClass
    implicit val encodeTest02: Encoder[enImpl2.Test02.type]  = UCirce.encodeCaseObject
    implicit val encodeTest04: Encoder[enImpl2.ParentTest04] = UCirce.encodeCaseClass
    implicit val encodeTest03: EncodeSealedApplication[enImpl2.ParentTest03, enImpl2.ParentTrait2] =
      new EncodeSealedApplication[enImpl2.ParentTest03, enImpl2.ParentTrait2]((name, value) => ("Test03", ParentTest04(value.i1, value.i2.toString).asJson(encodeTest04)))
    UCirce.encodeSealed
  }

  describe("A case class") {
    it("should encode to a json") {
      val test01: enImpl1.ParentTrait1 = enImpl1.Test01(i1 = "pro1", i2 = 2)
      val test02: enImpl1.ParentTrait1 = enImpl1.Test02
      val test03: enImpl1.ParentTrait1 = enImpl1.Test03(i1 = "pro1-test03", i2 = "4")

      val test01Json = {
        import io.circe.generic.auto._
        (test01: enImpl1.ParentTrait1).asJson
      }
      val test02Json = {
        import io.circe.generic.auto._
        (enImpl1.Test02: enImpl1.ParentTrait1).asJson
      }
      val test03Json = {
        import io.circe.generic.auto._
        (test03: enImpl1.ParentTrait1).asJson
      }

      val compareTest01: enImpl2.ParentTrait2 = enImpl2.Test01(i1 = "pro1", i2 = 2)
      val compareTest02: enImpl2.ParentTrait2 = enImpl2.Test02
      val compareTest03: enImpl2.ParentTrait2 = enImpl2.ParentTest03(i1 = "pro1-test03", i2 = 4)

      (compareTest01: enImpl2.ParentTrait2).asJson(encodeParentTrait) shouldEqual test01Json
      (compareTest02: enImpl2.ParentTrait2).asJson(encodeParentTrait) shouldEqual test02Json
      (compareTest03: enImpl2.ParentTrait2).asJson(encodeParentTrait) shouldEqual test03Json
    }
  }
}
