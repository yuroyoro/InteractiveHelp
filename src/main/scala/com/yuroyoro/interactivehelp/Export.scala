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

import scala.reflect.Manifest

import Indexies._
import Util._

object Export{

  def h():Unit = {
    // TODO usage
    println("""
Usage:
""")
  }

  def matchName( name:String , d:Document) = {
      d.name.compareToIgnoreCase( name ) == 0 ||
      d.pkg.compareToIgnoreCase( name ) == 0 ||
      d.fqcn.compareToIgnoreCase( name ) == 0 }

  def startsWithIgnoreCase( s1:String, s2:String ) =
    s1.toLowerCase.startsWith( s2.toLowerCase )

  def startsWithName( name:String , d:Document ) = {
    startsWithIgnoreCase( d.name, name ) ||
    startsWithIgnoreCase( d.pkg, name ) ||
    startsWithIgnoreCase( d.fqcn, name )}

  /** find scaladoc by name.  */
  def h( name:String ):Document = {
    val f = matchName( name, _:Document )
    seqToDocument(
      classIndexies.filter( f ) ++
      objectIndexies.filter( f ) ++
      packageIndexies.filter( f ),
      name
    )
  }
  def help( name:String ) = h( name )

  /** search scaladoc by name */
  def h( name:Symbol ):Document ={
    val n = name.toString.drop(1)
    val f = startsWithName( n, _:Document )
    seqToDocument(
      classIndexies.filter( f ) ++
      objectIndexies.filter( f ) ++
      packageIndexies.filter( f ),
      n
    )
  }
  def help( name:Symbol ) = h( name )

  /** search scaladoc by name */
  def h[T](obj:T)( implicit m: Manifest[T] ):Document = {
    val name = removeTypeParam( className( m.toString ) )

    if( name.endsWith( "$" ) )
      oh( convertJavaClassName( name ) )
    else
      ch( convertJavaClassName( name ) )
  }
  def help[T]( obj:T )( implicit m: Manifest[T] ):Document = h( obj )( m )

  /** find object documents by name.*/
  def oh( name:String ):Document =
    seqToDocument( objectIndexies.filter( i => i.name == name || i.fqcn == name ) , name)

  /** find class documents by name.*/
  def ch( name:String ):Document =
    seqToDocument( classIndexies.filter( i => i.name == name || i.fqcn == name ) , name)

  /** find package documents by name.*/
  def ph( name:String ):Document =
    seqToDocument( packageIndexies.filter( i => i.name == name || i.fqcn == name ) , name)

  def addFiles( paths:String* ) = paths.foreach( path => Indexies.docPath += path )
  def addUrls( paths:String* ) = paths.foreach( path => Indexies.docUrl += path )

}
