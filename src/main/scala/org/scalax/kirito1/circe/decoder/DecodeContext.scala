package org.scalax.kirito1.circe.decoder

import asuna.{AsunaTuple0, Context3, Plus3}
import io.circe.Decoder

object DecodeContext extends Context3[DecodeContent] {

  private val zeroValue = Right(AsunaTuple0.value)

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: DecodeContent[X1, X2, X3], y: DecodeContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): DecodeContent[Z1, Z2, Z3] = new DecodeContent[Z1, Z2, Z3] {
    override def getValue(name: Z2, defaultValue: Z3): Decoder[Z1] = {
      val xx1 = p.takeHead2(name)
      val xx2 = p.takeHead3(defaultValue)
      val yy1 = p.takeTail2(name)
      val yy2 = p.takeTail3(defaultValue)
      for {
        x1 <- x.getValue(xx1, xx2)
        y1 <- y.getValue(yy1, yy2)
      } yield {
        p.plus1(x1, y1)
      }
    }
  }

  override def start: DecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] = new DecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] {
    override def getValue(name: AsunaTuple0, defaultValue: AsunaTuple0): Decoder[AsunaTuple0] = {
      Decoder.instance { _ =>
        zeroValue
      }
    }
  }
}
