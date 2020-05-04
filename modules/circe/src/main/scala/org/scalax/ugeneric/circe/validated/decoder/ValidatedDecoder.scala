package org.scalax.ugeneric.circe.decoder

import cats.Applicative
import cats.data.Validated
import io.circe._

trait ValidatedDecoder[T] extends Any {
  self =>
  def getValue(json: ACursor): Validated[errorMessage, T]
  def fromJson(json: Json): Validated[errorMessage, T] = {
    getValue(json.hcursor)
  }
  def addPrefix(i: String): ValidatedDecoder[T] = new ValidatedDecoder[T] {
    override def getValue(json: ACursor): Validated[errorMessage, T] = {
      self.getValue(json).leftMap(_.addPrefix(i))
    }
  }
}

object ValidatedDecoder {

  def apply[T](implicit validatedDecoder: ValidatedDecoder[T]): ValidatedDecoder[T] = validatedDecoder

  implicit val applicativeTypeClass: Applicative[ValidatedDecoder] = new Applicative[ValidatedDecoder] {
    override def map[A, B](fa: ValidatedDecoder[A])(f: A => B): ValidatedDecoder[B] = new ValidatedDecoder[B] {
      override def getValue(json: ACursor): Validated[errorMessage, B] = fa.getValue(json).map(f)
    }
    override def pure[A](x: A): ValidatedDecoder[A] = new ValidatedDecoder[A] {
      override def getValue(json: ACursor): Validated[errorMessage, A] = Validated.valid(x)
    }
    override def ap[A, B](ff: ValidatedDecoder[A => B])(fa: ValidatedDecoder[A]): ValidatedDecoder[B] = new ValidatedDecoder[B] {
      override def getValue(json: ACursor): Validated[errorMessage, B] = {
        val aToB = ff.getValue(json)
        val a    = fa.getValue(json)
        aToB.andThen(r => a.map(r))
      }
    }
  }

}
