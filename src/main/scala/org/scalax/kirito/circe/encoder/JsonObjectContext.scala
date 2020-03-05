package org.scalax.kirito.circe.encoder

import asuna.{AsunaTuple0, Context3, Plus3}
import io.circe.Json

object JsonObjectContext extends Context3[JsonObjectContent] {

  val jsonObjectAppenderAsunaTuple0: JsonObjectAppender[AsunaTuple0] = new JsonObjectAppender[AsunaTuple0] {
    override def appendField(data: AsunaTuple0, m: List[(String, Json)]): List[(String, Json)] = m
  }

  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: JsonObjectContent[X1, X2, X3], y: JsonObjectContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): JsonObjectContent[Z1, Z2, Z3] = new JsonObjectContent[Z1, Z2, Z3] {
    override def appendField(name: Z2, rep: Z3): JsonObjectAppender[Z1] = {
      val appender1 = x.appendField(p.takeHead2(name), p.takeHead3(rep))
      val appender2 = y.appendField(p.takeTail2(name), p.takeTail3(rep))

      new JsonObjectAppender[Z1] {
        override def appendField(data: Z1, m: List[(String, Json)]): List[(String, Json)] = {
          val data1 = p.takeHead1(data)
          val data2 = p.takeTail1(data)
          appender2.appendField(data2, appender1.appendField(data1, m))
        }
      }

    }
  }

  override def start: JsonObjectContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] = new JsonObjectContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] {
    override def appendField(name: AsunaTuple0, rep: AsunaTuple0): JsonObjectAppender[AsunaTuple0] = jsonObjectAppenderAsunaTuple0
  }

}
