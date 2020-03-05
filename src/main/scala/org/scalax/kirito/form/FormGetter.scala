package org.scalax.kirito.form

import asuna.{Application3, Context3, PropertyTag1}

abstract class FormGetter[Name, Rep, Model] {
  def getterByName(names: Name, rep: Rep): FormContent[Model]
}

abstract class FormNameGetter[Model] {
  def getterByName(names: String): FormContent[Model]
}

object FormNameGetter {

  implicit def formNameGetterApplicationImplicit[Model]: Application3[FormGetter, PropertyTag1[FormNameGetter[Model], Model], String, FormNameGetter[Model], Model] =
    new Application3[FormGetter, PropertyTag1[FormNameGetter[Model], Model], String, FormNameGetter[Model], Model] {
      override def application(context: Context3[FormGetter]): FormGetter[String, FormNameGetter[Model], Model] = new FormGetter[String, FormNameGetter[Model], Model] {
        override def getterByName(names: String, rep: FormNameGetter[Model]): FormContent[Model] = rep.getterByName(names)
      }
    }

}
