name := "bank-ocr"

organization := "adamchandra"

version := "0.0"

scalaVersion := "2.9.1"

libraryDependencies +=  "org.scalaz" %% "scalaz-core" % "6.0.3"


seq(lsSettings :_*)


libraryDependencies += "net.databinder" %% "dispatch-core" % "0.8.7"


libraryDependencies += "net.databinder" %% "unfiltered-util" % "0.5.3"
