package org.scalax.ugeneric.circe.decoder

import asuna.macros.single.DefaultValue
import asuna.macros.ByNameImplicit
import asuna.macros.utils.PlaceHolder
import asuna.{Application4, Context4, PropertyTag1}
import io.circe._

trait PluginDecodeContent[T, II, D, Rep] extends Any {
  def getValue(name: II, defaultValue: D, rep: Rep): Decoder[T]
}

object PluginDecodeContent {

  implicit def asunaPlaceHolderDecoder[T](
    implicit dd: ByNameImplicit[Decoder[T]]
  ): Application4[PluginDecodeContent, PropertyTag1[PlaceHolder, T], T, String, DefaultValue[T], PlaceHolder] =
    new Application4[PluginDecodeContent, PropertyTag1[PlaceHolder, T], T, String, DefaultValue[T], PlaceHolder] {
      override def application(context: Context4[PluginDecodeContent]): PluginDecodeContent[T, String, DefaultValue[T], PlaceHolder] =
        new PluginDecodeContent[T, String, DefaultValue[T], PlaceHolder] {
          override def getValue(name: String, defaultValue: DefaultValue[T], rep: PlaceHolder): Decoder[T] = Decoder.instance { j => j.get(name)(dd.value) }
        }
    }

  implicit def asunaCirceDecoder[T]: Application4[PluginDecodeContent, PropertyTag1[Decoder[T], T], T, String, DefaultValue[T], Decoder[T]] =
    new Application4[PluginDecodeContent, PropertyTag1[Decoder[T], T], T, String, DefaultValue[T], Decoder[T]] {
      override def application(context: Context4[PluginDecodeContent]): PluginDecodeContent[T, String, DefaultValue[T], Decoder[T]] =
        new PluginDecodeContent[T, String, DefaultValue[T], Decoder[T]] {
          override def getValue(name: String, defaultValue: DefaultValue[T], rep: Decoder[T]): Decoder[T] = Decoder.instance { j => j.get(name)(rep) }
        }
    }

}
