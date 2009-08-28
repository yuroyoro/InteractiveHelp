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

object AnalyzerUtil {
  def getWith( n:List[Node] ):List[Node] = n match {
      case x::Nil => Nil
      case x::xs  => if( x.text.trim == "with") xs else getWith(xs )
      case _ => Nil
  }

  def filterEm( xml:List[Node] ):List[Node] = {
    xml.map( node => node match{
        case <em>{ con @ _ }</em> => con
        case <em>{ con @ _* }</em> => { println( "filterEm:match Seq[Node]");con.first}
        case x => x
    })
  }

  def dropTypeParam( n:List[Node] , tc:Int ):List[Node] = {
    def countChar( str:String,c:Char ):Int = str.toArray.filter( _ == c ).size

    n match {
      case x::Nil => x match{
        case a @ <a>{ name @ _*}</a> =>
          if( tc == 0 && isTypeParam( a \ "@href" text )) a::Nil
          else Nil
        case _ => Nil
      }
      case x::xs => x match{
        case a @ <a>{ name @ _*}</a> =>
          if( tc == 0 && isTypeParam( a \ "@href" text ) == false )
             a :: dropTypeParam( xs, tc )
          else
             dropTypeParam( xs, tc )
        case _ =>{
          val str = x.text.trim
          dropTypeParam( xs, tc + countChar( str, '[') - countChar( str, ']'))
        }
      }
      case Nil => Nil
    }
  }

  def getMemberSummary( xml:NodeSeq, memberType:String ) = {
    (for( t <- xml \ "body" \ "table";
         if t \ "@class" == "member";
         if (t \ "tr" \ "td" first).text.trim == memberType + " Summary")yield{
       (t \\ "tr")}).toList.take(1)
     match {
       case Nil => Nil
       case tb::_ => ( tb  \\ "tr").toList.drop(1)
     }
  }

  def getMemberDetail( xml:NodeSeq, memberType:String ) = {
    val div = (xml \ "body").elements.next.child.toList.
      dropWhile( e => e \ "@class" != "member-detail" ).
      dropWhile( e => e.text.trim != memberType + " Details" ).
      drop(1).
      take(1)
    div match {
      case Nil => Nil
      case x::_ => splitDetail( x.first.child.
          takeWhile( e => ("^(dl|a|hr|#PCDATA)$".r findAllIn e.label).hasNext ).toList)
    }
  }

  def splitDetail(xml:List[Node]):List[List[Node]]= {
    xml.span( node => node.label != "hr" ) match {
      case ( Nil, Nil ) => Nil
      case ( Nil, xs ) => splitDetail( xs.drop(1) )
      case ( l, Nil ) => List(l)
      case ( l, xs ) => l::splitDetail( xs.drop(1) )
    }
  }

  def getSignature( xml:Node ):String = {
     val mod =( xml \\ "td" ).first.text.trim.replace("\n", " " )
     val definition =( xml \\ "td" ).last.child.takeWhile( e => e.label != "div" ).
       map( e => e.text.trim.replace( "\n" ,"")).mkString(" ")
     mod + definition
  }

  def getSummaryDescription( xml:Node ):String = {
    val d = ( xml \\ "div" )
    trimCr(
      if( d.isEmpty ) "" else "  " + d.text.lines.map( _.trim ).mkString("\n    "))
  }

  def analizeMethodSigniture( xml:Node ):(
    String, // Method name
    String, // detail path
    Document,// param types
    Document,// return typ
  )={
    val definition =( xml \\ "td" ).last.child.takeWhile( e => e.label != "div" ).toList
    val a =( definition \ "a" ).first
    val name = a text
    val path = a \ "@href" text
    val types = dropTypeParam( filterEm( definition),0)
    val ret = if( types.isEmpty) NoneDocument( (definition \\ "a" ).last.text )
              else fromATag( types.last )
    val params = if( types.isEmpty ) NoneDocument("params")
                 else listToDocument(
                     types.reverse.tail.reverse.map( p => fromATag( p ) ),
                     "params")
    ( name, path,  params , ret )
  }

  def getDetailDescription( det:List[Node] ):String = {
    def trimLine( l:String ) =
      l.lines.filter( s => s.trim != "" ).map( _.trim ).toList

    def joinLine( l:List[String], indent:Int  ) = {
      val sp = " " * indent
      sp + l.mkString("\n" + sp ) + "\n"
    }

    def concat( s1:String, s2:String ) =
      ( if( s1.trim != "" ) s1 + "\n" else "" ) +
      ( if( s2.trim != "" ) s2 + "\n" else "" )

    trimCr(
      det.filter( e => e.label == "dl" ).
        map( e =>{ concat(
            trimLine( ( e \ "dt" ).text ).mkString(" ") ,
            joinLine( trimLine( ( e \ "dd" ).text ) , 2)
        ) }).mkString(""))
  }
}
