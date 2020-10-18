package ugeneric.circe.encoder

import zsg.{Context3, Plus3}
import io.circe.Json
import ugeneric.circe.NameTranslator

class PluginEncodeSealedContext[H] extends Context3[PluginEncodeSealedTraitSelector[H]#JsonEncoder] {
  private val con = PluginEncodeSealedTraitSelector[H]

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](
    x: PluginEncodeSealedTraitSelector[H]#JsonEncoder[X1, X2, X3],
    y: PluginEncodeSealedTraitSelector[H]#JsonEncoder[Y1, Y2, Y3]
  )(plus: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]): PluginEncodeSealedTraitSelector[H]#JsonEncoder[Z1, Z2, Z3] = new con.JsonEncoder[Z1, Z2, Z3] {
    override def subClassToJsonOpt(model: H, classTags: Z2, name: Z3, i: Option[NameTranslator]): Option[(String, Json)] = {
      val nameX = plus.takeHead3(name)
      val nameY = plus.takeTail3(name)
      val a     = x.subClassToJsonOpt(model, plus.takeHead2(classTags), nameX, i)
      a.orElse(y.subClassToJsonOpt(model, plus.takeTail2(classTags), nameY, i))
    }
  }
}

object PluginEncodeSealedContext {
  private val value                          = new PluginEncodeSealedContext[Any]
  def apply[T]: PluginEncodeSealedContext[T] = value.asInstanceOf[PluginEncodeSealedContext[T]]
}
