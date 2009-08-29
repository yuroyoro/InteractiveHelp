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

import Util._

case class PackageDocument(name:String, path:String, fqcn:String ) extends Document {

  def desc = fullname
  def kind = "Package"
  def fullname = fqcn + "." + name
  def displayString =  kind + " " + fullname + "\n" + classes.displayString

  lazy val xml = loader.loadXml( path )

  lazy val classes  = PackageAnalyzer( xml )

  override def length = classes.toSeq.length
  override def elements = classes.toSeq.elements

  def apply(i:Int):Document = classes(i)
  def apply(name:String):Document = classes( name )
  def apply(name:Symbol):Document = classes( name )
}
