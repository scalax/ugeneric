package org.scalax.ugeneric.circe.encoder.common.model

import asuna.{Application3, Context3, PropertyTag}
import io.circe.Encoder

trait JsonObjectContent[I, Model, Name] {
  def appendField(name: Name): JsonObjectAppender[Model]
}

object JsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: => Encoder[T]): Application3[JsonObjectContent, PropertyTag[T], T, String] =
    new Application3[JsonObjectContent, PropertyTag[T], T, String] with JsonObjectContent[PropertyTag[T], T, String] {
      override def application(context: Context3[JsonObjectContent]): JsonObjectContent[PropertyTag[T], T, String] = this
      override def appendField(name: String): JsonObjectAppender[T]                                                = data => m => (name, t(data)) :: m
    }

}
