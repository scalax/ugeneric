package org.scalax.ugeneric.circe.encoder.common.model

import asuna.{AsunaTuple0, Context3, Plus3}
import io.circe.Json

object JsonObjectContext extends Context3[JsonObjectContent] {

  private val initObjectAppender: JsonObjectFieldAppender = new JsonObjectFieldAppender {
    override def append(data: List[(String, Json)]): List[(String, Json)] = data
  }

  private val jsonObjectAppenderAsunaTuple0: JsonObjectAppender[AsunaTuple0] = new JsonObjectAppender[AsunaTuple0] {
    override def getAppender(data: AsunaTuple0): JsonObjectFieldAppender = {
      initObjectAppender
    }
  }

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: JsonObjectContent[X1, X2, X3], y: JsonObjectContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): JsonObjectContent[Z1, Z2, Z3] = new JsonObjectContent[Z1, Z2, Z3] {
    override def appendField(name: Z3): JsonObjectAppender[Z2] = {
      val appender1 = x.appendField(p.takeHead3(name))
      val appender2 = y.appendField(p.takeTail3(name))
      new JsonObjectAppender[Z2] {
        override def getAppender(data: Z2): JsonObjectFieldAppender = {
          val data1 = p.takeHead2(data)
          val data2 = p.takeTail2(data)
          new JsonObjectFieldAppender {
            override def append(m: List[(String, Json)]): List[(String, Json)] = {
              appender2.getAppender(data2).append(appender1.getAppender(data1).append(m))
            }
          }
        }
      }
    }
  }

  override val start: JsonObjectContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] = new JsonObjectContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] {
    override def appendField(name: AsunaTuple0): JsonObjectAppender[AsunaTuple0] = {
      jsonObjectAppenderAsunaTuple0
    }
  }
}
