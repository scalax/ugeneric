package org.scalax.kirito.poi.reader

import zsg.{Application3, Context3, PropertyTag}
import zsg.macros.ByNameImplicit
import cats.data.Validated
import cats.implicits._
import net.scalax.cpoi.api._

import scala.collection.compat._

object ReaderFieldMeta extends ReaderMeta.FieldMetaWithNotType[ReaderMeta.RequireImplicit] {
  override val fieldNameMeta: Option[String] = Option.empty
}

object ReaderMeta {

  sealed trait ImplicitRequireOrNot

  class RequireImplicit extends ImplicitRequireOrNot
  object RequireImplicit {
    implicit def asunaPoiUntypedReader[T](
      implicit dd: ByNameImplicit[CellReader[T]]
    ): ReaderContent[PropertyTag[FieldMetaWithNotType[RequireImplicit]], PropertyTag[T], T, String, FieldMetaWithNotType[RequireImplicit]] = {
      def getName(rep: FieldMetaWithNotType[RequireImplicit], name: String): String = rep.fieldNameMeta.getOrElse(name)
      new ReaderContent[PropertyTag[FieldMetaWithNotType[RequireImplicit]], PropertyTag[T], T, String, FieldMetaWithNotType[RequireImplicit]] {
        override def getNames(name: String, rep: FieldMetaWithNotType[RequireImplicit], col: List[String]): List[String] = getName(rep, name) :: col
        override def getValue(name: String, rep: FieldMetaWithNotType[RequireImplicit]): RowReader[T] = {
          val repName = getName(rep, name)
          new RowReader[T] {
            override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, T] = {
              data.get(repName) match {
                case Some(cellContent) =>
                  cellContent.tryValue(dd.value) match {
                    case Left(r)  => Validated.invalid(rowMessageContent.build(rowIndex, name, s"${repName}列数据格式不符合"))
                    case Right(r) => Validated.valid(r)
                  }
                case _ =>
                  CPoi.wrapCell(Option.empty).tryValue(dd.value) match {
                    case Left(r)  => Validated.invalid(rowMessageContent.build(rowIndex, name, s"${repName}列不能为空"))
                    case Right(r) => Validated.valid(r)
                  }
              }
            }
          }
        }
      }
    }

    implicit def asunaPoiTypedReader[R, T](
      implicit dd: ByNameImplicit[CellReader[R]]
    ): ReaderContent[PropertyTag[FieldMeta[RequireImplicit, R, T]], PropertyTag[T], T, String, FieldMeta[RequireImplicit, R, T]] = {
      def value(rep: FieldMeta[RequireImplicit, R, T]): Option[T]               = rep.cellReaderMeta.map(_ => throw new Exception("Require 列必须不能提供字面量或 CellReader"))
      def takeName(rep: FieldMeta[RequireImplicit, R, T], name: String): String = rep.fieldNameMeta.getOrElse(name)

      new ReaderContent[PropertyTag[FieldMeta[RequireImplicit, R, T]], PropertyTag[T], T, String, FieldMeta[RequireImplicit, R, T]] {
        override def getNames(name: String, rep: FieldMeta[RequireImplicit, R, T], col: List[String]): List[String] =
          takeName(rep, name) :: col
        override def getValue(name: String, rep: FieldMeta[RequireImplicit, R, T]): RowReader[T] = {
          val repName = takeName(rep, name)
          value(rep).map(Validated.valid)
          new RowReader[T] {
            override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, T] = {
              data.get(repName) match {
                case Some(cellContent) =>
                  cellContent.tryValue(dd.value) match {
                    case Left(r) => Validated.invalid(rowMessageContent.build(rowIndex, name, s"${repName}列数据格式不符合"))
                    case Right(r) =>
                      rep.dataToResult(r).leftMap(ii => rowMessageContent.build(rowIndex, name, ii.to(List)))
                  }
                case _ =>
                  CPoi.wrapCell(Option.empty).tryValue(dd.value) match {
                    case Left(r) => Validated.invalid(rowMessageContent.build(rowIndex, name, s"${repName}列不能为空"))
                    case Right(r) =>
                      rep.dataToResult(r).leftMap(ii => rowMessageContent.build(rowIndex, name, ii.to(List)))
                  }
              }
            }
          }
        }
      }
    }
  }

  class NotRequireImplicit extends ImplicitRequireOrNot
  object NotRequireImplicit {
    implicit def asunaPoiTypedReader[R, T]
      : ReaderContent[PropertyTag[FieldMeta[NotRequireImplicit, R, T]], PropertyTag[T], T, String, FieldMeta[NotRequireImplicit, R, T]] = {
      def takeName(rep: FieldMeta[NotRequireImplicit, R, T], name: String): String = rep.fieldNameMeta.getOrElse(name)
      new ReaderContent[PropertyTag[FieldMeta[NotRequireImplicit, R, T]], PropertyTag[T], T, String, FieldMeta[NotRequireImplicit, R, T]] {
        override def getNames(name: String, rep: FieldMeta[NotRequireImplicit, R, T], col: List[String]): List[String] =
          rep.fieldNameMeta.fold(takeName(rep, name) :: col)(_ => col)
        override def getValue(name: String, rep: FieldMeta[NotRequireImplicit, R, T]): RowReader[T] = {
          val literaOrReader = rep.cellReaderMeta.getOrElse(throw new Exception("字面量和 Reader 不能同时不设置"))
          val repName        = takeName(rep, name)

          new RowReader[T] {
            override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, T] = {
              literaOrReader.fold(
                r => rep.dataToResult(r).leftMap(ii => rowMessageContent.build(rowIndex, name, ii.to(List))),
                r =>
                  data.get(repName) match {
                    case Some(cellContent) =>
                      cellContent.tryValue(r) match {
                        case Left(r) => Validated.invalid(rowMessageContent.build(rowIndex, name, s"${repName}列数据格式不符合"))
                        case Right(r) =>
                          rep.dataToResult(r).leftMap(ii => rowMessageContent.build(rowIndex, name, ii.to(List)))
                      }
                    case _ =>
                      CPoi.wrapCell(Option.empty).tryValue(r) match {
                        case Left(r) => Validated.invalid(rowMessageContent.build(rowIndex, name, s"${repName}列不能为空"))
                        case Right(r) =>
                          rep.dataToResult(r).leftMap(ii => rowMessageContent.build(rowIndex, name, ii.to(List)))
                      }
                  }
              )
            }
          }
        }
      }
    }
  }

  trait FieldMetaWithNotType[Poly <: ImplicitRequireOrNot] {
    self =>
    def fieldNameMeta: Option[String]
    def fieldName(name: String): FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String] = Option(name)
    }
    def literal[DataType](d: DataType): FieldMeta[NotRequireImplicit, DataType, DataType] =
      new FieldMeta[NotRequireImplicit, DataType, DataType] {
        override def fieldNameMeta: Option[String]                                  = self.fieldNameMeta
        override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]] = Option(Left(d))
        override def convertMeta(i: DataType): Either[Set[String], DataType]        = Either.right(i)
        override def validateMeta(i: DataType): Validated[Set[String], CPoiDone]    = Validated.valid(CPoiDone)
      }
    def reader[DataType](cellReader: CellReader[DataType]): FieldMeta[NotRequireImplicit, DataType, DataType] = new FieldMeta[NotRequireImplicit, DataType, DataType] {
      override def fieldNameMeta: Option[String]                                  = self.fieldNameMeta
      override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]] = Option(Right(cellReader))
      override def convertMeta(i: DataType): Either[Set[String], DataType]        = Either.right(i)
      override def validateMeta(i: DataType): Validated[Set[String], CPoiDone]    = Validated.valid(CPoiDone)
    }
    def validate[DataType, ValidateType](validateFunc: DataType => Validated[Set[String], CPoiDone]): FieldMeta[Poly, DataType, DataType] =
      new FieldMeta[Poly, DataType, DataType] {
        override def fieldNameMeta: Option[String]                                  = self.fieldNameMeta
        override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]] = Option.empty
        override def convertMeta(i: DataType): Either[Set[String], DataType]        = Either.right(i)
        override def validateMeta(i: DataType): Validated[Set[String], CPoiDone]    = validateFunc(i)
      }
    def andThen[DataType, ValidateType](validateFunc: DataType => Either[Set[String], ValidateType]): FieldMeta[Poly, DataType, ValidateType] =
      new FieldMeta[Poly, DataType, ValidateType] {
        override def fieldNameMeta: Option[String]                                   = self.fieldNameMeta
        override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]]  = Option.empty
        override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = validateFunc(i)
        override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
      }
    def map[DataType, TargetType](convert: DataType => TargetType): FieldMeta[Poly, DataType, TargetType] = new FieldMeta[Poly, DataType, TargetType] {
      override def fieldNameMeta: Option[String]                                  = self.fieldNameMeta
      override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]] = Option.empty
      override def convertMeta(i: DataType): Either[Set[String], TargetType]      = Right(convert(i))
      override def validateMeta(i: TargetType): Validated[Set[String], CPoiDone]  = Validated.valid(CPoiDone)
    }
  }

  trait FieldMeta[Poly <: ImplicitRequireOrNot, DataType, ValidateType] {
    self =>
    def fieldNameMeta: Option[String]
    //def literalMeta: Option[DataType]
    def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]]
    def convertMeta(i: DataType): Either[Set[String], ValidateType]
    def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone]

    def dataToResult(d: DataType): Validated[Set[String], ValidateType] = {
      Validated.fromEither(convertMeta(d)).andThen(r => validateMeta(r).map(_ => r))
    }

    def fieldName(name: String): FieldMeta[Poly, DataType, ValidateType] = new FieldMeta[Poly, DataType, ValidateType] {
      override def fieldNameMeta: Option[String] = Option(name)
      //override def literalMeta: Option[DataType]                                   = self.literalMeta
      override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]]  = self.cellReaderMeta
      override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
      override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i)
    }
    def fillReader(cellReader: CellReader[DataType]): FieldMeta[NotRequireImplicit, DataType, ValidateType] = new FieldMeta[NotRequireImplicit, DataType, ValidateType] {
      override def fieldNameMeta: Option[String] = self.fieldNameMeta
      //override def literalMeta: Option[DataType]                                   = Option.empty
      override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]]  = Option(Right(cellReader))
      override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
      override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i)
    }
    def fillLitreal(data: DataType): FieldMeta[NotRequireImplicit, DataType, ValidateType] = new FieldMeta[NotRequireImplicit, DataType, ValidateType] {
      override def fieldNameMeta: Option[String] = self.fieldNameMeta
      //override def literalMeta: Option[DataType]                                   = Option(data)
      override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]]  = Option(Left(data))
      override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
      override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i)
    }
    def validate(validateFunc: ValidateType => Validated[Set[String], CPoiDone]): FieldMeta[Poly, DataType, ValidateType] =
      new FieldMeta[Poly, DataType, ValidateType] {
        override def fieldNameMeta: Option[String] = self.fieldNameMeta
        //override def literalMeta: Option[DataType]                                   = self.literalMeta
        override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]]  = self.cellReaderMeta
        override def convertMeta(i: DataType): Either[Set[String], ValidateType]     = self.convertMeta(i)
        override def validateMeta(i: ValidateType): Validated[Set[String], CPoiDone] = self.validateMeta(i).product(validateFunc(i)).map { case (ii, _) => ii }
      }
    def andThen[TargetType](convert: ValidateType => Either[Set[String], TargetType]): FieldMeta[Poly, DataType, TargetType] =
      new FieldMeta[Poly, DataType, TargetType] {
        override def fieldNameMeta: Option[String] = self.fieldNameMeta
        //override def literalMeta: Option[DataType]                = self.literalMeta
        override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]] = self.cellReaderMeta
        override def convertMeta(i: DataType): Either[Set[String], TargetType] =
          Validated.fromEither(self.convertMeta(i)).andThen(i => self.validateMeta(i).map(_ => i)).toEither.flatMap(convert)
        override def validateMeta(i: TargetType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
      }
    def map[TargetType](convert: ValidateType => TargetType): FieldMeta[Poly, DataType, TargetType] = new FieldMeta[Poly, DataType, TargetType] {
      override def fieldNameMeta: Option[String] = self.fieldNameMeta
      //override def literalMeta: Option[DataType]                = self.literalMeta
      override def cellReaderMeta: Option[Either[DataType, CellReader[DataType]]] = self.cellReaderMeta
      override def convertMeta(i: DataType): Either[Set[String], TargetType] =
        Validated.fromEither(self.convertMeta(i)).andThen(i => self.validateMeta(i).map(_ => i)).toEither.map(convert)
      override def validateMeta(i: TargetType): Validated[Set[String], CPoiDone] = Validated.valid(CPoiDone)
    }
  }

}
