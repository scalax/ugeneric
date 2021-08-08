package ugeneric.circe.decoder

import io.circe.Decoder
import ugeneric.circe.NameTranslator
import zsg.{Context, Plus, TypeHList}

class PluginDecodeContext extends Context[PluginDecoderTypeContext] {
  override def append[X <: TypeHList, Y <: TypeHList, Z <: TypeHList](
    x: PluginDecodeContent[X#Head, X#Tail#Head],
    y: PluginDecodeContent[Y#Head, Y#Tail#Head]
  )(plus: Plus[X, Y, Z]): PluginDecodeContent[Z#Head, Z#Tail#Head] = new PluginDecodeContent[Z#Head, Z#Tail#Head] {
    override def getDecoder(defaultValue: Z#Tail#Head, p: Option[NameTranslator], useDefaultValue: Boolean): Decoder[Z#Head] = {
      val xx2 = plus.tail.takeHead(defaultValue)
      val yy2 = plus.tail.takeTail(defaultValue)
      for {
        x1 <- x.getDecoder(xx2, p, useDefaultValue)
        y1 <- y.getDecoder(yy2, p, useDefaultValue)
      } yield plus.plus(x1, y1)
    }
  }
}

object PluginDecodeContext {
  val value: PluginDecodeContext = new PluginDecodeContext
}
