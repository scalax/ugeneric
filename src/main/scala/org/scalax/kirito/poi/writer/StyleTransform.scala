package org.scalax.kirito.poi.writer

import net.scalax.cpoi.style.StyleTransform
import org.apache.poi.ss.usermodel.{CellStyle, Workbook}

case object TextStyle extends StyleTransform {
  override def operation(workbook: Workbook, cellStyle: CellStyle): CellStyle = {
    val format = workbook.createDataFormat.getFormat("@")
    cellStyle.setDataFormat(format)
    cellStyle
  }
}

case object DoubleStyle extends StyleTransform {
  override def operation(workbook: Workbook, cellStyle: CellStyle): CellStyle = {
    val format = workbook.createDataFormat.getFormat("0.00")
    cellStyle.setDataFormat(format)
    cellStyle
  }
}

case object IntStyle extends StyleTransform {
  override def operation(workbook: Workbook, cellStyle: CellStyle): CellStyle = {
    val format = workbook.createDataFormat.getFormat("0")
    cellStyle.setDataFormat(format)
    cellStyle
  }
}

case class Locked(lock: Boolean) extends StyleTransform {
  override def operation(workbook: Workbook, cellStyle: CellStyle): CellStyle = {
    cellStyle.setLocked(lock)
    cellStyle
  }
}

case class DateStyle(formatter: String) extends StyleTransform {
  override def operation(workbook: Workbook, cellStyle: CellStyle): CellStyle = {
    val format = workbook.createDataFormat.getFormat(formatter)
    cellStyle.setDataFormat(format)
    cellStyle
  }
}
