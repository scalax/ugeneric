package ugeneric.circe

import zsg.macros.multiply.{ZsgMultiplyGeneric, ZsgMultiplyRepGeneric}
import zsg.macros.single.{
  ZsgDefaultValueGeneric,
  ZsgGeneric,
  ZsgGetterGeneric,
  ZsgLabelledGeneric,
  ZsgLabelledTypeGeneric,
  ZsgSealedClassGeneric,
  ZsgSealedGeneric,
  ZsgSealedLabelledGeneric,
  ZsgSetterGeneric
}
import zsg.{Application2, Application3, Application4, Application6}
import io.circe.{Decoder, JsonObject}
import org.scalax.ugeneric.circe.decoder.{ValidatedDecodeContent, ValidatedDecodeContext, ValidatedDecoder}
import cats.syntax.all._
import org.scalax.ugeneric.circe.NameTranslator
import org.scalax.ugeneric.circe.decoder.common.model.{DecodeContent, DecodeContext, PluginDecodeContent, PluginDecodeContext}
import org.scalax.ugeneric.circe.decoder.common.sealed_trait.{DecodeSealedContext, DecodeSealedTraitSelector, PluginDecodeSealedContext, PluginDecodeSealedTraitSelector}
import org.scalax.ugeneric.circe.encoder.common.model.{JsonObjectContent, JsonObjectContext, PluginJsonObjectContent, PluginJsonObjectContext}
import org.scalax.ugeneric.circe.encoder.common.sealed_trait.{EncodeSealedContext, EncodeSealedTraitSelector, PluginEncodeSealedContext, PluginEncodeSealedTraitSelector}

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
    implicit ll: ZsgGeneric.Aux[Model, R],
    nImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: Application3[JsonObjectContent, R, Name, Prop],
    cv2: ZsgGetterGeneric[Model, Prop]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val applicationEncoder = app.application(JsonObjectContext)
    VersionCompat.ObjectEncoderValue.instance { o =>
      val jsonList = applicationEncoder.getAppender(cv2.getter(o), List.empty)
      JsonObject.fromIterable(jsonList)
    }
  }

  def encodeCaseClassWithPlugin[Model, R, Prop, Name](p: Option[NameTranslator])(
    implicit ll: ZsgGeneric.Aux[Model, R],
    nImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: Application3[PluginJsonObjectContent, R, Name, Prop],
    cv2: ZsgGetterGeneric[Model, Prop]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val applicationEncoder = app.application(PluginJsonObjectContext)
    VersionCompat.ObjectEncoderValue.instance { o =>
      val jsonList = applicationEncoder.getAppender(cv2.getter(o), List.empty, p)
      JsonObject.fromIterable(jsonList)
    }
  }

  final def encodeCaseObject[T]: VersionCompat.ObjectEncoderType[T] = VersionCompat.ObjectEncoderValue.instance(_ => JsonObject.empty)

  final def encodeSealed[H, R, Cls, Lab](
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: Application3[EncodeSealedTraitSelector[H]#JsonEncoder, R, Cls, Lab],
    cv1: ZsgSealedLabelledGeneric[H, Lab],
    cv2: ZsgSealedClassGeneric[H, Cls]
  ): VersionCompat.ObjectEncoderType[H] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(EncodeSealedContext[H])
    VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1)) }
  }

  final def encodeSealedWithPlugin[H, R, Cls, Lab](nameTranslator: Option[NameTranslator])(
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: Application3[PluginEncodeSealedTraitSelector[H]#JsonEncoder, R, Cls, Lab],
    cv1: ZsgSealedLabelledGeneric[H, Lab],
    cv2: ZsgSealedClassGeneric[H, Cls]
  ): VersionCompat.ObjectEncoderType[H] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(PluginEncodeSealedContext[H])
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
    implicit ll: ZsgGeneric.Aux[Model, R],
    tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: Application3[DecodeContent, R, Name, Prop],
    cv3: ZsgSetterGeneric[Model, Prop]
  ): Decoder[Model] = app.application(DecodeContext).getDecoder.map(mm => cv3.setter(mm))

  def decodeCaseClassWithPlugin[Model, R, Prop, Name, DefaultValue](nameTranslator: Option[NameTranslator], useDefaultValue: Boolean)(
    implicit ll: ZsgGeneric.Aux[Model, R],
    tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: Application4[PluginDecodeContent, R, Name, Prop, DefaultValue],
    cv3: ZsgSetterGeneric[Model, Prop],
    cv4: ZsgDefaultValueGeneric[Model, DefaultValue]
  ): Decoder[Model] =
    app.application(PluginDecodeContext).getDecoder(cv4.defaultValues, nameTranslator, useDefaultValue).map(mm => cv3.setter(mm))

  def validatedDecodeCaseClassWithTable[Table, ModelTupeType, Model, R, Prop, Nam, DefVal, Rep](table: Table)(
    implicit ll: ZsgMultiplyGeneric.Aux[Table, Model, R],
    n: ZsgGeneric.Aux[Model, ModelTupeType],
    app: Application6[ValidatedDecodeContent, R, ModelTupeType, Prop, Nam, DefVal, Rep],
    repGeneric: ZsgMultiplyRepGeneric[Table, Model, Rep],
    cv1: ZsgLabelledGeneric[Model, Nam],
    cv3: ZsgSetterGeneric[Model, Prop],
    cv4: ZsgDefaultValueGeneric[Model, DefVal]
  ): ValidatedDecoder[Model] =
    app.application(ValidatedDecodeContext).getValue(cv1.names, cv4.defaultValues, repGeneric.rep(table)).map(mm => cv3.setter(mm))

  def decodeSealed[H, R, Nam](
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: Application2[DecodeSealedTraitSelector[H]#JsonDecoder, R, Nam],
    cv1: ZsgSealedLabelledGeneric[H, Nam]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(DecodeSealedContext[H]).getValue(names)
  }

  def decodeSealedWithPlugin[H, R, Nam](nameTranslator: Option[NameTranslator])(
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: Application2[PluginDecodeSealedTraitSelector[H]#JsonDecoder, R, Nam],
    cv1: ZsgSealedLabelledGeneric[H, Nam]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(PluginDecodeSealedContext[H]).getValue(names, nameTranslator)
  }

}
