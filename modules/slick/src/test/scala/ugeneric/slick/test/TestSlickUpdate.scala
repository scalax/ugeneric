package ugeneric.slick.test

import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers
import org.scalax.ugeneric.slick.USlick
import org.scalax.ugeneric.slick.mutiply.InserOrUpdateMeta

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class TestSlickUpdate extends AnyFunSpec with Matchers {

  import slick.jdbc.H2Profile.api._
  import scala.concurrent.ExecutionContext.Implicits.global

  val model1 = models.User(id = -1, name = "user_name1", age = 2, phone = "1256456456ds", sex = 3)
  val model2 = models.User(id = -1, name = "user_name2", age = 2, phone = "phone2", sex = 32)
  val model3 = models.User(id = -1, name = "user_name3", age = 33, phone = "phone3", sex = 43)

  object Poly1Empty

  object Poly2 {
    val name = InserOrUpdateMeta.needIgnore(true)
    val age  = InserOrUpdateMeta.needIgnore(true)
    val sex  = InserOrUpdateMeta.needIgnore(false)
  }

  val db = Database.forURL(url = "jdbc:h2:mem:default;DB_CLOSE_DELAY=-1", driver = "org.h2.Driver")

  Await.result(db.run(models.userTq.schema.create), Duration.Inf)

  describe("Slick") {
    it("can full update with empty PolyModel") {
      val iOrU1 = USlick.dataMapper(models.userTq, Poly1Empty)
      models.userTq.updateStatement shouldBe iOrU1.updateQ.updateStatement
      models.userBaseSlickTq.updateStatement shouldBe models.userTq.updateStatement
      models.userBaseSlickTq.updateStatement shouldBe iOrU1.updateQ.updateStatement
    }

    it("can dynamic update with PolyModel") {
      val iOrU1 = USlick.dataMapper(models.userTq.map(t => t).map(t => t).filter(_.age >= 2).map(t => t).map(t => t).map(t => t), Poly2)
      models.userTq.filter(_.age >= 2).map(s => (s.id, s.phone, s.sex)).updateStatement shouldBe iOrU1.updateQ.updateStatement
    }

    it("insert with all column") {
      Await.result(db.run(models.userTq.delete), Duration.Inf)
      val data = List(model1, model2, model3)
      Await.result(db.run(models.userTq ++= data), Duration.Inf)
      val selectData = Await.result(db.run(models.userBaseSlickTq.result), Duration.Inf)
      data shouldBe selectData
    }

    it("select with all column") {
      Await.result(db.run(models.userTq.delete), Duration.Inf)
      val data = List(model1, model2, model3)
      Await.result(db.run(models.userBaseSlickTq ++= data), Duration.Inf)
      val selectData = Await.result(db.run(models.userTq.map(t => t).map(t => t).map(t => t).map(t => t).map(t => t).result), Duration.Inf)
      data shouldBe selectData
    }
  }

}
