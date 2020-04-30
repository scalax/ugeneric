package org.scalax.ugeneric.circe.encoder.common.sealed_trait

import asuna.{Application2, Context2}
import asuna.macros.single.SealedTag
import io.circe.Json

class EncodeSealedApplication[Model, Sealed](final val toProperty: (String, Model) => (String, Json))
    extends Application2[EncodeSealedTraitSelector[Sealed]#JsonEncoder, SealedTag[Model], Class[Model], String] {
  val con = EncodeSealedTraitSelector[Sealed]

  override def application(context: Context2[EncodeSealedTraitSelector[Sealed]#JsonEncoder]): EncodeSealedTraitSelector[Sealed]#JsonEncoder[Class[Model], String] = {
    (model, classTags, labelled) =>
      if (classTags.isInstance(model))
        Some(toProperty(labelled, classTags.cast(model)))
      else
        Option.empty
  }: con.JsonEncoder[Class[Model], String]
}
