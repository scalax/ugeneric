package ugeneric.circe.test.encoder

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import io.circe.syntax._
import ugeneric.circe.test.encoder.model.AssertSimpleSealed
import ugeneric.circe.test.encoder.model.SimpleSealed

class TestUCirceSealedEncoder extends AnyFunSpec with Matchers {

  val test01: SimpleSealed.ParentTrait = SimpleSealed.Test01(i1 = "pro1", i2 = 2)
  val test02: SimpleSealed.ParentTrait = SimpleSealed.Test02
  val test03: SimpleSealed.ParentTrait = SimpleSealed.Test03(i1 = "pro1-test03", i2 = "4")

  val assertTest01: AssertSimpleSealed.ParentTrait = AssertSimpleSealed.Test01(i1 = "pro1", i2 = 2)
  val assertTest02: AssertSimpleSealed.ParentTrait = AssertSimpleSealed.Test02
  val assertTest03: AssertSimpleSealed.ParentTrait = AssertSimpleSealed.Test03(i1 = "pro1-test03", i2 = "4")

  object TestModelContent {
    import io.circe.generic.auto._
    val test01Json = assertTest01.asJson
    val test02Json = assertTest02.asJson
    val test03Json = assertTest03.asJson
  }

  describe("A case class") {
    it("should encode to a json") {
      test01.asJson shouldEqual TestModelContent.test01Json
      test02.asJson shouldEqual TestModelContent.test02Json
      test03.asJson shouldEqual TestModelContent.test03Json

      test01.asJson.noSpaces shouldEqual TestModelContent.test01Json.noSpaces
      test02.asJson.noSpaces shouldEqual TestModelContent.test02Json.noSpaces
      test03.asJson.noSpaces shouldEqual TestModelContent.test03Json.noSpaces
    }
  }

}
