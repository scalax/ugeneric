package org.scalax.ugeneric.slick

import zsg.ApplicationX6
import zsg.macros.multiply.{ZsgMultiplyGeneric, ZsgMultiplyRepGeneric}
import zsg.macros.single.{ZsgGeneric, ZsgGetterGeneric, ZsgSetterGeneric}
import org.scalax.ugeneric.slick.mutiply.{RepContext, RepNode}
import slick.ast.{MappedScalaType, Node, ProductNode, TypeMapping}
import slick.lifted.{AbstractTable, FlatShapeLevel, ProvenShape, Shape, ShapedValue}
import slick.util.{ConstArray, ProductWrapper, TupleSupport}

import scala.collection.compat._
import scala.reflect.ClassTag

object USlick {

  def mapWithTable[Data, Rep, HListDataType, Table <: AbstractTable[Data], P, RepType, DataType, EncodeRef, Packed1](
    table: Table
  )(
    implicit p: ZsgMultiplyGeneric.Aux[Table, Data, P],
    modelGeneric: ZsgGeneric.Aux[Data, HListDataType],
    app: ApplicationX6[RepNode, RepContext, P, HListDataType, RepType, DataType, EncodeRef, Packed1],
    zsgMultiplyRepGeneric: ZsgMultiplyRepGeneric[Table, Data, RepType],
    zsgGetterGeneric: ZsgGetterGeneric[Data, DataType],
    zsgSetterGeneric: ZsgSetterGeneric[Data, DataType],
    i: ClassTag[Data]
  ): ProvenShape[Data] = {
    val repType = app.application(RepContext.value)
    val shape = new Shape[FlatShapeLevel, Packed1, Data, Packed1] { self =>
      override def pack(value: Packed1): Packed1                              = value
      override def packedShape: Shape[FlatShapeLevel, Packed1, Data, Packed1] = self
      override def buildParams(extract: Any => Data): Packed1                 = repType.buildParams(extract.andThen(zsgGetterGeneric.getter))
      override def encodeRef(value: Packed, path: Node): Any                  = repType.encodeRef(value, path, 1)
      override def toNode(value: Packed): Node = {
        def toBase(v: Any)   = new ProductWrapper(repType.fieldPlus(zsgGetterGeneric.getter(v.asInstanceOf[Data]), List.empty).to(IndexedSeq))
        def toMapped(v: Any) = zsgSetterGeneric.setter(repType.fieldTail(TupleSupport.buildIndexedSeq(v.asInstanceOf[Product]).to(List))._1)
        TypeMapping(ProductNode(ConstArray.from(repType.node(value, List.empty))), MappedScalaType.Mapper(toBase, toMapped, None), i)
      }
    }
    val rep = zsgMultiplyRepGeneric.rep(table)
    ShapedValue(repType.pack(rep), shape)
  }

}
