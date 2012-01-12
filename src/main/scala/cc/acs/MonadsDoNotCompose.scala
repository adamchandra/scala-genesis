package cc.acs

import cc.acs.commons.util.StringOps._

trait Functor[F[_]] {
  def fmap[A, B](f: A => B, a: F[A]): F[B]
}

trait Applicative[F[_]] extends Functor[F] {
  def ap[A, B](f: F[A => B], a: F[A]): F[B]
  def point[A](a: A): F[A]
  override final def fmap[A, B](f: A => B, a: F[A]) =
    ap(point(f), a)
}

trait Monad[F[_]] extends Applicative[F] {
  def flatMap[A, B](f: A => F[B], a: F[A]): F[B]
  override final def ap[A, B](f: F[A => B], a: F[A]) =
    flatMap((ff: A => B) => fmap((aa: A) => ff(aa), a), f)
}


trait λf[M[_], N[_]] { 
  type λ[α] = M[N[α]]
}

trait λMofN[M[_], N[_]] { 
  type λ[α] = M[N[α]]
}


object Compose {
  def ?[A](implicit a:A) = a
  def FunctorCompose[M[_]: Functor, N[_]: Functor]: Functor[λf[M, N]#λ] =
    new Functor[λf[M, N]#λ] {
      def fmap[A, B](f: A => B, a: M[N[A]]) = ?[Functor[M]].fmap((na: N[A]) => ?[Functor[N]].fmap(f, na), a)
    }

  def ApplicativeCompose[M[_], N[_]](implicit ma: Applicative[M], na: Applicative[N]): Applicative[({ type λ[α] = M[N[α]] })#λ] =
    new Applicative[λf[M, N]#λ] {
      def ap[A, B](f: M[N[A => B]], a: M[N[A]]) = {
        def liftA2[X, Y, Z](f: X => Y => Z, a: M[X], b: M[Y]): M[Z] =
          ma.ap(ma.fmap(f, a), b)
        liftA2((ff: N[A => B]) => (aa: N[A]) => na.ap(ff, aa), f, a)
      }
      def point[A](a: A) =
        ma point (na point a)
    }

  def MonadCompose[M[_], N[_]](implicit mm: Monad[M], nm: Monad[N]): Monad[({ type λ[α] = M[N[α]] })#λ] = error("uninhabited")
}

object MonadsDoNotCompose extends Application {

  implicit val listFunctor = new Functor[List] {
    def fmap[A, B](f: A => B, a: List[A]): List[B] = {
      a.map(f)
    }
  }

  // trait MapFunctor[K,V] extends Functor[Map] {
  //   def fmap[K2,V2](f: V => V2, a: Map[K,V]): Map[K2,V2] = {
  //     a.map(f)
  //   }
  // }
  import scalaz._
  import Scalaz._

  override def main(args: Array[String]) = {
    val l1 = List(1, 2, 3, 4)
    val l2 = csv("a, b, c, d").toList
    val f1: (Int) => (String) = _.toString + "!"
    val f2: (String) => (Float) = _.toInt.toFloat
    implicit val listOfListFunctor = Compose.FunctorCompose(listFunctor, listFunctor)


    println( List(1, 2, 3, 4) ∘ f1 )
    // val v1 = List(List(1, 2), List(3, 4)) map (f1) (listOfListFunctor)
    
    
  }
}
