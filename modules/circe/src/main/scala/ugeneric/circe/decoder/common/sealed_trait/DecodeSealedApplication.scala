package ugeneric.circe.decoder

import zsg.macros.single.SealedTag
import io.circe.{Decoder, HCursor}

object DecodeSealedApplication {
  def apply[Model <: Sealed, Sealed](
    toProperty: (String, HCursor) => Decoder.Result[Sealed]
  ): DecodeSealedTraitSelector[Sealed]#JsonDecoder[SealedTag[Model], String] = {
    val con = DecodeSealedTraitSelector[Sealed]
    new con.JsonDecoder[SealedTag[Model], String] {
      override def getValue(name: String): Decoder[Sealed] = Decoder.instance { s => toProperty(name, s): Decoder.Result[Sealed] }
    }
  }
}
