package ugeneric.circe

import java.util

import zsg.macros.single.{
  ZsgDefaultValueGeneric,
  ZsgGeneric,
  ZsgGetterGeneric,
  ZsgLabelledTypeGeneric,
  ZsgSealedClassGeneric,
  ZsgSealedGeneric,
  ZsgSealedLabelledGeneric,
  ZsgSetterGeneric
}
import zsg.{ApplicationX2, ApplicationX3, ApplicationX4}
import io.circe.{Decoder, FromLinkHashMap, Json, JsonObject}
import ugeneric.circe.decoder.{
  DecodeContent,
  DecodeContext,
  DecodeSealedContext,
  DecodeSealedTraitSelector,
  PluginDecodeContent,
  PluginDecodeContext,
  PluginDecodeSealedContext,
  PluginDecodeSealedTraitSelector
}
import ugeneric.circe.encoder.{
  EncodeSealedContext,
  EncodeSealedTraitSelector,
  JsonObjectContent,
  JsonObjectContext,
  PluginEncodeSealedContext,
  PluginEncodeSealedTraitSelector,
  PluginJsonObjectContent,
  PluginJsonObjectContext
}

object UCirce {

  def encodeCaseClass[Model, R, Prop, Name](
    implicit
    ll: ZsgGeneric.Aux[Model, R],
    nImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: ApplicationX3[JsonObjectContent, JsonObjectContext, R, Name, Prop],
    cv2: ZsgGetterGeneric[Model, Prop]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val applicationEncoder = app.application(JsonObjectContext.value)
    new VersionCompat.ObjectEncoderType[Model] {
      override def encodeObject(a: Model): JsonObject = {
        val link = new util.LinkedHashMap[String, Json]
        applicationEncoder.getAppender(cv2.getter(a), link)
        FromLinkHashMap.from(link)
      }
    }
  }

  def encodeCaseClassWithPlugin[Model, R, Prop, Name](p: Option[NameTranslator])(
    implicit ll: ZsgGeneric.Aux[Model, R],
    nImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: ApplicationX3[PluginJsonObjectContent, PluginJsonObjectContext, R, Name, Prop],
    cv2: ZsgGetterGeneric[Model, Prop]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val applicationEncoder = app.application(PluginJsonObjectContext.value)
    VersionCompat.ObjectEncoderValue.instance { o =>
      val jsonList = applicationEncoder.getAppender(cv2.getter(o), List.empty, p)
      JsonObject.fromIterable(jsonList)
    }
  }

  final def encodeCaseObject[T]: VersionCompat.ObjectEncoderType[T] = VersionCompat.ObjectEncoderValue.instance(_ => JsonObject.empty)

  final def encodeSealed[Model, R, Cls, Lab](
    implicit ll: ZsgSealedGeneric.Aux[Model, R],
    app: ApplicationX3[EncodeSealedTraitSelector[Model]#JsonEncoder, EncodeSealedContext[Model], R, Cls, Lab],
    cv1: ZsgSealedLabelledGeneric[Model, Lab],
    cv2: ZsgSealedClassGeneric[Model, Cls]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(EncodeSealedContext[Model])
    VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1)) }
  }

  final def encodeSealedWithPlugin[H, R, Cls, Lab](nameTranslator: Option[NameTranslator])(
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: ApplicationX3[PluginEncodeSealedTraitSelector[H]#JsonEncoder, PluginEncodeSealedContext[H], R, Cls, Lab],
    cv1: ZsgSealedLabelledGeneric[H, Lab],
    cv2: ZsgSealedClassGeneric[H, Cls]
  ): VersionCompat.ObjectEncoderType[H] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(PluginEncodeSealedContext[H])
    VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1, nameTranslator)) }
  }

  def decodeCaseClass[Model, R, Prop, Name](
    implicit ll: ZsgGeneric.Aux[Model, R],
    tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: ApplicationX3[DecodeContent, DecodeContext, R, Name, Prop],
    cv3: ZsgSetterGeneric[Model, Prop]
  ): Decoder[Model] = app.application(DecodeContext.value).getDecoder.map(mm => cv3.setter(mm))

  def decodeCaseClassWithPlugin[Model, R, Prop, Name, DefaultValue](nameTranslator: Option[NameTranslator], useDefaultValue: Boolean)(
    implicit ll: ZsgGeneric.Aux[Model, R],
    tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: ApplicationX4[PluginDecodeContent, PluginDecodeContext, R, Name, Prop, DefaultValue],
    cv3: ZsgSetterGeneric[Model, Prop],
    cv4: ZsgDefaultValueGeneric[Model, DefaultValue]
  ): Decoder[Model] =
    app.application(PluginDecodeContext.value).getDecoder(cv4.defaultValues, nameTranslator, useDefaultValue).map(mm => cv3.setter(mm))

  def decodeSealed[H, R, Nam](
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: ApplicationX2[DecodeSealedTraitSelector[H]#JsonDecoder, DecodeSealedContext[H], R, Nam],
    cv1: ZsgSealedLabelledGeneric[H, Nam]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(DecodeSealedContext[H]).getValue(names)
  }

  def decodeSealedWithPlugin[H, R, Nam](nameTranslator: Option[NameTranslator])(
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: ApplicationX2[PluginDecodeSealedTraitSelector[H]#JsonDecoder, PluginDecodeSealedContext[H], R, Nam],
    cv1: ZsgSealedLabelledGeneric[H, Nam]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(PluginDecodeSealedContext[H]).getValue(names, nameTranslator)
  }

}
