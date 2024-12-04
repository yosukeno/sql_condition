package com.wangy.utils.sql.test

import com.wangy.utils.sql.NullCondition.{LF, where}

object Example {
  def main(args: Array[String]): Unit = {

    println(where
      .LF("c_dns_boundary").==.$(1)
      .and
      .LF("c_dns_boundary").==.&("").orCondition(LF("xxx").==.&("xxx"))
      .and
      .ASSERT_&("test", "test").withCondition(LF("a").==.&("b"))
      .and
      .LF("kkk").in.&("dsa , wqeq")
      .and
      .wrapWithParentheses(
        LF("666").==.&("")
          .and
          .LF("rrr").==.&("qqq")
      )
      .toString
    )


    println(where
      .LF("c_dns_boundary").==.$(1)
      .and
      .LF("c_dns_boundary").==.&("").orCondition(LF("xxx").==.&("xxx"))
      .and
      .ASSERT_&("test", "test").withCondition(LF("a").==.&("b"))
      .and
      .LF("kkk").in.&("dsa , wqeq")
      .and
      .wrapWithParentheses(
        LF("rrr").==.&("")
          .and
          .LF("rrr").==.&("xxx")
      )

      .build
    )
  }
}
