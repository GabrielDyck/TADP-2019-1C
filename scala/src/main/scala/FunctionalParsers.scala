import ParsedModels.{AlphaNumber, Digit, Letter, ParsedElement}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success, Try}

package object FunctionalParsers {

  type Parser[+T] = String => Try[ParsedElement[T]]

  implicit class CombinatorParser[T](firstParser: Parser[T]){
    //def <|>[K](secondParser: Parser[K]): Parser[_] = {
      //string: String => firstParser(string) orElse secondParser(string)
    //}

    def <>[K](secondParser: Parser[K]): Parser[(T, K)] = {
      string: String =>
        firstParser(string).flatMap(firstP =>{
          secondParser(firstP.tail).map(secondP =>
            ParsedElement((firstP.parsedElement, secondP.parsedElement), secondP.tail))
        })
    }
  }

  def anyCharParser(string: String): Try[ParsedElement[Char]] = {
    string match {
      case "" => Failure(new EmptyStringException)
      case _ => Success(ParsedElement(string.head, string.tail))
    }
  }

  def voidParser(string: String): Try[ParsedElement[Unit]] = {
    anyCharParser(string).map(p => ParsedElement((), p.tail))

  }

  def charParser(expectedChar: Char)(string: String): Try[ParsedElement[Char]] = {
    anyCharParser(string)
      .filter(p => p.parsedElement.equals(expectedChar))
      .map(p => ParsedElement(expectedChar, p.tail))
      .orElse(Failure(throw new InvalidCharException(expectedChar)))
  }

  def letterParser(string: String): Try[ParsedElement[Letter]] = {
    anyCharParser(string)
      .filter(p => p.parsedElement.isLetter)
      .map(p => ParsedElement(new Letter(p.parsedElement), p.tail))
      .orElse(Failure(throw new NotALetterException()))
  }

  def digitParser(string: String): Try[ParsedElement[Digit]] = {
    anyCharParser(string)
      .filter(p => p.parsedElement.isDigit)
      .map(p => ParsedElement(new Digit(p.parsedElement), p.tail))
      .orElse(Failure(throw new NotADigitException()))
  }

  def alphaNumParser(string: String): Try[ParsedElement[AlphaNumber]] = {
    anyCharParser(string)
      .filter(p => p.parsedElement.isLetterOrDigit)
      .map(p => ParsedElement(AlphaNumber(p.parsedElement), p.tail))
      .orElse(Failure(throw new NotAlphaNumException()))
  }

  def stringParser(expectedString: String)(string: String): Try[ParsedElement[String]] = {

    val parsers: List[String => Try[ParsedElement[Char]]] = expectedString.toList.map(char => charParser(char)(_))

    var parsedChar: Try[ParsedElement[Char]] = parsers.head(string)

    for(parser <- parsers.tail) parsedChar = parsedChar.flatMap(p => parser(p.tail))

    parsedChar.map(parsed => ParsedElement(expectedString, parsed.tail)) orElse
      Failure(throw new InvalidStringException(expectedString))

  }

  def <|>[T, K >: T ](firstParser: Parser[T], secondParser: Parser[K]): Parser[K] = {
    case string: String => firstParser(string) orElse secondParser(string) orElse Failure(new ORCombinatorException)
    case _ => Failure(new NotAStringException)
  }

  def <>[T, K](firstParser: Parser[T], secondParser: Parser[K]): Parser[(T, K)] = {
    case string: String =>
      firstParser(string).flatMap(firstP =>{
        secondParser(firstP.tail).map(secondP =>
          ParsedElement((firstP.parsedElement, secondP.parsedElement), secondP.tail))
      })
    case _ => Failure(throw new NotAStringException)
  }


  //TODO Consultar si debe parsear lo restante de parsear del left parser o parsear lo mismo pero retornar el right
  def ~>[T, K](firstParser: Parser[T], secondParser: Parser[K]): Parser[K] = {
    case string: String =>
      firstParser(string).flatMap(_ => secondParser(string))
    case _ => Failure(throw new NotAStringException)
  }

  //TODO Consultar si debe parsear lo restante de parsear del rightParser o parsear lo mismo pero retornar el left
  def <~[T, K](firstParser: Parser[T], secondParser: Parser[K]): Parser[T] = {
    case string: String =>
      secondParser(string).flatMap(_ => firstParser(string))
    case _ => Failure(throw new NotAStringException)
  }

  def satisfies[T](parser: Parser[T], condition: T => Boolean ): Parser[T] = {
    case string: String =>
      parser(string).filter(p => condition(p.parsedElement))
    case _ => Failure(throw new NotAStringException)
  }

  def opt[T](parser: Parser[T]): Parser[Any] = {
    case string: String => parser(string) orElse Success(ParsedElement((), string))
    case _ => Failure(throw new NotAStringException)
  }

  def kleeneClosure[T](parser: Parser[T]): Parser[List[T]] = {
    case string =>
      opt(parser)(string) match {
        case Success(ParsedElement((), _)) => Success(ParsedElement(List[T](), string))
        case Success(ParsedElement(parsed: T, tail: String)) =>
          var retList = new ListBuffer[T] += parsed
          kleeneClosure(parser)(tail).map(p =>{
            retList ++= p.parsedElement
            ParsedElement(retList.toList, p.tail)
          })
      }
    case _ => Failure(throw new NotAStringException)
  }

  def kleenePlus[T](parser: Parser[T]): Parser[List[T]] = {
    case string =>
      kleeneClosure(parser)(string).filter(p => p.parsedElement.isEmpty) orElse
        Failure(throw new ParserException("No kleene plus"))
    case _ => Failure(throw new NotAStringException)
  }

  def sepBy[T, K](parser: Parser[T], separator: Parser[K]): Parser[List[T]] = {
    case string =>
      kleenePlus(parser)(string) match {
        case Success(ParsedElement(list, "")) => Success(ParsedElement(list, ""))
        case Success(ParsedElement(list, tail)) =>
          separator(tail).flatMap(separated => {
            sepBy(parser, separator)(separated.tail).map(n => {
              var retList = new ListBuffer[T] ++= list
              retList ++= n.parsedElement
              ParsedElement(retList.toList, n.tail)
            })
          }) orElse Failure(throw new ParserException("Invalid separator"))
        case Failure(value) => Failure(value)
      }
    case _ => Failure(throw new NotAStringException)
  }

  def const[T, K](parser: Parser[T], constant: K): Parser[K] = {
    case string =>
      parser(string).map(p => ParsedElement(constant, string)) orElse
        Failure(throw new ParserException("Not the constant"))
    case _ => Failure(throw new NotAStringException)
  }

  def map[T, K](parser: Parser[T], mapper: T => K): Parser[K] = {
    case string =>
      parser(string).map(p => ParsedElement(mapper(p.parsedElement), p.tail))
    case _ => Failure(throw new NotAStringException)
  }

}
