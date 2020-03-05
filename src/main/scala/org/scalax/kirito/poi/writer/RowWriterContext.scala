package org.scalax.kirito.poi.writer

import asuna.{AsunaTuple0, Context3, Plus3}
import net.scalax.cpoi.content.CellDataAbs

object RowWriterContext extends Context3[RowWriterContent] {
  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: RowWriterContent[X1, X2, X3], y: RowWriterContent[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): RowWriterContent[Z1, Z2, Z3] = {
    new RowWriterContent[Z1, Z2, Z3] {
      override def appendColumnTitle(names: Z2, rep: Z3, titles: List[String]): List[String] = {
        val x2        = p.takeHead2(names)
        val y2        = p.takeTail2(names)
        val x3        = p.takeHead3(rep)
        val y3        = p.takeTail3(rep)
        val titleList = x.appendColumnTitle(x2, x3, titles)
        y.appendColumnTitle(y2, y3, titleList)
      }
      override def appendField(tt: Z1, titles: Z2, rep: Z3, data: List[(String, CellDataAbs)]): List[(String, CellDataAbs)] = {
        val x1        = p.takeHead1(tt)
        val y1        = p.takeTail1(tt)
        val x2        = p.takeHead2(titles)
        val y2        = p.takeTail2(titles)
        val x3        = p.takeHead3(rep)
        val y3        = p.takeTail3(rep)
        val titleList = x.appendField(x1, x2, x3, data)
        y.appendField(y1, y2, y3, titleList)
      }
    }
  }

  override def start: RowWriterContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] = new RowWriterContent[AsunaTuple0, AsunaTuple0, AsunaTuple0] {
    override def appendColumnTitle(names: AsunaTuple0, rep: AsunaTuple0, titles: List[String]): List[String]                                       = titles
    override def appendField(tt: AsunaTuple0, name: AsunaTuple0, rep: AsunaTuple0, data: List[(String, CellDataAbs)]): List[(String, CellDataAbs)] = data
  }
}
