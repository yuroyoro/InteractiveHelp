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
import AnalyzerUtil._

object ValueAnalyzer {
  def apply(loader:DocumentLoader,  xml:NodeSeq, fqcn:String ):Document = {
    val sum = getMemberSummary( xml , "Value")
    val det = getMemberDetail( xml, "Value")
    val res = sum.zip( det ).map( t  =>{
      val s = t._1
      val d = t._2
      val sig = getSignature( s )
      val header = getSummaryDescription( s )
      val ( name, path, param, ret ) = analizeMethodSigniture( s )
      val detail = getDetailDescription( d )
      ValueDoc( loader, fqcn, path, name, sig, header, detail, ret )
    })

    listToDocument( res , "value" )
  }
}
