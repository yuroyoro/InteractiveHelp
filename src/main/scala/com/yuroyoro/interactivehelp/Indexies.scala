package com.yuroyoro.interactivehelp

object Indexies{

  /** ファイルへのパスからパッケージ名に変換する */
  def packageName( path:String ) = ("""([^/]+)""".r findAllIn path)
    .toList.reverse.tail.reverse.filter( _ != "..").mkString(".")

  /** 型パラメータ付きのクラス名からクラス名のみ抽出する */
  def className( name:String ) = ( """([^.]+)""".r findAllIn name )
  .toList.last
  /** パスがObjectへのリンクか判定する */
  def isObject( path:String ) = """\$object\.html""".r findAllIn path hasNext

  /** パスと名前からFQCNを生成する */
  def fqcn( path:String, name:String) = packageName( path ) + "." + className( name )

  // xhtmlを読み込むためのDocumentLoader
  lazy val loader:DocumentLoader = DocumentLoader.load

  // 検索で使用するIndexの初期化
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

  /**
   * 名前からドキュメントを検索する。
   * 名前が完全修飾パッケージ名、完全修飾クラス名、クラス名に
   * 一致したものを返す。
   * 複数一致する場合は一覧を返す。
   */
  def h(name:String)= {
    result( name,
      classIndexies.filter( i=> i.name == name || i.fqcn == name ) ++
      objectIndexies.filter( i => i.name == name || i.fqcn == name ) ++
      packageIndexies.filter( i => i.name == name )
    )
  }

  def oh( name:String ) =
    result( name, objectIndexies.filter( i => i.name == name || i.fqcn == name ) )

  def ch( name:String ) =
    result( name, objectIndexies.filter( i => i.name == name || i.fqcn == name ) )

  def ph( name:String ) =
    result( name, objectIndexies.filter( i => i.name == name || i.fqcn == name ) )

  def result( name:String, res:Seq[Document] ) = {
    res size match {
      case 0 => "not found:%s" format name
      case 1 => res.first
      case _ => new IndexSeq( res )
    }
  }

  def fromPath( path:String, name:String) = {
    val cn = fqcn( path, name)
    (if( isObject( path ) ) objectIndexies.filter( _.fqcn == name )
     else classIndexies.filter( _.fqcn == name )) match{
      case x::_ => x
      case Nil => null
    }
  }
}

trait Document {
  def kind:String
  def desc:String
  def shortDesc = "%s %s".format( kind, desc )
  val loader = Indexies.loader
  def apply(i:Int):Document
  def apply(name:String):Document
}

class IndexSeq( theSeq:Seq[Document])
  extends Seq[Document] with Document {

  def kind:String = ""
  def desc:String = ""
  val indent = "\n  "
  def length = theSeq.length
  def elements = theSeq.elements
  def apply(i:Int):Document = theSeq.apply(i)
  def apply(name:String):Document = null // TODO

  override def toString = length match {
      case 0 => ""
      case _ => indent + toLines + indent
  }
  def toLines:String = List.range(0 , length ).
    zip( theSeq.toList ).
    map( e => "%2d:%s".format(e._1, e._2.shortDesc )).mkString( indent )
}

