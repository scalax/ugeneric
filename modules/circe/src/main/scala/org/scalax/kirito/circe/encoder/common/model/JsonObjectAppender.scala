package org.scalax.kirito.circe.encoder.common.model

abstract class JsonObjectAppender[T] {
  def getAppender(data: T): JsonObjectFieldAppender
}
