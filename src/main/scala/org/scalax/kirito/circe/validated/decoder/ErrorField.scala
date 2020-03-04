package org.scalax.kirito.circe.decoder

import io.circe.{Decoder, Encoder}
import org.scalax.kirito.circe.KCirce

case class ErrorField(path: List[Path], messages: Set[String]) {
  def addPrefix(name: String): ErrorField = this.copy(path = PropertyNamePath(name) :: this.path)
}

object ErrorField {
  object EmptyTable
  implicit val circeEncoder: Encoder.AsObject[ErrorField] = KCirce.encodeCaseClassWithTable(EmptyTable)
  implicit val circeDecoder: Decoder[ErrorField]          = KCirce.decodeCaseClassWithTable(EmptyTable)
}
