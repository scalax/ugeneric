package org.scalax.kirito.form

import asuna.{AsunaTuple0, Context3, Plus3}
import cats.data.Validated

import scala.concurrent.{ExecutionContext, Future}

object FormContext extends Context3[FormGetter] {
  override def append[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3](x: FormGetter[X1, X2, X3], y: FormGetter[Y1, Y2, Y3])(
    p: Plus3[X1, X2, X3, Y1, Y2, Y3, Z1, Z2, Z3]
  ): FormGetter[Z1, Z2, Z3] = {
    new FormGetter[Z1, Z2, Z3] {
      override def getterByName(names: Z1, rep: Z2): FormContent[Z3] = {
        val name1 = p.takeHead1(names)
        val name2 = p.takeTail1(names)
        val rep1  = p.takeHead2(rep)
        val rep2  = p.takeTail2(rep)
        new FormContent[Z3] {
          override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Z3]] = {
            for {
              x1 <- x.getterByName(name1, rep1).getModel(map)
              x2 <- y.getterByName(name2, rep2).getModel(map)
            } yield {
              x1.product(x2).map { case (x1, x2) => p.plus3(x1, x2) }
            }
          }
        }
      }
    }
  }

  val fu = Future.successful(Validated.valid(AsunaTuple0.value))

  override val start: FormGetter[AsunaTuple0, AsunaTuple0, AsunaTuple0] = {
    new FormGetter[AsunaTuple0, AsunaTuple0, AsunaTuple0] {
      override def getterByName(names: AsunaTuple0, rep: AsunaTuple0): FormContent[AsunaTuple0] = new FormContent[AsunaTuple0] {
        override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, AsunaTuple0]] = {
          fu
        }
      }
    }
  }
}
