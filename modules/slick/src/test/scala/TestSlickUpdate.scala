package ugeneric.circe.test.decoder

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalax.ugeneric.slick.USlick
import org.scalax.ugeneric.slick.mutiply.InserOrUpdateMeta

class TestSlickUpdate extends AnyFunSpec with Matchers {

  import slick.jdbc.H2Profile.api._

  case class User(id: Int, name: String, age: Int, phone: String, sex: Int)
  class UserTable(cons: Tag) extends Table[User](cons, "usert") {
    val id         = column[Int]("id")
    val name       = column[String]("name")
    val age        = column[Int]("age")
    val phone      = column[String]("phone")
    val sex        = column[Int]("sex")
    override def * = USlick.mapWithTable(this)
  }
  val userTq = TableQuery[UserTable]

  class UserTable1(cons: Tag) extends Table[User](cons, "usert") {
    val id         = column[Int]("id")
    val name       = column[String]("name")
    val age        = column[Int]("age")
    val phone      = column[String]("phone")
    val sex        = column[Int]("sex")
    override def * = (id, name, age, phone, sex).<>(User.tupled, User.unapply _)
  }
  val userTq1 = TableQuery[UserTable1]

  object Poly1Empty

  object Poly2 {
    val name = InserOrUpdateMeta.needIgnore(true)
    val age  = InserOrUpdateMeta.needIgnore(true)
    val sex  = InserOrUpdateMeta.needIgnore(false)
  }

  describe("Slick") {
    it("can full update with empty PolyModel") {
      val iOrU1 = USlick.dataMapper(userTq, Poly1Empty)
      userTq.updateStatement shouldBe iOrU1.updateQ.updateStatement
      userTq1.updateStatement shouldBe userTq.updateStatement
      userTq1.updateStatement shouldBe iOrU1.updateQ.updateStatement
    }

    it("can dynamic update with PolyModel") {
      val iOrU1 = USlick.dataMapper(userTq, Poly2)
      userTq.map(s => (s.id, s.phone, s.sex)).updateStatement shouldBe iOrU1.updateQ.updateStatement
    }
  }

}
