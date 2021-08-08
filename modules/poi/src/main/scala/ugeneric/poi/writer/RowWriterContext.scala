package org.scalax.kirito.poi.writer

import zsg.{Context5, Plus5}
import net.scalax.cpoi.content.CellDataAbs

class RowWriterContext extends Context5[RowWriterContent] {
  /*override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: RowWriterContent[X1, X2, X3], y: RowWriterContent[Y1, Y2, Y3])(
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

  override val start: RowWriterContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] = new RowWriterContent[ZsgTuple0, ZsgTuple0, ZsgTuple0] {
    override def appendColumnTitle(names: ZsgTuple0, rep: ZsgTuple0, titles: List[String]): List[String]                                       = titles
    override def appendField(tt: ZsgTuple0, name: ZsgTuple0, rep: ZsgTuple0, data: List[(String, CellDataAbs)]): List[(String, CellDataAbs)] = data
  }*/
  override def append[X1, X2, X3, X4, X5, Y1, Y2, Y3, Y4, Y5, Z1, Z2, Z3, Z4, Z5](x: RowWriterContent[X1, X2, X3, X4, X5], y: RowWriterContent[Y1, Y2, Y3, Y4, Y5])(
    p: Plus5[X1, X2, X3, X4, X5, Y1, Y2, Y3, Y4, Y5, Z1, Z2, Z3, Z4, Z5]
  ): RowWriterContent[Z1, Z2, Z3, Z4, Z5] = {
    new RowWriterContent[Z1, Z2, Z3, Z4, Z5] {
      override def appendColumnTitle(names: Z4, rep: Z5, titles: List[String]): List[String] = {
        val x2        = p.takeHead4(names)
        val y2        = p.takeTail4(names)
        val x3        = p.takeHead5(rep)
        val y3        = p.takeTail5(rep)
        val titleList = x.appendColumnTitle(x2, x3, titles)
        y.appendColumnTitle(y2, y3, titleList)
      }
      override def appendField(tt: Z3, titles: Z4, rep: Z5, data: List[(String, CellDataAbs)]): List[(String, CellDataAbs)] = {
        val x1        = p.takeHead3(tt)
        val y1        = p.takeTail3(tt)
        val x2        = p.takeHead4(titles)
        val y2        = p.takeTail4(titles)
        val x3        = p.takeHead5(rep)
        val y3        = p.takeTail5(rep)
        val titleList = x.appendField(x1, x2, x3, data)
        y.appendField(y1, y2, y3, titleList)
      }
    }
  }
}

object RowWriterContext {
  val value: RowWriterContext = new RowWriterContext
}
