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

import scala.collection.mutable.ListBuffer
import Util._
import java.io.File

object Indexies{

  val documentUrl = "http://www.scala-lang.org/docu/files/api/"

  lazy val scalaDocHome = {
    val execdir = System.getProperties().getProperty("user.dir")
    val scalaDocHome = System.getenv.get("SCALA_DOC_HOME") match {
      case null =>
        (System.getProperties().getProperty("scala.home") match{
          case null =>
            System.getenv.get("SCALA_HOME")
          case p => p
       }) + "/doc/api/"
      case x => x
    }
    println( scalaDocHome )
    scalaDocHome
  }

  lazy val scalaDocLoader = {
    (new File( scalaDocHome)).exists match {
      case false => UrlDocumentLoader( documentUrl )
      case _ => FileDocumentLoader( scalaDocHome )
    }
  }

  lazy val docPath:ListBuffer[String] = new ListBuffer
  lazy val docUrl:ListBuffer[String] = new ListBuffer

  // intialize indexies.indexies use to find documents by name.
  lazy val ( classIndexies , objectIndexies, packageIndexies ) = {
    val sdoc = Index( scalaDocLoader )

    val fdoc:List[Index] =
      docPath.map( path => Index( new FileDocumentLoader( path ))).toList
    val udoc:List[Index] =
      docUrl.map( path => Index( new UrlDocumentLoader( path ))).toList

    val c:List[Document] = udoc.flatten( i => i.classIndexies ):::
          fdoc.flatten( i => i.classIndexies ):::
          sdoc.classIndexies
    val o:List[Document] = udoc.flatten( i => i.objectIndexies ):::
          fdoc.flatten( i => i.objectIndexies ):::
          sdoc.objectIndexies
    val p:List[Document] = udoc.flatten( i => i.packageIndexies):::
          fdoc.flatten( i => i.packageIndexies):::
          sdoc.packageIndexies
    ( c, o, p)
  }
  def loadDocument( loader:DocumentLoader ):(
      List[Document],
      List[Document],
      List[Document] ) = {
    val xml = loader.loadXml( "all-classes.html" )
    val pxml = loader.loadXml( "root-content.html" )

    (
      // Init class indexies
      (for( li <- (xml \\ "ul").first \\ "li";
           name = li \\ "a" text;
           desc = li.text.trim;
           path = li \\ "a" \ "@href" text;
           pkg = packageName(path);
           fqcn = pkg + "." + name)yield{
          ClassDocument( loader, fqcn, className( name ) , path, desc )
      }).toList ,
      // Init Object indexies
      (for( li <- (xml \\ "ul").last \\ "li";
           name = li \\ "a" text;
           desc = li.text.trim;
           path = li \\ "a" \ "@href" text;
           pkg = packageName(path);
           fqcn = pkg + "." + name)yield{
          ObjectDocument( loader, fqcn, className( name ) , path, desc )
      }).toList,
      // Init package indexies
      (for( a <- pxml \\ "a";
           name = a.text.trim;
           path = a \ "@href" text)yield{
          PackageDocument( loader, className( name ), path, parentPackage( name ))
      }).toList
    )
  }

  case class Index( loader:DocumentLoader ){
    lazy val ( classIndexies:List[Document] ,
        objectIndexies:List[Document],
        packageIndexies:List[Document] ) = loadDocument( loader )
  }
}


