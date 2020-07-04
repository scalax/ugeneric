package ugeneric.circe.test.encoder

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import ugeneric.circe.test.encoder.model.{AssertNamedSealed, NamedSealed}

class TestUCirceExplicitSealedEncoderTest extends AnyFunSpec with Matchers {

  /*object EncoderContent {
    implicit val encodeTest01: UCirce.OEncoder[NamedSealed.Test01]       = implicit e => _.encode
    implicit val encodeTest02: Encoder[NamedSealed.Test02.type]          = UCirce.encodeCaseObject
    implicit val encodeTest04: UCirce.OEncoder[NamedSealed.ParentTest04] = implicit e => _.encode
    implicit val encodeTest03 /*: EncodeSealedApplication[enImpl2.ParentTest03, enImpl2.ParentTrait2]*/ =
      EncodeSealedApplication[NamedSealed.ParentTest03, NamedSealed.ParentTrait2]((name, value) =>
        ("Test03", NamedSealed.ParentTest04(value.i1, value.i2.toString).asJson(encodeTest04))
      )
    implicit val encodeParentTrait: Encoder[NamedSealed.ParentTrait2] = UCirce.encodeSealed(implicit c => _.encodeSealed)
  }*/

  val compareTest01: NamedSealed.ParentTrait2 = NamedSealed.Test01(i1 = "pro1", i2 = 2)
  val compareTest02: NamedSealed.ParentTrait2 = NamedSealed.Test02
  val compareTest03: NamedSealed.ParentTrait2 = NamedSealed.ParentTest03(i1 = "pro1-test03", i2 = 4)

  val assertTest01: AssertNamedSealed.ParentTrait = AssertNamedSealed.Test01(i1 = "pro1", i2 = 2)
  val assertTest02: AssertNamedSealed.ParentTrait = AssertNamedSealed.Test02
  val assertTest03: AssertNamedSealed.ParentTrait = AssertNamedSealed.Test03(i1 = "pro1-test03", i2 = "4")

  object DecoderModel {
    import io.circe.generic.auto._
    val test01Json = assertTest01.asJson
    val test02Json = assertTest02.asJson
    val test03Json = assertTest03.asJson
  }

  describe("A case class") {
    it("should encode to a json") {
      compareTest01.asJson shouldEqual DecoderModel.test01Json
      compareTest02.asJson shouldEqual DecoderModel.test02Json
      compareTest03.asJson shouldEqual DecoderModel.test03Json

      compareTest01.asJson.noSpaces shouldEqual DecoderModel.test01Json.noSpaces
      compareTest02.asJson.noSpaces shouldEqual DecoderModel.test02Json.noSpaces
      compareTest03.asJson.noSpaces shouldEqual DecoderModel.test03Json.noSpaces
    }
  }

}
