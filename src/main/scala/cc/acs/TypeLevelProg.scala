package cc.acs

trait Recurse {
  type Next <: Recurse
  // this is the recursive function definition
  type X[R <: Recurse] <: Int
}

// implementation
trait RecurseA extends Recurse {
  type Next = RecurseA
  // this is the implementation
  type X[R <: Recurse] = R#X[R#Next]
}

// Uncomment this to crash the compiler w/stack overflow:
// object Recurse {
//   // infinite loop
//   type C = RecurseA#X[RecurseA]
// }
