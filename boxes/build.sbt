name := "boxes"

organization := "adamchandra"

version := "0.0"

scalaVersion := "2.9.1"

libraryDependencies +=  "org.scalaz" %% "scalaz-core" % "6.0.3"

initialCommands in console := """
  import acs.boxes.Boxes._
  import scalaz._
  import Scalaz._
  println("Boxes imported...")
"""

seq(lsSettings :_*)


