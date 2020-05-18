package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import zsg.{Context3, Plus3, ZsgTuple0}
import io.circe.Decoder
import org.scalax.ugeneric.circe.NameTranslator

class PluginDecodeSealedContext[P] extends Context3[PluginDecodeSealedTraitSelector[P]#JsonDecoder] {
  private val con                     = PluginDecodeSealedTraitSelector[P]
  private val decoderZero: Decoder[P] = Decoder.failedWithMessage("Your sealed trait have no sub class")

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](
    x: PluginDecodeSealedTraitSelector[P]#JsonDecoder[X1, X2, X3],
    y: PluginDecodeSealedTraitSelector[P]#JsonDecoder[Y1, Y2, Y3]
  )(p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]): PluginDecodeSealedTraitSelector[P]#JsonDecoder[Z1, Z2, Z3] = new con.JsonDecoder[Z1, Z2, Z3] {
    override def getValue(name: Z2, toAbs: Z3, i: Option[NameTranslator]): Decoder[P] = {
      val a1       = p.takeHead2(name)
      val y1       = p.takeTail2(name)
      val a2       = p.takeHead3(toAbs)
      val y2       = p.takeTail3(toAbs)
      val decoderX = x.getValue(a1, a2, i)
      val decoderY = y.getValue(y1, y2, i)
      decoderX.or(decoderY)
    }
  }

  override val start: PluginDecodeSealedTraitSelector[P]#JsonDecoder[ZsgTuple0, ZsgTuple0, ZsgTuple0] = new con.JsonDecoder[ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def getValue(name: ZsgTuple0, tran: ZsgTuple0, i: Option[NameTranslator]): Decoder[P] = decoderZero
  }
}

object PluginDecodeSealedContext {
  private val value                          = new PluginDecodeSealedContext[Any]
  def apply[T]: PluginDecodeSealedContext[T] = value.asInstanceOf[PluginDecodeSealedContext[T]]
}
