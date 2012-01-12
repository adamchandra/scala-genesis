package cc.rexa2


import org.specs.{Sugar, Specification, ScalaCheck}

object MongoDBInteractionSpec extends Specification with Sugar with ScalaCheck { 
  import lensed.cc.rexa2.DBObjects._
  import MongoDBInteraction._


  "lensed mongo object" should {
    "use generated lenses" in {

      implicit val mydb = db("rexa2-dev-acs")
      implicit val mycoll = coll("authorEntities")

      val author = findOneAuthor()

      import AuthorEntity._
      println("author: " + author.canopy_last)
      println("AuthorEntity.mentions....: " + canopy_last.set(author, "anderson"))


    }
  }
}

