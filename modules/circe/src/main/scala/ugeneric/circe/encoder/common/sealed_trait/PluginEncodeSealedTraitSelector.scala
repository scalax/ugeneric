package ugeneric.circe.encoder

import zsg.macros.ByNameImplicit
import zsg.macros.single.SealedTag
import io.circe.{Encoder, Json}
import ugeneric.circe.NameTranslator

class PluginEncodeSealedTraitSelector[H] {
  abstract class JsonEncoder[M, T, Name] {
    def subClassToJsonOpt(model: H, classTags: T, name: Name, i: Option[NameTranslator]): Option[(String, Json)]
  }
}

object PluginEncodeSealedTraitSelector {

  private val value                                = new PluginEncodeSealedTraitSelector[Any]
  def apply[T]: PluginEncodeSealedTraitSelector[T] = value.asInstanceOf[PluginEncodeSealedTraitSelector[T]]

  implicit final def asunaCirceSealedEncoder[T, R](
    implicit t: ByNameImplicit[Encoder[R]]
  ): PluginEncodeSealedTraitSelector[T]#JsonEncoder[SealedTag[R], Class[R], String] = {
    val con = PluginEncodeSealedTraitSelector[T]
    new con.JsonEncoder[SealedTag[R], Class[R], String] {
      override def subClassToJsonOpt(model: T, classTags: Class[R], name: String, i: Option[NameTranslator]): Option[(String, Json)] = {
        val nameI = i.map(_.tran(name)).getOrElse(name)
        if (classTags.isInstance(model))
          Some((nameI, t.value(classTags.cast(model))))
        else
          Option.empty
      }
    }
  }

}
