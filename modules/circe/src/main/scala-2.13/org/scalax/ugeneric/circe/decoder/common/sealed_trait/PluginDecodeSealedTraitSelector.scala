package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import asuna.{Application3, Context3}
import asuna.macros.single.SealedTag
import io.circe.Decoder
import org.scalax.ugeneric.circe.NameTranslator

class PluginDecodeSealedTraitSelector[P] {

  trait JsonDecoder[I, II, T] {
    def getValue(name: II, tran: T, i: Option[NameTranslator]): Decoder[P]
  }

}

object PluginDecodeSealedTraitSelector {

  def apply[T]: PluginDecodeSealedTraitSelector[T]        = value.asInstanceOf[PluginDecodeSealedTraitSelector[T]]
  private val value: PluginDecodeSealedTraitSelector[Any] = new PluginDecodeSealedTraitSelector[Any]

  implicit def asunaCirceSealedDecoder[T, R](
    implicit t: => Decoder[R]
  ): PluginDecodeSealedTraitSelector[T]#JsonDecoder[SealedTag[R], String, R => T] = {
    val con = PluginDecodeSealedTraitSelector[T]

    { (name, tran, i) =>
      val nameI = i.map(_.tran(name)).getOrElse(name)
      _.get(nameI)(t).map(tran)
    }: con.JsonDecoder[SealedTag[R], String, R => T]

  }

}
