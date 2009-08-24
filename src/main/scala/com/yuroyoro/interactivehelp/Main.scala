package com.yuroyoro.interactivehelp

import java.io.File

import _root_.scala.io.Source
import _root_.scala.xml.NodeSeq
import _root_.scala.xml.parsing.XhtmlParser
import _root_.scala.tools.nsc.MainGenericRunner


object Main {
  val documentUrl = "http://www.scala-lang.org/docu/files/api/"

  lazy val loader:DocumentLoader = {
    val scalaHome = System.getProperties().getProperty("scala.home")
    val scalaDocHome = new File( scalaHome + "/doc/api/" )
    scalaDocHome.exists match {
      case false =>
        println("[Warging]Load documents from %s" format documentUrl )
        println("You shuld download api documents and extract it to $SCALA_HOME/doc/ .")
        UrlDocumentLoader( documentUrl )
      case _ => FileDocumentLoader( scalaHome + "/doc/api/")
    }
  }

  lazy val ( classIndexies , objectIndexies, packageIndexies ) = {
    val xml = loader.loadXml( "all-classes.html" )
    val pxml = loader.loadXml( "modules.html" )
    def packageName( path:String ) = ("""([^/]+)""".r findAllIn path).toList.reverse.tail.reverse.mkString(".")
    def className( name:String ) = ( """([^.]+)""".r findAllIn name ).toList.last

    (
      // Init class indexies
      for( li <- (xml \\ "ul").first \\ "li";
           name = li \\ "a" text;
           desc = li.text.trim;
           path = li \\ "a" \ "@href" text;
           pkg = packageName(path);
           fqcn = pkg + "." + name)yield{
          ClassIndex(pkg, fqcn, className( name ) , path, desc )
      } ,
      // Init Object indexies
      for( li <- (xml \\ "ul").last \\ "li";
           name = li \\ "a" text;
           desc = li.text.trim;
           path = li \\ "a" \ "@href" text;
           pkg = packageName(path);
           fqcn = pkg + "." + name)yield{
          ObjectIndex(pkg, fqcn, className( name ) , path, desc )
      },
      // Init package indexies
      for( li <- pxml \\ "li";
           name = li.text.trim;
           path = li \\ "a" \ "@href" text)yield{
          PackageIndex( name, path)
      }
    )
  }

  def main(args:Array[String]):Unit = {
    MainGenericRunner.main( args ++ Array("-i" , "import.scala"))
    exit(0)
  }

  def h(name:String):DocumentIndex = {
    val res = classIndexies.filter( i=> i.name == name || i.fqcn == name ) ++
      objectIndexies.filter( i => i.name == name || i.fqcn == name ) ++
      packageIndexies.filter( i => i.name == name )
    res size match {
      case 0 => NoneIndex( name )
      case 1 => res(0)
      case _ => new IndexSeq( res )
    }
  }

}

abstract case class DocumentIndex{
  def toLine(i:Int):String
}
case class ClassIndex(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends DocumentIndex{
    override def toString = desc
    override def toLine(i:Int) = i + ":class "+ desc
}
case class ObjectIndex(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends DocumentIndex{
    override def toString = desc
    override def toLine(i:Int) = i + ":object "+ desc
}
case class PackageIndex(name:String, path:String )
  extends DocumentIndex{
    override def toString = name
    override def toLine(i:Int) = i + ":package "+ name
}
case class NoneIndex(name:String) extends DocumentIndex{
  override def toString = "not found:%s" format name
  override def toLine(i:Int) = toString
}
class IndexSeq( theSeq:Seq[DocumentIndex])
  extends DocumentIndex with Seq[DocumentIndex]{
  val indent = "\n  "
  def length = theSeq.length
  override def elements = theSeq.elements
  def apply(i: Int): DocumentIndex = theSeq.apply(i)
  override def toString = length match {
      case 0 => ""
      case _ => indent + toLines + indent
  }
  override def toLine(i:Int) = this(i).toLine(i)
  def toLines:String = List.range(0 , length ).
    zip( theSeq.toList ).
    map( e => e._2.toLine(e._1) ).mkString( indent )
}

abstract case class DocumentLoader {
  def source( relPath:String ):Source
  def loadXml( relPath:String ):NodeSeq = XhtmlParser( source( relPath ) )
}

case class FileDocumentLoader( docHome:String) extends DocumentLoader{
  def source( relPath:String ):Source = Source.fromFile( docHome + relPath )
}
case class UrlDocumentLoader( docHome:String) extends DocumentLoader{
  def source( relPath:String ):Source = Source.fromURL( docHome + relPath )
}

