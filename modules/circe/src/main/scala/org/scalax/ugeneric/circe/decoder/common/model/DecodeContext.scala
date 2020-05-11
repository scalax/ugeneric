package org.scalax.ugeneric.circe.decoder.common.model

import asuna.{AsunaTuple0, Context3, Plus3}
import io.circe.Decoder

object DecodeContext extends Context3[DecodeContent] {

  private val zeroValue                         = Right(AsunaTuple0.value)
  private val zeroDecoder: Decoder[AsunaTuple0] = Decoder.instance { _ => zeroValue }

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: DecodeContent[X1, X2, X3], y: DecodeContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): DecodeContent[Z1, Z2, Z3] = new DecodeContent[Z1, Z2, Z3] {
    override def getDecoder(name: Z3): Decoder[Z2] = {
      val xx2 = p.takeHead3(name)
      val yy2 = p.takeTail3(name)
      for {
        x1 <- x.getDecoder(xx2)
        y1 <- y.getDecoder(yy2)
      } yield p.plus2(x1, y1)
    }
  }

  override val start: DecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] = new DecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] {
    override def getDecoder(name: AsunaTuple0): Decoder[AsunaTuple0] = zeroDecoder
  }
}
