package org.scalax.ugeneric.circe

object VersionCompat {

  type ObjectEncoderType[T] = io.circe.Encoder.AsObject[T]
  val ObjectEncoderValue = io.circe.Encoder.AsObject

  class CPoiDone
  val CPoiDone: CPoiDone = new CPoiDone

}
