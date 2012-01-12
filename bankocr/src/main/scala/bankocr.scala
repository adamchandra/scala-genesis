object Commons {
  def transpose(aas: List[List[Char]]) : List[List[Char]] = {
    aas match {
      case Nil              => Nil
      case Nil :: xss       => transpose(xss)
      case (x::xs) :: Nil   => (x :: Nil) :: transpose (xs :: Nil)
      case (x::xs) :: xss   => (x :: (xss map (_.head))) :: transpose (xs :: (xss map (_.tail)))
    }
  }

  def test_transpose() {
    val m = csv("abc,123,789") map (_.toList)
    val r = csv("a17,b28,c39") map (_.toList)
    val mp = transpose(m)
    assert(mp == r)
  }
  // test_transpose()

  def csv = (s:String) => (s.split(",") map (_.trim)).toList
  def wsv = (s:String) => (s.split(" ") map (_.trim)).toList
}

import Commons._

object BankOCR {
  // The haskell solution: 

  // main = interact (\x → parse x ⧺ "\n")

  // parse input = map (digit ∘ transpose ∘ tail) (chunk 4 $ dummy : columns input)
  //     where digit = fromJust ∘ (flip lookup) dTable
  //           columns  = transpose ∘ lines
  //           dummy = "   "

  // dTable = [
  //            ([" _ ",
  //              "| |",
  //              "|_|"], '0'), ...


  // read in a text block containing lcd-like numerals and print out the
  // the numbers they represent, e.g., 

  import scalaz._
  import Scalaz._

  //123 123 123 123 123 123 123 123 123
  val numerals = """
  |  _       _   _       _   _   _   _   _ 
  | | |   |  _|  _| |_| |_  |_    | |_| |_|
  | |_|   | |_   _|   |  _| |_|   | |_|  _|
  """.trim.stripMargin


  def lines = (s:String) => s.lines.toList
  def toCharMatrix: String => List[List[Char]] = s => lines(s) map (_.toList)

  def join = (ss:List[Char]) => ss.foldLeft("")((a,s) => a+s)

  def grouped[A](n:Int)(ls:List[List[A]]): List[List[List[A]]] = 
    ls.grouped(n).toList

  def fmtMatrix: List[List[Char]] => String =
    ccs => (ccs map (join(_))).mkString("\n")

  def fmtMatrixCM: List[List[Char]] => String =
    ccs => fmtMatrix(transpose(ccs))

  // this looks like an arrow fanout/act/fanin operation
  def passThru[A](f: A => Unit)(a:A): A = {
    f(a)
    a
  }
  
  val prnt = passThru { m:List[List[Char]] => println(fmtMatrix(m)) } _
  val prntcm = passThru { m:List[List[Char]] => println(fmtMatrixCM(m)) } _

  def numeralMap(numerals: String): Map[String, Int] = {
    val groups = toCharMatrix  ∘ transpose ∘ grouped(4) 
    val numeralStrs = groups(numerals) ∘ (_.tail) ∘ fmtMatrixCM
    (numeralStrs zip (0 to 9)).toMap
  }

  def parse: String => List[Int] = 
    str => {
      val digit = (s:String) => numeralMap(numerals)(s)
      val groups = toCharMatrix ∘ transpose ∘ grouped(3) 
      groups(str) ∘ (fmtMatrixCM ∘ digit)
  }

  // later variations - try to correct for noisy input
  // ...and number that have arbitrary spacing, e.g., 

  val testInput = """
  |    _     _  _  _  _  _  _  _  _ 
  |  | _||_| _| _||_  _||_   | _||_|
  |  ||_   ||_  _| _||_ |_|  ||_ |_|
  """.trim.stripMargin


}

object BankOCRApp extends App {
  import BankOCR._

  override def main(args: Array[String]) = {
    println("decodeNumerals() = " + parse(testInput).mkString("[", ", ", "]"))
  }
}
