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
import zsg.{Application, TagMerge2, TypeHList}
import io.circe.{Decoder, FromLinkHashMap, Json, JsonObject}
import ugeneric.circe.decoder.{
  DecodeContext,
  DecodeSealedContext,
  DecodeSealedTraitSelector,
  DecoderTypeContext,
  PluginDecodeContext,
  PluginDecodeSealedContext,
  PluginDecodeSealedTraitSelector,
  PluginDecoderTypeContext
}
import ugeneric.circe.encoder.{
  EncodeSealedContext,
  EncodeSealedTraitSelector,
  JsonObjectContext,
  JsonObjectTypeContext,
  PluginEncodeSealedContext,
  PluginEncodeSealedTraitSelector,
  PluginJsonObjectContext,
  PluginJsonObjectTypeContext
}

object UCirce {

  def encodeCaseClass[Model, R, Name, I, P <: TypeHList](implicit
    ll: ZsgGeneric.Aux[Model, R],
    nImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    merge: TagMerge2.Aux[R, Name, I],
    app: Application[JsonObjectTypeContext, JsonObjectContext, I, P],
    cv2: ZsgGetterGeneric[Model, P#Head]
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

  def encodeCaseClassWithPlugin[Model, R, Name, I, P <: TypeHList](p: Option[NameTranslator])(implicit
    ll: ZsgGeneric.Aux[Model, R],
    nImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    merge: TagMerge2.Aux[R, Name, I],
    app: Application[PluginJsonObjectTypeContext, PluginJsonObjectContext, I, P],
    cv2: ZsgGetterGeneric[Model, P#Head]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val applicationEncoder = app.application(PluginJsonObjectContext.value)
    VersionCompat.ObjectEncoderValue.instance { o =>
      val jsonList = applicationEncoder.getAppender(cv2.getter(o), List.empty, p)
      JsonObject.fromIterable(jsonList)
    }
  }

  final def encodeCaseObject[T]: VersionCompat.ObjectEncoderType[T] = VersionCompat.ObjectEncoderValue.instance(_ => JsonObject.empty)

  final def encodeSealed[Model, R, P <: TypeHList](implicit
    ll: ZsgSealedGeneric.Aux[Model, R],
    app: Application[EncodeSealedTraitSelector[Model], EncodeSealedContext[Model], R, P],
    cv1: ZsgSealedLabelledGeneric[Model, P#Tail#Head],
    cv2: ZsgSealedClassGeneric[Model, P#Head]
  ): VersionCompat.ObjectEncoderType[Model] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(EncodeSealedContext[Model])
    VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1)) }
  }

  final def encodeSealedWithPlugin[H, R, P <: TypeHList](nameTranslator: Option[NameTranslator])(implicit
    ll: ZsgSealedGeneric.Aux[H, R],
    app: Application[PluginEncodeSealedTraitSelector[H], PluginEncodeSealedContext[H], R, P],
    cv1: ZsgSealedLabelledGeneric[H, P#Tail#Head],
    cv2: ZsgSealedClassGeneric[H, P#Head]
  ): VersionCompat.ObjectEncoderType[H] = {
    val name1              = cv1.names
    val name2              = cv2.names
    val applicationEncoder = app.application(PluginEncodeSealedContext[H])
    VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1, nameTranslator)) }
  }

  def decodeCaseClass[Model, R, Name, I, P <: TypeHList](implicit
    ll: ZsgGeneric.Aux[Model, R],
    tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    merge: TagMerge2.Aux[R, Name, I],
    app: Application[DecoderTypeContext, DecodeContext, I, P],
    cv3: ZsgSetterGeneric[Model, P#Head]
  ): Decoder[Model] = app.application(DecodeContext.value).map(mm => cv3.setter(mm))

  def decodeCaseClassWithPlugin[Model, R, Name, I, P <: TypeHList](nameTranslator: Option[NameTranslator], useDefaultValue: Boolean)(implicit
    ll: ZsgGeneric.Aux[Model, R],
    tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
    merge: TagMerge2.Aux[R, Name, I],
    app: Application[PluginDecoderTypeContext, PluginDecodeContext, I, P],
    cv3: ZsgSetterGeneric[Model, P#Head],
    cv4: ZsgDefaultValueGeneric[Model, P#Tail#Head]
  ): Decoder[Model] =
    app.application(PluginDecodeContext.value).getDecoder(cv4.defaultValues, nameTranslator, useDefaultValue).map(mm => cv3.setter(mm))

  def decodeSealed[H, R, P <: TypeHList](implicit
    ll: ZsgSealedGeneric.Aux[H, R],
    app: Application[DecodeSealedTraitSelector[H], DecodeSealedContext[H], R, P],
    cv1: ZsgSealedLabelledGeneric[H, P#Head]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(DecodeSealedContext[H]).getValue(names)
  }

  def decodeSealedWithPlugin[H, R, P <: TypeHList](nameTranslator: Option[NameTranslator])(implicit
    ll: ZsgSealedGeneric.Aux[H, R],
    app: Application[PluginDecodeSealedTraitSelector[H], PluginDecodeSealedContext[H], R, P],
    cv1: ZsgSealedLabelledGeneric[H, P#Head]
  ): Decoder[H] = {
    val names = cv1.names
    app.application(PluginDecodeSealedContext[H]).getValue(names, nameTranslator)
  }

}
