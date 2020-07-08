package org.scalax.kirito.poi.writer

import net.scalax.cpoi.api._
import org.apache.poi.ss.usermodel.Row

trait WriteWithData[Model] {
  self =>
  def write(row: Row, model: Model): WriteWithData[Model]
}

object WriteWithData {
  private abstract class WriteWithDataImpl[Model] extends WriteWithData[Model] {
    self =>
    def styleGen: StyleGen
    def dataMap: Model => Map[String, CellDataAbs]
    def titleMap: List[(String, Int)]

    override def write(row: Row, model: Model): WriteWithDataImpl[Model] = {
      val dataList = titleMap.flatMap {
        case (title, index) =>
          dataMap(model).get(title).map(cellData => (Option(row.getCell(index)).getOrElse(row.createCell(index)), cellData))
      }
      val newStyleGen = CPoi.multiplySet(styleGen, dataList).get
      new WriteWithDataImpl[Model] {
        override val styleGen = newStyleGen
        override val dataMap  = self.dataMap
        override val titleMap = self.titleMap
      }
    }
  }

  def apply[Model](styleGen: StyleGen, modelToMap: Model => Map[String, CellDataAbs], titleMap: List[(String, Int)]): WriteWithData[Model] = {
    val styleGen1   = styleGen
    val modelToMap1 = modelToMap
    val titleMap1   = titleMap
    new WriteWithDataImpl[Model] {
      override val styleGen = styleGen1
      override val dataMap  = modelToMap1
      override val titleMap = titleMap1
    }
  }
}
