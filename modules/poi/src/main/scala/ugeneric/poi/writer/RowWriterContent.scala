package org.scalax.kirito.poi.writer

import cats._
import cats.syntax.all._
import net.scalax.cpoi.content.{CellDataAbs, CellDataImpl}
import net.scalax.cpoi.rw.CellWriter
import zsg.PropertyTag
import zsg.macros.utils.PlaceHolder

trait RowWriterContent[RepType, ModelPro, Pro, Name, Rep] {
  def appendColumnTitle(names: Name, rep: Rep, titles: List[String]): List[String]
  def appendField(tt: Pro, name: Name, rep: Rep, data: List[(String, CellDataAbs)]): List[(String, CellDataAbs)]
}

object RowWriterContent {
  implicit def rowWriterContentPlaceHolderImplicit[H](
    implicit writer: CellWriter[H]
  ): RowWriterContent[PropertyTag[PlaceHolder], PropertyTag[H], H, String, PlaceHolder] =
    new RowWriterContent[PropertyTag[PlaceHolder], PropertyTag[H], H, String, PlaceHolder] {
      override def appendColumnTitle(names: String, rep: PlaceHolder, titles: List[String]): List[String] = names :: titles
      override def appendField(tt: H, name: String, rep: PlaceHolder, m: List[(String, CellDataAbs)]): List[(String, CellDataAbs)] =
        (name, CellDataImpl(tt, List.empty)(writer)) :: m
    }
}
