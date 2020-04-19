package org.scalax.kirito.circe.validated.decoder

import asuna.{Application4, Context4, PropertyTag1}
import asuna.macros.ByNameImplicit
import asuna.macros.single.DefaultValue
import cats.data.Validated
import cats.implicits._
import io.circe.{ACursor, Decoder, Json}
import net.scalax.cpoi.api._
import org.scalax.kirito.circe.decoder.{errorMessage, ValidatedDecodeContent, ValidatedDecoder}

import scala.collection.compat._

object VDecorderFieldMeta extends ValidateDecorderMeta.FieldMetaWithNotType[ValidateDecorderMeta.RequireImplicit] {
  override val fieldNameMeta: Option[String] = Option.empty
  override val useDefaultMeta: Boolean       = false
}

object ValidateDecorderMeta {

  sealed trait ImplicitRequireOrNot

  class RequireImplicit extends ImplicitRequireOrNot
  object RequireImplicit {
    implicit def asunaCirceUntypedReader[T](
      implicit dd: ByNameImplicit[Decoder[T]]
    ): Application4[ValidatedDecodeContent, PropertyTag1[FieldMetaWithNotType[RequireImplicit], T], T, String, DefaultValue[T], FieldMetaWithNotType[RequireImplicit]] =
      new Application4[ValidatedDecodeContent, PropertyTag1[FieldMetaWithNotType[RequireImplicit], T], T, String, DefaultValue[T], FieldMetaWithNotType[RequireImplicit]] {
        override def application(context: Context4[ValidatedDecodeContent]): ValidatedDecodeContent[T, String, DefaultValue[T], FieldMetaWithNotType[RequireImplicit]] = {
          def getName(rep: FieldMetaWithNotType[RequireImplicit], name: String): String = rep.fieldNameMeta.getOrElse(name)
          new ValidatedDecodeContent[T, String, DefaultValue[T], FieldMetaWithNotType[RequireImplicit]] {
            override def getValue(name: String, defaultValue: DefaultValue[T], rep: FieldMetaWithNotType[RequireImplicit]): ValidatedDecoder[T] = {
              val fieldName = getName(rep, name)
              val simpleResult = new ValidatedDecoder[T] {
                override def getValue(json: ACursor): Validated[errorMessage, T] = {
                  json.downField(fieldName).focus match {
                    //case Some(Json.Null) => Validated.invalid(errorMessage.build(fieldName, s"${fieldName}不能为空"))
                    //case None            => Validated.invalid(errorMessage.build(fieldName, s"${fieldName}不能为空"))
                    case Some(j) =>
                      j.as(dd.value) match {
                        case Left(i) =>
                          Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                        case Right(value) => Validated.valid(value)
                      }
                    case _ =>
                      Json.Null.as(dd.value) match {
                        case Left(i) =>
                          Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                        case Right(value) => Validated.valid(value)
                      }
                  }
                }
              }
              if (rep.useDefaultMeta) {
                defaultValue.value
                  .map { (value) =>
                    new ValidatedDecoder[T] {
                      override def getValue(json: ACursor): Validated[errorMessage, T] = {
                        json.downField(fieldName).focus match {
                          case Some(Json.Null) => Validated.valid(value)
                          case None            => Validated.valid(value)
                          case Some(j) =>
                            j.as(dd.value) match {
                              case Left(i) =>
                                Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                              case Right(value) => Validated.valid(value)
                            }
                        }
                      }
                    }
                  }
                  .getOrElse(simpleResult)
              } else {
                simpleResult
              }
            }
          }
        }
      }

    /*implicit def asunaCirceUntypedOptionReader[T](
      implicit dd: ByNameImplicit[Decoder[Option[T]]]
    ): Application4[ValidatedDecodeContent, PropertyTag1[FieldMetaWithNotType[RequireImplicit], Option[T]], Option[T], String, DefaultValue[Option[T]], FieldMetaWithNotType[
      RequireImplicit
    ]] =
      new Application4[ValidatedDecodeContent, PropertyTag1[FieldMetaWithNotType[RequireImplicit], Option[T]], Option[T], String, DefaultValue[Option[T]], FieldMetaWithNotType[
        RequireImplicit
      ]] {
        override def application(
          context: Context4[ValidatedDecodeContent]
        ): ValidatedDecodeContent[Option[T], String, DefaultValue[Option[T]], FieldMetaWithNotType[RequireImplicit]] = {
          def getName(rep: FieldMetaWithNotType[RequireImplicit], name: String): String = rep.fieldNameMeta.getOrElse(name)
          new ValidatedDecodeContent[Option[T], String, DefaultValue[Option[T]], FieldMetaWithNotType[RequireImplicit]] {
            override def getValue(name: String, defaultValue: DefaultValue[Option[T]], rep: FieldMetaWithNotType[RequireImplicit]): ValidatedDecoder[Option[T]] = {
              val fieldName = getName(rep, name)
              new ValidatedDecoder[Option[T]] {
                override def getValue(json: ACursor): Validated[errorMessage, Option[T]] = {
                  json.get(fieldName)(dd.value) match {
                    case Left(i) =>
                      Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                    case Right(value) => Validated.valid(value)
                  }
                }
              }
            }
          }
        }
      }*/

    implicit def asunaCirceTypedReader[R, T](
      implicit dd: ByNameImplicit[Decoder[R]]
    ): Application4[ValidatedDecodeContent, PropertyTag1[FieldMeta[RequireImplicit, R, T], T], T, String, DefaultValue[T], FieldMeta[RequireImplicit, R, T]] =
      new Application4[ValidatedDecodeContent, PropertyTag1[FieldMeta[RequireImplicit, R, T], T], T, String, DefaultValue[T], FieldMeta[RequireImplicit, R, T]] {
        override def application(context: Context4[ValidatedDecodeContent]): ValidatedDecodeContent[T, String, DefaultValue[T], FieldMeta[RequireImplicit, R, T]] = {
          def value(rep: FieldMeta[RequireImplicit, R, T]): Option[T]               = rep.decoderMeta.map(_ => throw new Exception("Require 列必须不能提供字面量或 Decoder"))
          def takeName(rep: FieldMeta[RequireImplicit, R, T], name: String): String = rep.fieldNameMeta.getOrElse(name)

          new ValidatedDecodeContent[T, String, DefaultValue[T], FieldMeta[RequireImplicit, R, T]] {
            override def getValue(name: String, defaultValue: DefaultValue[T], rep: FieldMeta[RequireImplicit, R, T]): ValidatedDecoder[T] = {
              val fieldName = takeName(rep, name)
              value(rep).map(Validated.valid)

              val simpleResult = new ValidatedDecoder[T] {
                override def getValue(json: ACursor): Validated[errorMessage, T] = {
                  json.downField(fieldName).focus match {
                    case Some(j) =>
                      j.as(dd.value) match {
                        case Left(i) =>
                          Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                        case Right(value) =>
                          rep.dataToResult(value).leftMap(errorMessage.build(fieldName, _))
                      }
                    case _ =>
                      Json.Null.as(dd.value) match {
                        case Left(i) =>
                          Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                        case Right(value) =>
                          rep.dataToResult(value).leftMap(errorMessage.build(fieldName, _))
                      }
                  }
                }
              }
              if (rep.useDefaultMeta) {
                defaultValue.value
                  .map { (value) =>
                    new ValidatedDecoder[T] {
                      override def getValue(json: ACursor): Validated[errorMessage, T] = {
                        json.downField(fieldName).focus match {
                          case Some(Json.Null) => rep.validateMeta(value).map(_ => value).leftMap(errorMessage.build(fieldName, _))
                          case None            => rep.validateMeta(value).map(_ => value).leftMap(errorMessage.build(fieldName, _))
                          case Some(j) =>
                            j.as(dd.value) match {
                              case Left(i) =>
                                Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                              case Right(value) => rep.dataToResult(value).leftMap(errorMessage.build(fieldName, _))
                            }
                        }
                      }
                    }
                  }
                  .getOrElse(simpleResult)
              } else {
                simpleResult
              }
            }
          }
        }
      }
  }

  class NotRequireImplicit extends ImplicitRequireOrNot
  object NotRequireImplicit {
    implicit def asunaCirceTypedReader[R, T]
      : Application4[ValidatedDecodeContent, PropertyTag1[FieldMeta[NotRequireImplicit, R, T], T], T, String, DefaultValue[T], FieldMeta[NotRequireImplicit, R, T]] =
      new Application4[ValidatedDecodeContent, PropertyTag1[FieldMeta[NotRequireImplicit, R, T], T], T, String, DefaultValue[T], FieldMeta[NotRequireImplicit, R, T]] {
        override def application(context: Context4[ValidatedDecodeContent]): ValidatedDecodeContent[T, String, DefaultValue[T], FieldMeta[NotRequireImplicit, R, T]] = {
          def takeName(rep: FieldMeta[NotRequireImplicit, R, T], name: String): String = rep.fieldNameMeta.getOrElse(name)

          new ValidatedDecodeContent[T, String, DefaultValue[T], FieldMeta[NotRequireImplicit, R, T]] {
            override def getValue(name: String, defaultValue: DefaultValue[T], rep: FieldMeta[NotRequireImplicit, R, T]): ValidatedDecoder[T] = {
              val literaOrReader = rep.decoderMeta.getOrElse(throw new Exception("字面量和 Decoder 不能同时不设置"))
              val fieldName      = takeName(rep, name)

              new ValidatedDecoder[T] {
                override def getValue(json: ACursor): Validated[errorMessage, T] = {
                  literaOrReader
                    .map { (decoder) =>
                      def simpleResult = json.downField(fieldName).focus match {
                        case Some(j) =>
                          j.as(decoder) match {
                            case Left(i) =>
                              Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                            case Right(value) =>
                              rep.dataToResult(value).leftMap(errorMessage.build(fieldName, _))
                          }
                        case _ =>
                          Json.Null.as(decoder) match {
                            case Left(i) =>
                              Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                            case Right(value) =>
                              rep.dataToResult(value).leftMap(errorMessage.build(fieldName, _))
                          }
                      }
                      if (rep.useDefaultMeta) {
                        defaultValue.value
                          .map { (value) =>
                            json.downField(fieldName).focus match {
                              case Some(j) =>
                                j.as(decoder) match {
                                  case Left(i) =>
                                    Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                                  case Right(value) => rep.dataToResult(value).leftMap(errorMessage.build(fieldName, _))
                                }
                              case _ =>
                                Json.Null.as(decoder) match {
                                  case Left(i) =>
                                    Validated.invalid(errorMessage.build(fieldName, rep.typeErrorMessageMeta))
                                  case Right(value) => rep.dataToResult(value).leftMap(errorMessage.build(fieldName, _))
                                }
                            }
                          }
                          .getOrElse(simpleResult)
                      } else {
                        simpleResult
                      }
                    }
                    .fold(
                      { (value) => rep.dataToResult(value).leftMap(errorMessage.build(fieldName, _)) },
                      identity
                    )
                }
              }
            }
          }
        }
      }
  }

  trait FieldMetaWithNotType[Poly <: ImplicitRequireOrNot] {
    self =>
    def fieldNameMeta: Option[String]
    def useDefaultMeta: Boolean
    def typeErrorMessageMeta: String = fieldNameMeta.map(r => s"${r}字段 Json 类型不匹配。").getOrElse("Json 类型不匹配。")

    def fieldName(name: String): FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String] = Option(name)
      override val useDefaultMeta: Boolean       = self.useDefaultMeta
      override def typeErrorMessageMeta: String  = self.typeErrorMessageMeta
    }
    def useDefault: FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String] = self.fieldNameMeta
      override val useDefaultMeta: Boolean       = true
      override def typeErrorMessageMeta: String  = self.typeErrorMessageMeta
    }
    def notUseDefault: FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String] = self.fieldNameMeta
      override val useDefaultMeta: Boolean       = false
      override def typeErrorMessageMeta: String  = self.typeErrorMessageMeta
    }
    def typeErrorMessage(i: String): FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String] = self.fieldNameMeta
      override val useDefaultMeta: Boolean       = self.useDefaultMeta
      override def typeErrorMessageMeta: String  = self.typeErrorMessageMeta
    }
    def literal[DataType](d: DataType): FieldMeta[NotRequireImplicit, DataType, DataType] =
      new FieldMeta[NotRequireImplicit, DataType, DataType] {
        override def fieldNameMeta: Option[String]                               = self.fieldNameMeta
        override val useDefaultMeta: Boolean                                     = false
        override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]    = Option(Left(d))
        override def convertMeta(i: DataType): Either[Set[String], DataType]     = Either.right(i)
        override def validateMeta(i: DataType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
        override def typeErrorMessageMeta: String                                = self.typeErrorMessageMeta
      }
    def decoder[DataType](d: Decoder[DataType]): FieldMeta[NotRequireImplicit, DataType, DataType] =
      new FieldMeta[NotRequireImplicit, DataType, DataType] {
        override def fieldNameMeta: Option[String]                               = self.fieldNameMeta
        override val useDefaultMeta: Boolean                                     = false
        override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]    = Option(Right(d))
        override def convertMeta(i: DataType): Either[Set[String], DataType]     = Either.right(i)
        override def validateMeta(i: DataType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
        override def typeErrorMessageMeta: String                                = self.typeErrorMessageMeta
      }
    def validate[DataType, ValidateType](validateFunc: DataType => Validated[Set[String], CPoiDone]): FieldMeta[Poly, DataType, DataType] =
      new FieldMeta[Poly, DataType, DataType] {
        override def fieldNameMeta: Option[String]                               = self.fieldNameMeta
        override val useDefaultMeta: Boolean                                     = false
        override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]    = Option.empty
        override def convertMeta(i: DataType): Either[Set[String], DataType]     = Either.right(i)
        override def validateMeta(i: DataType): Validated[Set[String], CPoiDone] = validateFunc(i)
        override def typeErrorMessageMeta: String                                = self.typeErrorMessageMeta
      }
    def andThen[DataType, ValidateType](
      validateFunc: DataType => Either[Set[String], ValidateType]
    ): FieldMeta[Poly, DataType, ValidateType] =
      new FieldMeta[Poly, DataType, ValidateType] {
        override def fieldNameMeta: Option[String]                                   = self.fieldNameMeta
        override val useDefaultMeta: Boolean                                         = false
        override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]        = Option.empty
        override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = validateFunc(i)
        override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
        override def typeErrorMessageMeta: String                                    = self.typeErrorMessageMeta
      }
    def map[DataType, TargetType](convert: DataType => TargetType): FieldMeta[Poly, DataType, TargetType] =
      new FieldMeta[Poly, DataType, TargetType] {
        override def fieldNameMeta: Option[String]                                 = self.fieldNameMeta
        override val useDefaultMeta: Boolean                                       = false
        override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]      = Option.empty
        override def convertMeta(i: DataType): Either[Set[String], TargetType]     = Right(convert(i))
        override def validateMeta(i: TargetType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
        override def typeErrorMessageMeta: String                                  = self.typeErrorMessageMeta
      }
  }

  trait FieldMeta[Poly <: ImplicitRequireOrNot, DataType, ValidateType] {
    self =>
    def fieldNameMeta: Option[String]
    def useDefaultMeta: Boolean
    def decoderMeta: Option[Either[DataType, Decoder[DataType]]]
    def convertMeta(i: DataType): Either[Set[String], ValidateType]
    def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone]
    def typeErrorMessageMeta: String

    def dataToResult(d: DataType): Validated[Set[String], ValidateType] = Validated.fromEither(convertMeta(d)).andThen(r => validateMeta(r).map(_ => r))

    def fieldName(name: String): FieldMeta[Poly, DataType, ValidateType] = new FieldMeta[Poly, DataType, ValidateType] {
      override def fieldNameMeta: Option[String]                                   = Option(name)
      override val useDefaultMeta: Boolean                                         = false
      override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]        = self.decoderMeta
      override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
      override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i)
      override def typeErrorMessageMeta: String                                    = self.typeErrorMessageMeta
    }
    def fillDecoder(d: Decoder[DataType]): FieldMeta[NotRequireImplicit, DataType, ValidateType] = new FieldMeta[NotRequireImplicit, DataType, ValidateType] {
      override def fieldNameMeta: Option[String]                                   = self.fieldNameMeta
      override val useDefaultMeta: Boolean                                         = false
      override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]        = Option(Right(d))
      override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
      override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i)
      override def typeErrorMessageMeta: String                                    = self.typeErrorMessageMeta
    }
    def fillLitreal(data: DataType): FieldMeta[NotRequireImplicit, DataType, ValidateType] = new FieldMeta[NotRequireImplicit, DataType, ValidateType] {
      override def fieldNameMeta: Option[String]                                   = self.fieldNameMeta
      override val useDefaultMeta: Boolean                                         = false
      override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]        = Option(Left(data))
      override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
      override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i)
      override def typeErrorMessageMeta: String                                    = self.typeErrorMessageMeta
    }
    def validate(validateFunc: ValidateType => Validated[Set[String], CPoiDone]): FieldMeta[Poly, DataType, ValidateType] =
      new FieldMeta[Poly, DataType, ValidateType] {
        override def fieldNameMeta: Option[String]                                   = self.fieldNameMeta
        override val useDefaultMeta: Boolean                                         = false
        override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]        = self.decoderMeta
        override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
        override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i).product(validateFunc(i)).map { case (ii, _) => ii }
        override def typeErrorMessageMeta: String                                    = self.typeErrorMessageMeta
      }
    def andThen[TargetType](convert: ValidateType => Either[Set[String], TargetType]): FieldMeta[Poly, DataType, TargetType] =
      new FieldMeta[Poly, DataType, TargetType] {
        override def fieldNameMeta: Option[String]                            = self.fieldNameMeta
        override val useDefaultMeta: Boolean                                  = false
        override def decoderMeta: Option[Either[DataType, Decoder[DataType]]] = self.decoderMeta
        override def convertMeta(i: DataType): Either[Set[String], TargetType] =
          Validated.fromEither(self.convertMeta(i)).andThen(i => self.validateMeta(i).map(_ => i)).toEither.flatMap(convert)
        override def validateMeta(i: TargetType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
        override def typeErrorMessageMeta: String                                  = self.typeErrorMessageMeta
      }
    def map[TargetType](convert: ValidateType => TargetType): FieldMeta[Poly, DataType, TargetType] = new FieldMeta[Poly, DataType, TargetType] {
      override def fieldNameMeta: Option[String]                            = self.fieldNameMeta
      override val useDefaultMeta: Boolean                                  = false
      override def decoderMeta: Option[Either[DataType, Decoder[DataType]]] = self.decoderMeta
      override def convertMeta(i: DataType): Either[Set[String], TargetType] =
        Validated.fromEither(self.convertMeta(i)).andThen(i => self.validateMeta(i).map(_ => i)).toEither.map(convert)
      override def validateMeta(i: TargetType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
      override def typeErrorMessageMeta: String                                  = self.typeErrorMessageMeta
    }
    def typeErrorMessage(i: String): FieldMeta[Poly, DataType, ValidateType] = new FieldMeta[Poly, DataType, ValidateType] {
      override def fieldNameMeta: Option[String]                                   = self.fieldNameMeta
      override val useDefaultMeta: Boolean                                         = self.useDefaultMeta
      override def decoderMeta: Option[Either[DataType, Decoder[DataType]]]        = self.decoderMeta
      override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
      override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i)
      override def typeErrorMessageMeta: String                                    = self.typeErrorMessageMeta
    }
  }

}
