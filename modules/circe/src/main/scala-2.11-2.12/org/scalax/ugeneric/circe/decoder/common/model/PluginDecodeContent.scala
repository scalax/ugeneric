package org.scalax.ugeneric.circe.decoder.common.model

import asuna.macros.single.DefaultValue
import asuna.macros.ByNameImplicit
import asuna.{Application4, Context4, PropertyTag}
import io.circe._
import org.scalax.ugeneric.circe.NameTranslator

trait PluginDecodeContent[N, Model, Name, DefaultValue] extends Any {
  def getDecoder(name: Name, defaultValue: DefaultValue, p: Option[NameTranslator], useDefaultValue: Boolean): Decoder[Model]
}

object PluginDecodeContent {

  implicit def asunaPlaceHolderDecoder[T](implicit dd: ByNameImplicit[Decoder[T]]): Application4[PluginDecodeContent, PropertyTag[T], T, String, DefaultValue[T]] = {
    new Application4[PluginDecodeContent, PropertyTag[T], T, String, DefaultValue[T]] {
      override def application(context: Context4[PluginDecodeContent]): PluginDecodeContent[PropertyTag[T], T, String, DefaultValue[T]] = {
        new PluginDecodeContent[PropertyTag[T], T, String, DefaultValue[T]] {
          override def getDecoder(name: String, defaultValue: DefaultValue[T], p: Option[NameTranslator], useDefault: Boolean): Decoder[T] = {
            val nameI = p.map(_.tran(name)).getOrElse(name)

            Decoder.instance { j =>
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
      }
    }
  }

}
