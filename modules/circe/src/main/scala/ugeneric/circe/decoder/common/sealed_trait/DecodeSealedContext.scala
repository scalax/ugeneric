package ugeneric.circe.decoder

import zsg.{Context2, Plus2}
import io.circe.Decoder

class DecodeSealedContext[P] extends Context2[DecodeSealedTraitSelector[P]#JsonDecoder] {
  private val con = DecodeSealedTraitSelector[P]

  /*override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](
    x: DecodeSealedTraitSelector[P]#JsonDecoder[X1, X2, X3],
    y: DecodeSealedTraitSelector[P]#JsonDecoder[Y1, Y2, Y3]
  )(p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]): DecodeSealedTraitSelector[P]#JsonDecoder[Z1, Z2, Z3] = new con.JsonDecoder[Z1, Z2, Z3] {

    override def getValue(name: Z2, toAbs: Z3): Decoder[P] = {
      val a1       = p.takeHead2(name)
      val y1       = p.takeTail2(name)
      val a2       = p.takeHead3(toAbs)
      val y2       = p.takeTail3(toAbs)
      val decoderX = x.getValue(a1, a2)
      val decoderY = y.getValue(y1, y2)
      decoderX.or(decoderY)
    }
  }*/

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: DecodeSealedTraitSelector[P]#JsonDecoder[X1, X2], y: DecodeSealedTraitSelector[P]#JsonDecoder[Y1, Y2])(
    p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): DecodeSealedTraitSelector[P]#JsonDecoder[Z1, Z2] = new con.JsonDecoder[Z1, Z2] {
    override def getValue(name: Z2): Decoder[P] = {
      val a1       = p.takeHead2(name)
      val y1       = p.takeTail2(name)
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
