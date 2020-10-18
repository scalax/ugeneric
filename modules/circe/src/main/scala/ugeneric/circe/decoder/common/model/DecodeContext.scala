package ugeneric.circe.decoder

import zsg.{Context3, Plus3}
import io.circe.Decoder

class DecodeContext extends Context3[DecodeContent] {

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

}

object DecodeContext {
  val value: DecodeContext = new DecodeContext
}
