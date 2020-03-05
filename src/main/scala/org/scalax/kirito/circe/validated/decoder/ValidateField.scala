package org.scalax.kirito.circe.decoder

import io.circe._

sealed trait Path
object Path {
  implicit val circeEncoder: Encoder[Path] = Encoder.instance { (path: Path) =>
    path match {
      case IndexPath(r)          => Json.fromInt(r)
      case PropertyNamePath(pro) => Json.fromString(pro)
    }
  }

  implicit val circeDecoder: Decoder[Path] = Decoder.instance[Path] { (h) =>
    h.focus match {
      case Some(r) if r.isNumber => r.as[Int].map(IndexPath)
      case Some(r) if r.isString => r.as[String].map(PropertyNamePath)
      case _                     => Left(DecodingFailure("不正确的路径格式", h.history))
    }
  }
}

case class IndexPath(index: Int)          extends Path
case class PropertyNamePath(name: String) extends Path
