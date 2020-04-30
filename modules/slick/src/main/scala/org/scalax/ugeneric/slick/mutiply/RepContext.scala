package org.scalax.ugeneric.slick.mutiply

import asuna.{AsunaTuple0, Context4, Plus4}
import slick.ast.Node

object RepContext extends Context4[RepNode] {
  override def append[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4](x: RepNode[X1, X2, X3, X4], y: RepNode[Y1, Y2, Y3, Y4])(
    p: Plus4[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4]
  ): RepNode[Z1, Z2, Z3, Z4] = new RepNode[Z1, Z2, Z3, Z4] {
    override def node(rep: Z1, l: List[Node]): List[Node]     = y.node(p.takeTail1(rep), x.node(p.takeHead1(rep), l))
    override def fieldPlus(data: Z2, l: List[Any]): List[Any] = y.fieldPlus(p.takeTail2(data), x.fieldPlus(p.takeHead2(data), l))
    override def fieldTail(l: List[Any]): (Z2, List[Any]) = {
      val y2 = y.fieldTail(l)
      val x2 = x.fieldTail(y2._2)
      (p.plus2(x2._1, y2._1), x2._2)
    }
    override def encodeRef(rep: Z1, path: Node, index: Int): (Z3, Int) = {
      val x2 = x.encodeRef(p.takeHead1(rep), path, index)
      val y2 = y.encodeRef(p.takeTail1(rep), path, x2._2)
      (p.plus3(x2._1, y2._1), y2._2)
    }
    override def buildParams(extract: Any => Z2): Z4 = p.plus4(x.buildParams(extract.andThen(p.takeHead2)), y.buildParams(extract.andThen(p.takeTail2)))
  }

  override val start: RepNode[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] = new RepNode[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] {
    override def node(rep: AsunaTuple0, l: List[Node]): List[Node]                       = l
    override def fieldPlus(data: AsunaTuple0, l: List[Any]): List[Any]                   = l
    override def fieldTail(l: List[Any]): (AsunaTuple0, List[Any])                       = (AsunaTuple0.value, l)
    override def encodeRef(rep: AsunaTuple0, path: Node, index: Int): (AsunaTuple0, Int) = (AsunaTuple0.value, index)
    override def buildParams(extract: Any => AsunaTuple0): AsunaTuple0                   = AsunaTuple0.value
  }
}
