package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import asuna.{AsunaTuple0, Context2, Plus2}
import io.circe.Decoder

class DecodeSealedContext[P] extends Context2[DecodeSealedTraitSelector[P]#JsonDecoder] {
  private val con                     = DecodeSealedTraitSelector[P]
  private val decoderZero: Decoder[P] = Decoder.failedWithMessage("Your sealed trait have no sub class")

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: DecodeSealedTraitSelector[P]#JsonDecoder[X1, X2], y: DecodeSealedTraitSelector[P]#JsonDecoder[Y1, Y2])(
    p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): DecodeSealedTraitSelector[P]#JsonDecoder[Z1, Z2] = { (name, toAbs) =>
    val a1       = p.takeHead1(name)
    val y1       = p.takeTail1(name)
    val a2       = p.takeHead2(toAbs)
    val y2       = p.takeTail2(toAbs)
    val decoderX = x.getValue(a1, a2)
    val decoderY = y.getValue(y1, y2)
    decoderX.or(decoderY)
  }: con.JsonDecoder[Z1, Z2]

  override def start: DecodeSealedTraitSelector[P]#JsonDecoder[AsunaTuple0, AsunaTuple0] = { (_, _) => decoderZero }: con.JsonDecoder[AsunaTuple0, AsunaTuple0]
}

object DecodeSealedContext {
  private val value                    = new DecodeSealedContext[Any]
  def apply[T]: DecodeSealedContext[T] = value.asInstanceOf[DecodeSealedContext[T]]
}
