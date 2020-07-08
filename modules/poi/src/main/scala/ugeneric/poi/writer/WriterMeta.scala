package org.scalax.kirito.poi.writer

import asuna.{Application3, Context3, PropertyTag1}
import net.scalax.cpoi.content.{CellDataAbs, CellDataImpl}
import net.scalax.cpoi.rw.CellWriter
import net.scalax.cpoi.api._

import scala.collection.compat._

object WriterFieldMeta extends WriterMeta.FieldMetaWithNotType[WriterMeta.RequireImplicit] {
  override def fieldNameMeta: Option[String]                    = Option.empty
  override def ignoreMeta: Boolean                              = false
  override def cellStyleMeta: List[StyleTransform]              = List.empty
  override def literaMeta: Option[WriterMeta.LiteraMetaContent] = Option.empty
}

object WriterMeta {
  self =>

  sealed trait ImplicitRequireOrNot
  class RequireImplicit extends ImplicitRequireOrNot
  object RequireImplicit {
    implicit def untypedApplication[H](
      implicit writer: CellWriter[H]
    ): Application3[RowWriterContent, PropertyTag1[FieldMetaWithNotType[RequireImplicit], H], H, String, FieldMetaWithNotType[RequireImplicit]] =
      new Application3[RowWriterContent, PropertyTag1[FieldMetaWithNotType[RequireImplicit], H], H, String, FieldMetaWithNotType[RequireImplicit]] {
        override def application(context: Context3[RowWriterContent]): RowWriterContent[H, String, FieldMetaWithNotType[RequireImplicit]] =
          new RowWriterContent[H, String, FieldMetaWithNotType[RequireImplicit]] {
            override def appendColumnTitle(name: String, rep: FieldMetaWithNotType[RequireImplicit], titles: List[String]): List[String] = {
              if (rep.literaMeta.isDefined) {
                throw new Exception("需要隐式 CellWriter 的列不能有字面量类型")
              }
              if (rep.ignoreMeta) titles else rep.fieldNameMeta.getOrElse(name) :: titles
            }
            override def appendField(tt: H, name: String, rep: FieldMetaWithNotType[RequireImplicit], data: List[(String, CellDataAbs)]): List[(String, CellDataAbs)] = {
              if (rep.ignoreMeta) data
              else {
                ((rep.fieldNameMeta.getOrElse(name), CPoi.wrapData(tt).addTransform(rep.cellStyleMeta))) :: data
              }
            }
          }
      }

    implicit def typedApplication[H, I](
      implicit writer: CellWriter[I]
    ): Application3[RowWriterContent, PropertyTag1[FieldMetaWithType[RequireImplicit, H, I], H], H, String, FieldMetaWithType[RequireImplicit, H, I]] =
      new Application3[RowWriterContent, PropertyTag1[FieldMetaWithType[RequireImplicit, H, I], H], H, String, FieldMetaWithType[RequireImplicit, H, I]] {
        override def application(context: Context3[RowWriterContent]): RowWriterContent[H, String, FieldMetaWithType[RequireImplicit, H, I]] =
          new RowWriterContent[H, String, FieldMetaWithType[RequireImplicit, H, I]] {
            override def appendColumnTitle(name: String, rep: FieldMetaWithType[RequireImplicit, H, I], titles: List[String]): List[String] = {
              if (rep.writerMeta.isDefined) {
                throw new Exception("需要隐式 CellWriter 的列不能定义 CellWriter")
              }
              if (rep.ignoreMeta) titles else rep.fieldNameMeta.getOrElse(name) :: titles
            }
            override def appendField(
              tt: H,
              name: String,
              rep: FieldMetaWithType[RequireImplicit, H, I],
              data: List[(String, CellDataAbs)]
            ): List[(String, CellDataAbs)] = {
              if (rep.ignoreMeta) data else ((rep.fieldNameMeta.getOrElse(name), CPoi.wrapData(rep.mapperMeta(tt)).addTransform(rep.cellStyleMeta))) :: data
            }
          }
      }
  }

  class NotRequireImplicit extends ImplicitRequireOrNot

  object NotRequireImplicit {
    implicit def untypedApplication[H]
      : Application3[RowWriterContent, PropertyTag1[FieldMetaWithNotType[NotRequireImplicit], H], H, String, FieldMetaWithNotType[NotRequireImplicit]] =
      new Application3[RowWriterContent, PropertyTag1[FieldMetaWithNotType[NotRequireImplicit], H], H, String, FieldMetaWithNotType[NotRequireImplicit]] {
        override def application(context: Context3[RowWriterContent]): RowWriterContent[H, String, FieldMetaWithNotType[NotRequireImplicit]] =
          new RowWriterContent[H, String, FieldMetaWithNotType[NotRequireImplicit]] {
            override def appendColumnTitle(name: String, rep: FieldMetaWithNotType[NotRequireImplicit], titles: List[String]): List[String] = {
              if (rep.ignoreMeta) titles else rep.fieldNameMeta.getOrElse(name) :: titles
            }
            override def appendField(
              tt: H,
              name: String,
              rep: FieldMetaWithNotType[NotRequireImplicit],
              data: List[(String, CellDataAbs)]
            ): List[(String, CellDataAbs)] = {
              if (rep.ignoreMeta) data
              else {
                rep.literaMeta
                  .map(meta => ((rep.fieldNameMeta.getOrElse(name), meta.toCellDataAbs.addTransform(rep.cellStyleMeta))) :: data)
                  .getOrElse(throw new Exception("没有被忽略的不需要隐式 CellWriter 的列必须含有字面量"))

              }
            }
          }
      }

    implicit def typedApplication[H, I]
      : Application3[RowWriterContent, PropertyTag1[FieldMetaWithType[NotRequireImplicit, H, I], H], H, String, FieldMetaWithType[NotRequireImplicit, H, I]] =
      new Application3[RowWriterContent, PropertyTag1[FieldMetaWithType[NotRequireImplicit, H, I], H], H, String, FieldMetaWithType[NotRequireImplicit, H, I]] {
        override def application(context: Context3[RowWriterContent]): RowWriterContent[H, String, FieldMetaWithType[NotRequireImplicit, H, I]] =
          new RowWriterContent[H, String, FieldMetaWithType[NotRequireImplicit, H, I]] {
            override def appendColumnTitle(name: String, rep: FieldMetaWithType[NotRequireImplicit, H, I], titles: List[String]): List[String] = {
              if (rep.ignoreMeta) titles else rep.fieldNameMeta.getOrElse(name) :: titles
            }
            override def appendField(
              tt: H,
              name: String,
              rep: FieldMetaWithType[NotRequireImplicit, H, I],
              data: List[(String, CellDataAbs)]
            ): List[(String, CellDataAbs)] = {
              if (rep.ignoreMeta) data
              else {
                rep.writerMeta
                  .map(i => ((rep.fieldNameMeta.getOrElse(name), CPoi.wrapData(rep.mapperMeta(tt))(i).addTransform(rep.cellStyleMeta))) :: data)
                  .getOrElse(throw new Exception("没有被忽略的不需要 CellWriter 的列必须提供内部 CellWriter"))
              }
            }
          }
      }
  }

  trait LiteraMetaContent {
    type DataType
    def litera: DataType
    def cellWriter: CellWriter[DataType]
    def toCellData: CellDataImpl[DataType] = CellDataImpl(litera)(cellWriter)
    def toCellDataAbs: CellDataAbs         = toCellData
  }
  object LiteraMetaContent {
    def apply[T](litera1: T)(implicit cellWriter1: CellWriter[T]): LiteraMetaContent = new LiteraMetaContent {
      override type DataType = T
      override def litera: T                 = litera1
      override def cellWriter: CellWriter[T] = cellWriter1
    }
  }

  trait FieldMetaWithNotType[Poly <: ImplicitRequireOrNot] {
    self =>
    def fieldNameMeta: Option[String]
    def ignoreMeta: Boolean
    def cellStyleMeta: List[StyleTransform]
    def literaMeta: Option[LiteraMetaContent]

    def fieldName(name: String): FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String]         = Option(name)
      override def ignoreMeta: Boolean                   = self.ignoreMeta
      override def cellStyleMeta: List[StyleTransform]   = self.cellStyleMeta
      override def literaMeta: Option[LiteraMetaContent] = self.literaMeta
    }
    def ignore: FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = true
      override def cellStyleMeta: List[StyleTransform]   = self.cellStyleMeta
      override def literaMeta: Option[LiteraMetaContent] = self.literaMeta
    }
    def notIgnore: FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = false
      override def cellStyleMeta: List[StyleTransform]   = self.cellStyleMeta
      override def literaMeta: Option[LiteraMetaContent] = self.literaMeta
    }
    def cellStyle(style: StyleTransform*): FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = self.ignoreMeta
      override def cellStyleMeta: List[StyleTransform]   = style.to(List) ::: self.cellStyleMeta
      override def literaMeta: Option[LiteraMetaContent] = self.literaMeta
    }
    def litera[T](litera1: T)(implicit cellWriter1: CellWriter[T]): FieldMetaWithNotType[NotRequireImplicit] = new FieldMetaWithNotType[NotRequireImplicit] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = self.ignoreMeta
      override def cellStyleMeta: List[StyleTransform]   = self.cellStyleMeta
      override def literaMeta: Option[LiteraMetaContent] = Option(LiteraMetaContent(litera1)(cellWriter1))
    }
    def map[DataType, ResultType](i: DataType => ResultType): FieldMetaWithType[RequireImplicit, DataType, ResultType] =
      new FieldMetaWithType[RequireImplicit, DataType, ResultType] {
        override def fieldNameMeta: Option[String]              = self.fieldNameMeta
        override def ignoreMeta: Boolean                        = self.ignoreMeta
        override def cellStyleMeta: List[StyleTransform]        = self.cellStyleMeta
        override def mapperMeta(data: DataType): ResultType     = i(data)
        override def writerMeta: Option[CellWriter[ResultType]] = Option.empty
      }
    def fillWriter[ResultType](implicit i: CellWriter[ResultType]): FieldMetaWithType[NotRequireImplicit, ResultType, ResultType] =
      new FieldMetaWithType[NotRequireImplicit, ResultType, ResultType] {
        override def fieldNameMeta: Option[String]              = self.fieldNameMeta
        override def ignoreMeta: Boolean                        = self.ignoreMeta
        override def cellStyleMeta: List[StyleTransform]        = self.cellStyleMeta
        override def mapperMeta(data: ResultType): ResultType   = data
        override def writerMeta: Option[CellWriter[ResultType]] = Option(i)
      }
  }

  trait FieldMetaWithType[Poly <: ImplicitRequireOrNot, DataType, ResultType] {
    self =>
    def fieldNameMeta: Option[String]
    def ignoreMeta: Boolean
    def cellStyleMeta: List[StyleTransform]
    def mapperMeta(data: DataType): ResultType
    def writerMeta: Option[CellWriter[ResultType]]

    def fieldName(name: String): FieldMetaWithType[Poly, DataType, ResultType] = new FieldMetaWithType[Poly, DataType, ResultType] {
      override def fieldNameMeta: Option[String]              = Option(name)
      override def ignoreMeta: Boolean                        = self.ignoreMeta
      override def cellStyleMeta: List[StyleTransform]        = self.cellStyleMeta
      override def mapperMeta(data: DataType): ResultType     = self.mapperMeta(data)
      override def writerMeta: Option[CellWriter[ResultType]] = self.writerMeta
    }
    def ignore: FieldMetaWithType[Poly, DataType, ResultType] = new FieldMetaWithType[Poly, DataType, ResultType] {
      override def fieldNameMeta: Option[String]              = self.fieldNameMeta
      override def ignoreMeta: Boolean                        = true
      override def cellStyleMeta: List[StyleTransform]        = self.cellStyleMeta
      override def mapperMeta(data: DataType): ResultType     = self.mapperMeta(data)
      override def writerMeta: Option[CellWriter[ResultType]] = self.writerMeta
    }
    def notIgnore: FieldMetaWithType[Poly, DataType, ResultType] = new FieldMetaWithType[Poly, DataType, ResultType] {
      override def fieldNameMeta: Option[String]              = self.fieldNameMeta
      override def ignoreMeta: Boolean                        = false
      override def cellStyleMeta: List[StyleTransform]        = self.cellStyleMeta
      override def mapperMeta(data: DataType): ResultType     = self.mapperMeta(data)
      override def writerMeta: Option[CellWriter[ResultType]] = self.writerMeta
    }
    def cellStyle(style: StyleTransform*): FieldMetaWithType[Poly, DataType, ResultType] = new FieldMetaWithType[Poly, DataType, ResultType] {
      override def fieldNameMeta: Option[String]              = self.fieldNameMeta
      override def ignoreMeta: Boolean                        = self.ignoreMeta
      override def cellStyleMeta: List[StyleTransform]        = style.to(List) ::: self.cellStyleMeta
      override def mapperMeta(data: DataType): ResultType     = self.mapperMeta(data)
      override def writerMeta: Option[CellWriter[ResultType]] = self.writerMeta
    }
    def map[T](i: ResultType => T): FieldMetaWithType[RequireImplicit, DataType, T] = new FieldMetaWithType[RequireImplicit, DataType, T] {
      override def fieldNameMeta: Option[String]       = self.fieldNameMeta
      override def ignoreMeta: Boolean                 = self.ignoreMeta
      override def cellStyleMeta: List[StyleTransform] = self.cellStyleMeta
      override def mapperMeta(data: DataType): T       = i(self.mapperMeta(data))
      override def writerMeta: Option[CellWriter[T]]   = Option.empty
    }
    def fillWriter(implicit i: CellWriter[ResultType]): FieldMetaWithType[NotRequireImplicit, DataType, ResultType] =
      new FieldMetaWithType[NotRequireImplicit, DataType, ResultType] {
        override def fieldNameMeta: Option[String]              = self.fieldNameMeta
        override def ignoreMeta: Boolean                        = self.ignoreMeta
        override def cellStyleMeta: List[StyleTransform]        = self.cellStyleMeta
        override def mapperMeta(data: DataType): ResultType     = self.mapperMeta(data)
        override def writerMeta: Option[CellWriter[ResultType]] = Option(i)
      }
    def litera[T](litera1: T)(implicit cellWriter1: CellWriter[T]): FieldMetaWithNotType[NotRequireImplicit] = new FieldMetaWithNotType[NotRequireImplicit] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = self.ignoreMeta
      override def cellStyleMeta: List[StyleTransform]   = self.cellStyleMeta
      override def literaMeta: Option[LiteraMetaContent] = Option(LiteraMetaContent(litera1)(cellWriter1))
    }
  }

}
