package org.scalax.ugeneric.circe.encoder.common.model

import io.circe.Json

abstract class JsonObjectFieldAppender {
  def append(data: List[(String, Json)]): List[(String, Json)]
}
