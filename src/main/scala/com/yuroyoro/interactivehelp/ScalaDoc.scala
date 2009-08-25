package com.yuroyoro.interactivehelp

import _root_.scala.xml.NodeSeq

abstract case class ScalaDoc{
  def apply(i:Int):ScalaDoc
  def apply(i:String):ScalaDoc
}
case class PackageDoc( xml:NodeSeq ) extends ScalaDoc{
  val classes =
    for( tr <- ((xml \\ "table" ).first \\ "tr").toList.tail;
         mod = (tr \\ "td" first).text.trim;
         sg = (tr \\ "td" last).text.split("\n");
         name = tr.first.text.trim;
         sig = tr.take(2).mkString(" ");
         desc = tr.last) yield{ ( name , mod + " " + sig, desc )}

  val objects =
    for( tr <- ((xml \\ "table" ).last \\ "tr").toList.tail;
         mod = (tr \\ "td" first).text.trim;
         sg = (tr \\ "td" last).text.split("\n");
         name = tr.first.text.trim;
         sig = tr.take(2).mkString(" ");
         desc = tr.last) yield{ ( name , mod + " " + sig, desc )}
}

case class ClassDoc( xml:NodeSeq ) extends ScalaDoc{
  def s:IndexElement
  def apply(i:Int) = m(i)
  def m:MethodSeq
  def m(i:Int):MethodDoc
  def m(name:String):MethodSeq
  def t:IndexSeq
  def t(i:Int):IndexSeq
  def t(name:String):IndexSeq
}
case class MethodSeq( theSeq:Seq[MethodDoc] )
  extends ScalaDoc with Seq[MethodDoc]{

  def length = theSeq.length
  override def elements = theSeq.elements
  override def apply(i: Int):IndexElement = theSeq.apply(i)

}
case class MethodDoc( xml:NodeSeq ) extends ScalaDoc

