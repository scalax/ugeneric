package org.scalax.ugeneric.circe

import io.circe.Decoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import org.scalax.ugeneric.circe.deImpl2.ParentTest03
import org.scalax.ugeneric.circe.decoder.common.sealed_trait.DecodeSealedApplication

package deImpl1 {
  sealed trait ParentTrait1
  case class Test01(i1: String, i2: Int)    extends ParentTrait1
  case object Test02                        extends ParentTrait1
  case class Test03(i1: String, i2: String) extends ParentTrait1
}

package deImpl2 {
  sealed trait ParentTrait2
  case class Test01(i1: String, i2: Int)        extends ParentTrait2
  case object Test02                            extends ParentTrait2
  case class ParentTest03(i1: String, i2: Long) extends ParentTrait2
  case class ParentTest04(i1: String, i2: String)
}

class UCirceExplicitSealedDecoderTest extends AnyFunSpec with Matchers {

  val decodeParentTrait: Decoder[deImpl2.ParentTrait2] = {
    implicit val decodeTest01: Decoder[deImpl2.Test01]       = UCirce.decodeCaseClass
    implicit val decodeTest02: Decoder[deImpl2.Test02.type]  = Decoder.instance(_ => Right(deImpl2.Test02))
    implicit val decodeTest04: Decoder[deImpl2.ParentTest04] = UCirce.decodeCaseClass
    implicit val decodeTest03: DecodeSealedApplication[deImpl2.ParentTest03, deImpl2.ParentTrait2] = new DecodeSealedApplication((name, cursor) =>
      cursor.get("Test03")(decodeTest04).right.map(s => ParentTest03(s.i1, s.i2.toLong))
    )
    UCirce.decodeSealed
  }

  describe("A json") {
    it("should decode to a renamed sealed trait") {
      val test01 = deImpl1.Test01(i1 = "pro1", i2 = 2)
      val test03 = deImpl1.Test03(i1 = "pro1-test03", i2 = "4")

      val test01Json = {
        import io.circe.generic.auto._
        (test01: deImpl1.ParentTrait1).asJson
      }
      val test02Json = {
        import io.circe.generic.auto._
        (deImpl1.Test02: deImpl1.ParentTrait1).asJson
      }
      val test03Json = {
        import io.circe.generic.auto._
        (test03: deImpl1.ParentTrait1).asJson
      }

      val compareTest01 = deImpl2.Test01(i1 = "pro1", i2 = 2)
      val compareTest02 = deImpl2.Test02
      val compareTest03 = deImpl2.ParentTest03(i1 = "pro1-test03", i2 = 4)

      test01Json.as(decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual compareTest01
      test02Json.as(decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual compareTest02
      test03Json.as(decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual compareTest03
    }
  }
}
