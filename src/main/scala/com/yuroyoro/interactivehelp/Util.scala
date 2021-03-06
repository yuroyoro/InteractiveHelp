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

import _root_.scala.xml.Node
import java.awt.Desktop
import java.net.URI

import Indexies._

object Util {

  /** convert filepath to package name. */
  def packageName( path:String ) = ("""([^/]+)""".r findAllIn path).
        toList.reverse.tail.reverse.filter( _ != "..").mkString(".")

  def parentPackage( fqcn:String ) =
    fqcn.split("""\.""").reverse.toList.tail.reverse.mkString(".")

  /** get class name without type parameters. */
  def className( name:String ) =
    ( """([^.]+)""".r findAllIn removeTypeParam( name ) ).toList.last

  /** remove type paremeters. */
  def removeTypeParam( name:String ) = """(\[.+\])""".r.replaceAllIn( name, "")

  /** is filepath link to object? */
  def isObject( path:String ) = """\$object\.html""".r findAllIn path hasNext

  /** is java class */
  def isJava( name:String ) = name.startsWith("java.")||name.startsWith("javax.")

  /** make fqcn from filepath. */
  def getFqcn( path:String ) = ("""([^/]+)""".r findAllIn path).toList.
        filter( _ != "..").mkString(".").
        replace("$object", "").
        replace("$colon", ":").
        replace(".html", "")

  /** is path link to Type parameter? */
  def isTypeParam( path:String) = """\.html\#[\S]+$""".r findAllIn path hasNext

  def matchName( name:String , d:Document) = {
      d.name.compareToIgnoreCase( name ) == 0 ||
      d.pkg.compareToIgnoreCase( name ) == 0 ||
      d.fqcn.compareToIgnoreCase( name ) == 0 }

  def startsWithIgnoreCase( s1:String, s2:String ) =
    s1.toLowerCase.startsWith( s2.toLowerCase )

  /** opne url on browser */
  lazy val desktop = Desktop.getDesktop
  def open( url:String ):Unit = desktop.browse( new URI( url ) )

  /** Utility method search indexies by filepath and name */
  def fromPath( path:String, name:String):Document  = {
    if( isJava( name ) ){
      JavaDocument( name, className( name ), path )
    }else{
      val cn = getFqcn( path )
      val res = if( isObject( path ) ) objectIndexies.filter( _.fqcn == cn)
       else classIndexies.filter( _.fqcn == cn)

      seqToDocument( res, name )
    }
  }

  /** Utility method search Document by a tag */
  def fromATag( a:Node ):Document = fromPath( a \\ "@href" text, a.text )

  /** Utility method search Document by name */
  def searchDocument(doc:Document, name:String) = doc match{
    case n:NoneDocument => NoneDocument(name)
    case seq:DocumentSeq => seqToDocument( seq.filter(d => matchName( name, d) ), name )
    case d:ScalaDoc => if( matchName( name, d) ) d else NoneDocument( name )
  }

  /** Utility method search Document starts with  name */
  def searchDocument( doc:Document, name:Symbol ) =
    seqToDocument( doc.filter( d =>
      d.name.startsWith( name.toString.drop(1) ) ), name.toString)

  def seqToDocument( seq:Seq[Document], name:String ):Document = {
    listToDocument( seq.toList, name )
  }

  def listToDocument( list:List[Document], name:String ):Document = {
    list match{
      case Nil => NoneDocument( name )
      case x::Nil => x
      case xs => new DocumentSeq(xs )
    }
  }

  def trimCr( s:String ) = {

    def tc( l:List[String] ):List[String] = l match {
      case Nil => Nil
      case x::Nil => x::Nil
      case x::xs if x.trim == "" && xs.first.trim == "" => tc( xs.tail )
      case x::xs => x::tc( xs )
    }
    (tc( s.lines.toList.dropWhile( _.trim == "") ) match {
      case Nil => Nil
      case x if x.last.trim == "" => x.reverse.tail.reverse
      case x => x
     }).mkString( "\n" )
  }

  def convertJavaClassName( s:String ) =
    ( if( s.endsWith( "$" ) ) s.reverse.drop(1).reverse.toString else s).replace("$", ".")

}
