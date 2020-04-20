package org.scalax.kirito.circe.encoder.common.sealed_trait

import asuna.{AsunaTuple0, Context2, Plus2}

class EncodeSealedContext[H] extends Context2[EncodeSealedTraitSelector[H]#JsonEncoder] {
  private val con = EncodeSealedTraitSelector[H]

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: EncodeSealedTraitSelector[H]#JsonEncoder[X1, X2], y: EncodeSealedTraitSelector[H]#JsonEncoder[Y1, Y2])(
    plus: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): EncodeSealedTraitSelector[H]#JsonEncoder[Z1, Z2] = { (model, classTags, name) =>
    val ax = plus.takeHead2(name)
    val yx = plus.takeTail2(name)
    val cx = plus.takeHead1(classTags)
    val cy = plus.takeTail1(classTags)
    val a  = x.subClassToJsonOpt(model, cx, ax)
    a.orElse(y.subClassToJsonOpt(model, cy, yx))
  }: con.JsonEncoder[Z1, Z2]

  override val start: EncodeSealedTraitSelector[H]#JsonEncoder[AsunaTuple0, AsunaTuple0] = { (_, _, _) => Option.empty }: con.JsonEncoder[AsunaTuple0, AsunaTuple0]

}

object EncodeSealedContext {
  private val value                    = new EncodeSealedContext[Any]
  def apply[T]: EncodeSealedContext[T] = value.asInstanceOf[EncodeSealedContext[T]]
}
