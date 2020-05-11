package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import asuna.{Application3, Context3}
import asuna.macros.single.SealedTag
import io.circe.{Decoder, HCursor}

class DecodeSealedApplication[Model, Sealed](final val toProperty: (String, HCursor) => Decoder.Result[Sealed])
    extends Application3[DecodeSealedTraitSelector[Sealed]#JsonDecoder, SealedTag[Model], String, Model => Sealed] {
  private val con = DecodeSealedTraitSelector[Sealed]
  override def application(
    context: Context3[DecodeSealedTraitSelector[Sealed]#JsonDecoder]
  ): DecodeSealedTraitSelector[Sealed]#JsonDecoder[SealedTag[Model], String, Model => Sealed] = new con.JsonDecoder[SealedTag[Model], String, Model => Sealed] {
    override def getValue(name: String, tran: Model => Sealed): Decoder[Sealed] = {
      Decoder.instance { s => toProperty(name, s) }

    }
  }
}
