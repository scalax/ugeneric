package org.scalax.kirito.circe

trait NameTranslator extends Any {
  def tran(name: String): String
}
