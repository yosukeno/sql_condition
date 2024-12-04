package com.wangy.utils.sql

import ASSERT_OPR.ASSERT_OPR
import Node._
import RT._
import com.wangy.utils.sql.Node.{AND, CLOSE_BRACKET, Condition, OPEN_BRACKET, OR}


object Stage {
  case class firstStage(
                         var lf_Stage: lfStage = null,
                         var assert_StringStage: assertStringStage = null,
                         var assert_NumberStage: assertNumberStage = null,
                         var complete_Stage: completeStage = null
                       )(implicit conditions: DoublyLinkedList) {
    def LF(field: String): lfStage = lf_Stage.putLF(field)

    def wrapWithParentheses(wrap_condition: completeStage): completeStage = {
      conditions.append(OPEN_BRACKET())
      conditions.appendAll(wrap_condition.conditions)
      conditions.append(CLOSE_BRACKET())

      complete_Stage
    }

    // 如果 value==if_value 则后续可以加入条件 if_string_default
    def ASSERT_&(value: String, assert_value: String): assertStringStage =
      assert_StringStage.putAssertString(value, assert_value, ASSERT_OPR.is)


    def ASSERT_NOT_&(value: String, assert_value: String): assertStringStage =
      assert_StringStage.putAssertString(value, assert_value, ASSERT_OPR.not)

    def ASSERT_IsEmpty_&(value: String): assertStringStage =
      assert_StringStage.putAssertString(value, null, ASSERT_OPR.isEmpty)

    def ASSERT_NotEmpty_&(value: String): assertStringStage =
      assert_StringStage.putAssertString(value, null, ASSERT_OPR.notEmpty)
  }

  case class lfStage(
                      var lf: String = null,

                      var cmp_Stage: cmpStage = null
                    ) {
    def putLF(lf: String) = {
      this.lf = lf
      this
    }

    def == : cmpStage = cmp_Stage.putCmp(lf, CMP_OPR.==)

    def != : cmpStage = cmp_Stage.putCmp(lf, CMP_OPR.!=)

    def >= : cmpStage = cmp_Stage.putCmp(lf, CMP_OPR.>=)

    def > = cmp_Stage.putCmp(lf, CMP_OPR.>)

    def <= : cmpStage = cmp_Stage.putCmp(lf, CMP_OPR.<=)

    def < = cmp_Stage.putCmp(lf, CMP_OPR.<)

    def like: cmpStage = cmp_Stage.putCmp(lf, CMP_OPR.like)

    def in: cmpStage = cmp_Stage.putCmp(lf, CMP_OPR.in)
  }


  case class cmpStage(
                       var lf: String = null,
                       var cmp: CMP_OPR.Value = null,

                       var complete_Stage: completeStage = null
                     )(implicit conditions: DoublyLinkedList) {

    def putCmp(lf: String, cmp: CMP_OPR.Value) = {
      this.lf = lf
      this.cmp = cmp
      this
    }

    def $(value: Integer): completeStage = {
      conditions.append(Condition(lf, cmp, INT_RT(value)))
      complete_Stage
    }

    def $(value: Int): completeStage = {
      conditions.append(Condition(lf, cmp, INT_RT(value)))
      complete_Stage
    }

    def $(value: Long): completeStage = {
      conditions.append(Condition(lf, cmp, Long_RT(value)))
      complete_Stage
    }

    def $(value: Double): completeStage = {
      conditions.append(Condition(lf, cmp, Double_RT(value)))
      complete_Stage
    }

    def &(value: String): completeStage = {
      conditions.append(Condition(lf, cmp, STRING_RT(value)))
      complete_Stage
    }
  }

  case class completeStage(
                            var first_Stage: firstStage = null
                          )(implicit val conditions: DoublyLinkedList) {
    def and = {
      conditions.append(AND())

      //this stage over
      first_Stage
    }

    def or = {
      conditions.append(OR())
      //stage over
      first_Stage
    }

    def defaultValue_&(df_value: String): firstStage = {
      val res = conditions.last.get.asInstanceOf[Condition]
      if (res.right.isEmpty) res.right.asInstanceOf[STRING_RT].value = df_value
      first_Stage
    }

    def defaultValue_$(df_value: Int): firstStage = {
      val res = conditions.last.get.asInstanceOf[Condition]
      if (res.right.isEmpty) res.right.asInstanceOf[INT_RT].value = df_value
      first_Stage
    }

    def defaultValue_$(df_value: Long): firstStage = {
      val res = conditions.last.get.asInstanceOf[Condition]
      if (res.right.isEmpty) res.right.asInstanceOf[Long_RT].value = df_value
      first_Stage
    }

    def orCondition(if_condition: completeStage): completeStage = {
      //如果是特殊值 加条件
      val last = conditions.last.get.asInstanceOf[Condition]
      if (last.right.isEmpty) { //将condition_g 和 conditions加入现有的条件中
        conditions.append(AND())
        conditions.appendAll(if_condition.conditions)
      }

      this
    }

    def build: String = {
      /** 这里使用返回 引用的函数是因为 使用了 nodeReplaceChain 方法 */
      conditions.foreachRef(op => op match {
        case node: Node =>
          val prov = node.prev
          val next = node.next

          var ref = next

          node match {
            case Condition(left, comparisonOpr, right) =>
              if (right.isEmpty) { //中间节点为空时，去掉该中间节点
                if (prov.isDefined) {
                  //取消空节点的指向
                  //1. and-or || or-or => or
                  if (prov.get.isInstanceOf[AND] && prov.get.isInstanceOf[OR] || prov.get.isInstanceOf[OR] && prov.get.isInstanceOf[AND]) {
                    ref = Some(conditions.nodeReplaceChain(node.prev.get, node.next.get, OR()))
                  }

                  //2. and-and => and
                  if (prov.get.isInstanceOf[AND] && prov.get.isInstanceOf[AND]) {
                    ref = Some(conditions.nodeReplaceChain(node.prev.get, node.next.get, AND()))
                  }

                  if (prov.get.isInstanceOf[OPEN_BRACKET]) { //3. prov=b_,
                    if (next.get.isInstanceOf[CLOSE_BRACKET]) conditions.drorChain(node.pp().get, node.next.get) //('')
                    else { //(''
                      ref = node.pp()
                      conditions.drorChain(node, node.next.get)
                    }
                  } else if (next.get.isInstanceOf[CLOSE_BRACKET]) { //4. next=b_  // '')
                    ref = node.pp()
                    conditions.drorChain(node.prev.get, node)
                  }
                } else conditions.head = conditions.head.get.next.get.next
              }

            case _ =>
          }

          ref
      })

      //final result
      val res = if (!conditions.isEmpty)
        " where " + conditions.map[String](_.toString).mkString("")
      else
        ""

      res
    }

    override def toString: String = {
      " where " + conditions.map[String](_.toString).mkString("")
    }
  }

  case class assertStringStage(
                                var value: String = null,
                                var if_value: String = null,
                                var opr: ASSERT_OPR = null,

                                var complete_Stage: completeStage = null
                              )(implicit val conditions: DoublyLinkedList) {


    def putAssertString(value: String, if_value: String, opr: ASSERT_OPR): assertStringStage = {
      this.value = value
      this.if_value = if_value
      this.opr = opr
      this
    }

    def withCondition(if_condition: completeStage): completeStage = {
      if (opr match {
        case ASSERT_OPR.is => if_value.equals(value)
        case ASSERT_OPR.not => !if_value.equals(value)
        case ASSERT_OPR.isEmpty => value == null || value.isEmpty
        case ASSERT_OPR.notEmpty => !(value == null || value.isEmpty)
      }) {
        //将condition_g 和 conditions加入现有的条件中
        conditions.appendAll(if_condition.conditions)
      }
      complete_Stage
    }
  }

  case class assertNumberStage(
                                var value: String = null,
                                var if_value: String = null
                              )(implicit val conditions: DoublyLinkedList) {
    var complete_Stage: completeStage = _

    def putAssertNumber(value: String, if_value: String): assertNumberStage = {
      this.value = value
      this.if_value = if_value
      this
    }

    def withCondition(if_condition: completeStage) = {
      //如果是特殊值 加条件
      if (if_value.equals(value)) {
        //if_condition 和 conditions加入现有的条件中
        conditions.append(AND())
        conditions.appendAll(if_condition.conditions)
      }
      complete_Stage
    }
  }
}