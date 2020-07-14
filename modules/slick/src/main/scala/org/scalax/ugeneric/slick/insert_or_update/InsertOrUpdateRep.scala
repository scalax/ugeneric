package org.scalax.ugeneric.slick.mutiply

import zsg.PropertyTag
import slick.ast.{ElementSymbol, Node, Select}
import slick.lifted.{Shape, ShapeLevel}
import zsg.macros.utils.PlaceHolder

trait InsertOrUpdateRep[RepTag, DataTag, PolyTag, RepType, DataType, PolyType, EncodeRef, Packed] extends Any {
  def buildParams(extract: Any => DataType): Packed
  def encodeRef(rep: Packed, path: Node, index: Int, polyType: PolyType): (EncodeRef, Int)
  def pack(u: RepType): Packed
  def node(rep: Packed, l: List[Node], polyType: PolyType): List[Node]
  def fieldPlus(data: DataType, l: List[Any], polyType: PolyType): List[Any]
  // def fieldTail(l: List[Any], polyType: PolyType): (DataType, List[Any])
}

object InsertOrUpdateRep {

  implicit def slickRepNodeApplication[F <: ShapeLevel, Rep, Data, Out](
    implicit dd: Shape[F, Rep, Data, Out]
  ): InsertOrUpdateRep[PropertyTag[Rep], PropertyTag[Data], PropertyTag[PlaceHolder], Rep, Data, PlaceHolder, Any, Out] =
    new InsertOrUpdateRep[PropertyTag[Rep], PropertyTag[Data], PropertyTag[PlaceHolder], Rep, Data, PlaceHolder, Any, Out] {
      override def node(rep: Out, l: List[Node], p: PlaceHolder): List[Node]      = dd.packedShape.toNode(rep) :: l
      override def fieldPlus(data: Data, l: List[Any], p: PlaceHolder): List[Any] = data :: l
      // override def fieldTail(l: List[Any], p: PlaceHolder): (Data, List[Any])     = (l.head.asInstanceOf[Data], l.tail)
      override def encodeRef(rep: Out, path: Node, index: Int, p: PlaceHolder): (Any, Int) =
        (dd.packedShape.encodeRef(rep, Select(path, ElementSymbol(index))), index + 1)
      override def pack(u: Rep): Out                      = dd.pack(u)
      override def buildParams(extract: Any => Data): Out = dd.packedShape.buildParams(extract)
    }

}
