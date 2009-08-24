package com.yuroyoro.interactivehelp

abstract case class ScalaDoc
case class PackageDoc( packageName:String )
case class ClassDoc( packageName:String, className:String)
case class MethodDoc( pacageName:String, className:String, methodName:String)
