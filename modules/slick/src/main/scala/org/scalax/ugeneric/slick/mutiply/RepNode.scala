package org.scalax.ugeneric.slick.mutiply

import asuna.{Application4, Context4, PropertyTag1}
import slick.ast.{ElementSymbol, Node, Select}
import slick.lifted.{Shape, ShapeLevel}

trait RepNode[RepType, DataType, EncodeRef, Packed] extends Any {
  def buildParams(extract: Any => DataType): Packed
  def encodeRef(rep: Packed, path: Node, index: Int): (EncodeRef, Int)
  def pack(u: RepType): Packed
  def node(rep: Packed, l: List[Node]): List[Node]
  def fieldPlus(data: DataType, l: List[Any]): List[Any]
  def fieldTail(l: List[Any]): (DataType, List[Any])
}

object RepNode {

  implicit def slickRepNodeApplication[F <: ShapeLevel, Rep, Data, Out](
    implicit dd: Shape[F, Rep, Data, Out]
  ): Application4[RepNode, PropertyTag1[Rep, Data], Rep, Data, Any, Out] = new Application4[RepNode, PropertyTag1[Rep, Data], Rep, Data, Any, Out] {
    override def application(context: Context4[RepNode]): RepNode[Rep, Data, Any, Out] = new RepNode[Rep, Data, Any, Out] { self =>
      override def node(rep: Out, l: List[Node]): List[Node]               = dd.packedShape.toNode(rep) :: l
      override def fieldPlus(data: Data, l: List[Any]): List[Any]          = data :: l
      override def fieldTail(l: List[Any]): (Data, List[Any])              = (l.head.asInstanceOf[Data], l.tail)
      override def encodeRef(rep: Out, path: Node, index: Int): (Any, Int) = (dd.packedShape.encodeRef(rep, Select(path, ElementSymbol(index))), index + 1)
      override def pack(u: Rep): Out                                       = dd.pack(u)
      override def buildParams(extract: Any => Data): Out                  = dd.packedShape.buildParams(extract)
    }
  }

}
