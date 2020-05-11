package org.scalax.ugeneric.circe.decoder.common.model

import asuna.{AsunaTuple0, Context4, Plus4}
import io.circe.Decoder
import org.scalax.ugeneric.circe.NameTranslator

object PluginDecodeContext extends Context4[PluginDecodeContent] {

  private val zeroValue                         = Right(AsunaTuple0.value)
  private val zeroDecoder: Decoder[AsunaTuple0] = Decoder.instance(_ => zeroValue)

  override def append[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4](x: PluginDecodeContent[X1, X2, X3, X4], y: PluginDecodeContent[Y1, Y2, Y3, Y4])(
    p: Plus4[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4]
  ): PluginDecodeContent[Z1, Z2, Z3, Z4] = new PluginDecodeContent[Z1, Z2, Z3, Z4] {
    override def getDecoder(name: Z3, defaultValue: Z4, i: Option[NameTranslator], useDefault: Boolean): Decoder[Z2] = {
      val xx1 = p.takeHead3(name)
      val xx2 = p.takeHead4(defaultValue)
      val yy1 = p.takeTail3(name)
      val yy2 = p.takeTail4(defaultValue)
      for {
        x1 <- x.getDecoder(xx1, xx2, i, useDefault)
        y1 <- y.getDecoder(yy1, yy2, i, useDefault)
      } yield p.plus2(x1, y1)
    }
  }

  override val start: PluginDecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] =
    new PluginDecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] {
      override def getDecoder(name: AsunaTuple0, defaultValue: AsunaTuple0, p: Option[NameTranslator], useDefaultValue: Boolean): Decoder[AsunaTuple0] = zeroDecoder
    }
}
