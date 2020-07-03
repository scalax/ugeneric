package ugeneric.circe.test.encoder

import io.circe.Encoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import ugeneric.circe.{EncodeCaseClassApply, EncodeCaseClassApply1, UCirce, VersionCompat}
import ugeneric.circe.VersionCompat.ObjectEncoderType
import ugeneric.circe.encoder.JsonObjectContent
import ugeneric.circe.test.encoder.model.SimpleSealed._
import zsg.Context3

class TestUCirceSealedEncoder extends AnyFunSpec with Matchers {

  object EncoderContent {
    implicit val decodeTest01: VersionCompat.ObjectEncoderType[Test01] = UCirce.encodeCaseClass1(s => new s.IIII[Test01] {
      override def encodeCaseClass(n: EncodeCaseClassApply[Test01]): ObjectEncoderType[Test01] = n.encodeCaseClass
    })

    //val decodeTest0111: VersionCompat.ObjectEncoderType[Test01] = UCirce.encodeCaseClass1[Test01]((s: EncodeCaseClassApply[Test01]) => s.encodeCaseClass)

    implicit val decodeTest02: Encoder[Test02.type]      = UCirce.encodeCaseObject
    implicit val decodeTest03: Encoder[Test03]           = UCirce.encodeCaseClass(implicit c => _.encodeCaseClass)
    implicit val encodeParentTrait: Encoder[ParentTrait] = UCirce.encodeSealed(implicit c => _.encodeSealed)
  }

  val test01: ParentTrait = Test01(i1 = "pro1", i2 = 2)
  val test02: ParentTrait = Test02
  val test03: ParentTrait = Test03(i1 = "pro1-test03", i2 = "4")

  object TestModelContent {
    import io.circe.generic.auto._
    val test01Json = (test01: ParentTrait).asJson
    val test02Json = (Test02: ParentTrait).asJson
    val test03Json = (test03: ParentTrait).asJson
  }

  describe("A case class") {
    it("should encode to a json") {
      test01.asJson(EncoderContent.encodeParentTrait) shouldEqual TestModelContent.test01Json
      test02.asJson(EncoderContent.encodeParentTrait) shouldEqual TestModelContent.test02Json
      test03.asJson(EncoderContent.encodeParentTrait) shouldEqual TestModelContent.test03Json

      test01.asJson(EncoderContent.encodeParentTrait).noSpaces shouldEqual TestModelContent.test01Json.noSpaces
      test02.asJson(EncoderContent.encodeParentTrait).noSpaces shouldEqual TestModelContent.test02Json.noSpaces
      test03.asJson(EncoderContent.encodeParentTrait).noSpaces shouldEqual TestModelContent.test03Json.noSpaces
    }
  }

}
