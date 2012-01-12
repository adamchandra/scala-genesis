package cc.acs

import org.specs.{Sugar, Specification, ScalaCheck}

import scalaz._
import Scalaz._

object ScalazStateGameSpec extends Specification with Sugar with ScalaCheck { 
  "reimplementing state example from haskell" should {
    "not be hideous" in {
      // -- Game is to produce a number from the string.
      // -- By default the game is off, a C toggles the
      // -- game on and off. A 'a' gives +1 and a b gives -1.
      // -- E.g 
      // -- 'ab'    = 0
      // -- 'ca'    = 1
      // -- 'cabca' = 0
      // -- State = game is on or off & current score
      // --       = (Bool, Int)
      
      // type GameValue = Int
      // type GameState = (Bool, Int)
      type GameValue = Int
      case class GameState(on: Boolean, score:Int)
      
      // playGame :: String -> State GameState GameValue
      // playGame []     = do
      //     (_, score) <- get
      //     return score

      // playGame (x:xs) = do
      //     (on, score) <- get
      //     case x of
      //          'a' | on -> put (on, score + 1)
      //          'b' | on -> put (on, score - 1)
      //          'c'      -> put (not on, score)
      //          _        -> put (on, score)
      //     playGame xs

      def playGame: Seq[Char] => State[GameState, GameValue] = 
        str => str match {
          case Seq() => 
            for { s <- init[GameState] } 
            yield s.score

          case Seq(x, xs@_*) => 
            for {
              s <- init[GameState]
              _ <- x match {
                case 'a' if s.on   => put(GameState(s.on, s.score+1))
                case 'b' if s.on   => put(GameState(s.on, s.score-1))
                case 'c'           => put(GameState(!s.on, s.score))
                case _             => put(s)
              }
              g <- playGame(xs)
            } 
            yield g
          case _ => error("huh???")
        }

      val startState = GameState(false, 0)

      // main = print $ evalState (playGame "abcaaacbbcabbab") startState
      //         00[123]33[43232
      val seq = "abcaaacbbcabbab".toSeq
      val answer = playGame(seq) ! startState
      answer must_== 2
    }
  }

  "be more idiomatic" in {
    case class GameState(on: Boolean, score:Int) {
      def inc = copy(score = score+1)
      def dec = copy(score = score-1)
      def toggle = copy(on = !on)
    }
    
    def playGame(str: Seq[Char]): State[GameState, Int] = 
      str match {
        case Seq() => 
          for { s <- init[GameState] } 
          yield s.score

        case Seq(x, xs@_*) => 
          for {
            s <- init[GameState]
            _ <- x match {
              case 'a' if s.on   => put(s.inc)
              case 'b' if s.on   => put(s.dec)
              case 'c'           => put(s.toggle)
              case _             => put(s)
            }
            g <- playGame(xs)
          } 
          yield g
        case _ => error("huh???")
      }

    val startState = GameState(false, 0)

    //         00[123]33[43232
    val seq = "abcaaacbbcabbab".toSeq
    GameState(true, 2) must_== playGame(seq) ~> startState
    2 must_== playGame(seq) ! startState
  }
}
