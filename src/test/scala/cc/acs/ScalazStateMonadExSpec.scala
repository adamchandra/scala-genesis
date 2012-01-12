package cc.acs

import org.specs.{Sugar, Specification, ScalaCheck}

import scalaz._
import Scalaz._


object TreeExample {
  sealed abstract class Tree[+A] {
    /**
     * Label the Leaf nodes of a the tree with increasing integers, traversing
     * left to right. The current value of the label is be explicitly threaded
     * through the recursion.
     */
    // def number(seed: Int): (Tree[(A, Int)], Int) = this match {
    //   case Leaf(x) => (Leaf(x, seed), seed + 1)
    //     case Branch(left, right) => left number seed match {
    //       case (l, ls) => {
    //         right number ls match {
    //           case (r, rs) => (Branch(l, r), rs)
    //         }}}
    // }


    // def numberSMExplicit: State[Int, Tree[(A, Int)]] = this match {
    //   case Leaf(x) => {
    //     val sm = init[Int]
    //     sm flatMap {a => 
    //       modify((_: Int) + 1) map { _ => 
    //         Leaf((x, a))
    //       }}}
    // 
    //   case Branch(left, right) => {
    //     left.numberSMExplicit flatMap {l => 
    //       right.numberSMExplicit map {r => 
    //         Branch(l, r)
    //       }}}
    // }


    /*
     def modify[S](f: S => S) = init[S] flatMap (s => state(_ => (f(s), ())))

     def map[B](f: A => B): State[S, B] = state(apply(_) match {
       case (s, a) => (s, f(a))
     })

     def flatMap[B](f: A => State[S, B]): State[S, B] = state(apply(_) match {
       case (s, a) => f(a)(s)
     })

     */

    def numberSM: State[Int, Tree[(A, Int)]] = this match {
      case Leaf(x) => 
        for (s <- init[Int];
             _ <- modify((_: Int) + 1))
        yield Leaf((x, s))

      case Branch(x, left, right) => 
        for (s <- init[Int];
             _ <- modify((_: Int) + 1);
             l <- left.numberSM;
             r <- right.numberSM)
        yield Branch((x, s), l, r)
    }

    /**
     * As above, but using State as an Applicative Functor rather than as a Monad.
     * This is possible as the generators in the for comprehension above are independent.
     * Note the correspondence between `<* modify` and `_ <- modify`.
     */
    // def numberSA: State[Int, Tree[(A, Int)]] = this match {
    //   case Leaf(x) => {
    //     val af = init[Int] <* modify((_: Int) + 1)
    //     val afx = af ∘ { s: Int => Leaf((x, s)) }
    //     afx
    //   } 
    //   case Branch(x, left, right) => left.numberSA.<**>(right.numberSA)(Branch.apply)
    // }
  }

  final case class Leaf[A](a: A) extends Tree[A]
  final case class Branch[A](a: A, left: Tree[A], right: Tree[A]) extends Tree[A]

  implicit def TreeShow[X] = showA[Tree[X]]

  // implicit def StateShow[State[_, _], S, A] = showBy[State[S, A], S]( _.gets )

  implicit def TreeEqual[X] = equalA[Tree[X]]

}

object ScalazStateMonadExSpec extends Specification with Sugar with ScalaCheck { 
  import TreeExample._

  "carrying state across tree traversal" should {
    val tree =         Branch( "one",     Leaf( "two"    ), Branch( "three",     Leaf( "four"    ), Leaf( "five"    )))
    val expectedTree = Branch(("one", 1), Leaf(("two", 2)), Branch(("three", 3), Leaf(("four", 4)), Leaf(("five", 5))))

    "use monad implicitly" in {
      tree.numberSM ! 1 must_== expectedTree
    }
  }
}
