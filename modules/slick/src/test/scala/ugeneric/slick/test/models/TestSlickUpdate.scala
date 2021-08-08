package ugeneric.slick.test.models

import org.scalax.ugeneric.slick.USlick

import slick.jdbc.H2Profile.api._

case class User(id: Int, name: String, age: Int, phone: String, sex: Int)

class UserTable(cons: Tag) extends Table[User](cons, "user") {
  val id         = column[Int]("id")
  val name       = column[String]("name")
  val age        = column[Int]("age")
  val phone      = column[String]("phone")
  val sex        = column[Int]("sex")
  override def * = USlick.mapWithTable(this)
}

object userTq extends TableQuery[UserTable](cons => new UserTable(cons))

class UserBaseSlickTable(cons: Tag) extends Table[User](cons, "user") {
  val id         = column[Int]("id")
  val name       = column[String]("name")
  val age        = column[Int]("age")
  val phone      = column[String]("phone")
  val sex        = column[Int]("sex")
  override def * = (id, name, age, phone, sex).<>(User.tupled, User.unapply _)
}

object userBaseSlickTq extends TableQuery[UserBaseSlickTable](cons => new UserBaseSlickTable(cons))
