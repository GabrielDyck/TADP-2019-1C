import ParsedModels.{ParsedResult, _}

import scala.collection.mutable.ListBuffer
import scala.util.{Failure, Success}

package object ObjectParsers {

  case object AnyCharParser extends Parser[Char] {
    def apply(string: String): ParsedResult[Char] = {
      string match {
        case "" => Failure(new EmptyStringException)
        case _ => Success(ParsedElement(string.head, string.tail))
      }
    }
  }

  case object VoidParser extends Parser[Unit] {
    def apply(string: String): ParsedResult[Unit] = {
      AnyCharParser(string).map(p => ParsedElement((), p.tail))
    }
  }

  case class CharParser(expectedChar: Char) extends Parser[Char] {
    def apply(string: String): ParsedResult[Char] = {
      parseCharType(string, (c: Char) => c.equals(expectedChar), new InvalidCharException(expectedChar))
    }
  }

  case object LetterParser extends Parser[Char]{
    def apply(string: String): ParsedResult[Char] = {
      parseCharType(string, (c: Char) => c.isLetter, new NotALetterException)
    }
  }

  case object DigitParser extends Parser[Char] {
    def apply(string: String): ParsedResult[Char] ={
      parseCharType(string, (c: Char) => c.isDigit, new NotADigitException)
    }
  }

  case object AlphaNumParser extends Parser[Char] {
    def apply(string: String): ParsedResult[Char] = {
      parseCharType(string, (c: Char) => c.isLetterOrDigit, new NotAlphaNumException)
    }
  }

  case class StringParser(expectedString: String) extends Parser[String] {
    def apply(string: String): ParsedResult[String] = {
      val parsers: List[Parser[Char]] = expectedString.toList.map(char => CharParser(char))
      var parsedChar: ParsedResult[Char] = parsers.head(string)
      for(parser <- parsers.tail) parsedChar = parsedChar.flatMap(p => parser(p.tail))
      parsedChar.map(parsed => ParsedElement(expectedString, parsed.tail)) orElse
        Failure(throw new InvalidStringException(expectedString))
    }
  }

  trait Parser[+T] extends ParserType[T] {

    def apply(string: String): ParsedResult[T]

    def parseCharType(string: String, func: Char => Boolean, exception: ParserException):ParsedResult[Char] = {
      AnyCharParser(string)
        .filter(p => func(p.parsedElement))
        .map(p => ParsedElement(p.parsedElement, p.tail))
        .orElse(Failure(throw exception))
    }

    def <|>[K >:T](secondParser: Parser[K]): Parser[K] = {
      val f = {string: String =>
        this(string) orElse secondParser(string) orElse Failure(throw new ORCombinatorException()) }
      ParserCombinated(f)
    }

    def <>[K](secondParser: Parser[K]): Parser[(T, K)] ={
      val f = {string: String => for {
        firstParse <- this(string)
        secondParse <- secondParser(firstParse.tail)
      } yield ParsedElement((firstParse.parsedElement, secondParse.parsedElement), secondParse.tail) }

      ParserCombinated(f)
    }

    def ~>[K](secondParser: Parser[K]): Parser[K] = {
      val f = { string: String =>
        this.<>(secondParser)(string).map(p => ParsedElement(p.parsedElement._2, p.tail))
      }
      ParserCombinated(f)
    }

    def <~[K](secondParser: Parser[K]): Parser[T] = {
      val f = { string: String =>
        this.<>(secondParser)(string).map(p => ParsedElement(p.parsedElement._1, p.tail))
      }
      ParserCombinated(f)
    }

    def satisfies(condition: T => Boolean ): Parser[T] = {
      val f = { string: String =>
        this(string).filter(p => condition(p.parsedElement))}
      ParserCombinated(f)
    }

    def opt: Parser[Any] = {
      val f = {string: String =>
        this(string) match {
          case Success(p: ParsedElement[T]) => Success(p)
          case Failure(_) => Success(ParsedElement((), string))
        }
      }
      ParserCombinated(f)
    }

    def * : Parser[List[T]] = {
      val f = { string: String =>
        this.opt(string) match {
          case Success(ParsedElement((), _)) => Success(ParsedElement(List[T](), string))
          case Success(parsed: ParsedElement[T]) =>
            var retList = new ListBuffer[T] += parsed.parsedElement
            var lastTail = parsed.tail
            var parsedTemp = this(lastTail)
            while(parsedTemp.isSuccess) {
              retList += parsedTemp.get.parsedElement
              lastTail = parsedTemp.get.tail
              parsedTemp = this(lastTail)
            }
            Success(ParsedElement(retList.toList, lastTail))
          case Failure(value) => Failure(value)
        }
      }
      ParserCombinated(f)
    }

    def + : Parser[List[T]] = {
      val f = {string: String =>
        this.*(string) match {
          case Success(ParsedElement(List(),_)) => Failure(new ParserException("No kleene plus"))
          case Success(p: ParsedElement[T]) => Success(p)
          case Failure(_) =>  Failure(new ParserException("No kleene plus"))
        }
      }
      ParserCombinated(f)
    }


    def sepBy[K](separator: Parser[K]): Parser[List[T]] = {
      val f = {string: String =>
        this.+(string) match {
          case Success(ParsedElement(List(), _)) => Failure(new ParserException("Content must be present 1 time"))
          case Success(ParsedElement(list, "")) => Success(ParsedElement(list, ""))
          case Success(ParsedElement(list, tail)) =>
            var retList = new ListBuffer[T] ++= list
            var lastTail = tail
            var parsedTemp = separator.~>(this.+)(tail) // Primero hace el separador
            while(parsedTemp.isSuccess && lastTail != ""){
              retList ++= parsedTemp.get.parsedElement
              lastTail = parsedTemp.get.tail
              if(lastTail != "") // Si el ultimo string es "", no sigo parseando
                parsedTemp = separator.~>(this.+)(lastTail)
            }

            parsedTemp.map(p => ParsedElement(retList.toList,p.tail)) orElse
              Failure(new ParserException("Invalid separator"))
          case Failure(value) => Failure(value)
        } }
      ParserCombinated(f)
    }

    def const[K](constant: K): Parser[K] = {
      val f = {string: String =>
        this(string).map(p => ParsedElement(constant, string)) orElse
          Failure(throw new ParserException("Not the constant")) }
      ParserCombinated(f)
    }

    def map[K](mapper: T => K): Parser[K] = {
      val f = {string: String =>
        this(string).map(p => ParsedElement(mapper(p.parsedElement), p.tail)) }
      ParserCombinated(f)
    }
  }
}
