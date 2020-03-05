package org.scalax.kirito.poi.writer

import asuna.macros.utils.PlaceHolder
import asuna.{Application3, Context3, PropertyTag1}
import cats._
import cats.syntax.all._
import net.scalax.cpoi.content.{CellDataAbs, CellDataImpl}
import net.scalax.cpoi.rw.CellWriter

trait RowWriterContent[Pro, Name, Rep] {
  def appendColumnTitle(names: Name, rep: Rep, titles: List[String]): List[String]
  def appendField(tt: Pro, name: Name, rep: Rep, data: List[(String, CellDataAbs)]): List[(String, CellDataAbs)]
}

object RowWriterContent {
  implicit def rowWriterContentPlaceHolderImplicit[H](
    implicit writer: CellWriter[H]
  ): Application3[RowWriterContent, PropertyTag1[PlaceHolder, H], H, String, PlaceHolder] =
    new Application3[RowWriterContent, PropertyTag1[PlaceHolder, H], H, String, PlaceHolder] {
      override def application(context: Context3[RowWriterContent]): RowWriterContent[H, String, PlaceHolder] = new RowWriterContent[H, String, PlaceHolder] {
        override def appendColumnTitle(names: String, rep: PlaceHolder, titles: List[String]): List[String] = names :: titles
        override def appendField(tt: H, name: String, rep: PlaceHolder, m: List[(String, CellDataAbs)]): List[(String, CellDataAbs)] =
          (name, CellDataImpl(tt, List.empty)(writer)) :: m
      }
    }
}
