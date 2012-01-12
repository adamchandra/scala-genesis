package cc.acs

import org.specs.{Sugar, Specification, ScalaCheck}

import scalaz._
import Scalaz._


object V1 {
  object IntReader {
    // implicit def IntReaderShow: Show[IntReader[A]] = "rInt => "+show[A]
  }


  case class IntReader[A : Manifest](run: Int => A) {
    // println("    ~~ new read(Int => " + implicitly[Manifest[A]] + ")") 
    def map[B : Manifest](f: A => B): IntReader[B] = {
      IntReader(i => f(run(i)))
    }
    def flatMap[B : Manifest](f: A => IntReader[B]): IntReader[B] = {
      IntReader(i => {
        f(run(i)).run(i)
      })
    }
  }
}


// object V2 {
//   
//   object IntReader {
//     def apply[A, R](k: A => R, i: Int) = new IntReader[A] {
//       def apply(k: A => R, i: Int): R =
//         this(k, i)
//     }
//   }
// 
//   trait IntReader[A] {
//     def apply[R](k: A => R, i: Int): R
//     def map[B](f: A => B): IntReader[B] = new IntReader[B] {
//       def apply[R](k: B => R, i: Int): R =
//         IntReader.this(a => k(f(a)), i)
//     }
//     def flatMap[B](f: A => IntReader[B]): IntReader[B] = new IntReader[B] {
//       def apply[R](k: B => R, i: Int): R =
//         IntReader.this(a => k(f(a)(b => b, i)), i)
//     }
//   }
// }

object V3 {

  sealed trait Trampoline[A] {

    def map[B](f: A => B): Trampoline[B] =
      flatMap(a => More(() => Done(f(a))))

    def flatMap[B](f: A => Trampoline[B]): Trampoline[B] =
      Cont(this, f)

    def run: A = {
      var cur: Trampoline[_] = this
      var stack: List[Any => Trampoline[A]] = List()
      var result: Option[A] = None
      println("cur: " + cur)
      println("stack: " + stack)
      println("result: " + result)

      while (result.isEmpty) {
        println("while result.isEmpty")
        println("  cur: " + cur)
        println("  stack: " + stack)
        println("  result: " + result)

        cur match {
          case Done(a) => stack match {
            case Nil => result = Some(a.asInstanceOf[A])
            case c :: cs => { 
              cur = c(a)
              stack = cs
            }
          }
          case More(t) => cur = t()
          case Cont(a, f) => {
            cur = a
            stack = f.asInstanceOf[Any => Trampoline[A]] :: stack 
          }
        }
      }
      result.get
    }
  }

  case class Done[A](a: A) extends Trampoline[A]
  case class More[A](a: () => Trampoline[A]) extends Trampoline[A]
  case class Cont[A, B](a: Trampoline[A], f: A => Trampoline[B]) extends Trampoline[B]

}


object ScalazReaderMonadExSpec extends Specification with Sugar with ScalaCheck { 

  "trampolined int reader" should {
    import V3._

    def fib(n: Int): Trampoline[Int] =
      if (n < 2) Done(n) else for {
        x <- fib(n - 1)
        y <- fib(n - 2)
      } yield (x + y)


    "compute fib" in {
      // 1 2 3 4 5 6 7
      // 1 1 2 3 5 8 13

      println("fib(4).run: " + fib(4).run)
    }
  }

  // "v2 int reader monad" should {
  //   import V2._
  //   "do something" in {
  //     println("List.range(1, 4): " + List.range(1, 4))
  //   
  //     val f = List.range(1, 4).foldLeft (
  //       mzero) (
  //         (a, e) => {
  //           a.flatMap(xs => newIntReader(_ => e :: xs))
  //         })
  //     
  //     println("================")
  //     println("f.run(9): " + f.run(9))
  // 
  //   }
  // }


  "v1 int reader monad" should {
    import V1._
    import IntReader._  


    "use monad implicitly" in {
      println("List.range(1, 4): " + List.range(1, 4))
      val f = List.range(1, 4).foldLeft(
        IntReader(List(_)))(
          (a, e) => {
            // println("e: " + e)
            // println("a.flatMap(xs => IntReader(_ => e :: xs))")
            a.flatMap(xs => IntReader(_ => e :: xs))
          })

      println("================")
      println("f.run(9): " + f.run(9))
    }
  }


}
