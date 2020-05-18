package org.scalax.ugeneric.circe.decoder.common.model

import zsg.PropertyTag
import io.circe._

trait DecodeContent[N, Model, Name] extends Any {
  def getDecoder(name: Name): Decoder[Model]
}

object DecodeContent {

  implicit def asunaDecoder[Model](implicit dd: => Decoder[Model]): DecodeContent[PropertyTag[Model], Model, String] = (name: String) => _.get(name)(dd)

}
