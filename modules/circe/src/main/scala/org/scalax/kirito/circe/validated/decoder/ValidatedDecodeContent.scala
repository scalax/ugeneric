package org.scalax.kirito.circe.decoder

import asuna.macros.single.DefaultValue
import asuna.macros.ByNameImplicit
import asuna.macros.utils.PlaceHolder
import asuna.{Application4, Context4, PropertyTag1}
import cats.data.Validated
import io.circe._

trait ValidatedDecodeContent[T, II, D, Rep] extends Any {
  def getValue(name: II, defaultValue: D, rep: Rep): ValidatedDecoder[T]
}

object ValidatedDecodeContent {

  implicit def asunaCirceDecoder[T](
    implicit dd: ByNameImplicit[Decoder[T]]
  ): Application4[ValidatedDecodeContent, PropertyTag1[PlaceHolder, T], T, String, DefaultValue[T], PlaceHolder] =
    new Application4[ValidatedDecodeContent, PropertyTag1[PlaceHolder, T], T, String, DefaultValue[T], PlaceHolder] {
      override def application(context: Context4[ValidatedDecodeContent]): ValidatedDecodeContent[T, String, DefaultValue[T], PlaceHolder] =
        new ValidatedDecodeContent[T, String, DefaultValue[T], PlaceHolder] {
          override def getValue(name: String, defaultValue: DefaultValue[T], rep: PlaceHolder): ValidatedDecoder[T] = new ValidatedDecoder[T] {
            override def getValue(json: ACursor): Validated[errorMessage, T] = {
              json.downField(name).focus match {
                case Some(Json.Null) => Validated.invalid(errorMessage.build(name, s"${name}不能为空"))
                case None            => Validated.invalid(errorMessage.build(name, s"${name}不能为空"))
                case Some(j) =>
                  j.as(dd.value) match {
                    case Left(i) =>
                      Validated.invalid(errorMessage.build(name, i.message))
                    case Right(value) => Validated.valid(value)
                  }
              }
            }
          }
        }
    }

  /*implicit def asunaOptCirceDecoder[T](
    implicit dd: ByNameImplicit[Decoder[Option[T]]]
  ): Application4[ValidatedDecodeContent, PropertyTag1[PlaceHolder, Option[T]], Option[T], String, DefaultValue[Option[T]], PlaceHolder] =
    new Application4[ValidatedDecodeContent, PropertyTag1[PlaceHolder, Option[T]], Option[T], String, DefaultValue[Option[T]], PlaceHolder] {
      override def application(context: Context4[ValidatedDecodeContent]): ValidatedDecodeContent[Option[T], String, DefaultValue[Option[T]], PlaceHolder] =
        new ValidatedDecodeContent[Option[T], String, DefaultValue[Option[T]], PlaceHolder] {
          override def getValue(name: String, defaultValue: DefaultValue[Option[T]], rep: PlaceHolder): ValidatedDecoder[Option[T]] = new ValidatedDecoder[Option[T]] {
            override def getValue(json: ACursor): Validated[errorMessage, Option[T]] = {
              json.get(name)(dd.value) match {
                case Left(i)      => Validated.invalid(errorMessage.build(name, i.message))
                case Right(value) => Validated.valid(value)
              }
            }
          }
        }
    }*/

  implicit def asunaValidatedCirceDecoder[T](
    implicit dd: ByNameImplicit[ValidatedDecoder[T]]
  ): Application4[ValidatedDecodeContent, PropertyTag1[PlaceHolder, T], T, String, DefaultValue[T], PlaceHolder] =
    new Application4[ValidatedDecodeContent, PropertyTag1[PlaceHolder, T], T, String, DefaultValue[T], PlaceHolder] {
      override def application(context: Context4[ValidatedDecodeContent]): ValidatedDecodeContent[T, String, DefaultValue[T], PlaceHolder] =
        new ValidatedDecodeContent[T, String, DefaultValue[T], PlaceHolder] {
          override def getValue(name: String, defaultValue: DefaultValue[T], rep: PlaceHolder): ValidatedDecoder[T] = new ValidatedDecoder[T] {
            override def getValue(json: ACursor): Validated[errorMessage, T] = {
              dd.value.getValue(json.downField(name)).leftMap(_.addPrefix(name))
            }
          }
        }
    }

}
