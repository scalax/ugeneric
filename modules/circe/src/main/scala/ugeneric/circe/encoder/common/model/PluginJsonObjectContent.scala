package org.scalax.ugeneric.circe.encoder.common.model

import zsg.PropertyTag
import io.circe.{Encoder, Json}
import org.scalax.ugeneric.circe.NameTranslator
import zsg.macros.ByNameImplicit
import zsg.macros.single.{ColumnName, GenericColumnName, StringName}

abstract class PluginJsonObjectContent[I, Name, T] {
  def getAppender(data: T, l: List[(String, Json)], p: Option[NameTranslator]): List[(String, Json)]
}

object PluginJsonObjectContent {

  implicit final def zsgCirceImplicit[T, N <: StringName](
    implicit t: ByNameImplicit[Encoder[T]],
    g: GenericColumnName[N]
  ): PluginJsonObjectContent[PropertyTag[T], ColumnName[N], T] = new PluginJsonObjectContent[PropertyTag[T], ColumnName[N], T] {
    override def getAppender(data: T, l: List[(String, Json)], p: Option[NameTranslator]): List[(String, Json)] = {
      val nameI = p.map(_.tran(g.value)).getOrElse(g.value)
      (nameI, t.value(data)) :: l
    }
  }

}
