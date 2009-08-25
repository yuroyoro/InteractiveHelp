package com.yuroyoro.interactivehelp

import _root_.scala.xml.NodeSeq

case class MethodSeq( theSeq:Seq[MethodDoc] )
  //extends ScalaDoc with Seq[MethodDoc]
{

  //def length = theSeq.length
  //override def elements = theSeq.elements
  //override def apply(i: Int):MethodDoc = theSeq.apply(i)

}
case class MethodDoc( xml:NodeSeq ) //extends ScalaDoc
