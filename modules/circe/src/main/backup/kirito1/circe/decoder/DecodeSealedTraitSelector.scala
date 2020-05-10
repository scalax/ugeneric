package org.scalax.kirito1.circe.decoder

import asuna.{Application2, Context2}
import asuna.macros.ByNameImplicit
import asuna.macros.single.SealedTag
import io.circe.Decoder

class DecodeSealedTraitSelector[P] {

  trait JsonDecoder[II, T] {
    def getValue(name: II, tran: T): Decoder[P]
  }

}

object DecodeSealedTraitSelector {

  def apply[T]: DecodeSealedTraitSelector[T]        = value.asInstanceOf[DecodeSealedTraitSelector[T]]
  private val value: DecodeSealedTraitSelector[Any] = new DecodeSealedTraitSelector[Any]

  implicit def asunaCirceSealedDecoder[T, R](
    implicit t: ByNameImplicit[Decoder[R]]
  ): Application2[DecodeSealedTraitSelector[T]#JsonDecoder, SealedTag[R], String, R => T] =
    new Application2[DecodeSealedTraitSelector[T]#JsonDecoder, SealedTag[R], String, R => T] {
      override def application(context: Context2[DecodeSealedTraitSelector[T]#JsonDecoder]): DecodeSealedTraitSelector[T]#JsonDecoder[String, R => T] = {
        val con = DecodeSealedTraitSelector[T]
        new con.JsonDecoder[String, R => T] {
          override def getValue(name: String, tran: R => T): Decoder[T] = {
            Decoder.instance(j => j.get(name)(t.value).map(tran))
          }
        }
      }
    }

}
