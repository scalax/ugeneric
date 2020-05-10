package org.scalax.ugeneric.circe.decoder

import asuna.{AsunaTuple0, Context4, Plus4}
import io.circe.Decoder

object PluginDecodeContext extends Context4[PluginDecodeContent] {

  private val zeroValue = Right(AsunaTuple0.value)

  override def append[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4](x: PluginDecodeContent[X1, X2, X3, X4], y: PluginDecodeContent[Y1, Y2, Y3, Y4])(
    p: Plus4[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4]
  ): PluginDecodeContent[Z1, Z2, Z3, Z4] = new PluginDecodeContent[Z1, Z2, Z3, Z4] {
    override def getValue(name: Z2, defaultValue: Z3, rep: Z4): Decoder[Z1] = {
      val xx1 = p.takeHead2(name)
      val xx2 = p.takeHead3(defaultValue)
      val zz1 = p.takeHead4(rep)
      val yy1 = p.takeTail2(name)
      val yy2 = p.takeTail3(defaultValue)
      val zz2 = p.takeTail4(rep)
      for {
        x1 <- x.getValue(xx1, xx2, zz1)
        y1 <- y.getValue(yy1, yy2, zz2)
      } yield {
        p.plus1(x1, y1)
      }
    }
  }

  override def start: PluginDecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] =
    new PluginDecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] {
      override def getValue(name: AsunaTuple0, defaultValue: AsunaTuple0, rep: AsunaTuple0): Decoder[AsunaTuple0] = {
        Decoder.instance { _ => zeroValue }
      }
    }
}
