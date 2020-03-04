package org.scalax.kirito.form

import cats.Semigroup
import scala.collection.compat._

case class ErrorMessage(msg: Map[String, Set[String]]) {
  def total: Set[String] = Set.from(msg.values.flatten)
}

object ErrorMessage {
  def apply(key: String, msg: String): ErrorMessage = ErrorMessage(Map((key, Set(msg))))

  implicit val errorMessageSemigroupal: Semigroup[ErrorMessage] = new Semigroup[ErrorMessage] {
    override def combine(x: ErrorMessage, y: ErrorMessage): ErrorMessage = {
      ErrorMessage(x.msg.foldLeft(y.msg) {
        case (b, (name, value)) =>
          b.get(name) match {
            case Some(msgs) => b + ((name, value ++ msgs))
            case _          => b + ((name, value))
          }
      })
    }
  }
}
