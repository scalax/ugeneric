package org.scalax.kirito1.circe.decoder

import asuna.{Application2, Context2}
import asuna.macros.single.SealedTag
import io.circe.{Decoder, HCursor}

class DecodeSealedApplication[Model, Sealed](final val toProperty: (String, HCursor) => Decoder.Result[Sealed])
    extends Application2[DecodeSealedTraitSelector[Sealed]#JsonDecoder, SealedTag[Model], String, Model => Sealed] {
  override def application(context: Context2[DecodeSealedTraitSelector[Sealed]#JsonDecoder]): DecodeSealedTraitSelector[Sealed]#JsonDecoder[String, Model => Sealed] = {
    val con = DecodeSealedTraitSelector[Sealed]
    new con.JsonDecoder[String, Model => Sealed] {
      override def getValue(name: String, tran: Model => Sealed): Decoder[Sealed] = {
        Decoder.instance(s => toProperty(name, s))
      }
    }
  }
}
