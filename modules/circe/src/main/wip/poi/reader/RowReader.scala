package org.scalax.kirito.poi.reader

import cats.data.Validated
import cats.Functor
import io.circe.{Decoder, Encoder}
import net.scalax.cpoi.api._
import org.scalax.ugeneric.circe.UCirce

case class RowMessage(rowNum: Int, fieldName: String, message: String)

object RowMessage {
  implicit val circeEncoder: Encoder.AsObject[RowMessage] = UCirce.encodeCaseClass
  implicit val circeDecoder: Decoder[RowMessage]          = UCirce.decodeCaseClass
}

trait RowReader[D] extends Any {
  def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, D]
}

object RowReader {
  implicit val rowReaderTypeClass: Functor[RowReader] = new Functor[RowReader] {
    override def map[A, B](fa: RowReader[A])(f: A => B): RowReader[B] = new RowReader[B] {
      override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, B] = fa.read(data, rowIndex).map(f)
    }
  }

}
