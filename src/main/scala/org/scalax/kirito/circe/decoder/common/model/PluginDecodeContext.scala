package org.scalax.kirito.circe.decoder.common.model

import asuna.{AsunaTuple0, Context3, Plus3}
import io.circe.Decoder

object PluginDecodeContext extends Context3[PluginDecodeContent] {

  private val zeroValue                         = Right(AsunaTuple0.value)
  private val zeroDecoder: Decoder[AsunaTuple0] = _ => zeroValue

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: PluginDecodeContent[X1, X2, X3], y: PluginDecodeContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): PluginDecodeContent[Z1, Z2, Z3] = { (name, defaultValue, i, useDefault) =>
    val xx1 = p.takeHead2(name)
    val xx2 = p.takeHead3(defaultValue)
    val yy1 = p.takeTail2(name)
    val yy2 = p.takeTail3(defaultValue)
    for {
      x1 <- x.getDecoder(xx1, xx2, i, useDefault)
      y1 <- y.getDecoder(yy1, yy2, i, useDefault)
    } yield p.plus1(x1, y1)
  }

  override val start: PluginDecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] = { (_, _, _, _) => zeroDecoder }

}
