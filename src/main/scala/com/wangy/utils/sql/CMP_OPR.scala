package com.wangy.utils.sql

object CMP_OPR extends Enumeration {
  type CMP_OPR = Value

  // 定义带自定义名称的枚举值
  val == : CMP_OPR = Value(" == ")
  val != : CMP_OPR = Value(" != ")
  val >= : CMP_OPR = Value(" >= ")
  val > : CMP_OPR = Value(" > ")
  val <= : CMP_OPR = Value(" <= ")
  val < : CMP_OPR = Value(" < ")
  val like : CMP_OPR = Value(" like ")
  val in : CMP_OPR = Value(" in ")

  def main(args: Array[String]): Unit = {
    println(in)
  }
}
