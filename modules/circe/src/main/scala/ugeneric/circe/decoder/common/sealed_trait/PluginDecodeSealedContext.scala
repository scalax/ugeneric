package ugeneric.circe.decoder

import zsg.{Context, Plus, TypeHList}
import io.circe.Decoder
import ugeneric.circe.NameTranslator

class PluginDecodeSealedContext[P] extends Context[PluginDecodeSealedTraitSelector[P]] {

  private val con = PluginDecodeSealedTraitSelector[P]

  override def append[X <: TypeHList, Y <: TypeHList, Z <: TypeHList](
    x: PluginDecodeSealedTraitSelector[P]#JsonDecoder[X#Head],
    y: PluginDecodeSealedTraitSelector[P]#JsonDecoder[Y#Head]
  )(plus: Plus[X, Y, Z]): PluginDecodeSealedTraitSelector[P]#JsonDecoder[Z#Head] = new con.JsonDecoder[Z#Head] {
    override def getValue(name: Z#Head, i: Option[NameTranslator]): Decoder[P] = {
      val a1       = plus.takeHead(name)
      val y1       = plus.takeTail(name)
      val decoderX = x.getValue(a1, i)
      val decoderY = y.getValue(y1, i)
      decoderX.or(decoderY)
    }
  }

}

object PluginDecodeSealedContext {
  private val value                          = new PluginDecodeSealedContext[Any]
  def apply[T]: PluginDecodeSealedContext[T] = value.asInstanceOf[PluginDecodeSealedContext[T]]
}
