package org.scalax.ugeneric.circe.decoder

import io.circe.Decoder
import org.scalax.ugeneric.circe.{UCirce, VersionCompat}

case class ErrorField(path: List[Path], messages: Set[String]) {
  def addPrefix(name: String): ErrorField = this.copy(path = PropertyNamePath(name) :: this.path)
}

object ErrorField {
  object EmptyTable
  implicit val circeEncoder: VersionCompat.ObjectEncoderType[ErrorField] = UCirce.encodeCaseClass
  implicit val circeDecoder: Decoder[ErrorField]                         = UCirce.decodeCaseClass
}
