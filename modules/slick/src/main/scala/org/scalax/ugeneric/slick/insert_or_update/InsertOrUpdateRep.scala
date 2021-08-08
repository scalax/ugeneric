package org.scalax.ugeneric.slick.mutiply

import zsg.PropertyTag
import slick.ast.Node
import slick.lifted.{Shape, ShapeLevel}
import zsg.macros.utils.PlaceHolder

trait InsertOrUpdateRep[RepTag, DataTag, PolyTag, RepType, DataType, PolyType, Packed] extends Any {
  def pack(u: RepType): Packed
  def node(rep: Packed, l: List[Node], polyType: PolyType): List[Node]
  def fieldPlus(data: DataType, l: List[Any], polyType: PolyType): List[Any]
}

object InsertOrUpdateRep {

  implicit def slickRepNodeApplication[F <: ShapeLevel, Rep, Data, Out](implicit
    dd: Shape[F, Rep, Data, Out]
  ): InsertOrUpdateRep[PropertyTag[Rep], PropertyTag[Data], PropertyTag[PlaceHolder], Rep, Data, PlaceHolder, Out] =
    new InsertOrUpdateRep[PropertyTag[Rep], PropertyTag[Data], PropertyTag[PlaceHolder], Rep, Data, PlaceHolder, Out] {
      override def node(rep: Out, l: List[Node], p: PlaceHolder): List[Node]      = dd.packedShape.toNode(rep) :: l
      override def fieldPlus(data: Data, l: List[Any], p: PlaceHolder): List[Any] = data :: l
      override def pack(u: Rep): Out                                              = dd.pack(u)
    }

}
