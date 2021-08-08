package ugeneric.circe.decoder

import zsg.{Context, Plus, TypeHList}
import io.circe.Decoder

class DecodeSealedContext[P] extends Context[DecodeSealedTraitSelector[P]] {
  private val con = DecodeSealedTraitSelector[P]
  override def append[X <: TypeHList, Y <: TypeHList, Z <: TypeHList](
    x: DecodeSealedTraitSelector[P]#JsonDecoder[X#Head],
    y: DecodeSealedTraitSelector[P]#JsonDecoder[Y#Head]
  )(plus: Plus[X, Y, Z]): DecodeSealedTraitSelector[P]#JsonDecoder[Z#Head] = new con.JsonDecoder[Z#Head] {
    override def getValue(name: Z#Head): Decoder[P] = {
      val a1       = plus.takeHead(name)
      val y1       = plus.takeTail(name)
      val decoderX = x.getValue(a1)
      val decoderY = y.getValue(y1)
      decoderX.or(decoderY)
    }
  }
}

object DecodeSealedContext {
  private val value                    = new DecodeSealedContext[Any]
  def apply[T]: DecodeSealedContext[T] = value.asInstanceOf[DecodeSealedContext[T]]
}
