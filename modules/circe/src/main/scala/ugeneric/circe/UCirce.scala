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
import zsg.{Application2, Application3, Application4}
import io.circe.{Decoder, FromLinkHashMap, Json, JsonObject}
import ugeneric.circe.decoder.{DecodeContent, DecodeSealedTraitSelector, PluginDecodeContent, PluginDecodeSealedTraitSelector}
import ugeneric.circe.encoder.{EncodeSealedTraitSelector, JsonObjectContent, PluginEncodeSealedTraitSelector, PluginJsonObjectContent}

object UCirce {

  def encodeCaseClass[Model, R, Prop, Name](
    implicit
    ll: ZsgGeneric.Aux[Model, R],
    nImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: Application3[JsonObjectContent, R, Name, Prop],
    cv2: ZsgGetterGeneric[Model, Prop]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val applicationEncoder = app.application
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
    app: Application3[PluginJsonObjectContent, R, Name, Prop],
    cv2: ZsgGetterGeneric[Model, Prop]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val applicationEncoder = app.application
    VersionCompat.ObjectEncoderValue.instance { o =>
      val jsonList = applicationEncoder.getAppender(cv2.getter(o), List.empty, p)
      JsonObject.fromIterable(jsonList)
    }
  }

  final def encodeCaseObject[T]: VersionCompat.ObjectEncoderType[T] = VersionCompat.ObjectEncoderValue.instance(_ => JsonObject.empty)

  final def encodeSealed[Model, R, Cls, Lab](
    implicit ll: ZsgSealedGeneric.Aux[Model, R],
    app: Application3[EncodeSealedTraitSelector[Model]#JsonEncoder, R, Cls, Lab],
    cv1: ZsgSealedLabelledGeneric[Model, Lab],
    cv2: ZsgSealedClassGeneric[Model, Cls]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application
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
    val applicationEncoder = app.application
    VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1, nameTranslator)) }
  }

  def decodeCaseClass[Model, R, Prop, Name](
    implicit ll: ZsgGeneric.Aux[Model, R],
    tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: Application3[DecodeContent, R, Name, Prop],
    cv3: ZsgSetterGeneric[Model, Prop]
  ): Decoder[Model] = app.application.getDecoder.map(mm => cv3.setter(mm))

  def decodeCaseClassWithPlugin[Model, R, Prop, Name, DefaultValue](nameTranslator: Option[NameTranslator], useDefaultValue: Boolean)(
    implicit ll: ZsgGeneric.Aux[Model, R],
    tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    app: Application4[PluginDecodeContent, R, Name, Prop, DefaultValue],
    cv3: ZsgSetterGeneric[Model, Prop],
    cv4: ZsgDefaultValueGeneric[Model, DefaultValue]
  ): Decoder[Model] =
    app.application.getDecoder(cv4.defaultValues, nameTranslator, useDefaultValue).map(mm => cv3.setter(mm))

  def decodeSealed[H, R, Nam](
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: Application2[DecodeSealedTraitSelector[H]#JsonDecoder, R, Nam],
    cv1: ZsgSealedLabelledGeneric[H, Nam]
  ): Decoder[H] = {
    val names = cv1.names
    app.application.getValue(names)
  }

  def decodeSealedWithPlugin[H, R, Nam](nameTranslator: Option[NameTranslator])(
    implicit ll: ZsgSealedGeneric.Aux[H, R],
    app: Application2[PluginDecodeSealedTraitSelector[H]#JsonDecoder, R, Nam],
    cv1: ZsgSealedLabelledGeneric[H, Nam]
  ): Decoder[H] = {
    val names = cv1.names
    app.application.getValue(names, nameTranslator)
  }

}
