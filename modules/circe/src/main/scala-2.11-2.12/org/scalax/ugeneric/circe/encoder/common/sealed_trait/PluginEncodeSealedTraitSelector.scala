package org.scalax.ugeneric.circe.encoder.common.sealed_trait

import asuna.Application2
import asuna.macros.ByNameImplicit
import asuna.macros.single.SealedTag
import io.circe.{Encoder, Json}
import org.scalax.ugeneric.circe.NameTranslator

class PluginEncodeSealedTraitSelector[H] {
  trait JsonEncoder[T, Name] {
    def subClassToJsonOpt(model: H, classTags: T, name: Name, i: Option[NameTranslator]): Option[(String, Json)]
  }
}

object PluginEncodeSealedTraitSelector {

  private val value                                = new PluginEncodeSealedTraitSelector[Any]
  def apply[T]: PluginEncodeSealedTraitSelector[T] = value.asInstanceOf[PluginEncodeSealedTraitSelector[T]]

  implicit final def asunaCirceSealedEncoder[T, R](
    implicit t: ByNameImplicit[Encoder[R]]
  ): Application2[PluginEncodeSealedTraitSelector[T]#JsonEncoder, SealedTag[R], Class[R], String] = {
    val con = PluginEncodeSealedTraitSelector[T]
    _ =>
      { (model, classTags, name, i) =>
        val nameI = i.map(_.tran(name)).getOrElse(name)
        if (classTags.isInstance(model))
          Some((nameI, t.value(classTags.cast(model))))
        else
          Option.empty
      }: con.JsonEncoder[Class[R], String]
  }

}
