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

trait ScalaDoc extends Document {
  // load scaladoc as xthml and analize it.
  lazy val( header,     // class documentation
       inheritedDocs,   // inherited classes documents
       extendsDoc,  // extends class document
       subclassDoc, // sub classes documents
       traitsDocs,  // tarit classes documents
       valueDocs,   // values documents
       methodDocs   // methods documents
   ) =  ClassAnalizer( path )

  override def toString = header

  def apply(i:Int) = null //TODO  m(i)
  def apply(name:String) = null //TODO m(name)
  def apply(name:Symbol):Document = null //TODO m(name)

  def path:String
  override def e:Document = extendsDoc
  override def et:DocumentSeq = null // TODO

  override def s:Document = subclassDoc
  override def s(i:Int ):Document = subclassDoc(i)
  override def s(name:String):Document = searchDocument( subclassDoc , name )

  override def v:Document= null // TODO
  override def v(i:Int):Document= null // TODO
  override def v(name:String):Document = null // TODO
  override def m:DocumentSeq = null // TODO
  override def m(i:Int):Document = null // TODO
  override def m(name:String):DocumentSeq = null // TODO
  override def t:Document = traitsDocs
  override def t(i:Int):Document = traitsDocs(i)
  override def t(name:String):Document = searchDocument( traitsDocs, name )
}

case class ClassDocument(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends ScalaDoc {
  def kind = "Class"
  override def shortDesc = "%s %s".format( kind, fqcn)
}

case class ObjectDocument(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends ScalaDoc {
  def kind = "Object"
  override def shortDesc = "%s %s".format( kind, fqcn)
}
