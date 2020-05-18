package org.scalax.ugeneric.slick

import zsg.Application6
import zsg.macros.multiply.{AsunaMultiplyGeneric, AsunaMultiplyRepGeneric}
import zsg.macros.single.{AsunaGeneric, AsunaGetterGeneric, AsunaSetterGeneric}
import org.scalax.ugeneric.slick.mutiply.{RepContext, RepNode}
import slick.ast.{MappedScalaType, Node, ProductNode, TypeMapping}
import slick.lifted.{AbstractTable, FlatShapeLevel, ProvenShape, Shape, ShapedValue}
import slick.util.{ConstArray, ProductWrapper, TupleSupport}

import scala.collection.compat._
import scala.reflect.ClassTag

object USlick {

  def mapWithTable[Rep, HListDataType, Table <: AbstractTable[Data], Data, P, RepType, DataType, EncodeRef, Packed1](
    table: Table
  )(
    implicit p: AsunaMultiplyGeneric.Aux[Table, Data, P],
    modelGeneric: AsunaGeneric.Aux[Data, HListDataType],
    app: Application6[RepNode, P, HListDataType, RepType, DataType, EncodeRef, Packed1],
    asunaMultiplyRepGeneric: AsunaMultiplyRepGeneric[Table, Data, RepType],
    asunaGetterGeneric: AsunaGetterGeneric[Data, DataType],
    asunaSetterGeneric: AsunaSetterGeneric[Data, DataType],
    i: ClassTag[Data]
  ): ProvenShape[Data] = {
    val repType = app.application(RepContext)
    val shape = new Shape[FlatShapeLevel, Packed1, Data, Packed1] { self =>
      override def pack(value: Packed1): Packed1                              = value
      override def packedShape: Shape[FlatShapeLevel, Packed1, Data, Packed1] = self
      override def buildParams(extract: Any => Data): Packed1                 = repType.buildParams(extract.andThen(asunaGetterGeneric.getter))
      override def encodeRef(value: Packed, path: Node): Any                  = repType.encodeRef(value, path, 1)
      override def toNode(value: Packed): Node = {
        def toBase(v: Any)   = new ProductWrapper(repType.fieldPlus(asunaGetterGeneric.getter(v.asInstanceOf[Data]), List.empty).to(IndexedSeq))
        def toMapped(v: Any) = asunaSetterGeneric.setter(repType.fieldTail(TupleSupport.buildIndexedSeq(v.asInstanceOf[Product]).to(List))._1)
        TypeMapping(ProductNode(ConstArray.from(repType.node(value, List.empty))), MappedScalaType.Mapper(toBase, toMapped, None), i)
      }
    }
    val rep = asunaMultiplyRepGeneric.rep(table)
    ShapedValue(repType.pack(rep), shape)
  }

}
