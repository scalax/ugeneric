package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import asuna.{AsunaTuple0, Context3, Plus3}
import io.circe.Decoder

class DecodeSealedContext[P] extends Context3[DecodeSealedTraitSelector[P]#JsonDecoder] {
  private val con                     = DecodeSealedTraitSelector[P]
  private val decoderZero: Decoder[P] = Decoder.failedWithMessage("Your sealed trait have no sub class")

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](
    x: DecodeSealedTraitSelector[P]#JsonDecoder[X1, X2, X3],
    y: DecodeSealedTraitSelector[P]#JsonDecoder[Y1, Y2, Y3]
  )(p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]): DecodeSealedTraitSelector[P]#JsonDecoder[Z1, Z2, Z3] = {
    { (name, toAbs) =>
      val a1       = p.takeHead2(name)
      val y1       = p.takeTail2(name)
      val a2       = p.takeHead3(toAbs)
      val y2       = p.takeTail3(toAbs)
      val decoderX = x.getValue(a1, a2)
      val decoderY = y.getValue(y1, y2)
      decoderX.or(decoderY)
    }: con.JsonDecoder[Z1, Z2, Z3]
  }

  override val start: DecodeSealedTraitSelector[P]#JsonDecoder[AsunaTuple0, AsunaTuple0, AsunaTuple0] = {
    { (_, _) => decoderZero }: con.JsonDecoder[AsunaTuple0, AsunaTuple0,AsunaTuple0]
  }
}

object DecodeSealedContext {
  private val value                    = new DecodeSealedContext[Any]
  def apply[T]: DecodeSealedContext[T] = value.asInstanceOf[DecodeSealedContext[T]]
}
