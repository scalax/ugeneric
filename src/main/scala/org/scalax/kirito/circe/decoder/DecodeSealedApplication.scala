package org.scalax.kirito.circe.decoder

import asuna.{Application2, Context2}
import asuna.macros.single.SealedTag
import io.circe.{Decoder, HCursor}

class DecodeSealedApplication[Model, Sealed](final val toProperty: (String, HCursor) => Decoder.Result[Sealed])
    extends Application2[SealedTraitSelector[Sealed]#JsonDecoder, SealedTag[Model], String, Model => Sealed] {
  override def application(context: Context2[SealedTraitSelector[Sealed]#JsonDecoder]): SealedTraitSelector[Sealed]#JsonDecoder[String, Model => Sealed] = {
    val con = SealedTraitSelector[Sealed]
    new con.JsonDecoder[String, Model => Sealed] {
      override def getValue(name: String, tran: Model => Sealed): Decoder[Sealed] = {
        Decoder.instance(s => toProperty(name, s))
      }
    }
  }
}
