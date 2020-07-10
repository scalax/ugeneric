package ugeneric.circe.encoder

import zsg.{Context3, Plus3, ZsgTuple0}
import io.circe.Json
import ugeneric.circe.NameTranslator

class PluginJsonObjectContext extends Context3[PluginJsonObjectContent] {
  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: PluginJsonObjectContent[X1, X2, X3], y: PluginJsonObjectContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): PluginJsonObjectContent[Z1, Z2, Z3] = new PluginJsonObjectContent[Z1, Z2, Z3] {
    override def getAppender(data: Z3, l: List[(String, Json)], np: Option[NameTranslator]): List[(String, Json)] = {
      val data1 = p.takeHead3(data)
      val data2 = p.takeTail3(data)
      x.getAppender(data1, y.getAppender(data2, l, np), np)
    }
  }

  override val start: PluginJsonObjectContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] = new PluginJsonObjectContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def getAppender(data: ZsgTuple0, l: List[(String, Json)], p: Option[NameTranslator]): List[(String, Json)] = l
  }
}

object PluginJsonObjectContext {
  val value: PluginJsonObjectContext = new PluginJsonObjectContext
}
