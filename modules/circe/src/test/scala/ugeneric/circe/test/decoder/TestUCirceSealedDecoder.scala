package ugeneric.circe.test.decoder

import io.circe.Decoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import ugeneric.circe.UCirce
import ugeneric.circe.test.decoder.model.SimpleSealed._

class TestUCirceSealedDecoder extends AnyFunSpec with Matchers {

  object DecoderContent {
    implicit val decodeParentTrait: Decoder[ParentTrait] = UCirce.decodeSealed(implicit c => _.decodeSealed)
    implicit val decodeTest01: Decoder[Test01]           = UCirce.decodeCaseClass(implicit c => _.decodeCaseClass)
    implicit val decodeTest02: Decoder[Test02.type]      = Decoder.instance(_ => Right(Test02))
    implicit val decodeTest03: Decoder[Test03]           = UCirce.decodeCaseClass(implicit c => _.decodeCaseClass)
  }

  val test01 = Test01(i1 = "pro1", i2 = 2)
  val test03 = Test03(i1 = "pro1-test03", i2 = "4")

  object TestModelContenet {
    import io.circe.generic.auto._
    val test01Json = (test01: ParentTrait).asJson
    val test02Json = (Test02: ParentTrait).asJson
    val test03Json = (test03: ParentTrait).asJson
  }

  describe("A json") {
    it("should decode to a simple sealed trait") {
      TestModelContenet.test01Json.as(DecoderContent.decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual test01
      TestModelContenet.test02Json.as(DecoderContent.decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual Test02
      TestModelContenet.test03Json.as(DecoderContent.decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual test03
    }
  }

}
