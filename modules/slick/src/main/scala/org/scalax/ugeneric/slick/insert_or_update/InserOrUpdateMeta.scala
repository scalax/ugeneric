package org.scalax.ugeneric.slick.mutiply

import slick.ast.{ElementSymbol, Node, Select}
import slick.lifted.{Shape, ShapeLevel}
import zsg.PropertyTag

object InserOrUpdateMeta {

  case class NeedIgnore(ignore: Boolean)

  def needIgnore(i: Boolean): NeedIgnore = NeedIgnore(ignore = i)

  object NeedIgnore {
    implicit def slickRepNodeApplication[F <: ShapeLevel, Rep, Data, Out, PolyTag, PolyType](
      implicit dd: Shape[F, Rep, Data, Out]
    ): InsertOrUpdateRep[PropertyTag[Rep], PropertyTag[Data], PropertyTag[NeedIgnore], Rep, Data, NeedIgnore, Any, Out] =
      new InsertOrUpdateRep[PropertyTag[Rep], PropertyTag[Data], PropertyTag[NeedIgnore], Rep, Data, NeedIgnore, Any, Out] {
        override def node(rep: Out, l: List[Node], n: NeedIgnore): List[Node]      = if (n.ignore) l else dd.packedShape.toNode(rep) :: l
        override def fieldPlus(data: Data, l: List[Any], n: NeedIgnore): List[Any] = if (n.ignore) l else data :: l
        // override def fieldTail(l: List[Any], n: NeedIgnore): (Data, List[Any])     = if (n.ignore) 123 else (l.head.asInstanceOf[Data], l.tail)
        override def encodeRef(rep: Out, path: Node, index: Int, n: NeedIgnore): (Any, Int) =
          if (n.ignore) ((), index) else (dd.packedShape.encodeRef(rep, Select(path, ElementSymbol(index))), index + 1)
        override def pack(u: Rep): Out = dd.pack(u)
        // override def buildParams(extract: Any => Data): Out = dd.packedShape.buildParams(extract)
      }
  }

}
