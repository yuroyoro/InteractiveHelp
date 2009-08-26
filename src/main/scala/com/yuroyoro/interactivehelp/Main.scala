package com.yuroyoro.interactivehelp

import _root_.scala.tools.nsc.MainGenericRunner

object Main {

  def main(args:Array[String]):Unit = {
    MainGenericRunner.main( args ++ Array("-i" , "import.scala"))
    exit(0)
  }
}

