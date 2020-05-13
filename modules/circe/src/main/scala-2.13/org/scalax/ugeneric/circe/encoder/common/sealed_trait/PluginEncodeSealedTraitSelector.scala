package org.scalax.ugeneric.circe.encoder.common.sealed_trait

import asuna.{Application3, Context3}
import asuna.macros.single.SealedTag
import io.circe.{Encoder, Json}
import org.scalax.ugeneric.circe.NameTranslator

class PluginEncodeSealedTraitSelector[H] {
  trait JsonEncoder[M, T, Name] {
    def subClassToJsonOpt(model: H, classTags: T, name: Name, i: Option[NameTranslator]): Option[(String, Json)]
  }
}

object PluginEncodeSealedTraitSelector {

  private val value                                = new PluginEncodeSealedTraitSelector[Any]
  def apply[T]: PluginEncodeSealedTraitSelector[T] = value.asInstanceOf[PluginEncodeSealedTraitSelector[T]]

  implicit final def asunaCirceSealedEncoder[T, R](
    implicit t: => Encoder[R]
  ): PluginEncodeSealedTraitSelector[T]#JsonEncoder[SealedTag[R], Class[R], String] = {
    val con = PluginEncodeSealedTraitSelector[T]

    { (model, classTags, name, i) =>
      val nameI = i.map(_.tran(name)).getOrElse(name)
      if (classTags.isInstance(model))
        Some((nameI, t(classTags.cast(model))))
      else
        Option.empty
    }: con.JsonEncoder[SealedTag[R], Class[R], String]
  }

}
