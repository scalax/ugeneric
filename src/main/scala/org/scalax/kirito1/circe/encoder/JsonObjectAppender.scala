package org.scalax.kirito1.circe.encoder

import asuna.{Application2, Context2, PropertyTag0}
import asuna.macros.ByNameImplicit
import io.circe.{Encoder, Json}

trait JsonObjectAppender[T, II] extends Any {
  def appendField(tt: T, name: II, m: java.util.LinkedHashMap[String, Json]): Unit
}

object JsonObjectAppender {

  implicit final def asunaCirceEncoderImplicit[T](implicit t: ByNameImplicit[Encoder[T]]): Application2[JsonObjectAppender, PropertyTag0[T], T, String] =
    new Application2[JsonObjectAppender, PropertyTag0[T], T, String] {
      override final def application(context: Context2[JsonObjectAppender]): JsonObjectAppender[T, String] = new JsonObjectAppender[T, String] {
        override final def appendField(tt: T, name: String, m: java.util.LinkedHashMap[String, Json]) = m.put(name, t.value(tt))
      }
    }

}
