package lensed
package cc.rexa2

object DBObjects {
  import lensed.annotation.lensed

  @lensed
  case class AuthorEntity(
    id:String, 
    canopy_last:String, 
    mentions: List[AuthorMention]
  )
  
  @lensed
  case class AuthorMention(
    paperMention: String, // ref
    index: Int
  )
}
