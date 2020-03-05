package org.scalax.kirito.circe

import asuna.macros.multiply.{AsunaMultiplyGeneric, AsunaMultiplyRepGeneric}
import asuna.macros.single.{
  AsunaDefaultValueGeneric,
  AsunaGetterGeneric,
  AsunaLabelledGeneric,
  AsunaSealedClassGeneric,
  AsunaSealedGeneric,
  AsunaSealedLabelledGeneric,
  AsunaSealedToAbsGeneric,
  AsunaSetterGeneric
}
import asuna.{Application2, Application3, Application4, TupleTag}
import io.circe.{Decoder, Encoder, JsonObject}
import org.scalax.kirito.circe.decoder.ValidatedDecoder
import org.scalax.kirito.circe.encoder.{JsonObjectContent, JsonObjectContext}
import cats.syntax.all._

object KCirce {

  def encodeCaseClassWithTable[Table, Model, Rep, R <: TupleTag, Obj, Name](table: Table)(
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
  }

  final def encodeCaseObject[T]: Encoder.AsObject[T] = Encoder.AsObject.instance(_ => JsonObject.empty)

  final def encodeSealed[H, R <: TupleTag, Cls, Lab](
    implicit ll: AsunaSealedGeneric.Aux[H, R],
    app: Application2[encoder.EncodeSealedTraitSelector[H]#JsonEncoder, R, Cls, Lab],
    cv1: AsunaSealedLabelledGeneric[H, Lab],
    cv2: AsunaSealedClassGeneric[H, Cls]
  ): Encoder.AsObject[H] = {
    val name1              = cv1.names()
    val name2              = cv2.names()
    val applicationEncoder = app.application(encoder.EncodeSealedContext[H])
    Encoder.AsObject.instance { o: H =>
      JsonObject.fromIterable(applicationEncoder.subClassToJsonOpt(o, name2, name1))
    }
  }

  def decodeCaseClassWithTable[Table, Model, R <: TupleTag, Prop, Nam, DefVal, Rep](table: Table)(
    implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R],
    app: Application4[decoder.DecodeContent, R, Prop, Nam, DefVal, Rep],
    repGeneric: AsunaMultiplyRepGeneric[Table, Model, Rep],
    cv1: AsunaLabelledGeneric[Model, Nam],
    cv3: AsunaSetterGeneric[Model, Prop],
    cv4: AsunaDefaultValueGeneric[Model, DefVal]
  ): Decoder[Model] = {
    app.application(decoder.DecodeContext).getValue(cv1.names, cv4.defaultValues, repGeneric.rep(table)).map(mm => cv3.setter(mm))
  }

  /*def decodeCaseClassWithTable11[Model] = new {
    def ii[R <: TupleTag, Table](table: Table)(implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R]) = new {
      def mi[Prop, Nam, DefVal, Rep](implicit i: Application4[decoder.DecodeContent, R, Prop, Nam, DefVal, Rep]) = new {
        def imoim(implicit cv3: AsunaSetterGeneric[Model, Prop]) = cv3
      }
    }
  }*/

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
    app: Application2[decoder.DecodeSealedTraitSelector[H]#JsonDecoder, R, Nam, Tran],
    cv1: AsunaSealedLabelledGeneric[H, Nam],
    cv2: AsunaSealedToAbsGeneric[H, Tran]
  ): Decoder[H] = {
    val names = cv1.names()
    app.application(decoder.DecodeSealedContext[H]).getValue(names, cv2.names)
  }

}
