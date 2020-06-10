package ugeneric.circe.decoder

import zsg.{Context2, Plus2, ZsgTuple0}
import io.circe.Decoder
import ugeneric.circe.NameTranslator

class PluginDecodeSealedContext[P] extends Context2[PluginDecodeSealedTraitSelector[P]#JsonDecoder] {

  private val con                     = PluginDecodeSealedTraitSelector[P]
  private val decoderZero: Decoder[P] = Decoder.failedWithMessage("Your sealed trait have no sub class")

  override val start: PluginDecodeSealedTraitSelector[P]#JsonDecoder[ZsgTuple0, ZsgTuple0] = new con.JsonDecoder[ZsgTuple0, ZsgTuple0] {
    override def getValue(name: ZsgTuple0, i: Option[NameTranslator]): Decoder[P] = decoderZero
  }

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: PluginDecodeSealedTraitSelector[P]#JsonDecoder[X1, X2], y: PluginDecodeSealedTraitSelector[P]#JsonDecoder[Y1, Y2])(
    p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): PluginDecodeSealedTraitSelector[P]#JsonDecoder[Z1, Z2] = {
    new con.JsonDecoder[Z1, Z2] {
      override def getValue(name: Z2, i: Option[NameTranslator]): Decoder[P] = {
        val a1       = p.takeHead2(name)
        val y1       = p.takeTail2(name)
        val decoderX = x.getValue(a1, i)
        val decoderY = y.getValue(y1, i)
        decoderX.or(decoderY)
      }
    }
  }

}

object PluginDecodeSealedContext {
  private val value                          = new PluginDecodeSealedContext[Any]
  def apply[T]: PluginDecodeSealedContext[T] = value.asInstanceOf[PluginDecodeSealedContext[T]]
}
