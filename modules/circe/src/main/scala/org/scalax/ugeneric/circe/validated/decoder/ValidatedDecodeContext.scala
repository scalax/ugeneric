package org.scalax.ugeneric.circe.decoder

import asuna.{AsunaTuple0, Context4, Plus4}
import cats.data.Validated
import io.circe.ACursor

object ValidatedDecodeContext extends Context4[ValidatedDecodeContent] {

  override def append[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4](x: ValidatedDecodeContent[X1, X2, X3, X4], y: ValidatedDecodeContent[Y1, Y2, Y3, Y4])(
    p: Plus4[X1, X2, X3, X4, Y1, Y2, Y3, Y4, Z1, Z2, Z3, Z4]
  ): ValidatedDecodeContent[Z1, Z2, Z3, Z4] = new ValidatedDecodeContent[Z1, Z2, Z3, Z4] {
    override def getValue(name: Z2, defaultValue: Z3, rep: Z4): ValidatedDecoder[Z1] = {
      val xx1 = p.takeHead2(name)
      val xx2 = p.takeHead3(defaultValue)
      val zz1 = p.takeHead4(rep)
      val yy1 = p.takeTail2(name)
      val yy2 = p.takeTail3(defaultValue)
      val zz2 = p.takeTail4(rep)
      new ValidatedDecoder[Z1] {
        override def getValue(json: ACursor): Validated[errorMessage, Z1] = {
          val x3 = x.getValue(xx1, xx2, zz1).getValue(json)
          val y3 = y.getValue(yy1, yy2, zz2).getValue(json)
          x3.product(y3).map { case (xx3, yy3) => p.plus1(xx3, yy3) }
        }
      }
    }
  }

  override def start: ValidatedDecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] =
    new ValidatedDecodeContent[AsunaTuple0, AsunaTuple0, AsunaTuple0, AsunaTuple0] {
      override def getValue(name: AsunaTuple0, defaultValue: AsunaTuple0, rep: AsunaTuple0): ValidatedDecoder[AsunaTuple0] = {
        new ValidatedDecoder[AsunaTuple0] {
          override def getValue(json: ACursor): Validated[errorMessage, AsunaTuple0] = Validated.valid(AsunaTuple0.value)
        }
      }
    }

}
