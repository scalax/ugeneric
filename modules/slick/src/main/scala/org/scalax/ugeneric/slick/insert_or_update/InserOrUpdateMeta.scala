package org.scalax.ugeneric.slick.mutiply

import slick.ast.Node
import slick.lifted.{Shape, ShapeLevel}
import zsg.PropertyTag

object InserOrUpdateMeta {

  case class NeedIgnore(ignore: Boolean)

  def needIgnore(i: Boolean): NeedIgnore = NeedIgnore(ignore = i)

  object NeedIgnore {
    implicit def slickRepNodeApplication[F <: ShapeLevel, Rep, Data, Out, PolyTag, PolyType](
      implicit dd: Shape[F, Rep, Data, Out]
    ): InsertOrUpdateRep[PropertyTag[Rep], PropertyTag[Data], PropertyTag[NeedIgnore], Rep, Data, NeedIgnore, Out] =
      new InsertOrUpdateRep[PropertyTag[Rep], PropertyTag[Data], PropertyTag[NeedIgnore], Rep, Data, NeedIgnore, Out] {
        override def node(rep: Out, l: List[Node], n: NeedIgnore): List[Node]      = if (n.ignore) l else dd.packedShape.toNode(rep) :: l
        override def fieldPlus(data: Data, l: List[Any], n: NeedIgnore): List[Any] = if (n.ignore) l else data :: l
        override def pack(u: Rep): Out                                             = dd.pack(u)
      }
  }

}
