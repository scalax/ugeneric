package ugeneric.circe.test.decoder

import io.circe.Decoder
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import ugeneric.circe.UCirce
import ugeneric.circe.decoder.DecodeSealedApplication
import ugeneric.circe.test.decoder.model.{NamedSealed, SimpleSealed}

class TestUCirceExplicitSealedDecoder extends AnyFunSpec with Matchers {

  object DecoderContent {
    implicit val decodeTest01: Decoder[NamedSealed.Test01]       = UCirce.decodeCaseClass
    implicit val decodeTest02: Decoder[NamedSealed.Test02.type]  = Decoder.instance(_ => Right(NamedSealed.Test02))
    implicit val decodeTest04: Decoder[NamedSealed.ParentTest04] = UCirce.decodeCaseClass
    implicit val decodeTest03 = DecodeSealedApplication[NamedSealed.ParentTest03, NamedSealed.ParentTrait2]((name, cursor) =>
      cursor.get("Test03")(decodeTest04).right.map(s => NamedSealed.ParentTest03(s.i1, s.i2.toLong))
    )
    implicit val decodeParentTrait: Decoder[NamedSealed.ParentTrait2] = UCirce.decodeSealed
  }

  val test01 = SimpleSealed.Test01(i1 = "pro1", i2 = 2)
  val test03 = SimpleSealed.Test03(i1 = "pro1-test03", i2 = "4")

  object DecoderModelContent {
    import io.circe.generic.auto._
    val test01Json = (test01: SimpleSealed.ParentTrait).asJson
    val test02Json = (SimpleSealed.Test02: SimpleSealed.ParentTrait).asJson
    val test03Json = (test03: SimpleSealed.ParentTrait).asJson
  }

  describe("A json") {
    it("should decode to a renamed sealed trait") {

      val compareTest01 = NamedSealed.Test01(i1 = "pro1", i2 = 2)
      val compareTest02 = NamedSealed.Test02
      val compareTest03 = NamedSealed.ParentTest03(i1 = "pro1-test03", i2 = 4)

      DecoderModelContent.test01Json.as(DecoderContent.decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual compareTest01
      DecoderModelContent.test02Json.as(DecoderContent.decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual compareTest02
      DecoderModelContent.test03Json.as(DecoderContent.decodeParentTrait).right.getOrElse(throw new Exception("decode failed")) shouldEqual compareTest03
    }
  }

}
