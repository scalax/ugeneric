package ugeneric.circe.test.encoder.model

import io.circe.Encoder
import ugeneric.circe.{UCirce, VersionCompat}

object SimpleSealed {
  sealed trait ParentTrait
  object ParentTrait {
    private implicit val decodeTest01: VersionCompat.ObjectEncoderType[Test01]   = UCirce.encodeCaseClass
    private implicit val decodeTest02: Encoder[Test02.type]                      = UCirce.encodeCaseObject
    private implicit val decodeTest03: VersionCompat.ObjectEncoderType[Test03]   = UCirce.encodeCaseClass
    implicit val encodeParentTrait: VersionCompat.ObjectEncoderType[ParentTrait] = UCirce.encodeSealed
  }

  case class Test01(i1: String, i2: Int)    extends ParentTrait
  case object Test02                        extends ParentTrait
  case class Test03(i1: String, i2: String) extends ParentTrait
}
