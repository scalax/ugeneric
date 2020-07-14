package org.scalax.ugeneric.slick.mutiply

import slick.ast.Node
import zsg.{Context8, Plus8, ZsgTuple0}

class InsertOrUpdateContext extends Context8[InsertOrUpdateRep] {
  override def append[X1, X2, X3, X4, X5, X6, X7, X8, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8, Z1, Z2, Z3, Z4, Z5, Z6, Z7, Z8](
    x: InsertOrUpdateRep[X1, X2, X3, X4, X5, X6, X7, X8],
    y: InsertOrUpdateRep[Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8]
  )(p: Plus8[X1, X2, X3, X4, X5, X6, X7, X8, Y1, Y2, Y3, Y4, Y5, Y6, Y7, Y8, Z1, Z2, Z3, Z4, Z5, Z6, Z7, Z8]): InsertOrUpdateRep[Z1, Z2, Z3, Z4, Z5, Z6, Z7, Z8] =
    new InsertOrUpdateRep[Z1, Z2, Z3, Z4, Z5, Z6, Z7, Z8] {
      override def buildParams(extract: Any => Z5): Z8 = p.plus8(x.buildParams(extract.andThen(p.takeHead5)), y.buildParams(extract.andThen(p.takeTail5)))
      override def encodeRef(rep: Z8, path: Node, index: Int, polyType: Z6): (Z7, Int) = {
        val y2 = y.encodeRef(p.takeTail8(rep), path, index, p.takeTail6(polyType))
        val x2 = x.encodeRef(p.takeHead8(rep), path, y2._2, p.takeHead6(polyType))
        (p.plus7(x2._1, y2._1), x2._2)
      }
      override def pack(u: Z4): Z8 = p.plus8(x.pack(p.takeHead4(u)), y.pack(p.takeTail4(u)))
      override def node(rep: Z8, l: List[Node], polyType: Z6): List[Node] =
        x.node(p.takeHead8(rep), y.node(p.takeTail8(rep), l, p.takeTail6(polyType)), p.takeHead6(polyType))
      override def fieldPlus(data: Z5, l: List[Any], polyType: Z6): List[Any] =
        x.fieldPlus(p.takeHead5(data), y.fieldPlus(p.takeTail5(data), l, p.takeTail6(polyType)), p.takeHead6(polyType))
    }

  override val start: InsertOrUpdateRep[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] = {
    new InsertOrUpdateRep[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] {
      override def buildParams(extract: Any => ZsgTuple0): ZsgTuple0                                        = ZsgTuple0.value
      override def encodeRef(rep: ZsgTuple0, path: Node, index: Int, polyType: ZsgTuple0): (ZsgTuple0, Int) = (ZsgTuple0.value, index)
      override def pack(u: ZsgTuple0): ZsgTuple0                                                            = ZsgTuple0.value
      override def node(rep: ZsgTuple0, l: List[Node], polyType: ZsgTuple0): List[Node]                     = l
      override def fieldPlus(data: ZsgTuple0, l: List[Any], polyType: ZsgTuple0): List[Any]                 = l
    }
  }
}

object InsertOrUpdateContext {
  val value: InsertOrUpdateContext = new InsertOrUpdateContext
}
