package org.scalax.kirito.circe.decoder.common.model

import asuna.macros.single.DefaultValue
import asuna.macros.ByNameImplicit
import asuna.{Application3, PropertyTag0}
import io.circe._
import org.scalax.kirito.circe.NameTranslator

trait PluginDecodeContent[Model, Name, DefaultValue] extends Any {
  def getValue(name: Name, defaultValue: DefaultValue, p: Option[NameTranslator], useDefaultValue: Boolean): Decoder[Model]
}

object PluginDecodeContent {

  implicit def asunaPlaceHolderDecoder[T](implicit dd: ByNameImplicit[Decoder[T]]): Application3[PluginDecodeContent, PropertyTag0[T], T, String, DefaultValue[T]] = {
    _ => (name, defaultValue, p, useDefault) =>
      val nameI = p.map(_.tran(name)).getOrElse(name)

      { j =>
        val fieldValue = j.downField(nameI)
        val value      = fieldValue.as(dd.value)

        if (value.isRight) {
          value
        } else {
          //if this field is not exists or this field is null
          if (fieldValue.focus.map(_.isNull).getOrElse(true) && useDefault) {
            defaultValue.value.map(Right.apply).getOrElse(value)
          } else {
            value
          }
        }
      }
  }

}
