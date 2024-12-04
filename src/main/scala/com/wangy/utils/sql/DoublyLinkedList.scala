package com.wangy.utils.sql

import scala.collection.mutable.ListBuffer

class DoublyLinkedList {
  // 链表头和尾节点
  var head: Option[Node] = None
  var last: Option[Node] = None

  // 添加元素到链表尾部
  def append(newNode: Node): Unit = {
    last match {
      case Some(t) =>
        t.next = Some(newNode)
        newNode.prev = Some(t)
        last = Some(newNode)
      case None =>
        head = Some(newNode)
        last = Some(newNode)
    }
  }

  // 其他链表添加到本链表
  def appendAll(other: DoublyLinkedList): Unit = {
    if (other.isEmpty) return

    if (isEmpty) {
      head = other.head
      last = other.last
    } else {
      last.get.next = other.head
      other.head.get.prev = last
      last = other.last

      other.head = None
      other.last = None
    }
  }



  //节点取代链表
  def nodeReplaceChain(start: Node, end: Node, node: Node): Node = {
    if (isEmpty) throw new RuntimeException("DoublyLinkedList is Empty!")
    if (!isContains(node)) throw new RuntimeException("node is not DoublyLinkedList's element!")

    val ref = Some(node)
    if (start != head.get && end != last.get) { //取中间
      //链接前点
      start.prev.get.next = ref
      node.prev = start.prev

      //链接后点
      node.next = end.next
      end.next.get.prev = ref
    } else if (start == head.get) { //取左边
      //链接后点
      node.next = end.next
      end.next.get.prev = ref

      head = ref
    } else if (last.get == end) { //取右边
      //链接前点
      start.prev.get.next = ref
      node.prev = start.prev

      last = ref
    } else { //取全部
      head = ref
      last = ref
    }

    //GC 会处理不可达对象 只需处理 '舍弃链' 的head、last
    start.away()
    end.away()

    node
  }

  //todo 链表 取代 链表
  def chainReplaceChain(other: DoublyLinkedList): Unit = ???

  def drorChain(start: Node, end: Node): Unit = {
    if (isEmpty) throw new RuntimeException("DoublyLinkedList is Empty!")

    if (start != head.get && end != last.get) { //取中间
      start.prev.get.next = end.next
      end.next.get.prev = start.prev
    } else if (start == head.get) { //取左边
      end.next.get.prev = start.prev

      head = end.next
    } else if (last.get == end) { //取右边
      start.prev.get.next = end.next

      last = start.prev
    } else { //取全部
      head = None
      last = None
    }

    //GC 会处理不可达对象 只需处理 '舍弃链' 的head、last
    start.away()
    end.away()
  }

  // 删除指定值的节点
  def drop(node: Node): Unit = {
    if (isEmpty) throw new RuntimeException("DoublyLinkedList is Empty!")
    if (!isContains(node)) throw new RuntimeException("node is not DoublyLinkedList's element!")

    if (node != head.get) {
      node.prev.get.next = node.next
      node.next.get.prev = node.prev
    } else {
      head = None
      last = None
    }

    node.away()
  }

  // 检查链表是否为空
  def isEmpty: Boolean = head.isEmpty

  // 打印链表内容（从头到尾）
  //  def printForward(): Unit

  // 打印链表内容（从尾到头）
  //  def printBackward(): Unit

  def foreach(fEach: Node => Unit): Unit = {
    var current = head
    while (current.isDefined) {
      fEach.apply(current.get)
      current = current.get.next
    }
  }

  def foreachRef(fEach: Node => Option[Node]): Unit = {
    var current = head
    while (current.isDefined) {
      current = fEach.apply(current.get)
    }
  }


  def map[T](fEach: Node => T): ListBuffer[T] = {
    val buffer = new ListBuffer[T]
    var current = head
    while (current.isDefined) {
      buffer.append(fEach.apply(current.get))
      current = current.get.next
    }
    buffer
  }

  def isContains(node: Node): Boolean = {
    var current = head
    while (current.isDefined) {
      if (node == current.get) return true
      current = current.get.next
    }

    false
  }

  override def toString: String = map[String](_.toString).mkString("")
}

object DoublyLinkedList {}