package ugeneric.circe.decoder

import zsg.{Context4, Plus4, ZsgTuple0}
import io.circe.Decoder
import ugeneric.circe.NameTranslator

object PluginDecodeContext extends Context4[PluginDecodeContent] {

  private val zeroValue                       = Right(ZsgTuple0.value)
  private val zeroDecoder: Decoder[ZsgTuple0] = Decoder.instance(_ => zeroValue)

  override def append[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4](x: PluginDecodeContent[X1, X2, X3, X4], y: PluginDecodeContent[Y1, Y2, Y3, Y4])(
    p: Plus4[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4]
  ): PluginDecodeContent[Z1, Z2, Z3, Z4] = new PluginDecodeContent[Z1, Z2, Z3, Z4] {
    override def getDecoder(defaultValue: Z4, i: Option[NameTranslator], useDefault: Boolean): Decoder[Z3] = {
      val xx2 = p.takeHead4(defaultValue)
      val yy2 = p.takeTail4(defaultValue)
      for {
        x1 <- x.getDecoder(xx2, i, useDefault)
        y1 <- y.getDecoder(yy2, i, useDefault)
      } yield p.plus3(x1, y1)
    }
  }

  override val start: PluginDecodeContent[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] = new PluginDecodeContent[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def getDecoder(defaultValue: ZsgTuple0, p: Option[NameTranslator], useDefaultValue: Boolean): Decoder[ZsgTuple0] = zeroDecoder
  }

}
