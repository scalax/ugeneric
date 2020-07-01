package org.scalax.ugeneric.circe.decoder

import cats.Semigroup
import cats.data.Validated
import io.circe._
import ugeneric.circe.{UCirce, VersionCompat}

sealed trait ValidateResult[+T]
case class errorMessage(fields: List[ErrorField]) extends ValidateResult[Nothing] {
  def total: Set[String]                    = fields.foldLeft(Set.empty[String]) { (start, item) => start ++ item.messages }
  def addPrefix(name: String): errorMessage = this.copy(fields = this.fields.map(_.addPrefix(name)))
}
case class responseBody[T](body: T) extends ValidateResult[T]

object ValidateResult {
  object EmptyTable
  implicit def circeEncoder[T](implicit bodyEncoder: Encoder[T]): VersionCompat.ObjectEncoderType[ValidateResult[T]] = UCirce.encodeSealed(implicit c => _.encodeSealed)
  implicit def circeDecoder[T](implicit bodyDecoder: Decoder[T]): Decoder[ValidateResult[T]]                         = UCirce.decodeSealed(implicit c => _.decodeSealed)
}

object errorMessage {
  def build(path: String, message: String): errorMessage      = errorMessage(List(ErrorField(List(PropertyNamePath(path)), Set(message))))
  def build(path: Int, message: String): errorMessage         = errorMessage(List(ErrorField(List(IndexPath(path)), Set(message))))
  def build(path: String, message: Set[String]): errorMessage = errorMessage(List(ErrorField(List(PropertyNamePath(path)), message)))
  def build(path: Int, message: Set[String]): errorMessage    = errorMessage(List(ErrorField(List(IndexPath(path)), message)))

  case class ErrorMessageImpl(fields: List[ErrorField], total: Set[String])
  object ErrorMessageImpl {
    implicit val circeEncoder1: Encoder[ErrorMessageImpl] = UCirce.encodeCaseClass(implicit c => _.encodeCaseClass)
  }
  object EmptyTable
  implicit val circeEncoder: Encoder[errorMessage] = Encoder[ErrorMessageImpl].contramap(err => ErrorMessageImpl(err.fields, err.total))
  implicit val circeDecoder: Decoder[errorMessage] = UCirce.decodeCaseClass(implicit c => _.decodeCaseClass)

  implicit def validateCirceEncoder[T](implicit bodyEncoder: Encoder[T]): Encoder[Validated[errorMessage, T]] =
    Encoder[ValidateResult[T]].contramap { (s) =>
      s match {
        case Validated.Invalid(e)  => e
        case Validated.Valid(body) => responseBody(body)
      }
    }

  implicit def validateCirceDecoder[T](implicit bodyEncoder: Decoder[T]): Decoder[Validated[errorMessage, T]] =
    Decoder[ValidateResult[T]].map { (s) =>
      s match {
        case e: errorMessage    => Validated.Invalid(e)
        case responseBody(body) => Validated.Valid(body)
      }
    }

  implicit val semigroupTypeClass: Semigroup[errorMessage] = Semigroup.instance[errorMessage] { (x: errorMessage, y: errorMessage) =>
    errorMessage(
      fields = x.fields.foldLeft(y.fields) { (yfield, item) =>
        if (yfield.exists(p => item.path == p.path)) {
          yfield.map(r => if (item.path == r.path) ErrorField(item.path, item.messages ++ r.messages) else r)
        } else item :: yfield
      }
    )
  }
}

object responseBody {
  implicit def circeEncoder[T](implicit bodyEncoder: Encoder[T]): VersionCompat.ObjectEncoderType[responseBody[T]] =
    UCirce.encodeCaseClass(implicit c => _.encodeCaseClass)
  implicit def circeDecoder[T](implicit bodyDecoder: Decoder[T]): Decoder[responseBody[T]] = UCirce.decodeCaseClass(implicit c => _.decodeCaseClass)
}
