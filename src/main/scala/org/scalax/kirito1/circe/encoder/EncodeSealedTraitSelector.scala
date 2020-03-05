package org.scalax.kirito1.circe.encoder

import asuna.{Application2, Context2}
import asuna.macros.ByNameImplicit
import asuna.macros.single.SealedTag
import io.circe.{Encoder, Json}

class EncodeSealedTraitSelector[H] {
  trait JsonEncoder[T, II] {
    def subClassToJsonOpt(model: H, classTags: T, labelled: II): Option[(String, Json)]
  }
}

object EncodeSealedTraitSelector {

  private val value                          = new EncodeSealedTraitSelector[Any]
  def apply[T]: EncodeSealedTraitSelector[T] = value.asInstanceOf[EncodeSealedTraitSelector[T]]

  implicit final def asunaCirceSealedEncoder[T, R](
    implicit t: ByNameImplicit[Encoder[R]]
  ): Application2[EncodeSealedTraitSelector[T]#JsonEncoder, SealedTag[R], Class[R], String] =
    new Application2[EncodeSealedTraitSelector[T]#JsonEncoder, SealedTag[R], Class[R], String] {
      override def application(context: Context2[EncodeSealedTraitSelector[T]#JsonEncoder]): EncodeSealedTraitSelector[T]#JsonEncoder[Class[R], String] = {
        val con = EncodeSealedTraitSelector[T]
        new con.JsonEncoder[Class[R], String] {
          override def subClassToJsonOpt(model: T, classTags: Class[R], labelled: String): Option[(String, Json)] = {
            if (classTags.isInstance(model))
              Some((labelled, t.value(classTags.cast(model))))
            else
              Option.empty
          }
        }
      }
    }

}
