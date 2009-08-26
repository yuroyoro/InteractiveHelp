package com.yuroyoro.interactivehelp

import Util._

object ClassAnalizer {

  def apply( path:String ):(String,
       Document,   // inherited classes documents
       Document,   // extends class document
       Document,   // sub classes documents
       Document,   // tarit classes documents
       Document,   // values documents
       Document ) ={ // methods documents
    val xml = loader.loadXml( path )
    val fqcn = getFqcn( path )

    val definitions = (xml \ "body" \ "dl").first
    val sig = (definitions \ "dt").text.trim.split("\n").map(_.trim).mkString(" ")
    val ext = (definitions \ "dd").text.trim
    val description = (xml \ "body" \ "dl").drop(1) match{
      case x::Nil => x text
      case x::xs  => x text
      case _ => ""
    }
    val inherited = {
      val ic = for( t <- xml \\ "table";
        if ( t \ "@class" == "inherited");
        a = (t \\ "tr" first) \\ "a";
        path = a \\ "@href" text;
        name = a.text ;
        if isTypeParam( path ) == false ) yield{
          fromPath( path, name)
      }
      ic.toList match {
        case Nil => NoneDocument("inherited")
        case x::Nil => x
        case xs => new DocumentSeq(xs)
      }
    }
    val extendsClass = {
      val a = ((xml \\ "dd" first ) \\ "a" )
      if( a.isEmpty )
        NoneDocument("extends")
      else
        fromPath( a.first \\ "@href" text ,  a.first.text )
    }
    val subClass = {
      val ss = for( dl <- xml \\ "dl";
          if (dl \ "dt").text.trim == "Direct Known Subclasses:") yield{dl}
      if( ss.isEmpty ){
        NoneDocument("sub classes")
      }else{
        val ts = for( a <- ss.first \\ "a"; path = a \\ "@href" text; name = a text) yield{
          fromPath( path ,  name ) }
        ts match {
          case x::Nil => x
          case xs => new DocumentSeq(xs)
        }
      }
    }
    val traitsClass = {
      val as = ((xml \\ "dd" first ) \\ "a" toList )
      if( as.isEmpty ){
        NoneDocument("traits")
      }else{
        val ts = for( a <- as.tail ; path = a \\ "@href" text;
                      name = a text; if isTypeParam( path ) == false) yield{
            fromPath( path , name ) }
        ts.filter( tc => searchDocument( tc.s, fqcn) match{
            case s:ScalaDoc => true
            case _ => false
        }) match {
          case x::Nil => x
          case xs => new DocumentSeq(xs)
        }
      }
    }

    val valueSum = for( t <- xml \ "body" \ "table";
        if t \ "@class" == "member";
        if (t \ "tr" \ "td" first).text.trim == "Value Summary")yield{
      ( t \\ "tr").toList.tail}

    val valueDet = for( t <- xml \ "body" \ "table";
        if t \ "@class" == "member-detail";
        if (t \ "tr" \ "td" first).text.trim == "Value Details")yield{t}

    val methodSum = for( t <- xml \ "body" \ "table";
        if t \ "@class" == "member";
        if (t \ "tr" \ "td" first).text.trim == "MethodSummary")yield{
      (t \\ "tr").toList.tail}

    val methodDet = for( t <- xml \ "body" \ "table";
        if t \ "@class" == "member-detail";
        if (t \ "tr" \ "td" first).text.trim == "Method Details")yield{t}

    ( "== %s ==\n".format( fqcn ) + List( sig, ext, "", description).mkString("\n  "),
      inherited, extendsClass, subClass, traitsClass,
      new DocumentSeq(Nil), new DocumentSeq(Nil)
    )
  }
}
