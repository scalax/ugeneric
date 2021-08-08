package ugeneric.circe.encoder

import zsg.macros.ByNameImplicit
import zsg.macros.single.SealedTag
import io.circe.{Encoder, Json}
import zsg.{Application, TypeFunction, TypeHList, TypeHList2}

class EncodeSealedTraitSelector[H1] extends TypeFunction {

  abstract class JsonEncoder[T, II] {
    def subClassToJsonOpt(model: H1, classTags: T, labelled: II): Option[(String, Json)]
  }

  override type H[T <: TypeHList] = JsonEncoder[T#Head, T#Tail#Head]

}

object EncodeSealedTraitSelector {

  private val value                          = new EncodeSealedTraitSelector[Any]
  def apply[T]: EncodeSealedTraitSelector[T] = value.asInstanceOf[EncodeSealedTraitSelector[T]]

  implicit final def zsgCirceSealedEncoder[T, R](implicit
    t: ByNameImplicit[Encoder[R]]
  ): Application[EncodeSealedTraitSelector[T], EncodeSealedContext[T], SealedTag[R], TypeHList2[Class[R], String]] =
    new Application[EncodeSealedTraitSelector[T], EncodeSealedContext[T], SealedTag[R], TypeHList2[Class[R], String]] {
      override def application(context: EncodeSealedContext[T]): EncodeSealedTraitSelector[T]#JsonEncoder[Class[R], String] = {
        val con = EncodeSealedTraitSelector[T]
        new con.JsonEncoder[Class[R], String] {
          override def subClassToJsonOpt(model: T, classTags: Class[R], labelled: String): Option[(String, Json)] = {
            if (classTags.isInstance(model))
              Some((labelled, t.value(classTags.cast(model))))
            else
              Option.empty
          }
        }
      }
    }

}
