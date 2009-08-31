/*
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

import _root_.scala.io.{Source, BufferedSource}
import _root_.scala.xml.NodeSeq
import _root_.scala.xml.parsing.XhtmlParser

import java.io.File
import java.awt.Desktop
import java.net.URI

abstract case class DocumentLoader {
  val docHome:String
  def source( relPath:String ):Source
  def loadXml( relPath:String ):NodeSeq = {
    val src = source( relPath )
    try{
      XhtmlParser(src)
    }finally{
      // FIXME: how to closing Source object?
      src.asInstanceOf[BufferedSource].close
    }
  }

  val urlHome:String = docHome
  def normalizePath( path:String ) = path.split("/").filter( _ != "..").mkString("/")
  lazy val desktop = Desktop.getDesktop
  def openUrl( url:String ):Unit = desktop.browse(new URI( urlHome + normalizePath( url)))
}

case class FileDocumentLoader( docHome:String) extends DocumentLoader{
  override val urlHome = "file://" + docHome
  def source( relPath:String ):Source = Source.fromFile( docHome + relPath )
}
case class UrlDocumentLoader( docHome:String) extends DocumentLoader{
  println("[Warging]Load documents from %s" format docHome)
  println("You shuld download api documents and extract it to $SCALA_HOME/doc/ .")
  def source( relPath:String ):Source = Source.fromURL( docHome + relPath )
}

