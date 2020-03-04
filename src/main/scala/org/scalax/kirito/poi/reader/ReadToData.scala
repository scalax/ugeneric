package org.scalax.kirito.poi.reader

import cats.data.Validated
import org.apache.poi.ss.usermodel.Row

trait ReadToData[T] extends Any {
  def read(row: Row, rowIndex: Int): Validated[rowMessageContent, T]
}
