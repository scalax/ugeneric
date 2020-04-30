package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import asuna.{AsunaTuple0, Context2, Plus2}
import io.circe.Decoder

class PluginDecodeSealedContext[P] extends Context2[PluginDecodeSealedTraitSelector[P]#JsonDecoder] {
  private val con                     = PluginDecodeSealedTraitSelector[P]
  private val decoderZero: Decoder[P] = Decoder.failedWithMessage("Your sealed trait have no sub class")

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: PluginDecodeSealedTraitSelector[P]#JsonDecoder[X1, X2], y: PluginDecodeSealedTraitSelector[P]#JsonDecoder[Y1, Y2])(
    p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): PluginDecodeSealedTraitSelector[P]#JsonDecoder[Z1, Z2] = { (name, toAbs, i) =>
    val a1       = p.takeHead1(name)
    val y1       = p.takeTail1(name)
    val a2       = p.takeHead2(toAbs)
    val y2       = p.takeTail2(toAbs)
    val decoderX = x.getValue(a1, a2, i)
    val decoderY = y.getValue(y1, y2, i)
    decoderX.or(decoderY)
  }: con.JsonDecoder[Z1, Z2]

  override def start: PluginDecodeSealedTraitSelector[P]#JsonDecoder[AsunaTuple0, AsunaTuple0] = { (_, _, _) => decoderZero }: con.JsonDecoder[AsunaTuple0, AsunaTuple0]
}

object PluginDecodeSealedContext {
  private val value                          = new PluginDecodeSealedContext[Any]
  def apply[T]: PluginDecodeSealedContext[T] = value.asInstanceOf[PluginDecodeSealedContext[T]]
}
