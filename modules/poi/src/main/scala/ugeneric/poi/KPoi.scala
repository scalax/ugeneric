package org.scalax.kirito.poi

import zsg.{Application4, Application5}
import zsg.macros.multiply.{ZsgMultiplyGeneric, ZsgMultiplyRepGeneric}
import zsg.macros.single.{ZsgGeneric, ZsgGetterGeneric, ZsgLabelledGeneric, ZsgSetterGeneric}
import cats.{Contravariant, Functor, Semigroupal}
import cats.data.Validated
import cats.syntax.all._
import cats.instances.all._
import net.scalax.cpoi.api._
import net.scalax.cpoi.utils.compat.CollectionCompat
import org.apache.poi.ss.usermodel.{Row, Workbook}
import org.scalax.kirito.poi.reader.{rowMessageContent, ReadToData}
import org.scalax.kirito.poi.writer.{RowWriterContent, RowWriterContext, TextStyle, WriteWithData}

import scala.collection.compat._

object KPoi {

  abstract class CPoiRowWriter[Model] {
    self =>
    def keys: List[String]
    def write(model: Model): List[(String, CellDataAbs)]

    def writeTitleRow(row: Row): WriteWithData[Model] = {
      val styleGen = CPoi.newStyleGen
      import writers._
      val maxIndex = if (row.getLastCellNum >= 0) row.getLastCellNum.toInt else 0
      val titleMap = self.keys.zipWithIndex.map {
        case (r, index) =>
          val autalIndex = maxIndex + index
          (r, autalIndex)
      }
      val newStyleGen = CPoi
        .multiplySet(
          styleGen,
          titleMap.map {
            case (r, autalIndex) =>
              val titleWrap = CPoi.wrapData(r).addTransform(TextStyle)
              (Option(row.getCell(autalIndex)).getOrElse(row.createCell(autalIndex)), titleWrap)
          }
        )
        .get

      WriteWithData.apply(styleGen = newStyleGen, modelToMap = (self.write _).andThen(Map.from), titleMap = titleMap)
    }
  }

  object CPoiRowWriter {
    def apply[Model](implicit i: CPoiRowWriter[Model]): CPoiRowWriter[Model] = i
    def apply[Model](keys: List[String], func: Model => List[(String, CellDataAbs)]): CPoiRowWriter[Model] = {
      val keys1 = keys
      new CPoiRowWriter[Model] {
        override def keys: List[String]                               = keys1
        override def write(model: Model): List[(String, CellDataAbs)] = func(model)
      }
    }

    implicit val contravariantTypeClass: Contravariant[CPoiRowWriter] = new Contravariant[CPoiRowWriter] {
      override def contramap[A, B](fa: CPoiRowWriter[A])(f: B => A): CPoiRowWriter[B] = CPoiRowWriter(fa.keys, model => fa.write(f(model)))

    }

    implicit val semigroupalTypeClass: Semigroupal[CPoiRowWriter] = new Semigroupal[CPoiRowWriter] {
      override def product[A, B](fa: CPoiRowWriter[A], fb: CPoiRowWriter[B]): CPoiRowWriter[(A, B)] =
        CPoiRowWriter(fa.keys ::: fb.keys, model => fa.write(model._1) ::: fb.write(model._2))
    }

  }

  def writeCaseClassWithTable[Table, Model, Rep, R, ModelR, Obj, Name](table: Table)(
    implicit ll: ZsgMultiplyGeneric.Aux[Table, Model, R],
    p: ZsgGeneric.Aux[Model, ModelR],
    app: Application5[RowWriterContent, RowWriterContext, R, ModelR, Obj, Name, Rep],
    repGeneric: ZsgMultiplyRepGeneric[Table, Model, Rep],
    cv1: ZsgLabelledGeneric[Model, Name],
    cv2: ZsgGetterGeneric[Model, Obj]
  ): CPoiRowWriter[Model] = {
    val names              = cv1.names
    val applicationEncoder = app.application
    val titles             = applicationEncoder.appendColumnTitle(names, repGeneric.rep(table), List.empty)
    new CPoiRowWriter[Model] {
      def keys: List[String] = titles
      def write(model: Model): List[(String, CellDataAbs)] =
        applicationEncoder.appendField(cv2.getter(model), names, repGeneric.rep(table), List.empty)
    }
  }

  def renderExcel[Model](workbook: Workbook)(data: List[Model])(func: CPoiRowWriter[Model]): Workbook = {
    val sheet    = workbook.createSheet("查询结果")
    val row0     = sheet.createRow(0)
    val styleGen = CPoi.newMutableStyleGen
    import writers._
    CPoi.multiplySet(styleGen, func.keys.zipWithIndex.map {
      case (r, index) =>
        val titleWrap = CPoi.wrapData(r).addTransform(TextStyle)
        (row0.createCell(index), titleWrap)
    })
    CollectionCompat.seqToLazyList(data).zipWithIndex.foreach {
      case (rowData, index) =>
        val row    = sheet.createRow(index + 1)
        val rowMap = Map.from(func.write(rowData))
        val dataList = func.keys.zipWithIndex.flatMap {
          case (title, index) =>
            rowMap.get(title).flatMap(cellData => Option((row.createCell(index), cellData)))
        }
        CPoi.multiplySet(styleGen, dataList)
    }
    workbook
  }

  abstract class CPoiRowReader[Model] {
    self =>
    def keys: List[String]
    def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, Model]

    def readTitleRow(row: Row): ReadToData[Model] = {
      val optMap = for {
        i <- 0 to row.getLastCellNum
      } yield {
        val content = CPoi.wrapCell(Option(row.getCell(i)))
        import immutableReaders._
        content.tryValue[String].toOption.flatMap { (r) => self.keys.find(item => r == item).map(titleItem => (titleItem, i)) }
      }
      val titleMap = Map.from(optMap.flatMap(_.to(List)))
      new ReadToData[Model] {
        override def read(dataRow: Row, rowIndex: Int): Validated[rowMessageContent, Model] = {
          val cells = Map.from(for (i <- 0 to row.getLastCellNum) yield {
            (i, CPoi.wrapCell(Option(dataRow).flatMap(r => Option(r.getCell(i)))))
          })
          val dataMap = titleMap
            .map {
              case (key, value) =>
                (key, cells.get(value))
            }
            .collect { case (key, Some(value)) => (key, value) }
          self.read(dataMap, rowIndex)
        }
      }
    }

  }

  object CPoiRowReader {
    def apply[Model](implicit i: CPoiRowReader[Model]): CPoiRowReader[Model] = i
    def apply[Model](keys: List[String], func: (Map[String, CellContentAbs], Int) => Validated[rowMessageContent, Model]): CPoiRowReader[Model] = {
      val keys1 = keys
      new CPoiRowReader[Model] {
        override def keys: List[String]                                                                          = keys1
        override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, Model] = func(data, rowIndex)
      }
    }

    implicit val functorTypeClass: Functor[CPoiRowReader] = new Functor[CPoiRowReader] {
      override def map[A, B](fa: CPoiRowReader[A])(f: A => B): CPoiRowReader[B] = CPoiRowReader(fa.keys, (data, rowIndex) => fa.read(data, rowIndex).map(f))
    }

    implicit val semigroupalTypeClass: Semigroupal[CPoiRowReader] = new Semigroupal[CPoiRowReader] {
      override def product[A, B](fa: CPoiRowReader[A], fb: CPoiRowReader[B]): CPoiRowReader[(A, B)] =
        CPoiRowReader(fa.keys ::: fb.keys, (data, rowIndex) => fa.read(data, rowIndex).product(fb.read(data, rowIndex)))
    }
  }

  def readCaseClassWithTable[Table, Model, R, ModelR, Prop, Nam, Rep](table: Table)(
    implicit ll: ZsgMultiplyGeneric.Aux[Table, Model, R],
    p: ZsgGeneric.Aux[Model, ModelR],
    app: Application5[reader.ReaderContent, reader.ReaderContext, R, ModelR, Prop, Nam, Rep],
    repGeneric: ZsgMultiplyRepGeneric[Table, Model, Rep],
    cv1: ZsgLabelledGeneric[Model, Nam],
    cv3: ZsgSetterGeneric[Model, Prop]
  ): CPoiRowReader[Model] = {
    val i   = app.application
    val rep = repGeneric.rep(table)
    new CPoiRowReader[Model] {
      override def keys: List[String] = i.getNames(cv1.names, rep, List.empty)
      override def read(data: Map[String, CellContentAbs], rowIndex: Int): Validated[rowMessageContent, Model] =
        i.getValue(cv1.names, rep).map(mm => cv3.setter(mm)).read(data, rowIndex)
    }
  }

  /*def readCaseClassWithTable11[Model] = new {
    def table[Table](table: Table) = new {
      def app[R <: TupleTag, Prop, Nam, Rep](
        implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R]
      ) = new {
        def app1(implicit app: Application3[reader.ReaderContent, R, Prop, Nam, Rep]) = app
      }
    }
  }*/

  def readExcel[Model](workbook: Workbook, func: CPoiRowReader[Model]): Validated[rowMessageContent, List[Model]] = {
    val sheet       = workbook.getSheetAt(0)
    val titleRowOpt = Option(sheet.getRow(0))
    val titleList = titleRowOpt
      .map(titleRow =>
        for {
          i <- 0 to titleRow.getLastCellNum
        } yield {
          val content = CPoi.wrapCell(Option(titleRow.getCell(i)))
          import immutableReaders._
          content.tryValue[String].toOption.flatMap { (r) => func.keys.find(item => r == item).map(titleItem => (titleItem, i)) }
        }
      )
      .getOrElse(List.empty)
    val titleMap = Map.from(titleList.flatMap(_.to(List)))
    val rowMap = for (rowIndex <- 1 to sheet.getLastRowNum) yield {
      val row = sheet.getRow(rowIndex)
      val cells = Map.from(for (i <- 0 to row.getLastCellNum) yield {
        (i, CPoi.wrapCell(Option(row).flatMap(r => Option(r.getCell(i)))))
      })

      val dataMap = titleMap
        .map {
          case (key, value) =>
            (key, cells.get(value))
        }
        .collect { case (key, Some(value)) => (key, value) }
      func.read(dataMap, rowIndex)
    }
    cats.Traverse[List].traverse[({ type I[II] = Validated[rowMessageContent, II] })#I, Validated[rowMessageContent, Model], Model](rowMap.to(List))(identity)
  }

}
