package com.yuroyoro.interactivehelp

import Indexies._

object Export{

  def h():Unit = {
    // TODO usage
    println("""
Usage:
""")
  }

  /** find scaladoc by name.  */
  def h(name:String):Document = {
    result( name,
      classIndexies.filter( i=> i.name == name || i.fqcn == name ) ++
      objectIndexies.filter( i => i.name == name || i.fqcn == name ) ++
      packageIndexies.filter( i => i.name == name )
    )
  }
  /** search scaladoc by name */
  def h( name:Symbol ):Document ={
    // TODO
    NoneDocument("TODO")
  }
  /** search scaladoc by name */
  def h[T]( name:Class[T] ):Document ={
    // TODO
    NoneDocument("TODO")
  }
  /** search scaladoc by Object */
  def h( name:AnyRef ):Document ={
    // TODO
    NoneDocument("TODO")
  }

  /** find object documents by name.*/
  def oh( name:String ):Document =
    result( name, objectIndexies.filter( i => i.name == name || i.fqcn == name ) )

  /** find class documents by name.*/
  def ch( name:String ):Document =
    result( name, objectIndexies.filter( i => i.name == name || i.fqcn == name ) )

  /** find package documents by name.*/
  def ph( name:String ):Document =
    result( name, objectIndexies.filter( i => i.name == name || i.fqcn == name ) )

  private def result( name:String, res:Seq[Document] ):Document = {
    res size match {
      case 0 => NoneDocument( name )
      case 1 => res.first
      case _ => new DocumentSeq( res )
    }
  }
}
