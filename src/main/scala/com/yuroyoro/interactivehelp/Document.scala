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

import Util._

trait Document {
  def kind:String
  def desc:String
  def fqcn:String
  def shortDesc = "%s %s".format( kind, desc )
  def apply(i:Int):Document
  def apply(name:String):Document
  def apply(name:Symbol):Document

  def e:Document = NoneDocument( "Super class")
  def exntedsClass:Document = e

  def et:Document = NoneDocument( "Super class")
  def extendsTree = et

  def s:Document = NoneDocument("Sub classes")
  def s(i:Int ):Document = NoneDocument("Sub classes")
  def s(name:String):Document = NoneDocument("Sub classes")
  def sub:Document = s
  def sub(i:Int ):Document = s(i)
  def sub(name:String):Document = s(name)

  def v:Document= NoneDocument( "Values and Variables")
  def v(i:Int):Document= NoneDocument( "Values and Variables")
  def v(name:String):Document= NoneDocument( "Values and Variables")
  def values = v
  def value(i:Int) = v(i)
  def value(name:String) = v(name)

  def m:Document= NoneDocument( "Methods")
  def m(i:Int):Document = NoneDocument( "Methods")
  def m(name:String):Document = NoneDocument( "Methods")
  def methods = m
  def method(i:Int) = m(i)
  def method(name:String) = m(name)

  def r:Document = NoneDocument("return type")
  def returnType = r

  def t:Document = NoneDocument( "traits")
  def t(i:Int):Document = NoneDocument( "traits")
  def t(name:String):Document = NoneDocument( "traits")
  def traits = t
  def traits(i:Int) = t(i)
  def traits(name:String) = t(name)
}

case class NoneDocument( name:String) extends Document{
  def kind:String = "None"
  def desc:String = "not found " + name
  def fqcn:String = ""
  def apply(i:Int):Document = this
  def apply(name:String):Document = this
  def apply(name:Symbol):Document = this
}

class DocumentSeq( theSeq:Seq[Document])
  extends Seq[Document] with Document {

  def kind:String = "Seq"
  def desc:String = "found %d".format( theSeq.size )
  def fqcn:String = ""
  val indent = "\n  "
  def length = theSeq.length
  def elements = theSeq.elements
  def apply(i:Int):Document = theSeq.apply(i)
  def apply(name:String):Document = searchDocument(this, name)
  def apply(name:Symbol):Document = null // TODO

  override def toString = length match {
      case 0 => ""
      case _ => indent + toLines + indent
  }
  def toLines:String = List.range(0 , length ).
    zip( theSeq.toList ).
    map( e => "%2d:%s".format(e._1, e._2.shortDesc )).mkString( indent )
}
