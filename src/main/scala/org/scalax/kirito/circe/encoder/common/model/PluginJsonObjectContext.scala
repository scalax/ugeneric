package org.scalax.kirito.circe.encoder.common.model

import asuna.{AsunaTuple0, Context2, Plus2}
import io.circe.Json

object PluginJsonObjectContext extends Context2[PluginJsonObjectContent] {

  private val initObjectAppender: List[(String, Json)] => List[(String, Json)] = identity

  private val jsonObjectAppenderAsunaTuple0: JsonObjectAppender[AsunaTuple0] = _ => initObjectAppender

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: PluginJsonObjectContent[X1, X2], y: PluginJsonObjectContent[Y1, Y2])(
    p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): PluginJsonObjectContent[Z1, Z2] = { (name, i) =>
    val appender1 = x.appendField(p.takeHead2(name), i)
    val appender2 = y.appendField(p.takeTail2(name), i)

    { data =>
      val data1   = p.takeHead1(data)
      val data2   = p.takeTail1(data)
      val append1 = appender1.appendField(data1)
      val append2 = appender2.appendField(data2)
      m => append2(append1(m))
    }
  }

  override val start: PluginJsonObjectContent[AsunaTuple0, AsunaTuple0] = (_, _) => jsonObjectAppenderAsunaTuple0

}
