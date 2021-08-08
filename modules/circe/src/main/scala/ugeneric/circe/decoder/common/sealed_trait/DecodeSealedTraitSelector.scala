package ugeneric.circe.decoder

import zsg.macros.ByNameImplicit
import zsg.macros.single.SealedTag
import io.circe.Decoder
import zsg.{Application, TypeFunction, TypeHList, TypeHList1}

class DecodeSealedTraitSelector[P] extends TypeFunction {

  trait JsonDecoder[II] {
    def getValue(name: II): Decoder[P]
  }

  override type H[T <: TypeHList] = JsonDecoder[T#Head]

}

object DecodeSealedTraitSelector {

  def apply[T]: DecodeSealedTraitSelector[T]        = value.asInstanceOf[DecodeSealedTraitSelector[T]]
  private val value: DecodeSealedTraitSelector[Any] = new DecodeSealedTraitSelector[Any]

  implicit def ugenericCirceSealedDecoder[T, R <: T](implicit
    t: ByNameImplicit[Decoder[R]]
  ): Application[DecodeSealedTraitSelector[T], DecodeSealedContext[T], SealedTag[R], TypeHList1[String]] =
    new Application[DecodeSealedTraitSelector[T], DecodeSealedContext[T], SealedTag[R], TypeHList1[String]] {
      override def application(context: DecodeSealedContext[T]): DecodeSealedTraitSelector[T]#JsonDecoder[String] = {
        val c = DecodeSealedTraitSelector[T]
        new c.JsonDecoder[String] {
          override def getValue(name: String): Decoder[T] = Decoder.instance { _.get(name)(t.value): Decoder.Result[R] }
        }
      }
    }

}
