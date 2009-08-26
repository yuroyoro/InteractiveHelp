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

