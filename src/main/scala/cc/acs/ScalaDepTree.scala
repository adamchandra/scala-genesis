package cc.acs

import java.io.File
import cc.acs.commons.util.FileOps._
import cc.acs.commons.util.StringOps._
import scala.collection.mutable.{HashMap => Map}
import scala.collection.MapLike
import scala.io.Source

object ScalaDepTree {
  type MultiMap = Map[String, List[String]]

  def parseDepfile(f:File):MultiMap = {
    val openRE = "\\s*digraph\\s+[^{]\\{".r
    val qstr = "\"([^\"]+)*\""
    val depRE = ("\\s*" + qstr + "\\s*->\\s*" + qstr + "\\s*").r

    var deps = Map[String, List[String]]()
    val src = Source.fromFile(f)
    for (l <- src.getLines) try {
      val depRE(left, right) = l
      deps.update(left, right :: deps.getOrElseUpdate(left, Nil))
    }
    catch {
      case e: MatchError => // ok, just skip
    }
    deps
  }

  def depCounts(srcDeps:MultiMap, srcExternalDeps:MultiMap):List[(Int, String)] = {
    var depcount = Map[String, Int]()

    for (sd <- srcDeps) sd match {
      case (s, ds) => for (d <- ds) 
        depcount.update(d, depcount.getOrElseUpdate(d, 0) + 1)
    }

    for (ed <- srcExternalDeps) ed match {
      case (s, ds) => depcount.getOrElseUpdate(s, 0)
    } 
    
    def swap[A, B](t:Tuple2[A, B]) = (t._2, t._1);
    
    depcount.toList.map(swap _).sorted
  }


  def main(args: Array[String]):Unit = {
    val root = args(0)
    val sdeps = "graph/sources/dependencies"
    val sedeps = "graph/sources/external"
    val pdeps = "graph/packages/dependencies"
    val pedeps = "graph/packages/external"

    // val pkgdeps = parseDepfile(file(file(root), pdeps))
    // val pkgEdeps = parseDepfile(file(file(root), pedeps))

    val srcdeps = parseDepfile(file(file(root), sdeps))
    val srcEdeps = parseDepfile(file(file(root), sedeps))
    val counts = depCounts(srcdeps, srcEdeps)

    for (s <- counts) s match {
      case (x:Int, y:String) => println(x + " " + y)
    }

    // println(depcount.mkString("{\n  ", "\n  ", "\n}"))
  }
}
