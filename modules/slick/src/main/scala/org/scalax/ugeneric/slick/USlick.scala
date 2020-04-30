package org.scalax.ugeneric.slick

import asuna.TupleTag
import asuna.macros.multiply.AsunaMultiplyGeneric
import slick.lifted.{AbstractTable, ShapedValue}

object USlick {

  def mapWithTable[Rep, Table <: AbstractTable[Data], Data, P <: TupleTag](table: Table)(implicit p: AsunaMultiplyGeneric.Aux[Table, Data, P]): ShapedValue[_, Data] = {
    11
    22
  }

}
