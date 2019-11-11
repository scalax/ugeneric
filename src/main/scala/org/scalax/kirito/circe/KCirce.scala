package org.scalax.kirito.circe

import asuna.macros.{AsunaGeneric, AsunaGetterGeneric, AsunaLabelledGeneric}
import asuna.{Application2, TupleTag}
import io.circe.{CirceUtils, Encoder, Json, JsonObject}
import org.scalax.kirito.circe.encoder.{JsonObjectAppender, JsonObjectContext}

object KCirce {
  def encodeCaseClass[H, R <: TupleTag, Obj, Na](
    implicit ll: AsunaGeneric.Aux[H, R],
    app: Application2[JsonObjectAppender, R, Obj, Na],
    cv1: AsunaLabelledGeneric[H, Na],
    cv2: AsunaGetterGeneric[H, Obj]
  ): Encoder.AsObject[H] = {
    val names              = cv1.names
    val applicationEncoder = app.application(JsonObjectContext)
    Encoder.AsObject.instance { o: H =>
      val linkedMap = new java.util.LinkedHashMap[String, Json]
      applicationEncoder.appendField(cv2.getter(o), names, linkedMap)
      CirceUtils.jsonObjectFromMap(linkedMap)
    }
  }

  def encodeCaseObject[T]: Encoder.AsObject[T] = Encoder.AsObject.instance(_ => JsonObject.empty)
}
