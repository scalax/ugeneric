package ugeneric.circe.encoder

import zsg.{Context3, Plus3}
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
}

object PluginJsonObjectContext {
  val value: PluginJsonObjectContext = new PluginJsonObjectContext
}
