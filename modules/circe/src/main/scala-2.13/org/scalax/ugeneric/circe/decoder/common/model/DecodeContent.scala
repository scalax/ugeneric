package org.scalax.ugeneric.circe.decoder.common.model

import asuna.{Application3, PropertyTag}
import io.circe._

trait DecodeContent[N, Model, Name] extends Any {
  def getDecoder(name: Name): Decoder[Model]
}

object DecodeContent {

  implicit def asunaDecoder[Model](implicit dd: => Decoder[Model]): Application3[DecodeContent, PropertyTag[Model], Model, String] =
    _ => name => j => j.get(name)(dd)

}
