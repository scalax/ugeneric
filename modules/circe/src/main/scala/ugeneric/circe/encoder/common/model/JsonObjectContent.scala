package ugeneric.circe.encoder

import java.util
import zsg.{Application, PropertyTag, TagMerge2, TypeFunction, TypeHList, TypeHList1}
import io.circe.{Encoder, Json}
import zsg.macros.ByNameImplicit
import zsg.macros.single.ColumnName
import zsg.macros.utils.GenericColumnName

class JsonObjectTypeContext extends TypeFunction {
  override type H[T <: TypeHList] = JsonObjectContent[T#Head]
}
object JsonObjectTypeContext {
  implicit final def zsgCirceImplicit[T, N <: String](implicit
    t: ByNameImplicit[Encoder[T]],
    nameImplicit: GenericColumnName[N]
  ): Application[JsonObjectTypeContext, JsonObjectContext, TagMerge2[PropertyTag[T], ColumnName[N]], TypeHList1[T]] =
    new Application[JsonObjectTypeContext, JsonObjectContext, TagMerge2[PropertyTag[T], ColumnName[N]], TypeHList1[T]] {
      override def application(context: JsonObjectContext): JsonObjectContent[T] = new JsonObjectContent[T] {
        override def getAppender(data: T, l: util.LinkedHashMap[String, Json]): Unit = l.put(nameImplicit.value, t.value(data))
      }
    }
}

abstract class JsonObjectContent[T] {
  def getAppender(data: T, l: util.LinkedHashMap[String, Json]): Unit
}
