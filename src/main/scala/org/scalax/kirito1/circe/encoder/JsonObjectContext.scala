package org.scalax.kirito1.circe.encoder

import java.util

import asuna.{AsunaTuple0, Context2, Plus2}
import io.circe.Json

object JsonObjectContext extends Context2[JsonObjectAppender] {

  override def append[X1, X2, Y1, Y2, Z1, Z2](x: JsonObjectAppender[X1, X2], y: JsonObjectAppender[Y1, Y2])(
    p: Plus2[X1, X2, Y1, Y2, Z1, Z2]
  ): JsonObjectAppender[Z1, Z2] = new JsonObjectAppender[Z1, Z2] {
    def appendField(obj: Z1, name: Z2, m: java.util.LinkedHashMap[String, Json]): Unit = {
      y.appendField(p.takeTail1(obj), p.takeTail2(name), m)
      x.appendField(p.takeHead1(obj), p.takeHead2(name), m)
    }
  }

  override def start: JsonObjectAppender[AsunaTuple0, AsunaTuple0] = new JsonObjectAppender[AsunaTuple0, AsunaTuple0] {
    override def appendField(tt: AsunaTuple0, name: AsunaTuple0, m: util.LinkedHashMap[String, Json]): Unit = ()
  }

}
