package com.yuroyoro.interactivehelp

import Indexies._

trait ScalaDoc extends Document {
  lazy val xml = loader.loadXml( path )
  lazy val definitions = (xml \ "body" \ "dl")(0).text.trim.split("\n")
  lazy val sig = definitions.take(2).map( _.trim).mkString(" ")
  lazy val ext = definitions(3).trim
  lazy val description = (xml \ "body" \ "dl").drop(1) match{
    case x::Nil => x text
    case x::xs  => x text
    case _ => ""
  }
  lazy val inherited = for( t <- xml \\ "table";
      if ( t \ "@class" == "inherited");
      a = (t \\ "tr" first) \\ "a";
      path = a \\ "@href" text;
      name = a.text ) yield{ fromPath( path, name)}

  lazy val extendsClass = {
    val a = (xml \\ "dd" first ) \\ "a" first;
    fromPath( a \\ "@href" text ,  a text )
  }
  lazy val traits = {
    val as = ((xml \\ "dd" first ) \\ "a" toList ).tail
    for( a <- as ; path = a \\ "@href" text; name = a text ) yield{
      fromPath( path , name )
    }
  }

  override def toString = List( sig, ext, "", description).mkString("\n  ")

  def path:String
  def s:Document = extendsClass
  def et:IndexSeq = null // TODO
  def apply(i:Int) = null //TODO  m(i)
  def apply(name:String) = null //TODO m(name)

  // TODO ちゃんとした名前も作る
  def v:String = null // TODO
  def v(i:Int):String = null // TODO
  def v(name:String):String = null // TODO
  def m:MethodSeq = null // TODO
  def m(i:Int):MethodDoc = null // TODO
  def m(name:String):MethodSeq = null // TODO
  def t:IndexSeq = new IndexSeq( traits )
  def t(i:Int):ScalaDoc = traits(i)
  def t(name:String):IndexSeq = null // TODO
}

case class ClassDocument(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends ScalaDoc {
  def kind = "Class"
}

case class ObjectDocument(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends ScalaDoc {
  def kind = "Object"
}
