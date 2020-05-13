package org.scalax.kirito.poi.reader

trait ReaderContent[T, II, Rep] extends Any {
  def getNames(name: II, rep: Rep, col: List[String]): List[String]
  def getValue(name: II, rep: Rep): RowReader[T]
}
