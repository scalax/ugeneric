package org.scalax.kirito.circe.encoder

import java.util

import asuna.{Application2, Context2}
import asuna.macros.PropertyTag
import io.circe.{Encoder, Json}

trait EncoderApplication[T] extends Application2[JsonObjectAppender, PropertyTag[T], T, String] {

  def encoder: Encoder[T]

  override final def application(context: Context2[JsonObjectAppender]): JsonObjectAppender[T, String] = new JsonObjectAppender[T, String] {
    override final def appendField(data: T, name: String, m: util.LinkedHashMap[String, Json]): Unit = {
      val jsonData = encoder(data)
      m.put(name, jsonData)
    }
  }

}
