import ObjectParsers.Parser

import scala.util.Try

package object ParsedModels {

  case class ParsedElement[+T](parsedElement: T, tail: String)

  type ParsedResult[+T] = Try[ParsedElement[T]]

  type ParserType[+T] = String => ParsedResult[T]

  case class ParserCombinated[+T](parsedFunction: ParserType[T]) extends Parser[T]{
    def apply(string: String):ParsedResult[T] = parsedFunction(string)
  }

  case class AlphaNumber(value: Char)

  // Ver si estos dos tendrian que ser case, no se puede heredar entre cases

  class Letter(override val value: Char) extends AlphaNumber(value)

  class Digit(override val value: Char) extends AlphaNumber(value)

}