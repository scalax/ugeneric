package org.scalax.ugeneric.circe.encoder.common.model

import asuna.{Application3, Context3, PropertyTag}
import asuna.macros.ByNameImplicit
import io.circe.{Encoder, Json}

trait JsonObjectContent[I, Model, Name] {
  def appendField(name: Name): JsonObjectAppender[Model]
}

object JsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: ByNameImplicit[Encoder[T]]): JsonObjectContent[PropertyTag[T], T, String] =
    new JsonObjectContent[PropertyTag[T], T, String] {
      override def appendField(name: String): JsonObjectAppender[T] = {
        new JsonObjectAppender[T] {
          override def getAppender(data: T): JsonObjectFieldAppender = new JsonObjectFieldAppender {
            override def append(m: List[(String, Json)]): List[(String, Json)] = (name, t.value(data)) :: m
          }
        }
      }
    }

}
