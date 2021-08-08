package ugeneric.circe.decoder

import zsg.{Application, Context, Plus, PropertyTag, TagMerge2, TypeFunction, TypeHList, TypeHList1}
import io.circe.Decoder
import zsg.macros.ByNameImplicit
import zsg.macros.single.ColumnName
import zsg.macros.utils.GenericColumnName

class DecoderTypeContext extends TypeFunction {
  override type H[I <: TypeHList] = Decoder[I#Head]
}
object DecoderTypeContext {
  implicit def ugenericDecoder[Model, Name <: String](implicit
    name: GenericColumnName[Name],
    dd: ByNameImplicit[Decoder[Model]]
  ): Application[DecoderTypeContext, DecodeContext, TagMerge2[PropertyTag[Model], ColumnName[Name]], TypeHList1[Model]] =
    new Application[DecoderTypeContext, DecodeContext, TagMerge2[PropertyTag[Model], ColumnName[Name]], TypeHList1[Model]] {
      override def application(context: DecodeContext): Decoder[Model] = Decoder.instance(_.get(name.value)(dd.value))
    }
}

class DecodeContext extends Context[DecoderTypeContext] {
  override def append[X <: TypeHList, Y <: TypeHList, Z <: TypeHList](x: Decoder[X#Head], y: Decoder[Y#Head])(plus: Plus[X, Y, Z]): Decoder[Z#Head] = for {
    x1 <- x
    y1 <- y
  } yield plus.plus(x1, y1)
}

object DecodeContext {
  val value: DecodeContext = new DecodeContext
}
