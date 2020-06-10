package ugeneric.circe.encoder

import zsg.macros.single.SealedTag
import io.circe.Json

object EncodeSealedApplication {

  def apply[Model, Sealed](toProperty: (String, Model) => (String, Json)): EncodeSealedTraitSelector[Sealed]#JsonEncoder[SealedTag[Model], Class[Model], String] = {
    val con = EncodeSealedTraitSelector[Sealed]
    new con.JsonEncoder[SealedTag[Model], Class[Model], String] {
      override def subClassToJsonOpt(model: Sealed, classTags: Class[Model], labelled: String): Option[(String, Json)] = {
        if (classTags.isInstance(model))
          Some(toProperty(labelled, classTags.cast(model)))
        else
          Option.empty
      }
    }
  }

}
