package ugeneric.circe.test.encoder

import io.circe.Encoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import ugeneric.circe.UCirce
import ugeneric.circe.encoder.EncodeSealedApplication
import ugeneric.circe.test.encoder.model.{NamedSealed, SimpleSealed}

class TestUCirceExplicitSealedEncoderTest extends AnyFunSpec with Matchers {

  object EncoderContent {
    implicit val encodeTest01: Encoder[NamedSealed.Test01]       = UCirce.encodeCaseClass
    implicit val encodeTest02: Encoder[NamedSealed.Test02.type]  = UCirce.encodeCaseObject
    implicit val encodeTest04: Encoder[NamedSealed.ParentTest04] = UCirce.encodeCaseClass
    implicit val encodeTest03 /*: EncodeSealedApplication[enImpl2.ParentTest03, enImpl2.ParentTrait2]*/ =
      EncodeSealedApplication[NamedSealed.ParentTest03, NamedSealed.ParentTrait2]((name, value) =>
        ("Test03", NamedSealed.ParentTest04(value.i1, value.i2.toString).asJson(encodeTest04))
      )
    implicit val encodeParentTrait: Encoder[NamedSealed.ParentTrait2] = UCirce.encodeSealed(implicit c => _.encodeSealed)
  }

  val test01: SimpleSealed.ParentTrait = SimpleSealed.Test01(i1 = "pro1", i2 = 2)
  val test02: SimpleSealed.ParentTrait = SimpleSealed.Test02
  val test03: SimpleSealed.ParentTrait = SimpleSealed.Test03(i1 = "pro1-test03", i2 = "4")

  object DecoderModel {
    import io.circe.generic.auto._
    val test01Json = (test01: SimpleSealed.ParentTrait).asJson
    val test02Json = (SimpleSealed.Test02: SimpleSealed.ParentTrait).asJson
    val test03Json = (test03: SimpleSealed.ParentTrait).asJson
  }

  describe("A case class") {
    it("should encode to a json") {
      val compareTest01: NamedSealed.ParentTrait2 = NamedSealed.Test01(i1 = "pro1", i2 = 2)
      val compareTest02: NamedSealed.ParentTrait2 = NamedSealed.Test02
      val compareTest03: NamedSealed.ParentTrait2 = NamedSealed.ParentTest03(i1 = "pro1-test03", i2 = 4)

      (compareTest01: NamedSealed.ParentTrait2).asJson(EncoderContent.encodeParentTrait) shouldEqual DecoderModel.test01Json
      (compareTest02: NamedSealed.ParentTrait2).asJson(EncoderContent.encodeParentTrait) shouldEqual DecoderModel.test02Json
      (compareTest03: NamedSealed.ParentTrait2).asJson(EncoderContent.encodeParentTrait) shouldEqual DecoderModel.test03Json

      (compareTest01: NamedSealed.ParentTrait2).asJson(EncoderContent.encodeParentTrait).noSpaces shouldEqual DecoderModel.test01Json.noSpaces
      (compareTest02: NamedSealed.ParentTrait2).asJson(EncoderContent.encodeParentTrait).noSpaces shouldEqual DecoderModel.test02Json.noSpaces
      (compareTest03: NamedSealed.ParentTrait2).asJson(EncoderContent.encodeParentTrait).noSpaces shouldEqual DecoderModel.test03Json.noSpaces
    }
  }

}
