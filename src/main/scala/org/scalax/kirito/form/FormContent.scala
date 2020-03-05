package org.scalax.kirito.form

import asuna.{Application3, Context3, PropertyTag1}
import cats.{Monad, StackSafeMonad}
import cats.data.Validated
import cats.implicits._
import cats.syntax.all._
import cats.instances.future._

import scala.concurrent.{ExecutionContext, Future}

abstract class FormContent[Model] {
  def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Model]]
}

object FormContent {

  def pureFromValidate[Model](validate: Validated[ErrorMessage, Model]): FormContent[Model] = new FormContent[Model] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Model]] = Future.successful(validate)
  }
  def pureFromFuture[Model](validate: Future[Validated[ErrorMessage, Model]]): FormContent[Model] = new FormContent[Model] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Model]] = validate
  }
  def pure[Model](model: Model): FormContent[Model] = pureFromValidate(Validated.valid(model))

  def apply[Model](i: Map[String, Seq[String]] => Validated[ErrorMessage, Model]): FormContent[Model] = new FormContent[Model] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Model]] = Future.successful(i(map))
  }

  implicit val monadFormContent: Monad[FormContent] = new Monad[FormContent] with StackSafeMonad[FormContent] {
    override def pure[A](x: A): FormContent[A] = FormContent.pure(x)
    override def flatMap[A, B](fa: FormContent[A])(f: A => FormContent[B]): FormContent[B] = new FormContent[B] {
      def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, B]] =
        fa.getModel(map)
          .map(s => s.map(r => f(r).getModel(map)))
          .flatMap(
            r =>
              r match {
                case Validated.Invalid(r) =>
                  Future.successful(Validated.Invalid(r))
                case Validated.Valid(r) =>
                  r
              }
          )
    }
  }

  implicit def FormContentApplicationImplicit[Model]: Application3[FormGetter, PropertyTag1[FormContent[Model], Model], String, FormContent[Model], Model] =
    new Application3[FormGetter, PropertyTag1[FormContent[Model], Model], String, FormContent[Model], Model] {
      override def application(context: Context3[FormGetter]): FormGetter[String, FormContent[Model], Model] = new FormGetter[String, FormContent[Model], Model] {
        override def getterByName(names: String, rep: FormContent[Model]): FormContent[Model] = rep
      }
    }

}
