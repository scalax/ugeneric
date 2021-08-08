package ugeneric.circe.decoder

import zsg.macros.single.{ColumnName, DefaultValue}
import zsg.macros.ByNameImplicit
import zsg.{Application, PropertyTag, TagMerge2, TypeFunction, TypeHList, TypeHList2}
import io.circe._
import ugeneric.circe.NameTranslator
import zsg.macros.utils.GenericColumnName

class PluginDecoderTypeContext extends TypeFunction {
  override type H[I <: TypeHList] = PluginDecodeContent[I#Head, I#Tail#Head]
}
object PluginDecodeTypeContext {
  implicit def zsgDecoderContent[T, N <: String](implicit
    dd: ByNameImplicit[Decoder[T]],
    genName: GenericColumnName[N]
  ): Application[PluginDecoderTypeContext, PluginDecodeContext, TagMerge2[PropertyTag[T], ColumnName[N]], TypeHList2[T, DefaultValue[T]]] =
    new Application[PluginDecoderTypeContext, PluginDecodeContext, TagMerge2[PropertyTag[T], ColumnName[N]], TypeHList2[T, DefaultValue[T]]] {
      override def application(context: PluginDecodeContext): PluginDecodeContent[T, DefaultValue[T]] =
        new PluginDecodeContent[T, DefaultValue[T]] {
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

trait PluginDecodeContent[Model, DefaultValue] extends Any {
  def getDecoder(defaultValue: DefaultValue, p: Option[NameTranslator], useDefaultValue: Boolean): Decoder[Model]
}
