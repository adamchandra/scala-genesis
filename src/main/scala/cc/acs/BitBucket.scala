package cc.acs 

import cc.acs.commons.util.FileOps._


object BitBucket {
  def parseargs(args: Array[String]): Map[String, Any] = {
    val arglist = args.toList

    def nextOption(map: Map[String, Any], list: List[String]): Map[String, Any] = {
      list match {
        case Nil => map
        case (o @ "--train") :: tail                       => nextOption(map ++ Map(o -> true), tail)
        case (o @ "--predict") :: tail                     => nextOption(map ++ Map(o -> true), tail)
        case (o @ "--quick-ner") :: tail                   => nextOption(map ++ Map(o -> true), tail)
        case (o @ "--num-docs") :: value :: tail           => nextOption(map ++ Map(o -> value.toInt), tail)
        case (o @ "--entity-file") :: value :: tail        => nextOption(map ++ Map(o -> file(value)), tail)
        case (o @ "--relation-file") :: value :: tail      => nextOption(map ++ Map(o -> file(value)), tail)
        case (o @ "--relation-type-file") :: value :: tail => nextOption(map ++ Map(o -> file(value)), tail)
        case (o @ "--document-dir") :: value :: tail       => nextOption(map ++ Map(o -> file(value)), tail)
        case option :: tail => println("Unknown option: " + option); exit(1)
      }
    }

    val options = nextOption(Map(), arglist)
    options
  }
}

  /*
   * Annotating a document
   * A labelled document is represented as a tree, where each node represents
   * an (offset, length)-style span relative to its parent. The root of the tree
   * represents the entire input string. Each node contains a dictionary containing
   * key/value labels for its span.
   *
   * The implementation is as follows:
   * Labeling
   *   // begin is relative to parent
   *   type AnnotatedSpan = ((begin, length), Map[String, String])
   *   original_string:String
   *   labels:Tree[AnnotatedSpan]
   *
   */


  // def buildNytXml(xml: URL): JDomDocument = { }
  // val asdf = (new java.io.File("sd")).toURL()
  
    /*
     * mystr = "The quick brown fox. There's no place like home"
     * root repr:
     *   original = mystr
     *   node(original.length, {"tokenization" -> "document"})
     * sentence-split: 
     *   node(original.length, {"tokenization" -> "document"}) // paragraph, sentence, phrase, token, ...
     *     node(s1.length, {"tokenization" -> "sentence"}), i.e., "the qui..".length
     *     node(s2.length, {"tokenization" -> "sentence"}), i.e., "there's no place..".length
     *
     * tokenized
     *   node(original.length, {"tokenization" -> "document"})
     *     node(s1.length, {"tokenization" -> "sentence"}), i.e., "the qui..".length
     *       node(t1.length, {"tokenization" -> "token", "pos" -> "NNP", "ner" -> "person"}), i.e., "the qui..".length
     *     node(s2.length, {"tokenization" -> "sentence"}), i.e., "there's no place..".length

     */

    /*
     * given a doc w/labeled tokens, label sentences
     * 
     * tokenized
     *   node(original.length, {"tokenization" -> "document"}), 
     *     node(original.length, {"tokenization" -> "paragraph"}), 
     *       node(t1.length, {"tokenization" -> "word"}), node(t2.length, {"tokenization" -> "word"})
     *
     *   node(original.length, {"tokenization" -> "document"}), 
     *     node(original.length, {"tokenization" -> "paragraph"}), 
     *       node(original.length, {"tokenization" -> "sentence"}), // step 1 create a single sentence span over all tokens
     *         node(t1.length, {"tokenization" -> "word"}), node(t2.length, {"tokenization" -> "word"})
     * 
     */



