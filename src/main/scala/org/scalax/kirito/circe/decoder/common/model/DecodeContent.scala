package org.scalax.kirito.circe.decoder

import asuna.macros.ByNameImplicit
import asuna.{Application2, PropertyTag0}
import io.circe._

trait DecodeContent[Model, Name] extends Any {
  def getValue(name: Name): Decoder[Model]
}

object DecodeContent {

  implicit def asunaDecoder[Model](implicit dd: ByNameImplicit[Decoder[Model]]): Application2[DecodeContent, PropertyTag0[Model], Model, String] =
    _ => name => j => j.get(name)(dd.value)

}
