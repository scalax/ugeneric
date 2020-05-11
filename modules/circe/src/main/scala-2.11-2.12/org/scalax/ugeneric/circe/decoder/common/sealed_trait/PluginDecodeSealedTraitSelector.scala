package org.scalax.ugeneric.circe.decoder.common.sealed_trait

import asuna.{Application2, Application3, Context3}
import asuna.macros.ByNameImplicit
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
    implicit t: ByNameImplicit[Decoder[R]]
  ): Application3[PluginDecodeSealedTraitSelector[T]#JsonDecoder, SealedTag[R], String, R => T] = {
    val con = PluginDecodeSealedTraitSelector[T]
    new Application3[PluginDecodeSealedTraitSelector[T]#JsonDecoder, SealedTag[R], String, R => T] {
      override def application(
        context: Context3[PluginDecodeSealedTraitSelector[T]#JsonDecoder]
      ): PluginDecodeSealedTraitSelector[T]#JsonDecoder[SealedTag[R], String, R => T] = {
        new con.JsonDecoder[SealedTag[R], String, R => T] {
          override def getValue(name: String, tran: R => T, i: Option[NameTranslator]): Decoder[T] = {
            val nameI = i.map(_.tran(name)).getOrElse(name)
            Decoder.instance {
              _.get(nameI)(t.value).right.map(tran)
            }
          }
        }
      }
    }
  }

}
