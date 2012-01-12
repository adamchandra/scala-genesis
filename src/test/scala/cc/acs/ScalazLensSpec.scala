package cc.acs

import org.specs.{Sugar, Specification, ScalaCheck}

import scalaz._
import Scalaz._

object ExampleLens {
  case class Employee(name: String, salary: Int)
  case class Human(name: String, age: Int)


  val age: Lens[Human, Int] = Lens(_.age, (e, s) => e copy (age = s))
  val salary: Lens[Employee, Int] = Lens(_.salary, (e, s) => e copy (salary = s))
  val name: Lens[Employee, String] = Lens(_.name, (e, n) => e copy (name = n))

  val giveRaise: Employee => Employee = salary mod (_, _ + 100)

  val tom = Employee("Tom", 4000)
  val dick = Employee("Dick", 3000)
  val harry = Employee("Harry", 5000)

  val alex = Human("Alex", 23)

  val higherTom = giveRaise(tom) // Employee("Tom", 4100)

  val modBoth = (salary *** name) mod ((harry, tom), {
    case (s, n) => (s + 100, n + " Jones")
  }) // (Employee("Harry", 5100), Employee("Tom Jones", 4000))  

  val modMonadically = for {
    _ <- salary += 100
    n <- name
    _ <- name := n + " Jones"
    e <- init
  } yield e

  val tomJones = modMonadically ! tom // Employee("Tom Jones", 4100)
  
}


object ScalazLensSpec extends Specification with Sugar with ScalaCheck { 
  import lensed.samples._
  import ExampleLens._

  "lensed usage" should {
    "use generated lenses" in {
      val person = Person(Name("Foo", "Bar"), Address("Schlaraffenland"))
      // println(person)
      val legoPerson = Person.address.street.set(person, "Legoland")
      // println(legoPerson)
      val chuckNorris = Person.name.set(legoPerson, Name("Chuck", "Norris")) 
      // println(chuckNorris)

      println("---------------------------")
      val m:  Employee => Employee = salary mod (_, _ + 1)
      val mf: Employee => List[Employee] = salary modf (_, a => (a to (a + 2)).toList)
      val mp: Employee => (Employee, String) = salary modp (_, a => (a+100, ""+(a-100)))


      val m_mf = for {c <- m 
                      d <- mf
                    } yield (c, d)

      println("m > " + m(tom))
      println("mf> " + mf(tom))
      println("mp> " + mp(tom))

      println("(m andThen mf)> " + (m andThen mf) (tom))
      println("m_mf>           " + m_mf (tom))

      println("---------------------------")

      // xmap creates a lens that map the actual lensed value to/from some other value, 
      // e.g., lensed value is an 'id-type', xmap'd lens maps to and from string or numerical
      // representations
      val mx:Lens[Employee, String] = salary.xmap ((a:Int) => (a + 111).toString)((a:String) => a.reverse.toInt)
      println("mx>             " + mx.set(tom, "2098"))

      // |||
      println("age>            " + (age ||| salary) (alex.left))
      println("salary>         " + (age ||| salary) (tom.right))

      // ***
      val ageSal = age *** salary
      println("age/sal>        " + ageSal (alex, tom))
      println("---------------------------")

      println("age.state>        " + (age.toState ~> alex))
      println("age.state>        " + (age.toState ! alex))


      println("---------------------------")
      
    }
  }
}
