package org.scalax.ugeneric.circe.encoder.common.model

import io.circe.Json

abstract class JsonObjectAppender[T] {
  def getAppender(data: T, l: List[(String, Json)]): List[(String, Json)]
}
