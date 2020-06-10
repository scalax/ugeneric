package ugeneric.circe.test.decoder.model

object NamedSealed {
  sealed trait ParentTrait2
  case class Test01(i1: String, i2: Int)        extends ParentTrait2
  case object Test02                            extends ParentTrait2
  case class ParentTest03(i1: String, i2: Long) extends ParentTrait2
  case class ParentTest04(i1: String, i2: String)
}
