package ugeneric.circe.decoder

import zsg.{Context3, Plus3, ZsgTuple0}
import io.circe.Decoder

object DecodeContext extends Context3[DecodeContent] {

  private val zeroValue                       = Right(ZsgTuple0.value)
  private val zeroDecoder: Decoder[ZsgTuple0] = Decoder.instance { _ => zeroValue }

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: DecodeContent[X1, X2, X3], y: DecodeContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): DecodeContent[Z1, Z2, Z3] = new DecodeContent[Z1, Z2, Z3] {
    override def getDecoder: Decoder[Z3] = {
      for {
        x1 <- x.getDecoder
        y1 <- y.getDecoder
      } yield p.plus3(x1, y1)
    }
  }

  override val start: DecodeContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] = new DecodeContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def getDecoder: Decoder[ZsgTuple0] = zeroDecoder
  }

}
