package org.scalax.ugeneric.circe.encoder.common.model

import asuna.{Application3, PropertyTag}
import io.circe.Encoder

abstract class JsonObjectContent[I, Model, Name] {
  def appendField(name: Name): JsonObjectAppender[Model]
}

object JsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: => Encoder[T]): Application3[JsonObjectContent, PropertyTag[T], T, String] =
    _ => name => data => m => (name, t(data)) :: m

}
