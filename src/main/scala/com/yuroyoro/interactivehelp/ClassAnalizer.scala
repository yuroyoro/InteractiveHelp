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

import scala.xml._
import Util._

object ClassAnalizer {

  def apply( path:String ):(String,
       Document,   // inherited classes documents
       Document,   // extends class document
       Document,   // sub classes documents
       Document,   // tarit classes documents
       Document,   // values documents
       Document ) ={ // methods documents
    val xml = loader.loadXml( path )
    val fqcn = getFqcn( path )

    val definitions = (xml \ "body" \ "dl").first
    val sig = (definitions \ "dt").text.trim.split("\n").map(_.trim).mkString(" ")
    val ext = (definitions \ "dd").text.trim
    val description = (xml \ "body" \ "dl").drop(1) match{
      case x::Nil => x text
      case x::xs  => x text
      case _ => ""
    }
    val inherited = analizeInherited( xml )
    val extendsClass = analizeExtends( xml )
    val subClass = analizeSubClasses( xml )
    val traitsClass = analizeTrait( xml )
    val values = analizeValues( xml )
    val methods = analizeMethods( xml )


    ( "== %s ==\n".format( fqcn ) + List( sig, ext, "", description).mkString("\n  "),
      inherited, extendsClass, subClass, traitsClass,
      new DocumentSeq(Nil), new DocumentSeq(Nil)
    )
  }

  def analizeExtends( xml:NodeSeq ):Document = {
    val a = ((xml \\ "dd" first ) \\ "a" )
    if( a.isEmpty )
      NoneDocument("extends")
    else
      fromPath( a.first \\ "@href" text ,  a.first.text )
  }

  def analizeSubClasses( xml:NodeSeq ):Document = {
    val ss = for( dl <- xml \\ "dl";
        if (dl \ "dt").text.trim == "Direct Known Subclasses:") yield{dl}
    if( ss.isEmpty ){
      NoneDocument("sub classes")
    }else{
      val ts = for( a <- ss.first \\ "a"; path = a \\ "@href" text; name = a text) yield{
        fromPath( path ,  name ) }
      ts match {
        case x::Nil => x
        case xs => new DocumentSeq(xs)
      }
    }
  }

  def analizeInherited( xml:NodeSeq ):Document = {
    val ic = for( t <- xml \\ "table";
      if ( t \ "@class" == "inherited");
      a = (t \\ "tr" first) \\ "a";
      path = a \\ "@href" text;
      name = a.text ;
      if isTypeParam( path ) == false ) yield{
        fromPath( path, name)
    }
    ic.toList match {
      case Nil => NoneDocument("inherited")
      case x::Nil => x
      case xs => new DocumentSeq(xs)
    }
  }

  def analizeTrait( xml:NodeSeq ):Document = {
    val dd = ( xml \\ "dd" first ).child.toList
    def wt( n:List[Node] ):List[Node] = n match {
        case x::Nil => Nil
        case x::xs  => if( x.text.trim == "with") xs else wt(xs )
        case _ => Nil
    }

    def w( n:List[Node] , tc:Int ):List[Node] = {
      val rs = """^(\[.*)$""".r
      val re = """^(.*\])$""".r
      def countChar( str:String,c:Char ):Int = str.toArray.filter( _ == c ).size

        println( " " + tc + ":" + ( if( n.isEmpty) "" else n.first.text ))
      n match {
        case x::Nil => x match{
          case a @ <a>{ name @ _*}</a> =>
            if( tc == 0 ) a::Nil else Nil
          case _ => Nil
        }
        case x::xs => x match{
          case a @ <a>{ name @ _*}</a> =>
            if( tc == 0 ) a :: w( xs, tc ) else w( xs, tc )
          case _ => x.text.trim match {
            case rs( str ) => w( xs, tc + countChar( str, '[') )
            case re( str ) => w( xs, tc - countChar( str, ']') )
            case _ => w( xs, tc )
          }
        }
        case Nil => Nil
      }
    }
    val sc = w( wt( dd ), 0).map( a => fromPath( a \\ "@href" text, a.text ) )
    sc match{
      case Nil => NoneDocument("inherited")
      case x::Nil => x
      case xs => new DocumentSeq(xs)
    }
  }

  def analizeValues( xml:NodeSeq ):Document = {
    val valueSum = for( t <- xml \ "body" \ "table";
        if t \ "@class" == "member";
        if (t \ "tr" \ "td" first).text.trim == "Value Summary")yield{
      ( t \\ "tr").toList.tail}

    val valueDet = for( t <- xml \ "body" \ "table";
        if t \ "@class" == "member-detail";
        if (t \ "tr" \ "td" first).text.trim == "Value Details")yield{t}
    null
  }

  def analizeMethods( xml:NodeSeq ):Document = {
    val methodSum = for( t <- xml \ "body" \ "table";
        if t \ "@class" == "member";
        if (t \ "tr" \ "td" first).text.trim == "MethodSummary")yield{
      (t \\ "tr").toList.tail}

    val methodDet = for( t <- xml \ "body" \ "table";
        if t \ "@class" == "member-detail";
        if (t \ "tr" \ "td" first).text.trim == "Method Details")yield{t}
    null
  }

}
