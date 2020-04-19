package org.scalax.kirito.circe.decoder

import asuna.{AsunaTuple0, Context2, Plus2}
import io.circe.Decoder

class DecodeSealedContext[P] extends Context2[DecodeSealedTraitSelector[P]#JsonDecoder] {

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: DecodeSealedTraitSelector[P]#JsonDecoder[X1, X2], y: DecodeSealedTraitSelector[P]#JsonDecoder[Y1, Y2])(
    p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): DecodeSealedTraitSelector[P]#JsonDecoder[Z1, Z2] = {
    val con = DecodeSealedTraitSelector[P]
    new con.JsonDecoder[Z1, Z2] {
      override def getValue(name: Z1, toAbs: Z2): Decoder[P] = {
        val a1       = p.takeHead1(name)
        val y1       = p.takeTail1(name)
        val a2       = p.takeHead2(toAbs)
        val y2       = p.takeTail2(toAbs)
        val decoderX = x.getValue(a1, a2)
        val decoderY = y.getValue(y1, y2)
        decoderX.or(decoderY)
      }
    }

  }

  override def start: DecodeSealedTraitSelector[P]#JsonDecoder[AsunaTuple0, AsunaTuple0] = {
    val con = DecodeSealedTraitSelector[P]
    new con.JsonDecoder[AsunaTuple0, AsunaTuple0] {
      override def getValue(name: AsunaTuple0, tran: AsunaTuple0): Decoder[P] = {
        Decoder.failedWithMessage("Your sealed trait have no sub class")
      }
    }
  }
}

object DecodeSealedContext {
  private val value                    = new DecodeSealedContext[Any]
  def apply[T]: DecodeSealedContext[T] = value.asInstanceOf[DecodeSealedContext[T]]
}
