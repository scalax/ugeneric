package org.scalax.kirito.circe.decoder

import asuna.{AsunaTuple0, Context4, Plus4}
import io.circe.Decoder

object DecodeContext extends Context4[DecodeContent] {

  private val zeroValue = Right(AsunaTuple0.value)

  override def append[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4](x: DecodeContent[X1, X2, X3, X4], y: DecodeContent[Y1, Y2, Y3, Y4])(
    p: Plus4[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4]
  ): DecodeContent[Z1, Z2, Z3, Z4] = new DecodeContent[Z1, Z2, Z3, Z4] {
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

  override def start: DecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] = new DecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] {
    override def getValue(name: AsunaTuple0, defaultValue: AsunaTuple0, rep: AsunaTuple0): Decoder[AsunaTuple0] = {
      Decoder.instance { _ =>
        zeroValue
      }
    }
  }
}
