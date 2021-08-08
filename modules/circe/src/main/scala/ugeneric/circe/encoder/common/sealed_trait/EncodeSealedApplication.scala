package ugeneric.circe.encoder

import io.circe.Json

object EncodeSealedApplication {

  def apply[Model, Sealed](toProperty: (String, Model) => (String, Json)): EncodeSealedTraitSelector[Sealed]#JsonEncoder[Class[Model], String] = {
    val con = EncodeSealedTraitSelector[Sealed]
    new con.JsonEncoder[Class[Model], String] {
      override def subClassToJsonOpt(model: Sealed, classTags: Class[Model], labelled: String): Option[(String, Json)] = {
        if (classTags.isInstance(model))
          Some(toProperty(labelled, classTags.cast(model)))
        else
          Option.empty
      }
    }
  }

}
