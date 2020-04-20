package org.scalax.kirito.circe

import asuna.macros.multiply.{AsunaMultiplyGeneric, AsunaMultiplyRepGeneric}
import asuna.macros.single
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
import asuna.{Application2, Application4, TupleTag}
import io.circe.{Decoder, Encoder, JsonObject}
import org.scalax.kirito.circe.decoder.ValidatedDecoder
import cats.syntax.all._

object KCirce {

  /*def encodeCaseClassWithTable[Table, Model, Rep, R <: TupleTag, Obj, Name](table: Table)(
    implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R],
    app: Application3[JsonObjectContent, R, Obj, Name, Rep],
    repGeneric: AsunaMultiplyRepGeneric[Table, Model, Rep],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv2: AsunaGetterGeneric[Model, Obj]
  ): Encoder.AsObject[Model] = {
    val names              = cv1.names()
    val applicationEncoder = app.application(JsonObjectContext)
    val application2       = applicationEncoder.appendField(names, repGeneric.rep(table))
    Encoder.AsObject.instance { o: Model =>
      val jsonList = application2.appendField(cv2.getter(o), List.empty)
      JsonObject.fromIterable(jsonList)
    }
  }*/

  def encodeCaseClass[Model, R <: TupleTag, Prop, Name](
    implicit ll: single.AsunaGeneric.Aux[Model, R],
    app: Application2[encoder.common.model.JsonObjectContent, R, Prop, Name],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv2: AsunaGetterGeneric[Model, Prop]
  ): Encoder.AsObject[Model] = {
    val names              = cv1.names()
    val applicationEncoder = app.application(encoder.common.model.JsonObjectContext)
    val application2       = applicationEncoder.appendField(names)

    { o =>
      val jsonList = application2.appendField(cv2.getter(o))(List.empty)
      JsonObject.fromIterable(jsonList)
    }
  }

  def encodeCaseClassWithPlugin[Model, R <: TupleTag, Prop, Name](p: Option[NameTranslator])(
    implicit ll: single.AsunaGeneric.Aux[Model, R],
    app: Application2[encoder.common.model.PluginJsonObjectContent, R, Prop, Name],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv2: AsunaGetterGeneric[Model, Prop]
  ): Encoder.AsObject[Model] = {
    val names              = cv1.names()
    val applicationEncoder = app.application(encoder.common.model.PluginJsonObjectContext)
    val application2       = applicationEncoder.appendField(names, p)

    { o =>
      val jsonList = application2.appendField(cv2.getter(o))(List.empty)
      JsonObject.fromIterable(jsonList)
    }
  }

  final def encodeCaseObject[T]: Encoder.AsObject[T] = _ => JsonObject.empty

  final def encodeSealed[H, R <: TupleTag, Cls, Lab](
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application2[encoder.EncodeSealedTraitSelector[H]#JsonEncoder, R, Cls, Lab],
    cv1: AsunaSealedLabelledGeneric[H, Lab],
    cv2: AsunaSealedClassGeneric[H, Cls]
  ): Encoder.AsObject[H] = {
    val name1              = cv1.names()
    val name2              = cv2.names()
    val applicationEncoder = app.application(encoder.EncodeSealedContext[H])
    Encoder.AsObject.instance { o: H => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1)) }
  }

  /*def decodeCaseClassWithTable[Table, Model, R <: TupleTag, Prop, Nam, DefVal, Rep](table: Table)(
    implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R],
    app: Application4[decoder.DecodeContent, R, Prop, Nam, DefVal, Rep],
    repGeneric: AsunaMultiplyRepGeneric[Table, Model, Rep],
    cv1: AsunaLabelledGeneric[Model, Nam],
    cv3: AsunaSetterGeneric[Model, Prop],
    cv4: AsunaDefaultValueGeneric[Model, DefVal]
  ): Decoder[Model] = {
    app.application(decoder.DecodeContext).getValue(cv1.names, cv4.defaultValues, repGeneric.rep(table)).map(mm => cv3.setter(mm))
  }*/

  def decodeCaseClass[Model, R <: TupleTag, Prop, Name](
    implicit ll: AsunaGeneric.Aux[Model, R],
    app: Application2[decoder.common.model.DecodeContent, R, Prop, Name],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv3: AsunaSetterGeneric[Model, Prop]
  ): Decoder[Model] = app.application(decoder.common.model.DecodeContext).getDecoder(cv1.names).map(mm => cv3.setter(mm))

  def decodeCaseClassWithPlugin[Model, R <: TupleTag, Prop, Name](nameTranslator: Option[NameTranslator], useDefaultValue: Boolean)(
    implicit ll: AsunaGeneric.Aux[Model, R],
    app: Application2[decoder.common.model.DecodeContent, R, Prop, Name],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv3: AsunaSetterGeneric[Model, Prop]
  ): Decoder[Model] = app.application(decoder.common.model.DecodeContext).getDecoder(cv1.names).map(mm => cv3.setter(mm))

  def validatedDecodeCaseClassWithTable[Table, Model, R <: TupleTag, Prop, Nam, DefVal, Rep](table: Table)(
    implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R],
    app: Application4[decoder.ValidatedDecodeContent, R, Prop, Nam, DefVal, Rep],
    repGeneric: AsunaMultiplyRepGeneric[Table, Model, Rep],
    cv1: AsunaLabelledGeneric[Model, Nam],
    cv3: AsunaSetterGeneric[Model, Prop],
    cv4: AsunaDefaultValueGeneric[Model, DefVal]
  ): ValidatedDecoder[Model] = {
    app.application(decoder.ValidatedDecodeContext).getValue(cv1.names, cv4.defaultValues, repGeneric.rep(table)).map(mm => cv3.setter(mm))
  }

  def decodeSealed[H, R <: TupleTag, Nam, Tran](
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application2[decoder.common.sealed_trait.DecodeSealedTraitSelector[H]#JsonDecoder, R, Nam, Tran],
    cv1: AsunaSealedLabelledGeneric[H, Nam],
    cv2: AsunaSealedToAbsGeneric[H, Tran]
  ): Decoder[H] = {
    val names = cv1.names()
    app.application(decoder.common.sealed_trait.DecodeSealedContext[H]).getValue(names, cv2.names)
  }

}
