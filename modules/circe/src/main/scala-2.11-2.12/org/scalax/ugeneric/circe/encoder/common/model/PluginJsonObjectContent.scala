package org.scalax.ugeneric.circe.encoder.common.model

import zsg.PropertyTag
import zsg.macros.ByNameImplicit
import io.circe.{Encoder, Json}
import org.scalax.ugeneric.circe.NameTranslator

trait PluginJsonObjectContent[I, Model, Name] {
  def appendField(name: Name, p: Option[NameTranslator]): JsonObjectAppender[Model]
}

object PluginJsonObjectContent {

  implicit final def asunaCirceImplicit[T](implicit t: ByNameImplicit[Encoder[T]]): PluginJsonObjectContent[PropertyTag[T], T, String] = {
    new PluginJsonObjectContent[PropertyTag[T], T, String] {
      override def appendField(name: String, p: Option[NameTranslator]): JsonObjectAppender[T] = {
        val nameI = p.map(_.tran(name)).getOrElse(name)
        new JsonObjectAppender[T] {
          override def getAppender(data: T, l: List[(String, Json)]): List[(String, Json)] = (nameI, t.value(data)) :: l
        }
      }
    }
  }

}
