package ugeneric.circe.encoder

import zsg.{Context, Plus, TypeHList}
import io.circe.Json
import ugeneric.circe.NameTranslator

class PluginEncodeSealedContext[H] extends Context[PluginEncodeSealedTraitSelector[H]] {
  private val con = PluginEncodeSealedTraitSelector[H]
  override def append[X <: TypeHList, Y <: TypeHList, Z <: TypeHList](
    x: PluginEncodeSealedTraitSelector[H]#JsonEncoder[X#Head, X#Tail#Head],
    y: PluginEncodeSealedTraitSelector[H]#JsonEncoder[Y#Head, Y#Tail#Head]
  )(plus: Plus[X, Y, Z]): PluginEncodeSealedTraitSelector[H]#JsonEncoder[Z#Head, Z#Tail#Head] = new con.JsonEncoder[Z#Head, Z#Tail#Head] {
    override def subClassToJsonOpt(model: H, classTags: Z#Head, name: Z#Tail#Head, i: Option[NameTranslator]): Option[(String, Json)] = {
      val nameX = plus.tail.takeHead(name)
      val nameY = plus.tail.takeTail(name)
      val a     = x.subClassToJsonOpt(model, plus.takeHead(classTags), nameX, i)
      a.orElse(y.subClassToJsonOpt(model, plus.takeTail(classTags), nameY, i))
    }
  }
}

object PluginEncodeSealedContext {
  private val value                          = new PluginEncodeSealedContext[Any]
  def apply[T]: PluginEncodeSealedContext[T] = value.asInstanceOf[PluginEncodeSealedContext[T]]
}
