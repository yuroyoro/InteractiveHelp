InteractiveHelp -- scaladoc seach tool on scala interpreter.
===========================================================

InteractiveHelpは、scalaインタプリタ上でscaladocを検索できるように
するツールです。
インタプリタ上でコマンドを入力することで、クラスやメソッドや
パッケージのドキュメントを参照することができます。

## インストール

InteractiveHelpは、Java6以上と、scala2.8.5finalで動作します。
インストールの手順は、以下のとおりです。

- ソースコードをダウンロードします。
  gitが利用できる環境であれば、任意のディレクトリで以下のコマンドを
  入力することでソースコードを取得することができます。

    $ git clone git://github.com/yuroyoro/interactivehelp.git

  gitが利用できない場合はこのページの上にある"download"ボタンを
  クリックして圧縮ファイルを取得し、任意のディレクトリに解凍します。

- 添付のScalaDocを解凍します。
  ソースコードに含まれているscala-2.7.5-apidocs-fixed.zipを解凍します。
  InteractiveHelpでは、scaladocをXHTMLとして読み込み検索するようになっていますが、
  標準のscala api documentの一部はXHTMLとして読み込むとエラーになるので、
  修正したものを利用します。

- 環境変数SCALA_DOC_HOMEを設定します。
  解凍して出来たディレクトリを、環境変数SCALA_DOC_HOMEに設定します。

以上で設定は完了です。

## 使い方
scalaインタプリタを起動する際に、interactive-help-1.0.jarを
クラスパスに入れて起動します

  $ scala -cp ./interactive-help-1.0.jar

次に、Helpオブジェクトをインポートします。

  scala> import com.yuroyoro.interactivehelp.Export._

これでヘルプが利用できるようになりました。
以下のように入力してみましょう。

  scala> h("List")
     0:Class scala.List
     1:Object scala.List

     res0: com.yuroyoro.interactivehelp.Document = Seq

"List"に一致するクラス/オブジェクトの一覧が表示されました。
Listクラスの詳細を調べたい場合は以下の用に入力します。

  scala> h("List")(0)
  scala> h("List")(0)
  === scala.List ==================================================
  sealed abstract class List[+A]
    extends Seq[A] with Product

    A class representing an ordered collection of elements of type
    a. This class comes with two implementing case
    classes scala.Nil and scala.:: that
    implement the abstract members isEmpty,
    head and tail.

  ================================================================

このように、Listクラスの詳細が出力されます。
Listクラスのもつメソッドを調べたい場合は、続けてmと入力します。

  scala> h("List")(0).m
    0:def + [ B  > : A ]( x : B ) : List [ B ]
    1:override def ++ [ B  > : A ]( that : Iterable [ B ]) : List [ B ]
      Appends two list objects.
    ...

メソッドの一覧が出力されました。
さらに、個々のメソッドの詳細をみたい場合は、対応する番号を
続けて指定すればOKです。

  scala> h("List")(0).m(1)
  override def ++[B >: A](that : Iterable[B]) : List[B]
    Appends two list objects.

  Overrides
    Seq.++

このように、コマンドを入力することでscaladocを検索することが
可能です。

さらに、つづけて.oと入力すると、ブラウザが起動して対応するscaladocを
見ることが可能です。

  scala> h("List")(0).m(1).o

もっと詳しい使い方は、インタプリタでh()と入力するか、
下記のコマンド一覧を参照してください。

## もっと簡単に使うには
毎回scalaインタプリタを起動するたびに、クラスパスを指定して、
importを入力するのは面倒ですよね。

scalaコマンドのエイリアスをこのように指定しておくと、
常にヘルプが利用可能な状態でインタプリタが起動します。

  alias -i <InteraciveHelpのパス>/import.scala -cp <InteraciveHelpのパス>/interactive-help-1.0.jar


## コマンド一覧

InteractiveHelpがサポートするコマンドは以下の通りです。

コマンドが引数を取る場合は、Int、String, Symbolのいづれかを
指定することが可能です。
Intの場合はN番目の要素を表示します。
Stringの場合はクラス名やメソッド名などが完全一致するものを返します。
Symbolの場合は、クラス名などが前方一致するものを返します。

コマンド 引数               説明
-------- ----------------- ----------------------------------------------
h        なし              コマンドの説明を表示します。
h        Int/String/Symbol クラス、オブジェクト、パッケージを検索します。
ch       Int/String/Symbol クラスを検索します。
oh       Int/String/Symbol オブジェクトを検索します。
ph       Int/String/Symbol パッケージを検索します。

検索結果が複数ある場合は、さらにInt、String, Symbolのいづれかを
指定して絞り込むことが可能です。
また、List("scala").lとすると、改行せずに一覧を出力します。
検索結果はすべてSeq[Document]型ですので、mapやfilterなどが
利用できます。

検索結果が1件になると、そのクラスやオブジェクトの詳細が
表示されます。
この状態では、さらに以下のコマンドを指定することができます。

コマンド 引数               説明
-------- ----------------- ----------------------------------------------
o        なし              対応するscaladocをブラウザで表示します。
e        なし              スーパークラスを返します。
et       なし              スーパークラスの一覧を返します。
et       Int/String/Symbol スーパークラスの一覧を検索します。
s        なし              サブクラスの一覧を返します。
s        Int/String/Symbol サブクラスの一覧を検索します。
t        なし              traitの一覧を返します。
t        Int/String/Symbol traitの一覧を検索します。
m        なし              メソッドの一覧を返します。
m        Int/String/Symbol メソッドの一覧を検索します。
v        なし              valueの一覧を返します。
v        Int/String/Symbol valueの一覧を検索します。

Value, メソッドに対してはさらに以下のコマンドを利用できます。
コマンド 引数               説明
-------- ----------------- ----------------------------------------------
r        なし              戻り値またはvalueの型を返します。
p        なし              メソッドのみ。引数の型の一覧を返します。
p        Int/String/Symbol メソッドのみ。引数の型の一覧を検索します。

## 他のscaladocを検索できるようにするには
他のscaladocを検索できるようにするには、以下のように
addFilesメソッドでscaladocのあるディレクトリを指定します。

  scala> addFiles( "/your_scaladoc_path/", "/your_scaladoc_path2"...)

addUrlsでは、URLを追加することでhttpでドキュメントを取得出来ます。

  scala> addUrls("http://somedoc.url", ...)

注意しなければならないのは、追加するHTMLファイルはXHTMLとして解釈できるもので
なければならないと言うことです。
タグの閉じ忘れなどがあると、エラーが発生します。

## ビルドするには

interactivehelpをビルドするためには、java6とmaven2が必要です。
また、ソースコードを取得するためにgitが利用できることが
望ましいです

ソースコードからinteractivehelpをビルドするには、
単純に以下のようなmavenコマンドを入力するだけです。

  $ mvn package

ビルドが終わると、targetディレクトリにjarファイルが生成されているハズです。

