package org.scalax.ugeneric.circe.decoder

import io.circe.{Decoder, Encoder}
import org.scalax.ugeneric.circe.UCirce

case class ErrorField(path: List[Path], messages: Set[String]) {
  def addPrefix(name: String): ErrorField = this.copy(path = PropertyNamePath(name) :: this.path)
}

object ErrorField {
  object EmptyTable
  implicit val circeEncoder: Encoder.AsObject[ErrorField] = UCirce.encodeCaseClass
  implicit val circeDecoder: Decoder[ErrorField]          = UCirce.decodeCaseClass
}
