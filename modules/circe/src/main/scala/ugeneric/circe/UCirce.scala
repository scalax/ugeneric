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
import zsg.{Application2, Application3, Application4, Context2, Context3, Context4}
import io.circe.{Decoder, FromLinkHashMap, Json, JsonObject}
import ugeneric.circe.VersionCompat.ObjectEncoderType
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

  trait OEncoder[Model] extends VersionCompat.ObjectEncoderType[Model] {
    def contextImpl(context: Context3[JsonObjectContent]): OEncoder.InnerOEncoder2[Model]

    override def encodeObject(a: Model): JsonObject = {
      contextImpl(JsonObjectContext).toEncoder(OEncoder.InnerOEncoder[Model]).encodeObject(a)
    }
  }

  object OEncoder {
    def builder_211[T](n: Context3[JsonObjectContent] => OEncoder.InnerOEncoder[T] => VersionCompat.ObjectEncoderType[T]): OEncoder[T] = {
      new OEncoder[T] {
        override def contextImpl(context: Context3[JsonObjectContent]): InnerOEncoder2[T] = new InnerOEncoder2[T] {
          override def toEncoder(inner: InnerOEncoder[T]): ObjectEncoderType[T] = n(context)(inner)
        }
      }
    }

    class InnerOEncoder[Model] {
      def encode[R, Prop, Name](
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
    }

    object InnerOEncoder {
      val innerOEncoder: InnerOEncoder[Any] = new InnerOEncoder[Any]
      def apply[T]: InnerOEncoder[T]        = innerOEncoder.asInstanceOf[InnerOEncoder[T]]
    }

    abstract class InnerOEncoder2[Model] {
      def toEncoder(inner: InnerOEncoder[Model]): VersionCompat.ObjectEncoderType[Model]
    }
  }

  /*object EncodeCaseClassApply {
    val value: EncodeCaseClassApply[Any]  = new EncodeCaseClassApply[Any]
    def apply[T]: EncodeCaseClassApply[T] = value.asInstanceOf[EncodeCaseClassApply[T]]
  }*/

  /*def encodeCaseClass[Model](
    n: Context3[JsonObjectContent] => EncodeCaseClassApply[Model] => VersionCompat.ObjectEncoderType[Model]
  ): VersionCompat.ObjectEncoderType[Model] = n(JsonObjectContext)(EncodeCaseClassApply.apply)*/

  /*def encodeCaseClass[Model, R, Prop, Name](
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
  }*/

  /*def encodeCaseClass1[Model](
    n: EncodeCaseClassApply1[Model]
  ): VersionCompat.ObjectEncoderType[Model] = n.encodeCaseClass(new EncodeCaseClassApply[Model])*/

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

  /*class EncodeSealedApply[H] {
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
  ): VersionCompat.ObjectEncoderType[H] = n(EncodeSealedContext[H])(new EncodeSealedApply)*/

  trait SEncoder[Model] extends VersionCompat.ObjectEncoderType[Model] {
    def contextImpl(context: Context3[EncodeSealedTraitSelector[Model]#JsonEncoder]): SEncoder.InnerSEncoder2[Model]

    override def encodeObject(a: Model): JsonObject = {
      contextImpl(EncodeSealedContext[Model]).toEncoder(SEncoder.InnerSEncoder[Model]).encodeObject(a)
    }
  }

  object SEncoder {
    def builder_211[T](n: Context3[EncodeSealedTraitSelector[T]#JsonEncoder] => SEncoder.InnerSEncoder[T] => VersionCompat.ObjectEncoderType[T]): SEncoder[T] = {
      new SEncoder[T] {
        override def contextImpl(context: Context3[EncodeSealedTraitSelector[T]#JsonEncoder]): InnerSEncoder2[T] = new InnerSEncoder2[T] {
          override def toEncoder(inner: InnerSEncoder[T]): VersionCompat.ObjectEncoderType[T] = n(EncodeSealedContext[T])(inner)
        }
      }
    }

    class InnerSEncoder[Model] {
      final def encode[R, Cls, Lab](
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
    }

    object InnerSEncoder {
      val innerSEncoder: InnerSEncoder[Any] = new InnerSEncoder[Any]
      def apply[T]: InnerSEncoder[T]        = innerSEncoder.asInstanceOf[InnerSEncoder[T]]
    }

    abstract class InnerSEncoder2[Model] {
      def toEncoder(inner: InnerSEncoder[Model]): VersionCompat.ObjectEncoderType[Model]
    }
  }

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
