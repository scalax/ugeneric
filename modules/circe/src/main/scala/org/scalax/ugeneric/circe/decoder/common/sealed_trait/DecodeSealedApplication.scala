package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import zsg.macros.single.SealedTag
import io.circe.{Decoder, HCursor}

object DecodeSealedApplication {
  def apply[Model, Sealed](
    toProperty: (String, HCursor) => Decoder.Result[Sealed]
  ): DecodeSealedTraitSelector[Sealed]#JsonDecoder[SealedTag[Model], String, Model => Sealed] = {
    val con = DecodeSealedTraitSelector[Sealed]
    new con.JsonDecoder[SealedTag[Model], String, Model => Sealed] {
      override def getValue(name: String, tran: Model => Sealed): Decoder[Sealed] = {
        Decoder.instance { s => toProperty(name, s) }

      }
    }
  }
}
