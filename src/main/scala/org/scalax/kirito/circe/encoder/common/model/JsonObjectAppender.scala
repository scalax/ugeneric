package org.scalax.kirito.circe.encoder.common.model

import io.circe.Json

abstract class JsonObjectAppender[T] {
  def appendField(data: T): List[(String, Json)] => List[(String, Json)]
}
