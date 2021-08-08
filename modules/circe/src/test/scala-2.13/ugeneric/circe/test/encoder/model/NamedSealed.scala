package ugeneric.circe.test.encoder.model

import io.circe.Encoder
import io.circe.syntax._
import ugeneric.circe.encoder.{EncodeSealedApplication, EncodeSealedContext, EncodeSealedTraitSelector}
import ugeneric.circe.{UCirce, VersionCompat}
import zsg.{Application, TypeHList2}
import zsg.macros.single.SealedTag

object NamedSealed {

  sealed trait ParentTrait2
  object ParentTrait2 {
    private implicit val test01Encoder: VersionCompat.ObjectEncoderType[Test01]             = UCirce.encodeCaseClass
    private implicit val test02Encoder: Encoder[Test02.type]                                = UCirce.encodeCaseObject
    private implicit val parentTest04Encoder: VersionCompat.ObjectEncoderType[ParentTest04] = UCirce.encodeCaseClass
    private implicit val parentTest03Encoder: Application[EncodeSealedTraitSelector[NamedSealed.ParentTrait2], EncodeSealedContext[NamedSealed.ParentTrait2], SealedTag[
      NamedSealed.ParentTest03
    ], TypeHList2[Class[NamedSealed.ParentTest03], String]] = new Application[EncodeSealedTraitSelector[NamedSealed.ParentTrait2], EncodeSealedContext[
      NamedSealed.ParentTrait2
    ], SealedTag[NamedSealed.ParentTest03], TypeHList2[Class[NamedSealed.ParentTest03], String]] {
      override def application(context: EncodeSealedContext[ParentTrait2]): EncodeSealedTraitSelector[ParentTrait2]#JsonEncoder[Class[ParentTest03], String] =
        EncodeSealedApplication[NamedSealed.ParentTest03, NamedSealed.ParentTrait2]((name, value) =>
          ("Test03", NamedSealed.ParentTest04(value.i1, value.i2.toString).asJson)
        )
    }

    implicit val parentTrait2Encoder: VersionCompat.ObjectEncoderType[ParentTrait2] = UCirce.encodeSealed
  }

  case class Test01(i1: String, i2: Int)        extends ParentTrait2
  case object Test02                            extends ParentTrait2
  case class ParentTest03(i1: String, i2: Long) extends ParentTrait2
  case class ParentTest04(i1: String, i2: String)

}
