package ugeneric.circe.encoder

import java.util

import zsg.PropertyTag
import io.circe.{Encoder, Json}
import zsg.macros.ByNameImplicit
import zsg.macros.single.{ColumnName, GenericColumnName}

abstract class JsonObjectContent[I, Name, T] {
  def getAppender(data: T, l: util.LinkedHashMap[String, Json]): Unit
}

object JsonObjectContent {

  @inline implicit final def zsgCirceImplicit[T, N](implicit
    t: ByNameImplicit[Encoder[T]],
    nameImplicit: GenericColumnName[N]
  ): JsonObjectContent[PropertyTag[T], ColumnName[N], T] = new JsonObjectContent[PropertyTag[T], ColumnName[N], T] {
    override def getAppender(data: T, l: util.LinkedHashMap[String, Json]): Unit = l.put(nameImplicit.value, t.value(data))
  }

}
