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
import zsg.{Application2, Application3, Application4, Application6, Context2, Context3, Context4}
import io.circe.{Decoder, JsonObject}
import org.scalax.ugeneric.circe.decoder.{ValidatedDecodeContent, ValidatedDecodeContext, ValidatedDecoder}
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

  class EncodeCaseClassApply[Model] {
    def encodeCaseClass[R, Prop, Name](
      implicit ll: ZsgGeneric.Aux[Model, R],
      nImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
      app: Application3[JsonObjectContent, R, Name, Prop],
      cv2: ZsgGetterGeneric[Model, Prop]
    ): VersionCompat.ObjectEncoderType[Model] = {
      val applicationEncoder = app.application
      VersionCompat.ObjectEncoderValue.instance { o =>
        val jsonList = applicationEncoder.getAppender(cv2.getter(o), List.empty)
        JsonObject.fromIterable(jsonList)
      }
    }
  }

  def encodeCaseClass[Model](
    n: Context3[JsonObjectContent] => EncodeCaseClassApply[Model] => VersionCompat.ObjectEncoderType[Model]
  ): VersionCompat.ObjectEncoderType[Model] = n(JsonObjectContext)(new EncodeCaseClassApply)

  class EncodeCaseClassWithPlugin[Model] {
    def encodeCaseClassWithPlugin[R, Prop, Name](p: Option[NameTranslator])(
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
  }

  def encodeCaseClassWithPlugin[Model](
    n: Context3[PluginJsonObjectContent] => EncodeCaseClassWithPlugin[Model] => VersionCompat.ObjectEncoderType[Model]
  ): VersionCompat.ObjectEncoderType[Model] = n(PluginJsonObjectContext)(new EncodeCaseClassWithPlugin)

  final def encodeCaseObject[T]: VersionCompat.ObjectEncoderType[T] = VersionCompat.ObjectEncoderValue.instance(_ => JsonObject.empty)

  class EncodeSealedApply[H] {
    final def encodeSealed[R, Cls, Lab](
      implicit ll: ZsgSealedGeneric.Aux[H, R],
      app: Application3[EncodeSealedTraitSelector[H]#JsonEncoder, R, Cls, Lab],
      cv1: ZsgSealedLabelledGeneric[H, Lab],
      cv2: ZsgSealedClassGeneric[H, Cls]
    ): VersionCompat.ObjectEncoderType[H] = {
      val name1              = cv1.names
      val name2              = cv2.names
      val applicationEncoder = app.application
      VersionCompat.ObjectEncoderValue.instance { o => JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1)) }
    }
  }

  def encodeSealed[H](
    n: Context3[EncodeSealedTraitSelector[H]#JsonEncoder] => EncodeSealedApply[H] => VersionCompat.ObjectEncoderType[H]
  ): VersionCompat.ObjectEncoderType[H] = n(EncodeSealedContext[H])(new EncodeSealedApply)

  class EncodeSealedWithPluginApply[H] {
    final def encodeSealedWithPlugin[R, Cls, Lab](nameTranslator: Option[NameTranslator])(
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
  }

  def encodeSealedWithPlugin[H](
    n: Context3[PluginEncodeSealedTraitSelector[H]#JsonEncoder] => EncodeSealedWithPluginApply[H] => VersionCompat.ObjectEncoderType[H]
  ): VersionCompat.ObjectEncoderType[H] = n(PluginEncodeSealedContext[H])(new EncodeSealedWithPluginApply[H])

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

  class DecodeCaseClass[Model] {
    def decodeCaseClass[R, Prop, Name](
      implicit ll: ZsgGeneric.Aux[Model, R],
      tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
      app: Application3[DecodeContent, R, Name, Prop],
      cv3: ZsgSetterGeneric[Model, Prop]
    ): Decoder[Model] = app.application.getDecoder.map(mm => cv3.setter(mm))
  }

  def decodeCaseClass[Model](n: Context3[DecodeContent] => DecodeCaseClass[Model] => Decoder[Model]): Decoder[Model] = n(DecodeContext)(new DecodeCaseClass)

  class DecodeCaseClassWithPlugin[Model] {
    def decodeCaseClassWithPlugin[R, Prop, Name, DefaultValue](nameTranslator: Option[NameTranslator], useDefaultValue: Boolean)(
      implicit ll: ZsgGeneric.Aux[Model, R],
      tImplicit: ZsgLabelledTypeGeneric.Aux[Model, Name],
      app: Application4[PluginDecodeContent, R, Name, Prop, DefaultValue],
      cv3: ZsgSetterGeneric[Model, Prop],
      cv4: ZsgDefaultValueGeneric[Model, DefaultValue]
    ): Decoder[Model] =
      app.application.getDecoder(cv4.defaultValues, nameTranslator, useDefaultValue).map(mm => cv3.setter(mm))
  }

  def decodeCaseClassWithPlugin[Model](n: Context4[PluginDecodeContent] => DecodeCaseClassWithPlugin[Model] => Decoder[Model]): Decoder[Model] =
    n(PluginDecodeContext)(new DecodeCaseClassWithPlugin)

  /*def validatedDecodeCaseClassWithTable[Table, ModelTupeType, Model, R, Prop, Nam, DefVal, Rep](table: Table)(
    implicit ll: ZsgMultiplyGeneric.Aux[Table, Model, R],
    n: ZsgGeneric.Aux[Model, ModelTupeType],
    app: Application6[ValidatedDecodeContent, R, ModelTupeType, Prop, Nam, DefVal, Rep],
    repGeneric: ZsgMultiplyRepGeneric[Table, Model, Rep],
    cv1: ZsgLabelledGeneric[Model, Nam],
    cv3: ZsgSetterGeneric[Model, Prop],
    cv4: ZsgDefaultValueGeneric[Model, DefVal]
  ): ValidatedDecoder[Model] =
    app.application(ValidatedDecodeContext).getValue(cv1.names, cv4.defaultValues, repGeneric.rep(table)).map(mm => cv3.setter(mm))*/

  class DecodeSealed[H] {
    def decodeSealed[R, Nam](
      implicit ll: ZsgSealedGeneric.Aux[H, R],
      app: Application2[DecodeSealedTraitSelector[H]#JsonDecoder, R, Nam],
      cv1: ZsgSealedLabelledGeneric[H, Nam]
    ): Decoder[H] = {
      val names = cv1.names
      app.application.getValue(names)
    }
  }

  def decodeSealed[H](n: Context2[DecodeSealedTraitSelector[H]#JsonDecoder] => DecodeSealed[H] => Decoder[H]): Decoder[H] = n(DecodeSealedContext[H])(new DecodeSealed)

  class DecodeSealedWithPlugin[H] {
    def decodeSealedWithPlugin[R, Nam](nameTranslator: Option[NameTranslator])(
      implicit ll: ZsgSealedGeneric.Aux[H, R],
      app: Application2[PluginDecodeSealedTraitSelector[H]#JsonDecoder, R, Nam],
      cv1: ZsgSealedLabelledGeneric[H, Nam]
    ): Decoder[H] = {
      val names = cv1.names
      app.application.getValue(names, nameTranslator)
    }
  }

  def decodeSealedWithPlugin[H](n: Context2[PluginDecodeSealedTraitSelector[H]#JsonDecoder] => DecodeSealedWithPlugin[H] => Decoder[H]): Decoder[H] =
    n(PluginDecodeSealedContext[H])(new DecodeSealedWithPlugin)

}
