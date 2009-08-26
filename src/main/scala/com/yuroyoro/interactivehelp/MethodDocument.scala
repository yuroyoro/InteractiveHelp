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

import _root_.scala.xml.NodeSeq

case class MethodSeq( theSeq:Seq[MethodDoc] )
  //extends ScalaDoc with Seq[MethodDoc]
{

  //def length = theSeq.length
  //override def elements = theSeq.elements
  //override def apply(i: Int):MethodDoc = theSeq.apply(i)

}
case class MethodDoc( sXml:NodeSeq, dXml:NodeSeq )
  //extends ScalaDoc{
//}
