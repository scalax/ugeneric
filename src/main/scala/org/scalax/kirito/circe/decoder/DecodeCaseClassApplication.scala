package org.scalax.kirito.circe.decoder

import asuna.{Application3, Context3}
import asuna.macros.single.{DefaultValue, PropertyTag}
import io.circe.{Decoder, HCursor}

class DecodeCaseClassApplication[T](final val toProperty: (String, HCursor) => Decoder.Result[T])
    extends Application3[DecodeContent, PropertyTag[T], T, String, DefaultValue[T]] {
  override def application(context: Context3[DecodeContent]): DecodeContent[T, String, DefaultValue[T]] = {
    new DecodeContent[T, String, DefaultValue[T]] {
      override def getValue(name: String, defaultValue: DefaultValue[T]): Decoder[T] = {
        Decoder.instance { j =>
          toProperty(name, j) match {
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
