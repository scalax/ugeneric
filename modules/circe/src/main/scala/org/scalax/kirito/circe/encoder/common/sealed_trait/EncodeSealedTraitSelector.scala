package org.scalax.kirito.circe.encoder.common.sealed_trait

import asuna.Application2
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
  ): Application2[EncodeSealedTraitSelector[T]#JsonEncoder, SealedTag[R], Class[R], String] = {
    val con = EncodeSealedTraitSelector[T]
    _ =>
      { (model, classTags, labelled) =>
        if (classTags.isInstance(model))
          Some((labelled, t.value(classTags.cast(model))))
        else
          Option.empty
      }: con.JsonEncoder[Class[R], String]
  }

}
