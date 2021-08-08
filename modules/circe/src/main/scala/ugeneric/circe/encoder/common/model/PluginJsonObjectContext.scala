package ugeneric.circe.encoder

import zsg.{Context, Plus, TypeHList}
import io.circe.Json
import ugeneric.circe.NameTranslator

class PluginJsonObjectContext extends Context[PluginJsonObjectTypeContext] {
  override def append[X <: TypeHList, Y <: TypeHList, Z <: TypeHList](x: PluginJsonObjectContent[X#Head], y: PluginJsonObjectContent[Y#Head])(
    plus: Plus[X, Y, Z]
  ): PluginJsonObjectContent[Z#Head] = new PluginJsonObjectContent[Z#Head] {
    override def getAppender(data: Z#Head, l: List[(String, Json)], p: Option[NameTranslator]): List[(String, Json)] = {
      val data1 = plus.takeHead(data)
      val data2 = plus.takeTail(data)
      x.getAppender(data1, y.getAppender(data2, l, p), p)
    }
  }
}

object PluginJsonObjectContext {
  val value: PluginJsonObjectContext = new PluginJsonObjectContext
}
