package com.yuroyoro.interactivehelp

import _root_.scala.io.Source
import _root_.scala.xml.NodeSeq
import _root_.scala.xml.parsing.XhtmlParser

import java.io.File

object DocumentLoader{
  val documentUrl = "http://www.scala-lang.org/docu/files/api/"
  def load = {
    val phome = System.getProperties().getProperty("scala.home")
    val ehome = System.getenv.get("SCALA_HOME")
    val scalaHome = if( phome != null ) phome else ehome
    val scalaDocHome = new File( scalaHome + "/doc/api/" )

    scalaDocHome.exists match {
      case false => UrlDocumentLoader( documentUrl )
      case _ => FileDocumentLoader( scalaHome + "/doc/api/")
    }
  }
}

abstract case class DocumentLoader {
  def source( relPath:String ):Source
  def loadXml( relPath:String ):NodeSeq = XhtmlParser( source( relPath ) )
}

case class FileDocumentLoader( docHome:String) extends DocumentLoader{
  def source( relPath:String ):Source = Source.fromFile( docHome + relPath )
}
case class UrlDocumentLoader( docHome:String) extends DocumentLoader{
  println("[Warging]Load documents from %s" format docHome)
  println("You shuld download api documents and extract it to $SCALA_HOME/doc/ .")
  def source( relPath:String ):Source = Source.fromURL( docHome + relPath )
}
