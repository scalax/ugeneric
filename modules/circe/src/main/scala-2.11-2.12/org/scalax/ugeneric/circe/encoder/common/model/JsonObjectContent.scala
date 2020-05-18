package org.scalax.ugeneric.circe.encoder.common.model

import zsg.PropertyTag
import zsg.macros.ByNameImplicit
import io.circe.{Encoder, Json}

trait JsonObjectContent[I, Model, Name] {
  def appendField(name: Name): JsonObjectAppender[Model]
}

object JsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: ByNameImplicit[Encoder[T]]): JsonObjectContent[PropertyTag[T], T, String] =
    new JsonObjectContent[PropertyTag[T], T, String] {
      override def appendField(name: String): JsonObjectAppender[T] = {
        new JsonObjectAppender[T] {
          override def getAppender(data: T, l: List[(String, Json)]): List[(String, Json)] = (name, t.value(data)) :: l
        }
      }
    }

}
