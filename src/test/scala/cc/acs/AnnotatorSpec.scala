package cc.acs

import org.specs.{ Sugar, Specification, ScalaCheck }

import scalaz._
import Scalaz._

object AnnotatorSpec extends Specification with Sugar with ScalaCheck {

  sealed trait Annex {
    val begin: Int
    val end: Int
  }

  case class RootAnnex(source: String) extends Annex {
    val begin = 0
    val end = source.length
  }

  case class LAnnex(begin: Int, end: Int) extends Annex {
    val labels = Map[String, String]()
  }

  implicit def ShowAnnex = showA[Annex]

  // split can only occur at leaf, non-labeled spans
  // splitting an unlabeled span: [1-28] => [1-12][13-28]
  // splitting a labeled span: [1-28] => [[1-28], [[1-12][13-28]]]

  def labelFringe(atree: Tree[Annex], start: Int, end: Int): TreeLoc[Annex] = {
    val containing = atree.loc.cojoin.toTree.flatten.filter(l => l.getLabel.begin <= start && end <= l.getLabel.end)
    val lastLoc = containing.last
    lastLoc.getLabel match {
      case a @ RootAnnex(src) =>
        lastLoc.insertDownLast(LAnnex(start, end).leaf) // todo: insert in sorted order
      case a @ LAnnex(begin, end) =>
        lastLoc.insertDownLast(LAnnex(start, end).leaf)
    }
  }

  val doc1 = """
  |   [MARCIUS enters the gates]
  |
  |FIRST SOLDIER. Fool-hardiness; not I.
  |SECOND SOLDIER. Not I.                    [MARCIUS is shut in]
  |FIRST SOLDIER. See, they have shut him in.
  |ALL. To th' pot, I warrant him.             [Alarum continues]
  |""".trim.stripMargin

  "annotation" should {
    "load a document w/id" in {
      val ldoc = RootAnnex(doc1).leaf
    }

    "put a label on it" in {
      val ldoc = RootAnnex(doc1).leaf
      val labelled = labelFringe(ldoc, 20, 40)
      println("labelled: " + labelled.toTree.drawTree)
    }

    def labelRegex(atree: Tree[Annex], re: String): Stream[TreeLoc[Annex]] = {
      val mi = re.findAllIn(doc1)
      val newdoc = mi.matchData.foldLeft(ldoc) {
        case (doc, m) =>
          labelFringe(doc, m.start, m.end).toTree
      }
      println("newdoc: " + newdoc.drawTree)
    }

    "put a bunch of labels on a document" in {
      val ldoc: Tree[Annex] = RootAnnex(doc1).leaf
      // pick out the stage directions:
      val re = "[\\[]([^\\[]+)[\\]]".r
      val mi = re.findAllIn(doc1)
      val newdoc = mi.matchData.foldLeft(ldoc) {
        case (doc, m) =>
          labelFringe(doc, m.start, m.end).toTree
      }
      println("newdoc: " + newdoc.drawTree)
    }

    "parse an xml file" in {

      val sample1 = <sample>
                      <line><tbox llx="70" lly="705" urx="140" ury="724" f="1"><![CDATA[References ]]></tbox></line>
                      <line><tbox llx="71" lly="691" urx="182" ury="705" f="2"><![CDATA[Prof. M. Frans Kaashoek ]]></tbox><tbox llx="307" lly="691" urx="398" ury="705" f="2"><![CDATA[Prof. Robert Morris ]]></tbox></line>
                      <line><tbox llx="71" lly="679" urx="217" ury="693" f="2"><![CDATA[MIT Computer Science & AI Lab ]]></tbox><tbox llx="307" lly="679" urx="454" ury="693" f="2"><![CDATA[MIT Computer Science & AI Lab ]]></tbox></line>
                      <line><tbox llx="71" lly="667" urx="187" ury="681" f="2"><![CDATA[32 Vassar Street, 32-G992 ]]></tbox><tbox llx="308" lly="667" urx="424" ury="681" f="2"><![CDATA[32 Vassar Street, 32-G972 ]]></tbox></line>
                      <line><tbox llx="71" lly="655" urx="175" ury="669" f="2"><![CDATA[Cambridge, MA 02139 ]]></tbox><tbox llx="308" lly="655" urx="411" ury="669" f="2"><![CDATA[Cambridge, MA 02139 ]]></tbox></line>
                      <line><tbox llx="71" lly="643" urx="144" ury="657" f="2"><![CDATA[(617) 253-7149 ]]></tbox><tbox llx="307" lly="643" urx="381" ury="657" f="2"><![CDATA[(617) 253-5983 ]]></tbox></line>
                      <line><tbox llx="72" lly="631" urx="403" ury="641" f="3"><![CDATA[kaashoek@csail.mit.edu rtm@csail.mit.edu ]]></tbox></line>
                      <line><tbox llx="71" lly="607" urx="157" ury="622" f="2"><![CDATA[Prof. Eddie Kohler ]]></tbox><tbox llx="307" lly="607" urx="404" ury="622" f="2"><![CDATA[Prof. David Mazi`eres ]]></tbox></line>
                      <line><tbox llx="71" lly="595" urx="497" ury="610" f="2"><![CDATA[UCLA Computer Science Department Stanford University Computer Science Dept. ]]></tbox></line>
                      <line><tbox llx="71" lly="583" urx="160" ury="598" f="2"><![CDATA[4531C Boelter Hall ]]></tbox><tbox llx="307" lly="583" urx="403" ury="598" f="2"><![CDATA[353 Serra Mall, #290 ]]></tbox></line>
                      <line><tbox llx="71" lly="571" urx="178" ury="586" f="2"><![CDATA[Los Angeles, CA 90095 ]]></tbox><tbox llx="307" lly="571" urx="399" ury="586" f="2"><![CDATA[Stanford, CA 94305 ]]></tbox></line>
                      <line><tbox llx="71" lly="559" urx="144" ury="574" f="2"><![CDATA[(310) 267-5450 ]]></tbox><tbox llx="307" lly="559" urx="381" ury="574" f="2"><![CDATA[(650) 723-8777 ]]></tbox></line>
                      <line><tbox llx="72" lly="548" urx="476" ury="558" f="3"><![CDATA[kohler@cs.ucla.edu ]]></tbox></line>
                      <line><tbox llx="71" lly="520" urx="216" ury="534" f="2"><![CDATA[Cambridge, MA, January 5, 2008 ]]></tbox></line>
                      <line><tbox llx="302" lly="69" urx="318" ury="83" f="2"><![CDATA[6 ]]></tbox></line>
                    </sample>

      val sample2 = <sample>
                      <line><tbox llx="297" lly="518" urx="358" ury="530" f="4"><![CDATA[References ]]></tbox></line>
                      <line><tbox llx="302" lly="509" urx="364" ury="519" f="5"><![CDATA[1. Bishop-Clark, ]]></tbox><tbox llx="359" lly="509" urx="537" ury="519" f="5"><![CDATA[C. and Wheeler, D. The Myers-Briggs personality ]]></tbox></line>
                      <line><tbox llx="310" lly="500" urx="499" ury="510" f="5"><![CDATA[type and its relationship to computer programming. ]]></tbox><tbox llx="494" lly="500" urx="537" ury="510" f="6"><![CDATA[Journal of ]]></tbox></line>
                      <line><tbox llx="310" lly="491" urx="437" ury="501" f="6"><![CDATA[Research on Computing Education 26, ]]></tbox><tbox llx="430" lly="491" urx="502" ury="501" f="5"><![CDATA[3 (1994), 358--370. ]]></tbox></line>
                      <line><tbox llx="302" lly="482" urx="369" ury="492" f="5"><![CDATA[2. Boehm, B.W. ]]></tbox><tbox llx="365" lly="482" urx="487" ury="492" f="6"><![CDATA[Software Engineering Economics. ]]></tbox><tbox llx="484" lly="482" urx="538" ury="492" f="5"><![CDATA[Prentice-Hall ]]></tbox></line>
                      <line><tbox llx="310" lly="473" urx="537" ury="483" f="5"><![CDATA[advances in computing science and technology series. Prentice-Hall, ]]></tbox></line>
                      <line><tbox llx="310" lly="464" urx="411" ury="474" f="5"><![CDATA[Englewood Cliffs, NJ, 1981. ]]></tbox></line>
                      <line><tbox llx="302" lly="455" urx="494" ury="465" f="5"><![CDATA[3. Capretz, L.F. Personality types in software engineering. ]]></tbox><tbox llx="487" lly="455" urx="537" ury="465" f="6"><![CDATA[International ]]></tbox></line>
                      <line><tbox llx="310" lly="446" urx="436" ury="456" f="6"><![CDATA[Journal Human-Computer Studies 58 ]]></tbox><tbox llx="428" lly="446" urx="494" ury="456" f="5"><![CDATA[(2003), 207--214. ]]></tbox></line>
                      <line><tbox llx="302" lly="437" urx="537" ury="447" f="5"><![CDATA[4. Devito Da Cunha, A. The Myers-Briggs personality type as a predic- ]]></tbox></line>
                      <line><tbox llx="310" lly="428" urx="537" ury="438" f="5"><![CDATA[tor of success in the code review task. MPhil Dissertation, University ]]></tbox></line>
                      <line><tbox llx="310" lly="419" urx="383" ury="429" f="5"><![CDATA[of Newcastle, 2003. ]]></tbox></line>
                      <line><tbox llx="302" lly="410" urx="537" ury="420" f="5"><![CDATA[5. Furnham, A., et al. Do personality factors predict job satisfaction? ]]></tbox></line>
                      <line><tbox llx="310" lly="401" urx="449" ury="411" f="6"><![CDATA[Personality and Individual Differences 33, ]]></tbox><tbox llx="442" lly="401" urx="522" ury="411" f="5"><![CDATA[8 (2002), 1325--1342. ]]></tbox></line>
                    </sample>

      val asAnnex = """
      [[References ]]
      [[1. Bishop-Clark, ] [C. and Wheeler, D. The Myers-Briggs personality ]]
      [[type and its relationship to computer programming. ][Journal of ]]
      [[Research on Computing Education 26, ] [3 (1994), 358--370. ]]
      [[2. Boehm, B.W. ] [Software Engineering Economics. ] [Prentice-Hall ]]
      [[advances in computing science and technology series. Prentice-Hall, ]]
      [[Englewood Cliffs, NJ, 1981. ]]
      [[3. Capretz, L.F. Personality types in software engineering. ] [International ]]
      [[Journal Human-Computer Studies 58 ] [(2003), 207--214. ]]
      [[4. Devito Da Cunha, A. The Myers-Briggs personality type as a predic- ]]
      [[tor of success in the code review task. MPhil Dissertation, University ]]
      [[of Newcastle, 2003. ]]
      [[5. Furnham, A., et al. Do personality factors predict job satisfaction? ]]
      [[Personality and Individual Differences 33, ] [8 (2002), 1325--1342. ]]
      """

      // after running regex for lastname, first-initial: 

      val names = """
      References 
      1. [Bishop-Clark, C.] and [Wheeler, D.] The Myers-Briggs personality 
      type and its relationship to computer programming. Journal of 
      Research on Computing Education 26,  3 (1994), 358--370. 
      2. [Boehm, B.W.]  Software Engineering Economics.  Prentice-Hall 
      advances in computing science and technology series. Prentice-Hall, 
      Englewood Cliffs, NJ, 1981. 
      3. [Capretz, L.F.] Personality types in software engineering.  International 
      Journal Human-Computer Studies 58  (2003), 207--214. 
      4. Devito Da Cunha, A. The Myers-Briggs personality type as a predic- 
      tor of success in the code review task. MPhil Dissertation, University 
      of Newcastle, 2003. 
      5. [Furnham, A.], et al. Do personality factors predict job satisfaction? 
      Personality and Individual Differences 33,  8 (2002), 1325--1342. 
      """

      val isolatedNames = """
      [Bishop-Clark, C.] [Wheeler, D.] [Boehm, B.W.] [Capretz, L.F.] [Furnham, A.]
      """

      val isolatedNamesFmt = """
      [Bishop-Clark , C.] 
      [Boehm        , B. W.] 
      [Capretz      , L. F.] 
      [Furnham      , A.]
      [Wheeler      , D.] 
      """

      val isolatedNamesFmtWithContext = """
      1.  |Bishop-Clark , C.    | and
      2.  |Boehm        , B. W. | Software
      3.  |Capretz      , L. F. | Personalities
      5.  |Furnham      , A.    | , et al.
      and |Wheeler      , D.    | The Meyers
      """



      // Tag the source document as having a particular style of reference-tag, 
      // e.g., [1] vs. 1. vs. [Bishop 2005], as well as the page as actually 
      // being a references section
      val refPlus1 = """
      References | 1. Bishop-Clark, C. and Wheeler, D. The Myers-Briggs personality 
      """
    }

    // stream over a set of documents and produce an object suitable for markup
    // mark regions using:
    //    regex, hand-coded parser
    //    pos, sentence, ner taggers
    //    select by label, invert selection (or 'not' selection)

    // markup image regions
    // markup audio regions

    // filter out marked regions of a document
    // reorder document spans
    // render document to console / html / browser based console / emacs-friendly format/etc...
    // capture user interactions to apply labeling:
    //    rectangular, brushfire
    //       brushfire is 3x3 grid of regexes, with rules for applying
    // clone labeling
    // push labels upstream
    // serialize labeling either inband or out-of-band
    // visualizer to indicate how much of a given corpus is labeled
    // support for output to IOBLU, nested format, OWPL, ...
    // allow non-contiguous regions to have one label, e.g., half-lines
    //   in shakespeare, where one speaker finishes the verse line
    //   for another... not sure how to handle this yet...

  }

}
