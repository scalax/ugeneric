package ugeneric.circe.encoder

import java.util
import io.circe.Json
import zsg.{Context, Plus, TypeHList}

class JsonObjectContext extends Context[JsonObjectTypeContext] {
  override def append[X <: TypeHList, Y <: TypeHList, Z <: TypeHList](x: JsonObjectContent[X#Head], y: JsonObjectContent[Y#Head])(
    plus: Plus[X, Y, Z]
  ): JsonObjectContent[Z#Head] = new JsonObjectContent[Z#Head] {
    override def getAppender(data: Z#Head, l: util.LinkedHashMap[String, Json]): Unit = {
      val data2 = plus.takeTail(data)
      val data1 = plus.takeHead(data)
      x.getAppender(data1, l)
      y.getAppender(data2, l)
    }
  }
}

object JsonObjectContext {
  val value: JsonObjectContext = new JsonObjectContext
}
