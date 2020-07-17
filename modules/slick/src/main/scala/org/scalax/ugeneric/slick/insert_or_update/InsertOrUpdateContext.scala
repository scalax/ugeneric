package org.scalax.ugeneric.slick.mutiply

import slick.ast.Node
import zsg.{Context7, Plus7, ZsgTuple0}

class InsertOrUpdateContext extends Context7[InsertOrUpdateRep] {
  override def append[X1, X2, X3, X4, X5, X6, X7, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Z1, Z2, Z3, Z4, Z5, Z6, Z7](
    x: InsertOrUpdateRep[X1, X2, X3, X4, X5, X6, X7],
    y: InsertOrUpdateRep[Y1, Y2, Y3, Y4, Y5, Y6, Y7]
  )(p: Plus7[X1, X2, X3, X4, X5, X6, X7, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Z1, Z2, Z3, Z4, Z5, Z6, Z7]): InsertOrUpdateRep[Z1, Z2, Z3, Z4, Z5, Z6, Z7] =
    new InsertOrUpdateRep[Z1, Z2, Z3, Z4, Z5, Z6, Z7] {
      override def pack(u: Z4): Z7 = p.plus7(x.pack(p.takeHead4(u)), y.pack(p.takeTail4(u)))
      override def node(rep: Z7, l: List[Node], polyType: Z6): List[Node] =
        x.node(p.takeHead7(rep), y.node(p.takeTail7(rep), l, p.takeTail6(polyType)), p.takeHead6(polyType))
      override def fieldPlus(data: Z5, l: List[Any], polyType: Z6): List[Any] =
        x.fieldPlus(p.takeHead5(data), y.fieldPlus(p.takeTail5(data), l, p.takeTail6(polyType)), p.takeHead6(polyType))
    }

  override val start: InsertOrUpdateRep[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] = {
    new InsertOrUpdateRep[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] {
      override def pack(u: ZsgTuple0): ZsgTuple0                                            = u
      override def node(rep: ZsgTuple0, l: List[Node], polyType: ZsgTuple0): List[Node]     = l
      override def fieldPlus(data: ZsgTuple0, l: List[Any], polyType: ZsgTuple0): List[Any] = l
    }
  }
}

object InsertOrUpdateContext {
  val value: InsertOrUpdateContext = new InsertOrUpdateContext
}
