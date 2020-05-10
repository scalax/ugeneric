package org.scalax.ugeneric.circe.encoder.common.model

import asuna.{AsunaTuple0, Context3, Plus3}

object JsonObjectContext extends Context3[JsonObjectContent] {

  private val initObjectAppender: JsonObjectFieldAppender = m => m

  private val jsonObjectAppenderAsunaTuple0: JsonObjectAppender[AsunaTuple0] = _ => initObjectAppender


  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: JsonObjectContent[X1, X2, X3], y: JsonObjectContent[Y1, Y2, Y3])(p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]): JsonObjectContent[Z1, Z2, Z3] = {
    name =>
      val appender1 = x.appendField(p.takeHead3(name))
      val appender2 = y.appendField(p.takeTail3(name))

      { data =>
        val data1 = p.takeHead2(data)
        val data2 = p.takeTail2(data)
        m => appender2.getAppender(data2).append(appender1.getAppender(data1).append(m))
      }
  }

  override val start: JsonObjectContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] = _ => jsonObjectAppenderAsunaTuple0
}
