package com.yuroyoro.interactivehelp

object DocumentIndex{
  lazy val loader:DocumentLoader = DocumentLoader.load

  def load = {
    val xml = loader.loadXml( "all-classes.html" )
    val pxml = loader.loadXml( "root-content.html" )
    def packageName( path:String ) = ("""([^/]+)""".r findAllIn path).toList.reverse.tail.reverse.mkString(".")
    def className( name:String ) = ( """([^.]+)""".r findAllIn name ).toList.last

    (
      // Init class indexies
      for( li <- (xml \\ "ul").first \\ "li";
           name = li \\ "a" text;
           desc = li.text.trim;
           path = li \\ "a" \ "@href" text;
           pkg = packageName(path);
           fqcn = pkg + "." + name)yield{
          ClassIndex(pkg, fqcn, className( name ) , path, desc )
      } ,
      // Init Object indexies
      for( li <- (xml \\ "ul").last \\ "li";
           name = li \\ "a" text;
           desc = li.text.trim;
           path = li \\ "a" \ "@href" text;
           pkg = packageName(path);
           fqcn = pkg + "." + name)yield{
          ObjectIndex(pkg, fqcn, className( name ) , path, desc )
      },
      // Init package indexies
      for( a <- pxml \\ "a";
           name = a.text.trim;
           path = a \ "@href" text)yield{
          PackageIndex( name, path)
      }
    )
  }
}

abstract class DocumentIndex

trait IndexElement{
  def desc:String
  def kind:String
  def toLine(i:Int) = "%2d: %s %s ".format(i, kind, desc)
  override def toString = desc
}

trait DocumentWrapper{
  val loader = DocumentIndex.loader
  def doc:ScalaDoc
  def apply(i:Int) = doc(i)
}

case class ClassIndex(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends DocumentIndex with IndexElement with DocumentWrapper{
    def kind = "Class"
    lazy val scalaDoc = ClassDoc( loader.loadXml( path ) )
    def doc:ScalaDoc = scalaDoc
}

case class ObjectIndex(pkg:String, fqcn:String, name:String, path:String, desc:String)
  extends DocumentIndex with IndexElement with DocumentWrapper{
    def kind = "Object"
    lazy val scalaDoc = ClassDoc( loader.loadXml( path ) )
    def doc:ScalaDoc = scalaDoc
}

case class PackageIndex(name:String, path:String )
  extends DocumentIndex with IndexElement with DocumentWrapper{
    def desc = name
    def kind = "Package"
    lazy val scalaDoc = PackageDoc( loader.loadXml( path ) )
    def doc:ScalaDoc = scalaDoc
}

case class NoneIndex(name:String) extends DocumentIndex{
  override def toString = "not found:%s" format name
}

class IndexSeq( theSeq:Seq[IndexElement])
  extends DocumentIndex with Seq[IndexElement]{
  val indent = "\n  "
  def length = theSeq.length
  override def elements = theSeq.elements
  override def apply(i: Int):IndexElement = theSeq.apply(i)

  override def toString = length match {
      case 0 => ""
      case _ => indent + toLines + indent
  }
  override def toLine(i:Int) = this(i).toLine(i)
  def toLines:String = List.range(0 , length ).
    zip( theSeq.toList ).
    map( e => e._2.toLine(e._1) ).mkString( indent )
}


