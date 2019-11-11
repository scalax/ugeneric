package io.circe

import java.util

object CirceUtils {
  def jsonObjectFromMap(link: util.LinkedHashMap[String, Json]): JsonObject = JsonObject.fromLinkedHashMap(link)
}
