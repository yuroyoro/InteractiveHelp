package com.yuroyoro.interactivehelp


import _root_.scala.tools.nsc.MainGenericRunner


object Main {

  lazy val ( classIndexies , objectIndexies, packageIndexies ) = DocumentIndex.load

  def main(args:Array[String]):Unit = {
    MainGenericRunner.main( args ++ Array("-i" , "import.scala"))
    exit(0)
  }

  def h(name:String)= {
    val res = classIndexies.filter( i=> i.name == name || i.fqcn == name ) ++
      objectIndexies.filter( i => i.name == name || i.fqcn == name ) ++
      packageIndexies.filter( i => i.name == name )
    res size match {
      case 0 => NoneIndex( name )
      case 1 => res(0)
      case _ => new IndexSeq( res )
    }
  }
}

