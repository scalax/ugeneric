package ugeneric.circe.decoder

import zsg.macros.single.{ColumnName, DefaultValue, GenericColumnName, StringName}
import zsg.macros.ByNameImplicit
import zsg.PropertyTag
import io.circe._
import ugeneric.circe.NameTranslator

trait PluginDecodeContent[N, Name, Model, DefaultValue] extends Any {
  def getDecoder(defaultValue: DefaultValue, p: Option[NameTranslator], useDefaultValue: Boolean): Decoder[Model]
}

object PluginDecodeContent {

  implicit def zsgDecoderContent[T, N <: StringName](implicit
    dd: ByNameImplicit[Decoder[T]],
    genName: GenericColumnName[N]
  ): PluginDecodeContent[PropertyTag[T], ColumnName[N], T, DefaultValue[T]] = {
    new PluginDecodeContent[PropertyTag[T], ColumnName[N], T, DefaultValue[T]] {
      override def getDecoder(defaultValue: DefaultValue[T], p: Option[NameTranslator], useDefault: Boolean): Decoder[T] = {
        val nameI = p.map(_.tran(genName.value)).getOrElse(genName.value)

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
