package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import asuna.{Application3, Context3}
import asuna.macros.single.SealedTag
import io.circe.Decoder

class DecodeSealedTraitSelector[P] {

  trait JsonDecoder[I, II, T] {
    def getValue(name: II, tran: T): Decoder[P]
  }

}

object DecodeSealedTraitSelector {

  def apply[T]: DecodeSealedTraitSelector[T]        = value.asInstanceOf[DecodeSealedTraitSelector[T]]
  private val value: DecodeSealedTraitSelector[Any] = new DecodeSealedTraitSelector[Any]

  implicit def asunaCirceSealedDecoder[T, R](
    implicit t: => Decoder[R]
  ): Application3[DecodeSealedTraitSelector[T]#JsonDecoder, SealedTag[R], String, R => T] = {
    val con = DecodeSealedTraitSelector[T]
    new Application3[DecodeSealedTraitSelector[T]#JsonDecoder, SealedTag[R], String, R => T] with con.JsonDecoder[SealedTag[R], String, R => T] {
      override def application(context: Context3[DecodeSealedTraitSelector[T]#JsonDecoder]): DecodeSealedTraitSelector[T]#JsonDecoder[SealedTag[R], String, R => T] = this
      override def getValue(name: String, tran: R => T): Decoder[T]                                                                                                 = _.get(name)(t).map(tran)
    }
  }

}
