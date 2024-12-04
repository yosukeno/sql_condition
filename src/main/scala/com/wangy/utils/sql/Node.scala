package com.wangy.utils.sql

trait Node {
  var prev: Option[Node] = None
  var next: Option[Node] = None

  //取消当前Node的指向
  def away() = {
    prev = None
    next = None
  }

  def nn() = next.get.next

  def pp()= prev.get.prev
}

object Node {

  def aways(nodes: Node*): Unit = {
    nodes.foreach(_.away())
  }

  case class Condition(left: String, comparisonOpr: CMP_OPR.Value, right: RT) extends Node {
    override def toString: String = s"${left}${comparisonOpr.toString}${right.toString(comparisonOpr)}"

  }

  case class AND() extends Node {
    override def toString: String = " and "
  }

  case class OR() extends Node {
    override def toString: String = " or "
  }

  case class OPEN_BRACKET() extends Node {
    override def toString: String = " ("
  }

  case class CLOSE_BRACKET() extends Node {
    override def toString: String = ") "
  }
}
