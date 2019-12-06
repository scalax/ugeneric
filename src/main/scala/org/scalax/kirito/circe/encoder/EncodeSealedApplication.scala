package org.scalax.kirito.circe.encoder

import asuna.{Application2, Context2}
import asuna.macros.single.SealedTag
import io.circe.Json

class EncodeSealedApplication[Model](final val toProperty: (String, Model) => (String, Json))
    extends Application2[EncodeSealedTraitSelector[Model]#JsonEncoder, SealedTag[Model], Class[Model], String] {
  override def application(context: Context2[EncodeSealedTraitSelector[Model]#JsonEncoder]): EncodeSealedTraitSelector[Model]#JsonEncoder[Class[Model], String] = {
    val con = EncodeSealedTraitSelector[Model]
    new con.JsonEncoder[Class[Model], String] {
      override def subClassToJsonOpt(model: Model, classTags: Class[Model], labelled: String): Option[(String, Json)] = {
        if (classTags.isInstance(model))
          Some(toProperty(labelled, classTags.cast(model)))
        else
          Option.empty
      }
    }
  }
}
