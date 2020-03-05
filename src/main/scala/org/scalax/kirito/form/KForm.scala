package org.scalax.kirito.form

import asuna.macros.multiply.{AsunaMultiplyGeneric, AsunaMultiplyRepGeneric}
import asuna.macros.single.{AsunaLabelledGeneric, AsunaSetterGeneric}
import asuna.{Application3, TupleTag}
import cats.syntax.all._

object KForm {

  def validatedBindFormWithTable[Table, Model, R <: TupleTag, Prop, Nam, DefVal, Rep](table: Table)(
    implicit ll: AsunaMultiplyGeneric.Aux[Table, Model, R],
    app: Application3[FormGetter, R, Nam, Rep, Prop],
    repGeneric: AsunaMultiplyRepGeneric[Table, Model, Rep],
    cv1: AsunaLabelledGeneric[Model, Nam],
    cv3: AsunaSetterGeneric[Model, Prop]
  ): FormContent[Model] = {
    app.application(FormContext).getterByName(cv1.names, repGeneric.rep(table)).map(cv3.setter)
  }

}
