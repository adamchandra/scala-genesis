package cc.acs

import cc.acs.commons.util.FileOps._
import scalaz._
import Scalaz._

object MTG extends Application {

  def f1(i:Int) = i + 1
  def f2(s:String) = "??" ++ s ++ "!!"
  def f3(ss:List[String]) = ss.mkString("(", "++", ")")

  override def main(args: Array[String]) = {
    val v1 = (0 to 5) toList
    val v2 = "a b c d e f g".split(" ").toList

    // want to write:
    // val vr = (f2 . f1) <$> v1 * v2

    // (v1 ∘ (f2 _ compose ((s:Int) => s.toString) compose f1))
    // val ff = (f2 _ compose ((s:Int) => s.toString) compose f1)
    // val ff = (f1 _) ∘ List((s:Int) => s.toString, (s:Int) => s.toString) ∘ () ∘ f2
    // val ff = (f1 _) ∘ List((s:Int) => s.toString, (s:Int) => s.toString)
    // println(v1)
    // println (v1 ∘ ff)
    
    val xx = List(1, 2, 3).<**>(List(40, 50, 60)) (_ * _) 
    println(xx)
    asdf()
  }

  case object Zero
  case class Succ[N](n:N)
  val three = Succ(Succ(Succ(Zero)))

  def asdf():Unit = {
    val len1: String => Int = s => s.length
    val len2 = (s:String) => s.length
    val len3 = ((_:String).length)
    val lenstr: String => String => Int = s => t => t.length + s.length
    println(three)
  }
}


// class Iterable[Container[X], T]
// trait NumericIterable[T <: Number] extends Iterable[NumericIterable, T]

