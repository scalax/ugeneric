package org.scalax.kirito.circe.decoder

import asuna.{AsunaTuple0, Context2, Plus2}
import io.circe.Decoder

object DecodeContext extends Context2[DecodeContent] {

  private val zeroValue                         = Right(AsunaTuple0.value)
  private val zeroDecoder: Decoder[AsunaTuple0] = _ => zeroValue

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: DecodeContent[X1, X2], y: DecodeContent[Y1, Y2])(
    p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): DecodeContent[Z1, Z2] = { name =>
    val xx2 = p.takeHead2(name)
    val yy2 = p.takeTail2(name)
    for {
      x1 <- x.getValue(xx2)
      y1 <- y.getValue(yy2)
    } yield {
      p.plus1(x1, y1)
    }

  }

  override val start: DecodeContent[AsunaTuple0, AsunaTuple0] = _ => zeroDecoder

}
