import _root_.com.yuroyoro.interactivehelp.Export._

// TODO for test
import _root_.com.yuroyoro.interactivehelp.Indexies._
import _root_.com.yuroyoro.interactivehelp.Util._
import _root_.com.yuroyoro.interactivehelp.AnalizerUtil._

import _root_.scala.io.Source
import _root_.scala.xml.parsing.XhtmlParser

val dp ="/Users/ozaki/dev/Java/Scala/scala-2.7.5.final/rt/doc/api/"
val xml = XhtmlParser( Source.fromFile( dp + "scala/List.html") )

