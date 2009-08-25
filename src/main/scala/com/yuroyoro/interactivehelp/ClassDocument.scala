package com.yuroyoro.interactivehelp

import Indexies._

trait ScalaDoc extends Document {
  lazy val xml = loader.loadXml( path )
  lazy val definitions = (xml \ "body" \ "dl").first
  lazy val sig = (definitions \ "dt").text.trim.split("\n").map(_.trim).mkString(" ")
  lazy val ext = (definitions \ "dd").text.trim
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
  lazy val traitsClass = {
    val as = ((xml \\ "dd" first ) \\ "a" toList ).tail
    for( a <- as ; path = a \\ "@href" text; name = a text ) yield{
      fromPath( path , name )
    }
  }

  lazy val valueSum = for( t <- xml \ "body" \ "table";
      if t \ "@class" == "member";
      if (t \ "tr" \ "td" first).text.trim == "Value Summary")yield{( t \\ "tr").toList.tail}

  lazy val valueDet = for( t <- xml \ "body" \ "table";
      if t \ "@class" == "member-detail";
      if (t \ "tr" \ "td" first).text.trim == "Value Details")yield{t}

  lazy val methodSum = for( t <- xml \ "body" \ "table";
      if t \ "@class" == "member";
      if (t \ "tr" \ "td" first).text.trim == "MethodSummary")yield{(t \\ "tr").toList.tail}

  lazy val methodDet = for( t <- xml \ "body" \ "table";
      if t \ "@class" == "member-detail";
      if (t \ "tr" \ "td" first).text.trim == "Method Details")yield{t}

  override def toString = "\n" + List( sig, ext, "", description).mkString("\n  ")

  override def apply(i:Int) = null //TODO  m(i)
  override def apply(name:String) = null //TODO m(name)

  def path:String
  override def s:Document = extendsClass
  override def et:IndexSeq = null // TODO

  override def v:Document= null // TODO
  override def v(i:Int):Document= null // TODO
  override def v(name:String):Document = null // TODO
  override def m:IndexSeq = null // TODO
  override def m(i:Int):Document = null // TODO
  override def m(name:String):IndexSeq = null // TODO
  override def t:IndexSeq = new IndexSeq( traitsClass )
  override def t(i:Int):Document = traitsClass(i)
  override def t(name:String):IndexSeq = null // TODO
}

case class ClassDocument(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends ScalaDoc {
  def kind = "Class"
  override def shortDesc = "%s %s".format( kind, fqcn)
}

case class ObjectDocument(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends ScalaDoc {
  def kind = "Object"
  override def shortDesc = "%s %s".format( kind, fqcn)
}
