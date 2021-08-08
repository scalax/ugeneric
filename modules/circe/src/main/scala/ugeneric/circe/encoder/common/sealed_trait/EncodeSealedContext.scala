package ugeneric.circe.encoder

import zsg.{Context, Plus, TypeHList}
import io.circe.Json

class EncodeSealedContext[H] extends Context[EncodeSealedTraitSelector[H]] {
  private val con = EncodeSealedTraitSelector[H]
  override def append[X <: TypeHList, Y <: TypeHList, Z <: TypeHList](
    x: EncodeSealedTraitSelector[H]#JsonEncoder[X#Head, X#Tail#Head],
    y: EncodeSealedTraitSelector[H]#JsonEncoder[Y#Head, Y#Tail#Head]
  )(plus: Plus[X, Y, Z]): EncodeSealedTraitSelector[H]#JsonEncoder[Z#Head, Z#Tail#Head] = new con.JsonEncoder[Z#Head, Z#Tail#Head] {
    override def subClassToJsonOpt(model: H, classTags: Z#Head, labelled: Z#Tail#Head): Option[(String, Json)] = {
      val ax = plus.tail.takeHead(labelled)
      val yx = plus.tail.takeTail(labelled)
      val cx = plus.takeHead(classTags)
      val cy = plus.takeTail(classTags)
      val a  = x.subClassToJsonOpt(model, cx, ax)
      a.orElse(y.subClassToJsonOpt(model, cy, yx))
    }
  }
}

object EncodeSealedContext {
  private val value                    = new EncodeSealedContext[Any]
  def apply[T]: EncodeSealedContext[T] = value.asInstanceOf[EncodeSealedContext[T]]
}
