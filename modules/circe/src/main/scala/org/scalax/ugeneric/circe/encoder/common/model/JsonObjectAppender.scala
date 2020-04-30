package org.scalax.ugeneric.circe.encoder.common.model

abstract class JsonObjectAppender[T] {
  def getAppender(data: T): JsonObjectFieldAppender
}
