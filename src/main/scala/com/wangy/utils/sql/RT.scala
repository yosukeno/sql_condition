package com.wangy.utils.sql

import CMP_OPR.CMP_OPR


//rt trait
trait RT {
  def isEmpty: Boolean

  def toString(comparisonOpr: CMP_OPR):String
}

object RT {
  case class INT_RT(var value: Integer) extends RT {
    override def isEmpty: Boolean = value == null

    override def toString(comparisonOpr: CMP_OPR): String = value.toString
  }

  case class Long_RT(var value: Long) extends RT {
    override def isEmpty: Boolean = value == null

    override def toString(comparisonOpr: CMP_OPR): String = value.toString
  }

  case class Double_RT(var value: Double) extends RT {
    override def isEmpty: Boolean = value == null

    override def toString(comparisonOpr: CMP_OPR): String =value.toString
  }

  case class STRING_RT(var value: String) extends RT {
    override def isEmpty: Boolean = value == null || value.length == 0 || value.equals("%%")

    def toString(comparisonOpr: CMP_OPR): String = {
      if (comparisonOpr == CMP_OPR.like) {
        s"'%$value%'"
      } else if (comparisonOpr == CMP_OPR.in) {
        if (value == null) "" else s"(${value.split(",").map(_.trim).map(m => s"'${m}'").mkString(", ")})"
      } else s"'$value'"
    }
  }
}






