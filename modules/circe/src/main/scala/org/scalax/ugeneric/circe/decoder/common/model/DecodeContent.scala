package org.scalax.ugeneric.circe.decoder.common.model

import zsg.macros.ByNameImplicit
import zsg.PropertyTag
import io.circe._
import zsg.macros.single.{ColumnName, GenericColumnName, StringName}

trait DecodeContent[N, Name, Model] extends Any {
  def getDecoder: Decoder[Model]
}

object DecodeContent {

  implicit def ugenericDecoder[Model, N <: StringName](
    implicit dd: ByNameImplicit[Decoder[Model]],
    genName: GenericColumnName[N]
  ): DecodeContent[PropertyTag[Model], ColumnName[N], Model] = new DecodeContent[PropertyTag[Model], ColumnName[N], Model] {
    override def getDecoder: Decoder[Model] = Decoder.instance(_.get(genName.value)(dd.value))
  }

}
