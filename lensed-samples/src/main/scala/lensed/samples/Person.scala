package lensed.samples

import lensed.annotation.lensed


@lensed
case class Address[T](street: T)
//case class Address(street: String)

@lensed
case class Person[T](name: Name, address: lensed.samples.Address[T])

@lensed
case class Name(first: String, last: String)
