


object BankOCR {

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

  def transpose(aas: List[List[Char]]) : List[List[Char]] = {
    try {
      aas match {
        case Nil              => Nil
        case Nil :: xss       => transpose(xss)
        case (x::xs) :: Nil   => (x :: Nil) :: transpose (xs :: Nil)
        case (x::xs) :: xss   => (x :: (xss map (_.head))) :: transpose (xs :: (xss map (_.tail)))
      }
    } catch {
      case e: Exception => 
        error("transpose=\n" + aas.mkString("", "\n", "") + "\n===")
    }
  }

  def csv = (s:String) => (s.split(",") map (_.trim)).toList
  def wsv = (s:String) => (s.split(" ") map (_.trim)).toList

  def test_transpose() {
    val m = csv("abc,123,789") map (_.toList)
    val r = csv("a17,b28,c39") map (_.toList)
    val mp = transpose(m)
    assert(mp == r)
  }
  // test_transpose()

  def lines = (s:String) => s.lines.toList
  def toCharMatrix(s:String): List[List[Char]] = lines(s) map (_.toList)

  def join = (ss:List[Char]) => ss.foldLeft("")((a,s) => a+s)

  def grouped[A](n:Int)(ls:List[List[A]]): List[List[List[A]]] = 
    ls.grouped(n).toList

  def fmtMatrix: List[List[Char]] => String =
    ccs => (ccs map (join(_))).mkString("\n")

  def fmtMatrixCM: List[List[Char]] => String =
    ccs => fmtMatrix(transpose(ccs))

  def passThru[A](f: A => Unit)(a:A): A = {
    f(a)
    a
  }
  
  val prnt = passThru { m:List[List[Char]] => println(fmtMatrix(m)) } _
  val prntcm = passThru { m:List[List[Char]] => println(fmtMatrixCM(m)) } _

  def numeralMap(numerals: String): Map[String, Int] = {
    val r = (toCharMatrix _ andThen 
             transpose andThen 
             (grouped(4)(_)) andThen 
             (s => 
               (s map (_.tail) map (transpose(_)) map fmtMatrix)
             )
           )  (numerals)

    val rs = (r.map(_.mkString("")) zip (0 to 9)).toMap
    // rs.keys.foreach { v => println(v) }
    rs
  }

  def decodeNumerals: String => List[Int] = 
    str => {
      val nlookup = numeralMap(numerals)

      val rfn = (
        toCharMatrix _ andThen 
        transpose andThen 
        grouped(3) andThen 
        (gs => gs.map (fmtMatrixCM(_)) map (nlookup(_)))
      ) 
      rfn(str)
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
    println("decodeNumerals() = " + decodeNumerals(testInput).mkString("[", ", ", "]"))
  }
}
