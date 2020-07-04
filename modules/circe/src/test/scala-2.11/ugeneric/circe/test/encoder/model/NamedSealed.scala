package ugeneric.circe.test.encoder.model

import io.circe.Encoder
import io.circe.syntax._
import ugeneric.circe.encoder.EncodeSealedApplication
import ugeneric.circe.{UCirce, VersionCompat}

package object NamedSealed {

  sealed trait ParentTrait2
  object ParentTrait2 {
    private implicit val test01Encoder: UCirce.OEncoder[Test01]             = UCirce.OEncoder.builder_211(implicit e => _.encode)
    private implicit val test02Encoder: Encoder[Test02.type]                = UCirce.encodeCaseObject
    private implicit val parentTest04Encoder: UCirce.OEncoder[ParentTest04] = UCirce.OEncoder.builder_211(implicit e => _.encode)
    private implicit val parentTest03Encoder /*: EncodeSealedApplication[enImpl2.ParentTest03, enImpl2.ParentTrait2]*/ =
      EncodeSealedApplication[NamedSealed.ParentTest03, NamedSealed.ParentTrait2]((name, value) =>
        ("Test03", NamedSealed.ParentTest04(value.i1, value.i2.toString).asJson)
      )
    implicit val parentTrait2Encoder: UCirce.SEncoder[ParentTrait2] = UCirce.SEncoder.builder_211(implicit c => _.encode)
  }

  case class Test01(i1: String, i2: Int)        extends ParentTrait2
  case object Test02                            extends ParentTrait2
  case class ParentTest03(i1: String, i2: Long) extends ParentTrait2
  case class ParentTest04(i1: String, i2: String)

}
