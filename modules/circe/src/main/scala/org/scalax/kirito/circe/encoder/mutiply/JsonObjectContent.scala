package org.scalax.kirito.circe.encoder

import asuna.{Application3, Context3, PropertyTag1}
import asuna.macros.ByNameImplicit
import asuna.macros.utils.PlaceHolder
import io.circe.{Encoder, Json}

trait JsonObjectContent[T, II, Rep] extends Any {
  def appendField(name: II, rep: Rep): JsonObjectAppender[T]
}

object JsonObjectContent {

  implicit final def asunaCircePlaceHolderImplicit[T](
    implicit t: ByNameImplicit[Encoder[T]]
  ): Application3[JsonObjectContent, PropertyTag1[PlaceHolder, T], T, String, PlaceHolder] =
    new Application3[JsonObjectContent, PropertyTag1[PlaceHolder, T], T, String, PlaceHolder] {
      override final def application(context: Context3[JsonObjectContent]): JsonObjectContent[T, String, PlaceHolder] = new JsonObjectContent[T, String, PlaceHolder] {
        override final def appendField(name: String, rep: PlaceHolder): JsonObjectAppender[T] = new JsonObjectAppender[T] {
          override def appendField(data: T, m: List[(String, Json)]): List[(String, Json)] = {
            (name, t.value(data)) :: m
          }
        }
      }
    }

  implicit final def asunaCirceEncoderImplicit[T]: Application3[JsonObjectContent, PropertyTag1[Encoder[T], T], T, String, Encoder[T]] =
    new Application3[JsonObjectContent, PropertyTag1[Encoder[T], T], T, String, Encoder[T]] {
      override final def application(context: Context3[JsonObjectContent]): JsonObjectContent[T, String, Encoder[T]] =
        new JsonObjectContent[T, String, Encoder[T]] {
          override final def appendField(name: String, rep: Encoder[T]): JsonObjectAppender[T] =
            new JsonObjectAppender[T] {
              override def appendField(data: T, m: List[(String, Json)]): List[(String, Json)] = (name, rep(data)) :: m
            }
        }
    }

}
