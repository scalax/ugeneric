package org.scalax.kirito.poi.reader

import asuna.{AsunaTuple0, Context3, Plus3}
import cats.data.Validated
import net.scalax.cpoi.content.CellContentAbs

object ReaderContext extends Context3[ReaderContent] {
  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: ReaderContent[X1, X2, X3], y: ReaderContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): ReaderContent[Z1, Z2, Z3] = new ReaderContent[Z1, Z2, Z3] {
    override def getNames(name: Z2, rep: Z3, col: List[String]): List[String] =
      x.getNames(p.takeHead2(name), p.takeHead3(rep), y.getNames(p.takeTail2(name), p.takeTail3(rep), col))
    override def getValue(name: Z2, rep: Z3): RowReader[Z1] = {
      val xx1 = p.takeHead2(name)
      val zz1 = p.takeHead3(rep)
      val yy1 = p.takeTail2(name)
      val zz2 = p.takeTail3(rep)
      new RowReader[Z1] {
        override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, Z1] = {
          x.getValue(xx1, zz1).read(data, rowIndex).product(y.getValue(yy1, zz2).read(data, rowIndex)).map { case (i1, i2) => p.plus1(i1, i2) }
        }
      }
    }
  }

  override def start: ReaderContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] = new ReaderContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] {
    override def getNames(name: AsunaTuple0, rep: AsunaTuple0, col: List[String]): List[String] = col
    override def getValue(name: AsunaTuple0, defaultValue: AsunaTuple0): RowReader[AsunaTuple0] = {
      new RowReader[AsunaTuple0] {
        override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, AsunaTuple0] = {
          Validated.valid(AsunaTuple0.value)
        }
      }
    }
  }
}
