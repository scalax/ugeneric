package org.scalax.ugeneric.circe.encoder.common.model

import zsg.PropertyTag
import io.circe.{Encoder, Json}
import zsg.macros.ByNameImplicit
import zsg.macros.single.{ColumnName, GenericColumnName, StringName}

abstract class JsonObjectContent[I, Name, T] {
  def getAppender(data: T, l: List[(String, Json)]): List[(String, Json)]
}

object JsonObjectContent {

  @inline implicit final def asunaCirceImplicit[T, N <: StringName](
    implicit t: ByNameImplicit[Encoder[T]],
    nameImplicit: GenericColumnName[N]
  ): JsonObjectContent[PropertyTag[T], ColumnName[N], T] = new JsonObjectContent[PropertyTag[T], ColumnName[N], T] {
    override def getAppender(data: T, l: List[(String, Json)]): List[(String, Json)] = (nameImplicit.value, t.value(data)) :: l
  }

}
