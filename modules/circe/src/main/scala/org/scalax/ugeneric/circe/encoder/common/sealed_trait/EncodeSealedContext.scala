package org.scalax.ugeneric.circe.encoder.common.sealed_trait

import asuna.{AsunaTuple0, Context3, Plus3}

class EncodeSealedContext[H] extends Context3[EncodeSealedTraitSelector[H]#JsonEncoder] {
  private val con = EncodeSealedTraitSelector[H]

  /*override def append[X1, X2, Y1, Y2, Z1, Z2](x: EncodeSealedTraitSelector[H]#JsonEncoder[X1, X2], y: EncodeSealedTraitSelector[H]#JsonEncoder[Y1, Y2])(
    plus: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): EncodeSealedTraitSelector[H]#JsonEncoder[Z1, Z2] = { (model, classTags, name) =>
    val ax = plus.takeHead2(name)
    val yx = plus.takeTail2(name)
    val cx = plus.takeHead1(classTags)
    val cy = plus.takeTail1(classTags)
    val a  = x.subClassToJsonOpt(model, cx, ax)
    a.orElse(y.subClassToJsonOpt(model, cy, yx))
  }: con.JsonEncoder[Z1, Z2]

  override val start: EncodeSealedTraitSelector[H]#JsonEncoder[AsunaTuple0, AsunaTuple0] = { (_, _, _) => Option.empty }: con.JsonEncoder[AsunaTuple0, AsunaTuple0]*/
  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](
    x: EncodeSealedTraitSelector[H]#JsonEncoder[X1, X2, X3],
    y: EncodeSealedTraitSelector[H]#JsonEncoder[Y1, Y2, Y3]
  )(plus: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]): EncodeSealedTraitSelector[H]#JsonEncoder[Z1, Z2, Z3] = {
    { (model, classTags, name) =>
      val ax = plus.takeHead3(name)
      val yx = plus.takeTail3(name)
      val cx = plus.takeHead2(classTags)
      val cy = plus.takeTail2(classTags)
      val a  = x.subClassToJsonOpt(model, cx, ax)
      a.orElse(y.subClassToJsonOpt(model, cy, yx))
    }: con.JsonEncoder[Z1, Z2, Z3]
  }

  override val start: EncodeSealedTraitSelector[H]#JsonEncoder[AsunaTuple0, AsunaTuple0, AsunaTuple0] = {
    { (_, _, _) => Option.empty }: con.JsonEncoder[AsunaTuple0, AsunaTuple0, AsunaTuple0]
  }
}

object EncodeSealedContext {
  private val value                    = new EncodeSealedContext[Any]
  def apply[T]: EncodeSealedContext[T] = value.asInstanceOf[EncodeSealedContext[T]]
}
