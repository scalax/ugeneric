package org.scalax.ugeneric.slick.mutiply

import asuna.{Context6, Plus6, ZsgTuple0}
import slick.ast.Node

object RepContext extends Context6[RepNode] {
  override def append[X1, X2, X3, X4, X5, X6, Y1, Y2, Y3, Y4, Y5, Y6, Z1, Z2, Z3, Z4, Z5, Z6](x: RepNode[X1, X2, X3, X4, X5, X6], y: RepNode[Y1, Y2, Y3, Y4, Y5, Y6])(
    p: Plus6[X1, X2, X3, X4, X5, X6, Y1, Y2, Y3, Y4, Y5, Y6, Z1, Z2, Z3, Z4, Z5, Z6]
  ): RepNode[Z1, Z2, Z3, Z4, Z5, Z6] = {
    new RepNode[Z1, Z2, Z3, Z4, Z5, Z6] {
      override def node(rep: Z6, l: List[Node]): List[Node]     = y.node(p.takeTail6(rep), x.node(p.takeHead6(rep), l))
      override def fieldPlus(data: Z4, l: List[Any]): List[Any] = y.fieldPlus(p.takeTail4(data), x.fieldPlus(p.takeHead4(data), l))
      override def fieldTail(l: List[Any]): (Z4, List[Any]) = {
        val y2 = y.fieldTail(l)
        val x2 = x.fieldTail(y2._2)
        (p.plus4(x2._1, y2._1), x2._2)
      }
      override def encodeRef(rep: Z6, path: Node, index: Int): (Z5, Int) = {
        val x2 = x.encodeRef(p.takeHead6(rep), path, index)
        val y2 = y.encodeRef(p.takeTail6(rep), path, x2._2)
        (p.plus5(x2._1, y2._1), y2._2)
      }
      override def buildParams(extract: Any => Z4): Z6 = p.plus6(x.buildParams(extract.andThen(p.takeHead4)), y.buildParams(extract.andThen(p.takeTail4)))
      override def pack(u: Z3): Z6                     = p.plus6(x.pack(p.takeHead3(u)), y.pack(p.takeTail3(u)))
    }
  }

  override val start: RepNode[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] = {
    new RepNode[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] {
      override def node(rep: ZsgTuple0, l: List[Node]): List[Node]                     = l
      override def fieldPlus(data: ZsgTuple0, l: List[Any]): List[Any]                 = l
      override def fieldTail(l: List[Any]): (ZsgTuple0, List[Any])                     = (ZsgTuple0.value, l)
      override def encodeRef(rep: ZsgTuple0, path: Node, index: Int): (ZsgTuple0, Int) = (ZsgTuple0.value, index)
      override def buildParams(extract: Any => ZsgTuple0): ZsgTuple0                   = ZsgTuple0.value
      override def pack(u: ZsgTuple0): ZsgTuple0                                       = u
    }
  }
}
