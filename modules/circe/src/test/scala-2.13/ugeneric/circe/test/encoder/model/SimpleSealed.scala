package ugeneric.circe.test.encoder.model

package object SimpleSealed {
  sealed trait ParentTrait
  case class Test01(i1: String, i2: Int)    extends ParentTrait
  case object Test02                        extends ParentTrait
  case class Test03(i1: String, i2: String) extends ParentTrait
}
