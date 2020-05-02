package org.scalax.ugeneric.slick

import asuna.{Application4, TupleTag}
import asuna.macros.multiply.{AsunaMultiplyGeneric, AsunaMultiplyRepGeneric}
import asuna.macros.single.{AsunaGetterGeneric, AsunaSetterGeneric}
import org.scalax.ugeneric.slick.mutiply.{RepContext, RepNode}
import slick.ast.{Node, ProductNode}
import slick.lifted.{AbstractTable, FlatShapeLevel, MappedProjection, Shape, ShapedValue}
import slick.util.ConstArray

import scala.reflect.ClassTag

object USlick {

  def mapWithTable[Rep, Table <: AbstractTable[Data], Data, P <: TupleTag, RepType, DataType, EncodeRef, Packed1](
    table: Table
  )(
    implicit p: AsunaMultiplyGeneric.Aux[Table, Data, P],
    app: Application4[RepNode, P, RepType, DataType, EncodeRef, Packed1],
    asunaMultiplyRepGeneric: AsunaMultiplyRepGeneric[Table, Data, RepType],
    asunaGetterGeneric: AsunaGetterGeneric[Data, DataType],
    asunaSetterGeneric: AsunaSetterGeneric[Data, DataType],
    i: ClassTag[Data]
  ): MappedProjection[Data, DataType] = {
    val repType = app.application(RepContext)
    val shape = new Shape[FlatShapeLevel, Packed1, DataType, Packed1] { self =>
      override def pack(value: Packed1): Packed1                                  = value
      override def packedShape: Shape[FlatShapeLevel, Packed1, DataType, Packed1] = self
      override def buildParams(extract: Any => DataType): Packed1                 = repType.buildParams(extract)
      override def encodeRef(value: Packed, path: Node): Any                      = repType.encodeRef(value, path, 1)
      override def toNode(value: Packed): Node                                    = ProductNode(ConstArray.from(repType.node(value, List.empty)))
    }
    val rep = asunaMultiplyRepGeneric.rep(table)
    ShapedValue(repType.pack(rep), shape).<>(f = asunaSetterGeneric.setter, g = (asunaGetterGeneric.getter _).andThen(Option.apply))
  }

}
