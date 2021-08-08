package ugeneric.circe.encoder

import zsg.macros.ByNameImplicit
import zsg.macros.single.SealedTag
import io.circe.{Encoder, Json}
import ugeneric.circe.NameTranslator
import zsg.{Application, TypeFunction, TypeHList, TypeHList2}

class PluginEncodeSealedTraitSelector[H1] extends TypeFunction {
  abstract class JsonEncoder[T, Name] {
    def subClassToJsonOpt(model: H1, classTags: T, name: Name, i: Option[NameTranslator]): Option[(String, Json)]
  }
  override type H[T <: TypeHList] = JsonEncoder[T#Head, T#Tail#Head]
}

object PluginEncodeSealedTraitSelector {

  private val value                                         = new PluginEncodeSealedTraitSelector[Any]
  implicit def apply[T]: PluginEncodeSealedTraitSelector[T] = value.asInstanceOf[PluginEncodeSealedTraitSelector[T]]

  implicit final def zsgCirceSealedEncoder[T, R](implicit
    t: ByNameImplicit[Encoder[R]]
  ): Application[PluginEncodeSealedTraitSelector[T], PluginEncodeSealedContext[T], SealedTag[R], TypeHList2[Class[R], String]] =
    new Application[PluginEncodeSealedTraitSelector[T], PluginEncodeSealedContext[T], SealedTag[R], TypeHList2[Class[R], String]] {
      override def application(context: PluginEncodeSealedContext[T]): PluginEncodeSealedTraitSelector[T]#JsonEncoder[Class[R], String] = {
        val con = PluginEncodeSealedTraitSelector[T]
        new con.JsonEncoder[Class[R], String] {
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

}
