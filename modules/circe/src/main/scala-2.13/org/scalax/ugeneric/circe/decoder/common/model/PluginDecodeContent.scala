package org.scalax.ugeneric.circe.decoder.common.model

import zsg.macros.single.DefaultValue
import zsg.PropertyTag
import io.circe._
import org.scalax.ugeneric.circe.NameTranslator

trait PluginDecodeContent[N, Model, Name, DefaultValue] extends Any {
  def getDecoder(name: Name, defaultValue: DefaultValue, p: Option[NameTranslator], useDefaultValue: Boolean): Decoder[Model]
}

object PluginDecodeContent {

  implicit def asunaPlaceHolderDecoder[T](implicit dd: => Decoder[T]): PluginDecodeContent[PropertyTag[T], T, String, DefaultValue[T]] = {
    (name, defaultValue, p, useDefaultValue) =>
      val nameI = p.map(_.tran(name)).getOrElse(name)

      { j =>
        val fieldValue = j.downField(nameI)
        val value      = fieldValue.as(dd)

        if (value.isRight) {
          value
        } else {
          //if this field is not exists or this field is null
          if (fieldValue.focus.map(_.isNull).getOrElse(true) && useDefaultValue) {
            defaultValue.value.map(Right.apply).getOrElse(value)
          } else {
            value
          }
        }
      }
  }

}
