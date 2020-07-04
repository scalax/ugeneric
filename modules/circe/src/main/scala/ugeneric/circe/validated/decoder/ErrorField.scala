package org.scalax.ugeneric.circe.decoder

import io.circe.Decoder
import ugeneric.circe.UCirce

case class ErrorField(path: List[Path], messages: Set[String]) {
  def addPrefix(name: String): ErrorField = this.copy(path = PropertyNamePath(name) :: this.path)
}

object ErrorField {
  implicit val circeEncoder: UCirce.OEncoder[ErrorField] = UCirce.OEncoder.builder_211(implicit e => _.encode)
  implicit val circeDecoder: Decoder[ErrorField]         = UCirce.decodeCaseClass(implicit c => _.decodeCaseClass)
}
