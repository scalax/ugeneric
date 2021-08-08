package ugeneric.circe.decoder

import zsg.macros.ByNameImplicit
import zsg.macros.single.SealedTag
import io.circe.Decoder
import ugeneric.circe.NameTranslator
import zsg.{Application, TypeFunction, TypeHList, TypeHList1}

class PluginDecodeSealedTraitSelector[P] extends TypeFunction {

  trait JsonDecoder[II] {
    def getValue(name: II, i: Option[NameTranslator]): Decoder[P]
  }

  override type H[T <: TypeHList] = JsonDecoder[T#Head]

}

object PluginDecodeSealedTraitSelector {

  implicit def apply[T]: PluginDecodeSealedTraitSelector[T] = value.asInstanceOf[PluginDecodeSealedTraitSelector[T]]
  private val value: PluginDecodeSealedTraitSelector[Any]   = new PluginDecodeSealedTraitSelector[Any]

  implicit def asunaCirceSealedDecoder[T, R <: T](implicit
    t: ByNameImplicit[Decoder[R]]
  ): Application[PluginDecodeSealedTraitSelector[T], PluginDecodeSealedContext[T], SealedTag[R], TypeHList1[String]] =
    new Application[PluginDecodeSealedTraitSelector[T], PluginDecodeSealedContext[T], SealedTag[R], TypeHList1[String]] {
      override def application(context: PluginDecodeSealedContext[T]): PluginDecodeSealedTraitSelector[T]#JsonDecoder[String] = {
        val con = PluginDecodeSealedTraitSelector[T]
        new con.JsonDecoder[String] {
          override def getValue(name: String, i: Option[NameTranslator]): Decoder[T] = {
            val nameI = i.map(_.tran(name)).getOrElse(name)
            Decoder.instance {
              _.get(nameI)(t.value): Decoder.Result[R]
            }
          }
        }
      }
    }

}
