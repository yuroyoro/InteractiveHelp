/*
 *
 * Copyright 2009 yuroyoro,Tomohito Ozaki
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
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
  def loadXml( relPath:String ):NodeSeq = {
    //println( "load file : " + relPath )
    XhtmlParser( source( relPath ) )
  }
}

case class FileDocumentLoader( docHome:String) extends DocumentLoader{
  def source( relPath:String ):Source = Source.fromFile( docHome + relPath )
}
case class UrlDocumentLoader( docHome:String) extends DocumentLoader{
  println("[Warging]Load documents from %s" format docHome)
  println("You shuld download api documents and extract it to $SCALA_HOME/doc/ .")
  def source( relPath:String ):Source = Source.fromURL( docHome + relPath )
}
