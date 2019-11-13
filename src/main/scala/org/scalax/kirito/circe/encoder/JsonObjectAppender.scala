package org.scalax.kirito.circe.encoder

import asuna.{Application2, Context2}
import asuna.macros.{ByNameImplicit, PropertyTag}
import io.circe.{Encoder, Json}

trait JsonObjectAppender[T, II] extends Any {
  def appendField(tt: T, name: II, m: java.util.LinkedHashMap[String, Json]): Unit
}

object JsonObjectAppender {
  implicit final def asunaCirceEncoderImplicit[T](implicit t: ByNameImplicit[Encoder[T]]): Application2[JsonObjectAppender, PropertyTag[T], T, String] =
    new AppenderApplication2(t)

  class JsonObjectAppenderImpl[T](private val en: ByNameImplicit[Encoder[T]]) extends AnyVal with JsonObjectAppender[T, String] {
    override final def appendField(tt: T, name: String, m: java.util.LinkedHashMap[String, Json]) = m.put(name, en.value(tt))
  }

  class AppenderApplication2[T](private val en: ByNameImplicit[Encoder[T]]) extends AnyVal with Application2[JsonObjectAppender, PropertyTag[T], T, String] {
    override final def application(context: Context2[JsonObjectAppender]): JsonObjectAppenderImpl[T] = new JsonObjectAppenderImpl(en)
  }
}
