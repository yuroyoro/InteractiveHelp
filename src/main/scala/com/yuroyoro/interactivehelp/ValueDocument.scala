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

case class ValueDoc(
  loader:DocumentLoader,
  fqcn:String,
  path:String,
  name:String,
  sig:String,
  header:String,
  desc:String,
  valueClass:Document
) extends Document {
  def kind:String = "Value"
  override def shortDesc = "%s\n  %s\n".format( sig , header )
  def displayString:String = desc

  override def apply(i:Int):Document = this
  override def apply(name:String):Document = this
  override def apply(name:Symbol):Document = this

  override def v:Document= this
  override def v(i:Int):Document= this
  override def v(name:String):Document= this

  override def r:Document = valueClass
  override def o:Unit = loader.openDocument( path )
}
