package org.scalax.kirito1.circe.encoder

import asuna.{AsunaTuple0, Context2, Plus2}
import io.circe.Json

class EncodeSealedContext[H] extends Context2[EncodeSealedTraitSelector[H]#JsonEncoder] {

  override final def append[X1, X2, Y1, Y2, Z1, Z2](x: EncodeSealedTraitSelector[H]#JsonEncoder[X1, X2], y: EncodeSealedTraitSelector[H]#JsonEncoder[Y1, Y2])(
    plus: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): EncodeSealedTraitSelector[H]#JsonEncoder[Z1, Z2] = {
    val con = EncodeSealedTraitSelector[H]
    new con.JsonEncoder[Z1, Z2] {
      override def subClassToJsonOpt(model: H, classTags: Z1, labelled: Z2): Option[(String, Json)] = {
        val a = x.subClassToJsonOpt(model, plus.takeHead1(classTags), plus.takeHead2(labelled))
        a.orElse(y.subClassToJsonOpt(model, plus.takeTail1(classTags), plus.takeTail2(labelled)))
      }
    }

  }

  override final val start: EncodeSealedTraitSelector[H]#JsonEncoder[AsunaTuple0, AsunaTuple0] = {
    val con = EncodeSealedTraitSelector[H]
    new con.JsonEncoder[AsunaTuple0, AsunaTuple0] {
      override def subClassToJsonOpt(model: H, classTags: AsunaTuple0, labelled: AsunaTuple0): Option[(String, Json)] = Option.empty
    }
  }

}

object EncodeSealedContext {
  private val value                    = new EncodeSealedContext[Any]
  def apply[T]: EncodeSealedContext[T] = value.asInstanceOf[EncodeSealedContext[T]]
}
