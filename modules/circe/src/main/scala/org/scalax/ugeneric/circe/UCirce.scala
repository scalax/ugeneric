package org.scalax.ugeneric.circe

import asuna.macros.multiply.{AsunaMultiplyGeneric, AsunaMultiplyRepGeneric}
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
import asuna.{Application3, Application4, Application6}
import io.circe.{Decoder, Encoder, JsonObject}
import org.scalax.ugeneric.circe.decoder.ValidatedDecoder
import cats.syntax.all._

object UCirce {

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

  def encodeCaseClass[Model, R, Prop, Name](
    implicit ll: AsunaGeneric.Aux[Model, R],
    app: Application3[encoder.common.model.JsonObjectContent, R, Prop, Name],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv2: AsunaGetterGeneric[Model, Prop]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val names              = cv1.names
    val applicationEncoder = app.application(encoder.common.model.JsonObjectContext)
    val application2       = applicationEncoder.appendField(names)

    VersionCompat.ObjectEncoderValue.instance { o =>
      val jsonList = application2.getAppender(cv2.getter(o)).append(List.empty)
      JsonObject.fromIterable(jsonList)
    }
  }

  def encodeCaseClassWithPlugin[Model, R, Prop, Name](p: Option[NameTranslator])(
    implicit ll: AsunaGeneric.Aux[Model, R],
    app: Application3[encoder.common.model.PluginJsonObjectContent, R, Prop, Name],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv2: AsunaGetterGeneric[Model, Prop]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val names              = cv1.names
    val applicationEncoder = app.application(encoder.common.model.PluginJsonObjectContext)
    val application2       = applicationEncoder.appendField(names, p)

    VersionCompat.ObjectEncoderValue.instance { o =>
      val jsonList = application2.getAppender(cv2.getter(o)).append(List.empty)
      JsonObject.fromIterable(jsonList)
    }
  }

  final def encodeCaseObject[T]: VersionCompat.ObjectEncoderType[T] = VersionCompat.ObjectEncoderValue.instance(_ => JsonObject.empty)

  final def encodeSealed[H, R, Cls, Lab](
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application3[encoder.common.sealed_trait.EncodeSealedTraitSelector[H]#JsonEncoder, R, Cls, Lab],
    cv1: AsunaSealedLabelledGeneric[H, Lab],
    cv2: AsunaSealedClassGeneric[H, Cls]
  ): VersionCompat.ObjectEncoderType[H] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(encoder.common.sealed_trait.EncodeSealedContext[H])
    VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1)) }
  }

  final def encodeSealedWithPlugin[H, R, Cls, Lab](nameTranslator: Option[NameTranslator])(
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application3[encoder.common.sealed_trait.PluginEncodeSealedTraitSelector[H]#JsonEncoder, R, Cls, Lab],
    cv1: AsunaSealedLabelledGeneric[H, Lab],
    cv2: AsunaSealedClassGeneric[H, Cls]
  ): VersionCompat.ObjectEncoderType[H] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(encoder.common.sealed_trait.PluginEncodeSealedContext[H])
    VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1, nameTranslator)) }
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

  def decodeCaseClass[Model, R, Prop, Name](
    implicit ll: AsunaGeneric.Aux[Model, R],
    app: Application3[decoder.common.model.DecodeContent, R, Prop, Name],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv3: AsunaSetterGeneric[Model, Prop]
  ): Decoder[Model] = app.application(decoder.common.model.DecodeContext).getDecoder(cv1.names).map(mm => cv3.setter(mm))

  def decodeCaseClassWithPlugin[Model, R, Prop, Name, DefaultValue](nameTranslator: Option[NameTranslator], useDefaultValue: Boolean)(
    implicit ll: AsunaGeneric.Aux[Model, R],
    app: Application4[decoder.common.model.PluginDecodeContent, R, Prop, Name, DefaultValue],
    cv1: AsunaLabelledGeneric[Model, Name],
    cv3: AsunaSetterGeneric[Model, Prop],
    cv4: AsunaDefaultValueGeneric[Model, DefaultValue]
  ): Decoder[Model] =
    app.application(decoder.common.model.PluginDecodeContext).getDecoder(cv1.names, cv4.defaultValues, nameTranslator, useDefaultValue).map(mm => cv3.setter(mm))

  def validatedDecodeCaseClassWithTable[Table, ModelTupeType, Model, R, Prop, Nam, DefVal, Rep](table: Table)(
    implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R],
    n: AsunaGeneric.Aux[Model, ModelTupeType],
    app: Application6[decoder.ValidatedDecodeContent, R, ModelTupeType, Prop, Nam, DefVal, Rep],
    repGeneric: AsunaMultiplyRepGeneric[Table, Model, Rep],
    cv1: AsunaLabelledGeneric[Model, Nam],
    cv3: AsunaSetterGeneric[Model, Prop],
    cv4: AsunaDefaultValueGeneric[Model, DefVal]
  ): ValidatedDecoder[Model] =
    app.application(decoder.ValidatedDecodeContext).getValue(cv1.names, cv4.defaultValues, repGeneric.rep(table)).map(mm => cv3.setter(mm))

  def decodeSealed[H, R, Nam, Tran](
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application3[decoder.common.sealed_trait.DecodeSealedTraitSelector[H]#JsonDecoder, R, Nam, Tran],
    cv1: AsunaSealedLabelledGeneric[H, Nam],
    cv2: AsunaSealedToAbsGeneric[H, Tran]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(decoder.common.sealed_trait.DecodeSealedContext[H]).getValue(names, cv2.names)
  }

  def decodeSealedWithPlugin[H, R, Nam, Tran](nameTranslator: Option[NameTranslator])(
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application3[decoder.common.sealed_trait.PluginDecodeSealedTraitSelector[H]#JsonDecoder, R, Nam, Tran],
    cv1: AsunaSealedLabelledGeneric[H, Nam],
    cv2: AsunaSealedToAbsGeneric[H, Tran]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(decoder.common.sealed_trait.PluginDecodeSealedContext[H]).getValue(names, cv2.names, nameTranslator)
  }

}
