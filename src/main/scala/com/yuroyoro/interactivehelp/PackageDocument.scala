package com.yuroyoro.interactivehelp

import Util._

case class PackageDocument(name:String, path:String ) extends Document {

  def desc = name
  def kind = "Package"
  def fqcn = name
  lazy val xml = loader.loadXml( path )

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

  def apply(i:Int):Document = null // TODO
  def apply(name:String):Document = null // TODO
  def apply(name:Symbol):Document = null //TODO m(name)
}
