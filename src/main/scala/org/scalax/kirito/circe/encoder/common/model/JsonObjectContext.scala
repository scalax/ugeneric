package org.scalax.kirito.circe.encoder.common.model

import asuna.{AsunaTuple0, Context2, Plus2}
import io.circe.Json

object JsonObjectContext extends Context2[JsonObjectContent] {

  private val initObjectAppender: List[(String, Json)] => List[(String, Json)] = identity

  private val jsonObjectAppenderAsunaTuple0: JsonObjectAppender[AsunaTuple0] = _ => initObjectAppender

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: JsonObjectContent[X1, X2], y: JsonObjectContent[Y1, Y2])(p: Plus2[X1, X2, Y1, Y2, Z1, Z2]): JsonObjectContent[Z1, Z2] = {
    name =>
      val appender1 = x.appendField(p.takeHead2(name))
      val appender2 = y.appendField(p.takeTail2(name))

      { data =>
        val data1 = p.takeHead1(data)
        val data2 = p.takeTail1(data)
        m => appender2.appendField(data2)(appender1.appendField(data1)(m))
      }
  }

  override val start: JsonObjectContent[AsunaTuple0, AsunaTuple0] = _ => jsonObjectAppenderAsunaTuple0

}
