package org.scalax.ugeneric.circe.encoder.common.model

import asuna.PropertyTag
import io.circe.Encoder

abstract class JsonObjectContent[I, Model, Name] {
  def appendField(name: Name): JsonObjectAppender[Model]
}

object JsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: => Encoder[T]): JsonObjectContent[PropertyTag[T], T, String] = name => data => m => (name, t(data)) :: m

}
