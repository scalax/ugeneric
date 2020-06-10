package org.scalax.ugeneric.circe.encoder

import asuna.macros.ByNameImplicit
import asuna.{Application3, Context3, PropertyTag1}
import io.circe.{Encoder, Json}

object EncoderFieldMeta extends EncoderMeta.FieldMetaWithNotType[EncoderMeta.RequireImplicit] {
  override def fieldNameMeta: Option[String]                     = Option.empty
  override def ignoreMeta: Boolean                               = false
  override def literaMeta: Option[EncoderMeta.LiteraMetaContent] = Option.empty
}

object EncoderMeta {
  self =>

  sealed trait ImplicitRequireOrNot
  class RequireImplicit extends ImplicitRequireOrNot
  object RequireImplicit {
    implicit def untypedApplication[H](
      implicit encoder: ByNameImplicit[Encoder[H]]
    ): Application3[JsonObjectContent, PropertyTag1[FieldMetaWithNotType[RequireImplicit], H], H, String, FieldMetaWithNotType[RequireImplicit]] =
      new Application3[JsonObjectContent, PropertyTag1[FieldMetaWithNotType[RequireImplicit], H], H, String, FieldMetaWithNotType[RequireImplicit]] {
        override def application(context: Context3[JsonObjectContent]): JsonObjectContent[H, String, FieldMetaWithNotType[RequireImplicit]] =
          new JsonObjectContent[H, String, FieldMetaWithNotType[RequireImplicit]] {
            override def appendField(name: String, rep: FieldMetaWithNotType[RequireImplicit]): JsonObjectAppender[H] = {
              if (rep.literaMeta.isDefined) {
                throw new Exception("需要隐式 Encoder 的列不能有字面量类型")
              }
              if (rep.ignoreMeta) {
                new JsonObjectAppender[H] {
                  override def appendField(data: H, m: List[(String, Json)]): List[(String, Json)] = m
                }
              } else {
                val fieldName = rep.fieldNameMeta.getOrElse(name)
                new JsonObjectAppender[H] {
                  override def appendField(data: H, m: List[(String, Json)]): List[(String, Json)] = {
                    (fieldName, encoder.value(data)) :: m
                  }
                }
              }
            }
          }
      }
  }

  implicit def typedApplication[H, I](
    implicit encoder: ByNameImplicit[Encoder[I]]
  ): Application3[JsonObjectContent, PropertyTag1[FieldMetaWithType[RequireImplicit, H, I], H], H, String, FieldMetaWithType[RequireImplicit, H, I]] =
    new Application3[JsonObjectContent, PropertyTag1[FieldMetaWithType[RequireImplicit, H, I], H], H, String, FieldMetaWithType[RequireImplicit, H, I]] {
      override def application(context: Context3[JsonObjectContent]): JsonObjectContent[H, String, FieldMetaWithType[RequireImplicit, H, I]] =
        new JsonObjectContent[H, String, FieldMetaWithType[RequireImplicit, H, I]] {
          override def appendField(name: String, rep: FieldMetaWithType[RequireImplicit, H, I]): JsonObjectAppender[H] = {
            if (rep.encoderMeta.isDefined) {
              throw new Exception("需要隐式 Encoder 的列不能定义 Encoder")
            }
            if (rep.ignoreMeta) {
              new JsonObjectAppender[H] {
                override def appendField(data: H, m: List[(String, Json)]): List[(String, Json)] = m
              }
            } else {
              val fieldName = rep.fieldNameMeta.getOrElse(name)
              new JsonObjectAppender[H] {
                override def appendField(data: H, m: List[(String, Json)]): List[(String, Json)] = (fieldName, encoder.value(rep.mapperMeta(data))) :: m
              }
            }
          }
        }
    }

  class NotRequireImplicit extends ImplicitRequireOrNot

  object NotRequireImplicit {
    implicit def untypedApplication[H]
      : Application3[JsonObjectContent, PropertyTag1[FieldMetaWithNotType[NotRequireImplicit], H], H, String, FieldMetaWithNotType[NotRequireImplicit]] =
      new Application3[JsonObjectContent, PropertyTag1[FieldMetaWithNotType[NotRequireImplicit], H], H, String, FieldMetaWithNotType[NotRequireImplicit]] {
        override def application(context: Context3[JsonObjectContent]): JsonObjectContent[H, String, FieldMetaWithNotType[NotRequireImplicit]] =
          new JsonObjectContent[H, String, FieldMetaWithNotType[NotRequireImplicit]] {
            override def appendField(name: String, rep: FieldMetaWithNotType[NotRequireImplicit]): JsonObjectAppender[H] = {
              if (rep.ignoreMeta) {
                new JsonObjectAppender[H] {
                  override def appendField(data: H, m: List[(String, Json)]): List[(String, Json)] = m
                }
              } else {
                val fieldName = rep.fieldNameMeta.getOrElse(name)
                rep.literaMeta
                  .map(meta =>
                    new JsonObjectAppender[H] {
                      override def appendField(data: H, m: List[(String, Json)]): List[(String, Json)] = (fieldName, meta.encoder(meta.litera)) :: m
                    }
                  )
                  .getOrElse(throw new Exception("没有被忽略的不需要隐式 Encoder 的列必须含有字面量"))
              }
            }
          }
      }

    implicit def typedApplication[H, I]
      : Application3[JsonObjectContent, PropertyTag1[FieldMetaWithType[NotRequireImplicit, H, I], H], H, String, FieldMetaWithType[NotRequireImplicit, H, I]] =
      new Application3[JsonObjectContent, PropertyTag1[FieldMetaWithType[NotRequireImplicit, H, I], H], H, String, FieldMetaWithType[NotRequireImplicit, H, I]] {
        override def application(context: Context3[JsonObjectContent]): JsonObjectContent[H, String, FieldMetaWithType[NotRequireImplicit, H, I]] =
          new JsonObjectContent[H, String, FieldMetaWithType[NotRequireImplicit, H, I]] {
            override def appendField(name: String, rep: FieldMetaWithType[NotRequireImplicit, H, I]): JsonObjectAppender[H] = {
              if (rep.ignoreMeta) {
                new JsonObjectAppender[H] {
                  override def appendField(data: H, m: List[(String, Json)]): List[(String, Json)] = m
                }
              } else {
                val fieldName = rep.fieldNameMeta.getOrElse(name)
                rep.encoderMeta
                  .map(i =>
                    new JsonObjectAppender[H] {
                      override def appendField(data: H, m: List[(String, Json)]): List[(String, Json)] = {
                        (fieldName, i(rep.mapperMeta(data))) :: m
                      }
                    }
                  )
                  .getOrElse(throw new Exception("没有被忽略的不需要 Encoder 的列必须提供内部 Encoder"))
              }
            }
          }
      }
  }

  trait LiteraMetaContent {
    type DataType
    def litera: DataType
    def encoder: Encoder[DataType]
  }
  object LiteraMetaContent {
    def apply[T](litera1: T)(implicit encoder1: Encoder[T]): LiteraMetaContent = new LiteraMetaContent {
      override type DataType = T
      override def litera: T           = litera1
      override def encoder: Encoder[T] = encoder1
    }
  }

  trait FieldMetaWithNotType[Poly <: ImplicitRequireOrNot] {
    self =>
    def fieldNameMeta: Option[String]
    def ignoreMeta: Boolean
    def literaMeta: Option[LiteraMetaContent]

    def fieldName(name: String): FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String]         = Option(name)
      override def ignoreMeta: Boolean                   = self.ignoreMeta
      override def literaMeta: Option[LiteraMetaContent] = self.literaMeta
    }
    def ignore: FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = true
      override def literaMeta: Option[LiteraMetaContent] = self.literaMeta
    }
    def notIgnore: FieldMetaWithNotType[Poly] = new FieldMetaWithNotType[Poly] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = false
      override def literaMeta: Option[LiteraMetaContent] = self.literaMeta
    }
    def litera[T](litera1: T)(implicit encoder1: Encoder[T]): FieldMetaWithNotType[NotRequireImplicit] = new FieldMetaWithNotType[NotRequireImplicit] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = self.ignoreMeta
      override def literaMeta: Option[LiteraMetaContent] = Option(LiteraMetaContent(litera1)(encoder1))
    }
    def map[DataType, ResultType](i: DataType => ResultType): FieldMetaWithType[RequireImplicit, DataType, ResultType] =
      new FieldMetaWithType[RequireImplicit, DataType, ResultType] {
        override def fieldNameMeta: Option[String]            = self.fieldNameMeta
        override def ignoreMeta: Boolean                      = self.ignoreMeta
        override def mapperMeta(data: DataType): ResultType   = i(data)
        override def encoderMeta: Option[Encoder[ResultType]] = Option.empty
      }
    def fillWriter[ResultType](implicit i: Encoder[ResultType]): FieldMetaWithType[NotRequireImplicit, ResultType, ResultType] =
      new FieldMetaWithType[NotRequireImplicit, ResultType, ResultType] {
        override def fieldNameMeta: Option[String]            = self.fieldNameMeta
        override def ignoreMeta: Boolean                      = self.ignoreMeta
        override def mapperMeta(data: ResultType): ResultType = data
        override def encoderMeta: Option[Encoder[ResultType]] = Option(i)
      }
  }

  trait FieldMetaWithType[Poly <: ImplicitRequireOrNot, DataType, ResultType] {
    self =>
    def fieldNameMeta: Option[String]
    def ignoreMeta: Boolean
    def mapperMeta(data: DataType): ResultType
    def encoderMeta: Option[Encoder[ResultType]]

    def fieldName(name: String): FieldMetaWithType[Poly, DataType, ResultType] = new FieldMetaWithType[Poly, DataType, ResultType] {
      override def fieldNameMeta: Option[String]            = Option(name)
      override def ignoreMeta: Boolean                      = self.ignoreMeta
      override def mapperMeta(data: DataType): ResultType   = self.mapperMeta(data)
      override def encoderMeta: Option[Encoder[ResultType]] = self.encoderMeta
    }
    def ignore: FieldMetaWithType[Poly, DataType, ResultType] = new FieldMetaWithType[Poly, DataType, ResultType] {
      override def fieldNameMeta: Option[String]            = self.fieldNameMeta
      override def ignoreMeta: Boolean                      = true
      override def mapperMeta(data: DataType): ResultType   = self.mapperMeta(data)
      override def encoderMeta: Option[Encoder[ResultType]] = self.encoderMeta
    }
    def notIgnore: FieldMetaWithType[Poly, DataType, ResultType] = new FieldMetaWithType[Poly, DataType, ResultType] {
      override def fieldNameMeta: Option[String]            = self.fieldNameMeta
      override def ignoreMeta: Boolean                      = false
      override def mapperMeta(data: DataType): ResultType   = self.mapperMeta(data)
      override def encoderMeta: Option[Encoder[ResultType]] = self.encoderMeta
    }
    def map[T](i: ResultType => T): FieldMetaWithType[RequireImplicit, DataType, T] = new FieldMetaWithType[RequireImplicit, DataType, T] {
      override def fieldNameMeta: Option[String]   = self.fieldNameMeta
      override def ignoreMeta: Boolean             = self.ignoreMeta
      override def mapperMeta(data: DataType): T   = i(self.mapperMeta(data))
      override def encoderMeta: Option[Encoder[T]] = Option.empty
    }
    def fillWriter(implicit i: Encoder[ResultType]): FieldMetaWithType[NotRequireImplicit, DataType, ResultType] =
      new FieldMetaWithType[NotRequireImplicit, DataType, ResultType] {
        override def fieldNameMeta: Option[String]            = self.fieldNameMeta
        override def ignoreMeta: Boolean                      = self.ignoreMeta
        override def mapperMeta(data: DataType): ResultType   = self.mapperMeta(data)
        override def encoderMeta: Option[Encoder[ResultType]] = Option(i)
      }
    def litera[T](litera1: T)(implicit cellWriter1: Encoder[T]): FieldMetaWithNotType[NotRequireImplicit] = new FieldMetaWithNotType[NotRequireImplicit] {
      override def fieldNameMeta: Option[String]         = self.fieldNameMeta
      override def ignoreMeta: Boolean                   = self.ignoreMeta
      override def literaMeta: Option[LiteraMetaContent] = Option(LiteraMetaContent(litera1)(cellWriter1))
    }
  }

}
