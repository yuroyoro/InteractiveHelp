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

object Indexies{

  // intialize indexies.indexies use to find documents by name.
  lazy val ( classIndexies , objectIndexies, packageIndexies ) = {
    val xml = loader.loadXml( "all-classes.html" )
    val pxml = loader.loadXml( "root-content.html" )

    (
      // Init class indexies
      for( li <- (xml \\ "ul").first \\ "li";
           name = li \\ "a" text;
           desc = li.text.trim;
           path = li \\ "a" \ "@href" text;
           pkg = packageName(path);
           fqcn = pkg + "." + name)yield{
          ClassDocument(pkg, fqcn, className( name ) , path, desc )
      } ,
      // Init Object indexies
      for( li <- (xml \\ "ul").last \\ "li";
           name = li \\ "a" text;
           desc = li.text.trim;
           path = li \\ "a" \ "@href" text;
           pkg = packageName(path);
           fqcn = pkg + "." + name)yield{
          ObjectDocument(pkg, fqcn, className( name ) , path, desc )
      },
      // Init package indexies
      for( a <- pxml \\ "a";
           name = a.text.trim;
           path = a \ "@href" text)yield{
          PackageDocument( name, path)
      }
    )
  }
}

