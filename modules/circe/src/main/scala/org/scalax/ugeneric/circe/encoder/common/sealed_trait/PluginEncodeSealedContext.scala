package org.scalax.ugeneric.circe.encoder.common.sealed_trait

import asuna.{AsunaTuple0, Context3, Plus3}

class PluginEncodeSealedContext[H] extends Context3[PluginEncodeSealedTraitSelector[H]#JsonEncoder] {
  private val con = PluginEncodeSealedTraitSelector[H]

  /*override def append[X1, X2, Y1, Y2, Z1, Z2](x: PluginEncodeSealedTraitSelector[H]#JsonEncoder[X1, X2], y: PluginEncodeSealedTraitSelector[H]#JsonEncoder[Y1, Y2])(
    plus: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): PluginEncodeSealedTraitSelector[H]#JsonEncoder[Z1, Z2] = { (model, classTags, name, i) =>
    val nameX = plus.takeHead2(name)
    val nameY = plus.takeTail2(name)
    val a     = x.subClassToJsonOpt(model, plus.takeHead1(classTags), nameX, i)
    a.orElse(y.subClassToJsonOpt(model, plus.takeTail1(classTags), nameY, i))
  }: con.JsonEncoder[Z1, Z2]

  override val start
    : PluginEncodeSealedTraitSelector[H]#JsonEncoder[AsunaTuple0, AsunaTuple0] = { (_, _, _, _) => Option.empty }: con.JsonEncoder[AsunaTuple0, AsunaTuple0]*/
  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: PluginEncodeSealedTraitSelector[H]#JsonEncoder[X1, X2, X3], y: PluginEncodeSealedTraitSelector[H]#JsonEncoder[Y1, Y2, Y3])(plus: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]): PluginEncodeSealedTraitSelector[H]#JsonEncoder[Z1, Z2, Z3] = {
    { (model, classTags, name, i) =>
      val nameX = plus.takeHead3(name)
      val nameY = plus.takeTail3(name)
      val a     = x.subClassToJsonOpt(model, plus.takeHead2(classTags), nameX, i)
      a.orElse(y.subClassToJsonOpt(model, plus.takeTail2(classTags), nameY, i))
    }: con.JsonEncoder[Z1, Z2,Z3]
  }

  override val start: PluginEncodeSealedTraitSelector[H]#JsonEncoder[AsunaTuple0, AsunaTuple0, AsunaTuple0] = {
    { (_, _, _, _) => Option.empty }: con.JsonEncoder[AsunaTuple0, AsunaTuple0]
  }
}

object PluginEncodeSealedContext {
  private val value                          = new PluginEncodeSealedContext[Any]
  def apply[T]: PluginEncodeSealedContext[T] = value.asInstanceOf[PluginEncodeSealedContext[T]]
}
