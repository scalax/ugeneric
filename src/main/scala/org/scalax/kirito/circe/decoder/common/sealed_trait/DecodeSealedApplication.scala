package org.scalax.kirito.circe.decoder.common.sealed_trait

import asuna.{Application2, Context2}
import asuna.macros.single.SealedTag
import io.circe.{Decoder, HCursor}

class DecodeSealedApplication[Model, Sealed](final val toProperty: (String, HCursor) => Decoder.Result[Sealed])
    extends Application2[DecodeSealedTraitSelector[Sealed]#JsonDecoder, SealedTag[Model], String, Model => Sealed] {
  override def application(context: Context2[DecodeSealedTraitSelector[Sealed]#JsonDecoder]): DecodeSealedTraitSelector[Sealed]#JsonDecoder[String, Model => Sealed] = {
    val con = DecodeSealedTraitSelector[Sealed]

    { (name, tran) => s => toProperty(name, s) }: con.JsonDecoder[String, Model => Sealed]
  }
}
