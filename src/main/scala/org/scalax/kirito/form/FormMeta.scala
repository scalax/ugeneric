package org.scalax.kirito.form

import cats.data.Validated

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Try
import scala.collection.compat._

trait FormMeta extends FormMetaTrait {
  self =>

  val nameMeta: Option[String]

  def fieldName(name: String): FormMeta = new FormMeta {
    override val nameMeta: Option[String] = Option(name)
  }

  def nonEmptyText(msg: String): FormNameGetter[String] = new FormNameGetter[String] {
    override def getterByName(names: String): FormContent[String] = {
      val fieldName = nameMeta.getOrElse(names)
      nonEmptyText(fieldName, msg)
      /*new FormContent[String] {
        override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, String]] = {
          val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
            case List(i) =>
              val str = i.trim
              if (str.isEmpty) {
                Validated.invalid(ErrorMessage(fieldName, msg))
              } else {
                Validated.valid(str)
              }
            case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
            case _   => Validated.invalid(mtOne(fieldName))
          }
          Future.successful(nameOpt)
        }
      }*/
    }
  }

  def trimText: FormNameGetter[Option[String]] = new FormNameGetter[Option[String]] {
    override def getterByName(names: String): FormContent[Option[String]] = {
      val fieldName = nameMeta.getOrElse(names)
      trimText(fieldName)
      /*new FormContent[Option[String]] {
        override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Option[String]]] = {
          val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
            case List(i) =>
              val str = i.trim
              if (str.isEmpty) {
                Validated.valid(Option.empty)
              } else {
                Validated.valid(Option(str))
              }
            case Nil => Validated.valid(Option.empty)
            case _   => Validated.invalid(mtOne(fieldName))
          }
          Future.successful(nameOpt)
        }
      }*/
    }
  }

  def int(msg: String): FormNameGetter[Int] = new FormNameGetter[Int] {
    override def getterByName(names: String): FormContent[Int] = {
      val fieldName = nameMeta.getOrElse(names)
      int(fieldName, msg)
      /*new FormContent[Int] {
        override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Int]] = {
          val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
            case List(i) =>
              val intResult = Try { i.trim.toInt }
              intResult.map(Validated.valid).getOrElse(Validated.invalid(ErrorMessage(fieldName, msg)))
            case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
            case _   => Validated.invalid(mtOne(fieldName))
          }
          Future.successful(nameOpt)
        }
      }*/
    }
  }

  def long(msg: String): FormNameGetter[Long] = new FormNameGetter[Long] {
    override def getterByName(names: String): FormContent[Long] = {
      val fieldName = nameMeta.getOrElse(names)
      getterByName(fieldName)
      /*new FormContent[Long] {
            override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Long]] = {
              val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
                case List(i) =>
                  val intResult = Try { i.trim.toLong }
                  intResult.map(Validated.valid).getOrElse(Validated.invalid(ErrorMessage(fieldName, msg)))
                case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
                case _   => Validated.invalid(mtOne(fieldName))
              }
              Future.successful(nameOpt)
            }
          }*/
    }
  }

  def telephone(msg: String): FormNameGetter[String] = new FormNameGetter[String] {
    override def getterByName(names: String): FormContent[String] = {
      val fieldName = nameMeta.getOrElse(names)
      telephone(fieldName, msg)
      /*new FormContent[String] {
        override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, String]] = {
          val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
            case List(i) =>
              Try { BigDecimal(i) }.toOption.filter(_ => i.size == 11).map(r => Validated.valid(i)).getOrElse(Validated.invalid(ErrorMessage(fieldName, msg)))
            case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
            case _   => Validated.invalid(mtOne(fieldName))
          }
          Future.successful(nameOpt)
        }
      }*/
    }
  }

  def boolean(msg: String): FormNameGetter[Boolean] = new FormNameGetter[Boolean] {
    override def getterByName(names: String): FormContent[Boolean] = {
      val fieldName = nameMeta.getOrElse(names)
      boolean(fieldName, msg)
      /*new FormContent[Boolean] {
        override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Boolean]] = {
          val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
            case List(i) =>
              i.trim match {
                case "是" => Validated.valid(true)
                case "否" => Validated.valid(false)
                case _   => Validated.invalid(ErrorMessage(fieldName, msg))
              }
            case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
            case _   => Validated.invalid(mtOne(fieldName))
          }
          Future.successful(nameOpt)
        }
      }*/
    }
  }

  def nonEmptyTextList: FormNameGetter[List[String]] = new FormNameGetter[List[String]] {
    override def getterByName(names: String): FormContent[List[String]] = {
      val fieldName = nameMeta.getOrElse(names)
      nonEmptyTextList(fieldName)
    }
  }

}

object FormMeta extends FormMeta {
  override val nameMeta: Option[String] = Option.empty
}

trait FormMetaTrait {

  def literal[T](t: T): FormContent[T] = new FormContent[T] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, T]] = {
      val nameOpt = Validated.valid(t)
      Future.successful(nameOpt)
    }
  }

  protected def mtOne(fieldName: String): ErrorMessage = ErrorMessage(fieldName, s"字段${fieldName}有多于一个值，请保留此消息并联系管理员。")

  def nonEmptyText(fieldName: String, msg: String): FormContent[String] = new FormContent[String] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, String]] = {
      val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
        case List(i) =>
          val str = i.trim
          if (str.isEmpty) {
            Validated.invalid(ErrorMessage(fieldName, msg))
          } else {
            Validated.valid(str)
          }
        case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
        case _   => Validated.invalid(mtOne(fieldName))
      }
      Future.successful(nameOpt)
    }
  }

  def trimText(fieldName: String): FormContent[Option[String]] = new FormContent[Option[String]] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Option[String]]] = {
      val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
        case List(i) =>
          val str = i.trim
          if (str.isEmpty) {
            Validated.valid(Option.empty)
          } else {
            Validated.valid(Option(str))
          }
        case Nil => Validated.valid(Option.empty)
        case _   => Validated.invalid(mtOne(fieldName))
      }
      Future.successful(nameOpt)
    }
  }

  def nonEmptyTextList(fieldName: String): FormContent[List[String]] = new FormContent[List[String]] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, List[String]]] = {
      Future.successful(Validated.valid(map.get(fieldName).to(List).flatMap(_.to(List)).map(_.trim).filter(!_.isEmpty)))
    }
  }

  def int(fieldName: String, msg: String): FormContent[Int] = new FormContent[Int] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Int]] = {
      val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
        case List(i) =>
          val intResult = Try { i.trim.toInt }
          intResult.map(Validated.valid).getOrElse(Validated.invalid(ErrorMessage(fieldName, msg)))
        case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
        case _   => Validated.invalid(mtOne(fieldName))
      }
      Future.successful(nameOpt)
    }
  }

  def long(fieldName: String, msg: String): FormContent[Long] = new FormContent[Long] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Long]] = {
      val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
        case List(i) =>
          val intResult = Try { i.trim.toLong }
          intResult.map(Validated.valid).getOrElse(Validated.invalid(ErrorMessage(fieldName, msg)))
        case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
        case _   => Validated.invalid(mtOne(fieldName))
      }
      Future.successful(nameOpt)
    }
  }

  def telephone(fieldName: String, msg: String): FormContent[String] = new FormContent[String] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, String]] = {
      val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
        case List(i) =>
          Try { BigDecimal(i) }.toOption.filter(_ => i.size == 11).map(r => Validated.valid(i)).getOrElse(Validated.invalid(ErrorMessage(fieldName, msg)))
        case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
        case _   => Validated.invalid(mtOne(fieldName))
      }
      Future.successful(nameOpt)
    }
  }

  def boolean(fieldName: String, msg: String): FormContent[Boolean] = new FormContent[Boolean] {
    override def getModel(map: Map[String, Seq[String]])(implicit ec: ExecutionContext): Future[Validated[ErrorMessage, Boolean]] = {
      val nameOpt = map.get(fieldName).to(List).flatMap(_.to(List)) match {
        case List(i) =>
          i.trim match {
            case "是" => Validated.valid(true)
            case "否" => Validated.valid(false)
            case _   => Validated.invalid(ErrorMessage(fieldName, msg))
          }
        case Nil => Validated.invalid(ErrorMessage(fieldName, msg))
        case _   => Validated.invalid(mtOne(fieldName))
      }
      Future.successful(nameOpt)
    }
  }

}
