package org.scalax.kirito.poi.writer

import asuna.{Application3, Context3, PropertyTag1}
import cats.Contravariant
import net.scalax.cpoi.content.{CellDataAbs, CellDataImpl}
import net.scalax.cpoi.rw.CellWriter
import net.scalax.cpoi.style.StyleTransform
import scala.collection.compat._

trait CPoiRowWriter[H] {
  self =>
  type WriterType
  def title: String
  def styleTransform: List[StyleTransform]
  def writer: CellWriter[WriterType]
  def tranData(data: H): WriterType

  def wrapData(data: H): CellDataAbs = CellDataImpl(tranData(data), styleTransform)(writer)
  def changeColumnName(name: String): CPoiRowWriter[H] = new CPoiRowWriter[H] {
    override type WriterType = self.WriterType
    override def title: String                        = name
    override def styleTransform: List[StyleTransform] = self.styleTransform
    override def writer: CellWriter[self.WriterType]  = self.writer
    override def tranData(data: H): self.WriterType   = self.tranData(data)
  }
  def changeStyle(styleList: List[StyleTransform]): CPoiRowWriter[H] = new CPoiRowWriter[H] {
    override type WriterType = self.WriterType
    override def title: String                        = self.title
    override def styleTransform: List[StyleTransform] = styleList
    override def writer: CellWriter[self.WriterType]  = self.writer
    override def tranData(data: H): self.WriterType   = self.tranData(data)
  }
}

object CPoiRowWriter {
  def apply[W](name: String, style: List[StyleTransform] = List.empty)(implicit cellWriter: CellWriter[W]): CPoiRowWriter[W] = new CPoiRowWriter[W] {
    override type WriterType = W
    override def title: String                        = name
    override def styleTransform: List[StyleTransform] = style
    override def writer: CellWriter[W]                = cellWriter
    override def tranData(data: W): W                 = data
  }

  def apply[W](name: String, style: StyleTransform*)(implicit cellWriter: CellWriter[W]): CPoiRowWriter[W] = apply(name, style.to(List))

  implicit def contravariantTypeClass: Contravariant[CPoiRowWriter] = new Contravariant[CPoiRowWriter] {
    override def contramap[A, B](fa: CPoiRowWriter[A])(f: B => A): CPoiRowWriter[B] = new CPoiRowWriter[B] {
      override type WriterType = fa.WriterType
      override def title: String                        = fa.title
      override def styleTransform: List[StyleTransform] = fa.styleTransform
      override def writer: CellWriter[fa.WriterType]    = fa.writer
      override def tranData(data: B): fa.WriterType     = fa.tranData(f(data))
    }
  }

  implicit def cPoiRowWriterImplicit[H]: Application3[RowWriterContent, PropertyTag1[CPoiRowWriter[H], H], H, String, CPoiRowWriter[H]] =
    new Application3[RowWriterContent, PropertyTag1[CPoiRowWriter[H], H], H, String, CPoiRowWriter[H]] {
      override def application(context: Context3[RowWriterContent]): RowWriterContent[H, String, CPoiRowWriter[H]] = {
        new RowWriterContent[H, String, CPoiRowWriter[H]] {
          override def appendColumnTitle(names: String, rep: CPoiRowWriter[H], titles: List[String]): List[String] = rep.title :: titles
          override def appendField(tt: H, name: String, rep: CPoiRowWriter[H], data: List[(String, CellDataAbs)]): List[(String, CellDataAbs)] =
            (rep.title, rep.wrapData(tt)) :: data
        }
      }
    }

}
