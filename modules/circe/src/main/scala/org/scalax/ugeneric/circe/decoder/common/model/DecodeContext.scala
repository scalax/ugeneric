package org.scalax.ugeneric.circe.decoder.common.model

import zsg.{Context3, Plus3, ZsgTuple0}
import io.circe.Decoder

object DecodeContext extends Context3[DecodeContent] {

  private val zeroValue                       = Right(ZsgTuple0.value)
  private val zeroDecoder: Decoder[ZsgTuple0] = Decoder.instance { _ => zeroValue }

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

  override val start: DecodeContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] = new DecodeContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def getDecoder(name: ZsgTuple0): Decoder[ZsgTuple0] = zeroDecoder
  }
}
