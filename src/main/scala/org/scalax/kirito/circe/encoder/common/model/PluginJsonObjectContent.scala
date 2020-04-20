package org.scalax.kirito.circe.encoder.common.model

import asuna.{Application2, PropertyTag0}
import asuna.macros.ByNameImplicit
import io.circe.Encoder
import org.scalax.kirito.circe.NameTranslator

abstract class PluginJsonObjectContent[Model, Name] {
  def appendField(name: Name, p: Option[NameTranslator]): JsonObjectAppender[Model]
}

object PluginJsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: ByNameImplicit[Encoder[T]]): Application2[PluginJsonObjectContent, PropertyTag0[T], T, String] = {
    _ => (name, p) =>
      val nameI = p.map(_.tran(name)).getOrElse(name)
      data => m => (nameI, t.value(data)) :: m
  }

}
