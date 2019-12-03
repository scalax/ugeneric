package org.scalax.kirito.circe.decoder

import asuna.macros.single.{DefaultValue, PropertyTag}
import asuna.macros.ByNameImplicit
import asuna.{Application3, Context3}
import io.circe._

trait DecodeContent[T, II, D] extends Any {
  def getValue(name: II, defaultValue: D): Decoder[T]
}

object DecodeContent {

  implicit def asunaCirceDecoder[T](
    implicit dd: ByNameImplicit[Decoder[T]]
  ): Application3[DecodeContent, PropertyTag[T], T, String, DefaultValue[T]] = new Application3[DecodeContent, PropertyTag[T], T, String, DefaultValue[T]] {
    override def application(context: Context3[DecodeContent]): DecodeContent[T, String, DefaultValue[T]] = new DecodeContent[T, String, DefaultValue[T]] {
      override def getValue(name: String, defaultValue: DefaultValue[T]): Decoder[T] = {
        Decoder.instance { j =>
          j.get(name)(dd.value) match {
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
        }
      }
    }
  }

}
