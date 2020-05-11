package org.scalax.ugeneric.circe.encoder.common.model

import asuna.{Application3, Context3, PropertyTag}
import asuna.macros.ByNameImplicit
import io.circe.{Encoder, Json}
import org.scalax.ugeneric.circe.NameTranslator

abstract class PluginJsonObjectContent[I, Model, Name] {
  def appendField(name: Name, p: Option[NameTranslator]): JsonObjectAppender[Model]
}

object PluginJsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: ByNameImplicit[Encoder[T]]): Application3[PluginJsonObjectContent, PropertyTag[T], T, String] = {
    new Application3[PluginJsonObjectContent, PropertyTag[T], T, String] {
      override def application(context: Context3[PluginJsonObjectContent]): PluginJsonObjectContent[PropertyTag[T], T, String] = {
        new PluginJsonObjectContent[PropertyTag[T], T, String] {
          override def appendField(name: String, p: Option[NameTranslator]): JsonObjectAppender[T] = {
            val nameI = p.map(_.tran(name)).getOrElse(name)
            new JsonObjectAppender[T] {
              override def getAppender(data: T): JsonObjectFieldAppender = new JsonObjectFieldAppender {
                override def append(m: List[(String, Json)]): List[(String, Json)] = (nameI, t.value(data)) :: m
              }
            }
          }
        }
      }
    }
  }

}
