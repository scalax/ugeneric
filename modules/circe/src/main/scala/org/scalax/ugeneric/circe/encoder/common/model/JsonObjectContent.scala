package org.scalax.ugeneric.circe.encoder.common.model

import asuna.{Application2, PropertyTag0}
import asuna.macros.ByNameImplicit
import io.circe.Encoder

abstract class JsonObjectContent[Model, Name] {
  def appendField(name: Name): JsonObjectAppender[Model]
}

object JsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: ByNameImplicit[Encoder[T]]): Application2[JsonObjectContent, PropertyTag0[T], T, String] =
    _ => name => data => m => (name, t.value(data)) :: m

}
