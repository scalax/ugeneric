package org.scalax.ugeneric.circe.decoder.common.model

import asuna.macros.ByNameImplicit
import asuna.{Application3, Context3, PropertyTag}
import io.circe._

trait DecodeContent[N, Model, Name] extends Any {
  def getDecoder(name: Name): Decoder[Model]
}

object DecodeContent {

  implicit def asunaDecoder[Model](implicit dd: ByNameImplicit[Decoder[Model]]): Application3[DecodeContent, PropertyTag[Model], Model, String] =
    new Application3[DecodeContent, PropertyTag[Model], Model, String] {
      override def application(context: Context3[DecodeContent]): DecodeContent[PropertyTag[Model], Model, String] = {
        new DecodeContent[PropertyTag[Model], Model, String] {
          override def getDecoder(name: String): Decoder[Model] = {
            Decoder.instance(_.get(name)(dd.value))
          }
        }
      }
    }

}
