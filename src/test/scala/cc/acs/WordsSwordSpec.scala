package cc.acs

import org.specs.{Sugar, Specification, ScalaCheck}

import scalaz._
import Scalaz._


object TextLayout {

  /*
   * box
   *   dup
   *   atop
   * 
   */
  
}


object MutableWT {
  import scala.collection.{mutable => m}
  type Word = String


  case class WordTree(
    words: m.Buffer[Word] = m.ListBuffer[Word](), 
    children: m.Map[Char, WordTree] = m.HashMap[Char, WordTree]()) 
  {
    def apply(c: Char): WordTree = {
      if (!children.keySet.contains(c)) 
        children.put(c, WordTree())
      
      children(c)
    }

    def drawTree: String = {
      draw.foldMap(_ + "\n")
    }

    def draw: Stream[String] = {
      def drawSubTrees(s: Stream[(Char, WordTree)]): Stream[String] = s match {
        case Stream.Empty => Stream.Empty
        case Stream((c, t)) => "|" #:: shift("`"+c+" ", "   ", t.draw)
        case (c, t) #:: ts => "|" #:: shift("+"+c+" ", "|  ", t.draw) append drawSubTrees(ts)
      }
      def shift(first: String, other: String, s: Stream[String]): Stream[String] =
        s.ʐ <*> ((first #:: other.repeat[Stream]).ʐ ∘ ((_: String) + (_: String)).curried)

      words.mkString("[", ",", "]") #:: drawSubTrees(children.toIterable.toStream)
    }

  }
  
}

object WordSwordSpec extends Specification with Sugar with ScalaCheck { 
  import MutableWT._

  def buildTree(words: Seq[String]): WordTree= {
    val tree: WordTree = WordTree()

    for (word <- words) {
      var curr = tree
      val wsort = word.toSeq.sortWith((s, t) => s < t)
      for(c <- wsort) {
        curr = curr(c)
      }
      curr.words.append(word)
    }
    tree
  }

  "mutable data structure version" should {
    val vocab = "a are ear era ears ate eat tea".split(" ")

    "statefully build prefix tree" in {
      val tree = buildTree(vocab)
      println(tree.drawTree)
    }

    "format text like this" in {
      """
      > ae_ (0) {r,t-y}
      > aer_ (3) {a-z}
      are ear era

      """
      
      val tree = buildTree(vocab)
      val path = "aer"

      def render(tree:WordTree, path: String): String = {
        // describe the formatting: 
        // line over box etc...
        // ..or...
        val node = path.toSeq.foldLeft(tree)((t, c) => t(c))
        val words = node.words.mkString("\n  ", "\n  ", "\n")
        val wc = "("+node.words.length+")"
        val nexts = node.children.keys.mkString("{", ", ", "}")

        path + "_" + " " + wc + " " + nexts + "\n    " + words
      }

      // val s = tree('a')('e')('r')
      println(render(tree, "aer"))
    }

    "incorporate a command line for viewing" in {
      """
      ae\tr\t
      ae
      aer aet
      """
    }

  }
}
