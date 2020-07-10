package org.scalax.kirito.poi.reader

import zsg.{Context5, Plus3, Plus5, ZsgTuple0}
import cats.data.Validated
import net.scalax.cpoi.content.CellContentAbs

class ReaderContext extends Context5[ReaderContent] {
  override def append[X1, X2, X3, X4, X5, Y1, Y2, Y3, Y4, Y5, Z1, Z2, Z3, Z4, Z5](x: ReaderContent[X1, X2, X3, X4, X5], y: ReaderContent[Y1, Y2, Y3, Y4, Y5])(
    p: Plus5[X1, X2, X3, X4, X5, Y1, Y2, Y3, Y4, Y5, Z1, Z2, Z3, Z4, Z5]
  ): ReaderContent[Z1, Z2, Z3, Z4, Z5] = new ReaderContent[Z1, Z2, Z3, Z4, Z5] {
    override def getNames(name: Z4, rep: Z5, col: List[String]): List[String] =
      x.getNames(p.takeHead4(name), p.takeHead5(rep), y.getNames(p.takeTail4(name), p.takeTail5(rep), col))
    override def getValue(name: Z4, rep: Z5): RowReader[Z3] = {
      val xx1 = p.takeHead4(name)
      val zz1 = p.takeHead5(rep)
      val yy1 = p.takeTail4(name)
      val zz2 = p.takeTail5(rep)
      new RowReader[Z3] {
        override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, Z3] = {
          x.getValue(xx1, zz1).read(data, rowIndex).product(y.getValue(yy1, zz2).read(data, rowIndex)).map { case (i1, i2) => p.plus3(i1, i2) }
        }
      }
    }
  }

  override def start: ReaderContent[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] = new ReaderContent[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def getNames(name: ZsgTuple0, rep: ZsgTuple0, col: List[String]): List[String] = col
    override def getValue(name: ZsgTuple0, defaultValue: ZsgTuple0): RowReader[ZsgTuple0] = {
      new RowReader[ZsgTuple0] {
        override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, ZsgTuple0] = {
          Validated.valid(ZsgTuple0.value)
        }
      }
    }
  }
}

object ReaderContext {
  val value: ReaderContext = new ReaderContext
}
