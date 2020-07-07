package ugeneric.circe.encoder

import java.util

import zsg.{Context3, Plus3, ZsgTuple0}
import io.circe.Json

class JsonObjectContext extends Context3[JsonObjectContent] {
  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: JsonObjectContent[X1, X2, X3], y: JsonObjectContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): JsonObjectContent[Z1, Z2, Z3] = new JsonObjectContent[Z1, Z2, Z3] {
    override def getAppender(data: Z3, l: util.LinkedHashMap[String, Json]): Unit = {
      val data1 = p.takeHead3(data)
      val data2 = p.takeTail3(data)
      y.getAppender(data2, l)
      x.getAppender(data1, l)
    }
  }

  override val start: JsonObjectContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] = new JsonObjectContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def getAppender(data: ZsgTuple0, l: util.LinkedHashMap[String, Json]): Unit = ()
  }
}

object JsonObjectContext {
  implicit val value: JsonObjectContext = new JsonObjectContext
}
