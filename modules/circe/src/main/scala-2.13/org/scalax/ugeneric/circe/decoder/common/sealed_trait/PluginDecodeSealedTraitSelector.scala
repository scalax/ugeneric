package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import asuna.Application2
import asuna.macros.ByNameImplicit
import asuna.macros.single.SealedTag
import io.circe.Decoder
import org.scalax.ugeneric.circe.NameTranslator

class PluginDecodeSealedTraitSelector[P] {

  trait JsonDecoder[II, T] {
    def getValue(name: II, tran: T, i: Option[NameTranslator]): Decoder[P]
  }

}

object PluginDecodeSealedTraitSelector {

  def apply[T]: PluginDecodeSealedTraitSelector[T]        = value.asInstanceOf[PluginDecodeSealedTraitSelector[T]]
  private val value: PluginDecodeSealedTraitSelector[Any] = new PluginDecodeSealedTraitSelector[Any]

  implicit def asunaCirceSealedDecoder[T, R](
    implicit t: => Decoder[R]
  ): Application2[PluginDecodeSealedTraitSelector[T]#JsonDecoder, SealedTag[R], String, R => T] = {
    val con = PluginDecodeSealedTraitSelector[T]
    _ =>
      { (name, tran, i) => j =>
        val nameI = i.map(_.tran(name)).getOrElse(name)
        j.get(nameI)(t).map(tran)
      }: con.JsonDecoder[String, R => T]
  }

}
