package org.scalax.kirito.circe.encoder.common.model

import io.circe.Json

abstract class JsonObjectFieldAppender {
  def append(data: List[(String, Json)]): List[(String, Json)]
}
