package org.scalax.kirito1.circe

import asuna.macros.single.{
  AsunaDefaultValueGeneric,
  AsunaGeneric,
  AsunaGetterGeneric,
  AsunaLabelledGeneric,
  AsunaSealedClassGeneric,
  AsunaSealedGeneric,
  AsunaSealedLabelledGeneric,
  AsunaSealedToAbsGeneric,
  AsunaSetterGeneric
}
import asuna.{Application2, Application3, TupleTag}
import io.circe.{CirceUtils, Decoder, Encoder, Json, JsonObject}
import org.scalax.kirito1.circe.encoder.{JsonObjectAppender, JsonObjectContext}

object UCirce1 {
  final def encodeCaseClass[H, R <: TupleTag, Obj, Na](
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

  final def encodeCaseObject[T]: Encoder.AsObject[T] = Encoder.AsObject.instance(_ => JsonObject.empty)

  final def encodeSealed[H, R <: TupleTag, Cls, Lab](
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application2[encoder.EncodeSealedTraitSelector[H]#JsonEncoder, R, Cls, Lab],
    cv1: AsunaSealedLabelledGeneric[H, Lab],
    cv2: AsunaSealedClassGeneric[H, Cls]
  ): Encoder.AsObject[H] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(encoder.EncodeSealedContext[H])
    Encoder.AsObject.instance { o: H => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1)) }
  }

  def decodeCaseClass[T, R <: TupleTag, Model, Nam, DefVal](
    implicit ll: AsunaGeneric.Aux[T, R],
    app: Application3[decoder.DecodeContent, R, Model, Nam, DefVal],
    cv1: AsunaLabelledGeneric[T, Nam],
    cv3: AsunaSetterGeneric[T, Model],
    cv4: AsunaDefaultValueGeneric[T, DefVal]
  ): Decoder[T] = {
    app.application(decoder.DecodeContext).getValue(cv1.names, cv4.defaultValues).map(mm => cv3.setter(mm))
  }

  def decodeSealed[H, R <: TupleTag, Nam, Tran](
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application2[decoder.DecodeSealedTraitSelector[H]#JsonDecoder, R, Nam, Tran],
    cv1: AsunaSealedLabelledGeneric[H, Nam],
    cv2: AsunaSealedToAbsGeneric[H, Tran]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(decoder.DecodeSealedContext[H]).getValue(names, cv2.names)
  }

}
