package org.scalax.ugeneric.circe.encoder.common.model

import asuna.{Application3, Context3, PropertyTag}
import io.circe.Encoder

trait JsonObjectContent[I, Model, Name] {
  def appendField(name: Name): JsonObjectAppender[Model]
}

object JsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: => Encoder[T]): JsonObjectContent[PropertyTag[T], T, String] = name => data => m => (name, t(data)) :: m

}
