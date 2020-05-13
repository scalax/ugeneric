package org.scalax.ugeneric.circe.decoder

import asuna.{Context6, Plus6, ZsgTuple0}
import cats.data.Validated
import io.circe.ACursor

object ValidatedDecodeContext extends Context6[ValidatedDecodeContent] {

  override def append[X1, X2, X3, X4, X5, X6, Y1, Y2, Y3, Y4, Y5, Y6, Z1, Z2, Z3, Z4, Z5, Z6](
    x: ValidatedDecodeContent[X1, X2, X3, X4, X5, X6],
    y: ValidatedDecodeContent[Y1, Y2, Y3, Y4, Y5, Y6]
  )(p: Plus6[X1, X2, X3, X4, X5, X6, Y1, Y2, Y3, Y4, Y5, Y6, Z1, Z2, Z3, Z4, Z5, Z6]): ValidatedDecodeContent[Z1, Z2, Z3, Z4, Z5, Z6] = {
    new ValidatedDecodeContent[Z1, Z2, Z3, Z4, Z5, Z6] {
      override def getValue(name: Z4, defaultValue: Z5, rep: Z6): ValidatedDecoder[Z3] = {
        val xx1 = p.takeHead4(name)
        val xx2 = p.takeHead5(defaultValue)
        val zz1 = p.takeHead6(rep)
        val yy1 = p.takeTail4(name)
        val yy2 = p.takeTail5(defaultValue)
        val zz2 = p.takeTail6(rep)
        new ValidatedDecoder[Z3] {
          override def getValue(json: ACursor): Validated[errorMessage, Z3] = {
            val x3 = x.getValue(xx1, xx2, zz1).getValue(json)
            val y3 = y.getValue(yy1, yy2, zz2).getValue(json)
            x3.product(y3).map { case (xx3, yy3) => p.plus3(xx3, yy3) }
          }
        }
      }
    }
  }

  override val start: ValidatedDecodeContent[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] = {
    new ValidatedDecodeContent[ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0, ZsgTuple0] {
      override def getValue(name: ZsgTuple0, defaultValue: ZsgTuple0, rep: ZsgTuple0): ValidatedDecoder[ZsgTuple0] = {
        new ValidatedDecoder[ZsgTuple0] {
          override def getValue(json: ACursor): Validated[errorMessage, ZsgTuple0] = Validated.valid(ZsgTuple0.value)
        }
      }
    }
  }
}
