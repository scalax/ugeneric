package org.scalax.ugeneric.circe.decoder

import asuna.{Application4, Context4, PropertyTag1}
import asuna.macros.ByNameImplicit
import asuna.macros.single.DefaultValue
import io.circe.Decoder

object PluginDecoderHelper {
  def key(key: String): ProName                                                          = new ProName(key, false)
  def decoder[T](implicit decoder: Decoder[T]): ProDecoder[T]                            = new ProDecoder(decoder, false)
  def keyWithDecoder[T](key: String)(implicit decoder: Decoder[T]): ProDecoderWithKey[T] = new ProDecoderWithKey(key = key, decoder = decoder, isUseDefault = false)

  class ProName(val key: String, val isUseDefault: Boolean) {
    def useDefault: ProName    = new ProName(key, true)
    def notUseDefault: ProName = new ProName(key, false)
  }

  object ProName {
    implicit def asunaCirceDecoder[T](
      implicit dd: ByNameImplicit[Decoder[T]]
    ): Application4[PluginDecodeContent, PropertyTag1[ProName, T], T, String, DefaultValue[T], ProName] =
      new Application4[PluginDecodeContent, PropertyTag1[ProName, T], T, String, DefaultValue[T], ProName] {
        override def application(context: Context4[PluginDecodeContent]): PluginDecodeContent[T, String, DefaultValue[T], ProName] =
          new PluginDecodeContent[T, String, DefaultValue[T], ProName] {
            override def getValue(name: String, defaultValue: DefaultValue[T], rep: ProName): Decoder[T] = {
              Decoder.instance { j =>
                if (rep.isUseDefault) {
                  j.get(rep.key)(dd.value) match {
                    case Left(s) =>
                      val default = defaultValue.value
                      default match {
                        case Some(r) =>
                          Right(r)
                        case _ =>
                          Left(s)
                      }
                    case value @ Right(_) =>
                      value
                  }
                } else {
                  j.get(rep.key)(dd.value)
                }
              }
            }
          }
      }
  }

  class ProDecoder[T](val decoder: Decoder[T], val isUseDefault: Boolean) {
    def useDefault: ProDecoder[T] = new ProDecoder(decoder, true)

    def notUseDefault: ProDecoder[T] = new ProDecoder(decoder, false)
  }

  object ProDecoder {
    implicit def asunaCirceDecoder[T]: Application4[PluginDecodeContent, PropertyTag1[ProDecoder[T], T], T, String, DefaultValue[T], ProDecoder[T]] =
      new Application4[PluginDecodeContent, PropertyTag1[ProDecoder[T], T], T, String, DefaultValue[T], ProDecoder[T]] {
        override def application(context: Context4[PluginDecodeContent]): PluginDecodeContent[T, String, DefaultValue[T], ProDecoder[T]] =
          new PluginDecodeContent[T, String, DefaultValue[T], ProDecoder[T]] {
            override def getValue(name: String, defaultValue: DefaultValue[T], rep: ProDecoder[T]): Decoder[T] = {
              Decoder.instance { j =>
                if (rep.isUseDefault) {
                  j.get(name)(rep.decoder) match {
                    case Left(s) =>
                      val default = defaultValue.value
                      default match {
                        case Some(r) =>
                          Right(r)
                        case _ =>
                          Left(s)
                      }
                    case value @ Right(_) =>
                      value
                  }
                } else {
                  j.get(name)(rep.decoder)
                }
              }
            }
          }
      }
  }

  class ProDecoderWithKey[T](val key: String, val decoder: Decoder[T], val isUseDefault: Boolean) {
    def changeName(newKey: String): ProDecoderWithKey[T] = new ProDecoderWithKey(key = newKey, decoder = decoder, isUseDefault)

    def useDefault: ProDecoderWithKey[T] = new ProDecoderWithKey[T](key = key, decoder = decoder, isUseDefault = true)

    def notUseDefault: ProDecoderWithKey[T] = new ProDecoderWithKey[T](key = key, decoder = decoder, isUseDefault = false)
  }

  object ProDecoderWithKey {
    implicit def asunaCirceDecoder[T]: Application4[PluginDecodeContent, PropertyTag1[ProDecoderWithKey[T], T], T, String, DefaultValue[T], ProDecoderWithKey[T]] =
      new Application4[PluginDecodeContent, PropertyTag1[ProDecoderWithKey[T], T], T, String, DefaultValue[T], ProDecoderWithKey[T]] {
        override def application(context: Context4[PluginDecodeContent]): PluginDecodeContent[T, String, DefaultValue[T], ProDecoderWithKey[T]] =
          new PluginDecodeContent[T, String, DefaultValue[T], ProDecoderWithKey[T]] {
            override def getValue(name: String, defaultValue: DefaultValue[T], rep: ProDecoderWithKey[T]): Decoder[T] = {
              Decoder.instance { j =>
                if (rep.isUseDefault) {
                  j.get(rep.key)(rep.decoder) match {
                    case Left(s) =>
                      val default = defaultValue.value
                      default match {
                        case Some(r) =>
                          Right(r)
                        case _ =>
                          Left(s)
                      }
                    case value @ Right(_) =>
                      value
                  }
                } else {
                  j.get(rep.key)(rep.decoder)
                }
              }
            }
          }
      }
  }

}
