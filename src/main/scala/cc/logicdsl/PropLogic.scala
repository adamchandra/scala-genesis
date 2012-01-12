package logicdsl 

// Fun with UTF-8 encoding in scala

trait Prop {
	def of[A](a: Traversable[A]) = Of(this, a)
}


case object AllP extends Prop
case object SomeP extends Prop
case object NoneP extends Prop

case class Of[A](val p: Prop, val a: Traversable[A]) {
	def is(f: A ⇒ Boolean): Hold = p match {
	  case AllP  ⇒ Hold(() ⇒ a forall f)
	  case SomeP ⇒ Hold(() ⇒ a exists f)
	  case NoneP ⇒ Hold(() ⇒ !(a exists f))
	}
	
	def is(elem: A): Hold = is(elem == _)
}

case class Hold(val pred: () ⇒ Boolean) {
	def release = pred()
	def &&(that: Hold) = Hold( () ⇒ pred() && that.pred() )
	def and(that: Hold) = this && that
	def ||(that: Hold) = Hold( () ⇒ pred() || that.pred() )
	def or(that: Hold) = this || that
	def unary_! = Hold( () ⇒ !pred() )
	def implies(that: Hold) = Implication(this, that)
}

case class Implication(lhs: Hold, rhs: Hold) {
	def release = ! (lhs release) || (rhs release)
}

object Props {
  val Φ = Nil

  def all = AllP
  def some = SomeP
  def none = NoneP
  def not(h: Hold) = !h
}

object ScalaBeauty {
  
  import Props._
  
  type ℤ = Int
  
  val λx = (x: ℤ) ⇒ x * x
  
  val sum = (_: ℤ) + (_: ℤ)
  val square = λx
  val addOne = sum(1, _: ℤ)
  val even = (_:ℤ) % 2 == 0
  val odd = ! even(_: ℤ)
  
  implicit def iterOps[A, R](f: A ⇒ R) = new {
    def /@(x: Traversable[A]) = x map f
  }
  
  def out[A](x: Traversable[A]) =
    println( x mkString ("[", ", ", "]") )
  
  def main(args: Array[String]) {
    
    val a = (addOne andThen square) /@ ( 1 :: 2 :: 3 :: Φ)
    out(a)
    
    val λy = square andThen addOne
    val b = λy /@ (1 :: 2 :: 3 :: Φ)
    out(b)
    
    val μ = (2 :: 5 :: 8 :: Φ);
      
    val p1 = all of μ is even
    val p2 = all of μ is odd
    val p3 = (some of μ is even) and (some of μ is odd)
    val p4 = not ( all of μ is ( _ > 5 ) )
    val p5 = p1 implies p2
    val p6 = none of μ is 13

    println(p4 release)
    println(p6 release)
  }
}



