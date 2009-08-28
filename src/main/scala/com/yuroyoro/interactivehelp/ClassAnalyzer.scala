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

import _root_.scala.xml._
import Util._
import AnalyaerUtil._

object ClassAnalyaer {

  def apply( path:String ):(
       String,     // class description
       Document,   // inherited classes documents
       Document,   // extends class document
       Document,   // sub classes documents
       Document,   // tarit classes documents
       Document,   // values documents
       Document ) = { // methods documents
    val xml = loader.loadXml( path )
    val fqcn = getFqcn( path )

    def makeDefinition( xml:NodeSeq ):String = {
      val definitions = (xml \ "body" \ "dl").first
      val sig = (definitions \ "dt").text.trim.split("\n").map(_.trim).mkString(" ")
      val ext = (definitions \ "dd").text.trim
      val description = (xml \ "body" \ "dl").drop(1) match{
        case x::_ => x text
        case _ => ""
      }
      val startLine = "=== %s =========================================================".
        format( fqcn ).take(65)
"""
%s
%s
  %s

  %s

================================================================ """.
      format( startLine, sig, ext, description)
    }

    def analizeExtends( xml:NodeSeq ):Document = {
      ((xml \\ "dd" first ) \\ "a" ).toList match {
        case Nil => NoneDocument("extends")
        case a::_ => fromATag( a )
      }
    }

    def analizeSubClasses( xml:NodeSeq ):Document = {
      val ss = for( dl <- xml \\ "dl";
          if (dl \ "dt").text.trim == "Direct Known Subclasses:") yield{dl}
      if( ss.isEmpty ){
        NoneDocument("sub classes")
      }else{
        val ts = for( a <- ss.first \\ "a" ) yield{ fromATag( a )}
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
      seqToDocument( ic, "inherited" )
    }

    def analizeTrait( xml:NodeSeq ):Document = {
      val dd = ( xml \\ "dd" first ).child.toList

      val sc = dropTypeParam( getWith( dd ), 0).map( a => fromATag( a ) )
      listToDocument( sc , "tratis")
    }

    def analizeValues( xml:NodeSeq , fqcn:String):Document = {
      val valueSum = for( t <- xml \ "body" \ "table";
          if t \ "@class" == "member";
          if (t \ "tr" \ "td" first).text.trim == "Value Summary")yield{
        ( t \\ "tr").toList.tail}

      val valueDet = for( t <- xml \ "body" \ "table";
          if t \ "@class" == "member-detail";
          if (t \ "tr" \ "td" first).text.trim == "Value Details")yield{t}

      new DocumentSeq(Nil)
    }

    ( makeDefinition( xml ),
      analizeInherited( xml ),
      analizeExtends( xml ),
      analizeSubClasses( xml ),
      analizeTrait( xml ),
      analizeValues( xml , fqcn ),
      MethodAnalyaer( xml , fqcn )
    )
  }
}
