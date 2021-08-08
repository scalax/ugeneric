package ugeneric.circe.encoder

import zsg.{Application, PropertyTag, TagMerge2, TypeFunction, TypeHList, TypeHList1}
import io.circe.{Encoder, Json}
import ugeneric.circe.NameTranslator
import zsg.macros.ByNameImplicit
import zsg.macros.single.ColumnName
import zsg.macros.utils.GenericColumnName

class PluginJsonObjectTypeContext extends TypeFunction {
  override type H[T <: TypeHList] = PluginJsonObjectContent[T#Head]
}
object PluginJsonObjectTypeContext {
  implicit final def zsgCirceImplicit[T, N <: String](implicit
    t: ByNameImplicit[Encoder[T]],
    g: GenericColumnName[N]
  ): Application[PluginJsonObjectTypeContext, PluginJsonObjectContext, TagMerge2[PropertyTag[T], ColumnName[N]], TypeHList1[T]] =
    new Application[PluginJsonObjectTypeContext, PluginJsonObjectContext, TagMerge2[PropertyTag[T], ColumnName[N]], TypeHList1[T]] {
      override def application(context: PluginJsonObjectContext): PluginJsonObjectContent[T] = new PluginJsonObjectContent[T] {
        override def getAppender(data: T, l: List[(String, Json)], p: Option[NameTranslator]): List[(String, Json)] = {
          val nameI = p.map(_.tran(g.value)).getOrElse(g.value)
          (nameI, t.value(data)) :: l
        }
      }
    }
}

abstract class PluginJsonObjectContent[T] {
  def getAppender(data: T, l: List[(String, Json)], p: Option[NameTranslator]): List[(String, Json)]
}
