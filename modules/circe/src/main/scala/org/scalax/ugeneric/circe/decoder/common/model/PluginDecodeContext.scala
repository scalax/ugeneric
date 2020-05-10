package org.scalax.ugeneric.circe.decoder.common.model

import asuna.{AsunaTuple0, Context4, Plus4}
import io.circe.Decoder

object PluginDecodeContext extends Context4[PluginDecodeContent] {

  private val zeroValue                         = Right(AsunaTuple0.value)
  private val zeroDecoder: Decoder[AsunaTuple0] = _ => zeroValue

  override def append[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4](x: PluginDecodeContent[X1, X2, X3, X4], y: PluginDecodeContent[Y1, Y2, Y3, Y4])(
    p: Plus4[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4]
  ): PluginDecodeContent[Z1, Z2, Z3, Z4] = {
    { (name, defaultValue, i, useDefault) =>
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

  override val start: PluginDecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] = {
    { (_, _, _, _) => zeroDecoder }
  }
}
