package org.scalax.ugeneric.circe.encoder.common.model

import zsg.{Context3, Plus3, ZsgTuple0}
import io.circe.Json
import org.scalax.ugeneric.circe.NameTranslator

object PluginJsonObjectContext extends Context3[PluginJsonObjectContent] {

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: PluginJsonObjectContent[X1, X2, X3], y: PluginJsonObjectContent[Y1, Y2, Y3])(
    plus: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): PluginJsonObjectContent[Z1, Z2, Z3] = new PluginJsonObjectContent[Z1, Z2, Z3] {
    override def appendField(name: Z3, p: Option[NameTranslator]): JsonObjectAppender[Z2] = {
      val appender1 = x.appendField(plus.takeHead3(name), p)
      val appender2 = y.appendField(plus.takeTail3(name), p)
      new JsonObjectAppender[Z2] {
        override def getAppender(data: Z2, l: List[(String, Json)]): List[(String, Json)] = {
          val data1 = plus.takeHead2(data)
          val data2 = plus.takeTail2(data)
          appender2.getAppender(data2, appender1.getAppender(data1, l))
        }
      }
    }
  }

  override val start: PluginJsonObjectContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] = new PluginJsonObjectContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    val jsonObjectAppenderAsunaTuple0: JsonObjectAppender[ZsgTuple0] = new JsonObjectAppender[ZsgTuple0] {
      override def getAppender(data: ZsgTuple0, l: List[(String, Json)]): List[(String, Json)] = l
    }
    override def appendField(name: ZsgTuple0, p: Option[NameTranslator]): JsonObjectAppender[ZsgTuple0] = {
      jsonObjectAppenderAsunaTuple0
    }
  }

}
