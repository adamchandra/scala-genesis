package cc.rexa2

import lensed.cc.rexa2.DBObjects

object MongoDBInteraction {
  import DBObjects._
  // list of dbs
  // def dbs = {}


  // authors
  // <author>Werner John</author>
  // <author>Dominik Ley</author>
  // <author>Joachim M&uuml;ller</author>

  // scala rep:
  object DBLP {
    trait Record {
      case class Author()
    }
    object Article extends Record {
      case class Journal()
    }
  }
    
  case class Database(name:String) 
  case class Collection(name:String)

  // construct a db object for use as an implicit param
  def db(name:String) = Database(name)
  def coll(name:String) = Collection(name)

  def findOne(
    
  )(implicit db:Database, coll:Collection):AuthorEntity = {


    AuthorEntity(
      id = "aid",
      canopy_last = "smith",
      mentions = List(
        AuthorMention(
          paperMention = "amid",
          index = 0
        )
      ))
  }

  def findOneAuthor()(implicit db:Database, coll:Collection):AuthorEntity = {
    AuthorEntity(
      id = "aid",
      canopy_last = "smith",
      mentions = List(
        AuthorMention(
          paperMention = "amid",
          index = 0
        )
      ))
  }
}
