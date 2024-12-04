package com.wangy.utils.sql

import com.wangy.utils.sql.Stage._
import com.wangy.utils.sql.Stage.{firstStage, lfStage}


object NullCondition {
  def where: firstStage = {
    implicit val impl = new DoublyLinkedList


    val first_Stage = firstStage()
    val lf_Stage = lfStage()
    val cmp_Stage = cmpStage()
    val complete_Stage = completeStage()
    val assertNumber_Stage = assertNumberStage()
    val assertString_Stage = assertStringStage()

    first_Stage.lf_Stage = lf_Stage
    first_Stage.assert_StringStage = assertString_Stage
    first_Stage.assert_NumberStage = assertNumber_Stage
    first_Stage.complete_Stage = complete_Stage

    assertString_Stage.complete_Stage = complete_Stage
    assertNumber_Stage.complete_Stage = complete_Stage

    lf_Stage.cmp_Stage = cmp_Stage

    cmp_Stage.complete_Stage = complete_Stage

    complete_Stage.first_Stage = first_Stage

    first_Stage
  }


  def LF(field: String): lfStage = {//括号内 习惯用的方式
    implicit val impl = new DoublyLinkedList

    //构建状态机
    val first_Stage = firstStage()
    val lf_Stage = lfStage(field)
    val cmp_Stage = cmpStage()
    val complete_Stage = completeStage()
    val assertNumber_Stage = assertNumberStage()
    val assertString_Stage = assertStringStage()

    first_Stage.lf_Stage = lf_Stage
    first_Stage.assert_StringStage = assertString_Stage
    first_Stage.assert_NumberStage = assertNumber_Stage
    first_Stage.complete_Stage = complete_Stage

    assertString_Stage.complete_Stage = complete_Stage
    assertNumber_Stage.complete_Stage = complete_Stage

    lf_Stage.cmp_Stage = cmp_Stage

    cmp_Stage.complete_Stage = complete_Stage

    complete_Stage.first_Stage = first_Stage

    lf_Stage
  }
}