package io.circe

import java.util

object FromLinkHashMap {
  def from(l: util.LinkedHashMap[String, Json]): JsonObject = JsonObject.fromLinkedHashMap(l)
}
