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

import Indexies._

object Util {

  /** convert filepath to package name. */
  def packageName( path:String ) = ("""([^/]+)""".r findAllIn path)
    .toList.reverse.tail.reverse.filter( _ != "..").mkString(".")

  /** get class name without type parameters. */
  def className( name:String ) = ( """([^.]+)""".r findAllIn name )
  .toList.last

  /** is filepath link to object? */
  def isObject( path:String ) = """\$object\.html""".r findAllIn path hasNext

  /** make fqcn from filepath. */
  def getFqcn( path:String ) = ("""([^/]+)""".r findAllIn path).toList
    .filter( _ != "..").mkString(".")
    .replace("$object", "")
    .replace("$colon", ":")
    .replace(".html", "")

  /** is path link to Type parameter? */
  def isTypeParam( path:String) = """\.html\#[\S]+$""".r findAllIn path hasNext

  // xml file loader object
  lazy val loader:DocumentLoader = DocumentLoader.load
  /** Utility method search indexies by filepath and name */
  def fromPath( path:String, name:String):Document  = {
    val cn = getFqcn( path )
    println( "fromPath %s %s %s".format( path, name, cn))
    val res = if( isObject( path ) ) objectIndexies.filter( _.fqcn == cn)
     else classIndexies.filter( _.fqcn == cn)
    res.toList match{
      case x::_ => x
      case Nil => println("res == Nil");println(res);NoneDocument( name )
      case _ => println("res == _");println(res);NoneDocument( name )
    }
  }
  /** Utility method search Document by name */
  def searchDocument(doc:Document, name:String) = doc match{
    case n:NoneDocument => NoneDocument(name)
    case seq:DocumentSeq => {
      val res = seq.filter(e => e match {
          case s:ScalaDoc => ( s.fqcn == name )
          case _ => false })
      res.toList  match{
          case Nil => NoneDocument( name )
          case x::Nil => x
          case xs => {
            println("search seq -------------")
            println(xs.getClass )
            println("--------------")
            new DocumentSeq(xs )
          }
        }
    }
    case d:ScalaDoc => if( d.fqcn == name )d else NoneDocument( name )
  }
}