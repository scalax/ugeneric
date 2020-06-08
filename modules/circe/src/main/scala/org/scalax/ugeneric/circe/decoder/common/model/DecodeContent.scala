package org.scalax.ugeneric.circe.decoder.common.model

import zsg.macros.ByNameImplicit
import zsg.PropertyTag
import io.circe._

trait DecodeContent[N, Model, Name] extends Any {
  def getDecoder(name: Name): Decoder[Model]
}

object DecodeContent {

  implicit def asunaDecoder[Model](implicit dd: ByNameImplicit[Decoder[Model]]): DecodeContent[PropertyTag[Model], Model, String] =
    new DecodeContent[PropertyTag[Model], Model, String] {
      override def getDecoder(name: String): Decoder[Model] = Decoder.instance(_.get(name)(dd.value))
    }

}
