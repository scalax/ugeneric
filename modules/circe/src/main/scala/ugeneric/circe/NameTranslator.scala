package org.scalax.ugeneric.circe

trait NameTranslator extends Any {
  def tran(name: String): String
}
