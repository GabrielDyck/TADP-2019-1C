import FunctionalParsers.Parser
import ParsedModels._
import org.scalatest.{FreeSpec, Matchers}

import scala.util.{Failure, Success}

class FunctionalParsersTests extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: T, expectedResult: T): Unit = {
    actualResult shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ T): Unit = {
    assertThrows[ParserException](actualResult)
  }

  def assertExceptionFailureAndMessage[T](actualResult: T, expectedResult: Failure[Throwable]): Unit = {

    actualResult match {
      case Failure(exception) =>
        exception.getClass shouldBe expectedResult.exception.getClass
        exception.getMessage shouldBe expectedResult.exception.getMessage
      case _ => fail()
    }
  }

  "AnyCharParser" - {
    "when parsing 'hello'" - {
      "should return success with character 'h'" in {

        assertParsesSucceededWithResult(FunctionalParsers.anyCharParser("hello")
          , Success(ParsedElement('h', "ello")))
      }
    }

    "when parsing an empty string" - {
      "should return failure" in {
        assertExceptionFailureAndMessage(FunctionalParsers.anyCharParser("")
          , Failure(new EmptyStringException))

      }
    }
    "when parsing an empty string" - {
      "should reddturn failure" in {
        assertExceptionFailureAndMessage(FunctionalParsers.anyCharParser("")
          , Failure(new EmptyStringException))
      }
    }

  }
  "CharParser" - {
    "when parsing s'chau'" - {
      "should return success with character 'c'" in {
        assertParsesSucceededWithResult(FunctionalParsers.charParser('c')("chau")
          , Success(ParsedElement('c', "hau")))
      }
    }
    "when parsing s'hola' and expect char 'c'" - {
      "should return failure " in {
        assertExceptionFailureAndMessage(FunctionalParsers.charParser('c')("hola")
          , Failure(new InvalidCharException('c')))
      }
    }
  }

  "VoidParser" - {

    "when parsing string 'hola'" - {
      "should return success empty char " in {
        assertParsesSucceededWithResult(FunctionalParsers.voidParser("hola")
          , Success(ParsedElement((), "ola")))
      }
    }

    "when parsing empty string ''" - {
      "should fail" in {
        assertExceptionFailureAndMessage(FunctionalParsers.voidParser("")
          , Failure(new EmptyStringException))

      }
    }
  }
  "LetterParser" - {
    "when parsing s'Hola'" - {
      "should return success with character 'H'" in {
        assertParsesSucceededWithResult(FunctionalParsers.letterParser("Hola")
          , Success(ParsedElement(new Letter('H'), "ola")))
      }
    }
    "when parsing s'hola'" - {
      "should return success with character 'h'" in {
        assertParsesSucceededWithResult(FunctionalParsers.letterParser("hola")
          , Success(ParsedElement(new Letter('h'), "ola")))
      }
    }
    "when parsing s'1234' " - {
      "should return failure " in {
        assertExceptionFailureAndMessage(FunctionalParsers.letterParser("12345")
          , Failure(new NotALetterException()))
      }
    }
  }

  "DigitParser" - {
    "when parsing s'1234'" - {
      "should return success with number '1'" in {
        assertParsesSucceededWithResult(FunctionalParsers.digitParser("1234")
          , Success(ParsedElement(new Digit('1'), "234")))
      }
    }
    "when parsing s'hola'" - {
      "should return fail with character 'h'" in {
        assertExceptionFailureAndMessage(FunctionalParsers.digitParser("hola")
          , Failure(new NotADigitException()))
      }
    }
  }
  "AlfaNumParser" - {
    "when parsing char 'S'" - {
      "should return success with character 'S'" in {
        assertParsesSucceededWithResult(FunctionalParsers.alphaNumParser("S")
          , Success(ParsedElement(AlphaNumber('S'), "")))
      }
    }
    "when parsing char 's'" - {
      "should return success with character 's'" in {
        assertParsesSucceededWithResult(FunctionalParsers.alphaNumParser("s")
          , Success(ParsedElement(AlphaNumber('s'), "")))
      }
    }
    "when parsing number 1" - {
      "should return success with num 1" in {
        assertParsesSucceededWithResult(FunctionalParsers.alphaNumParser("1")
          , Success(ParsedElement(AlphaNumber('1'), "")))
      }
    }
    "when parsing char'*' " - {
      "should return failure " in {
        assertExceptionFailureAndMessage(FunctionalParsers.alphaNumParser("*")
          , Failure(new NotAlphaNumException))
      }
    }
  }

  "StringParser" - {
    "when parsing string 'hola mundo!'" - {
      "should return parsed only hola" in {
        assertParsesSucceededWithResult(FunctionalParsers.stringParser("hola")("hola mundo!")
          , Success(ParsedElement("hola", " mundo!")))
      }
    }
    "when parsing string 'holanga!'" - {
      "should return parsed only hola" in {
        //val toParse = Success(ParsedChar(' ',"hola mundo!"))
        assertParsesSucceededWithResult(FunctionalParsers.stringParser("hola")("holanga!")
          , Success(ParsedElement("hola", "nga!")))
      }
    }
    "when parsing string 'holgado'" - {
      "should return fail" in {
        assertExceptionFailureAndMessage(FunctionalParsers.stringParser("hola")("holgado")
          , Failure(new InvalidStringException("hola")))
      }
    }
  }


  "ORCombinator" - {
    "when parsing 'a' OR 'b' with 'arbol'" - {
      "should return success with char 'a'" in {

        assertParsesSucceededWithResult(FunctionalParsers.<|>(FunctionalParsers.charParser('a'), FunctionalParsers.charParser('b'))("arbol")
          , Success(ParsedElement('a', "rbol")))
      }
    }
    "when parsing 'a' OR 'b' with 'bort'" - {
      "should return success with char 'b'" in {

        assertParsesSucceededWithResult(FunctionalParsers.<|>(FunctionalParsers.charParser('a'), FunctionalParsers.charParser('b'))("bort")
          , Success(ParsedElement('b', "ort")))
      }
    }
    "when parsing 'a' OR 'b' with 'chau'" - {
      "should return failure" in {

        assertExceptionFailureAndMessage(FunctionalParsers.<|>(FunctionalParsers.charParser('a'), FunctionalParsers.charParser('b'))("chau")
          , Failure(new ORCombinatorException))
      }
    }
  }

  "ConcatCombinator" - {
    "when parsing 'hola' concat with 'mundo' with 'holamundo'" - {
      "should return success" in {

        assertParsesSucceededWithResult(FunctionalParsers.<>(FunctionalParsers.stringParser("hola"), FunctionalParsers.stringParser("mundo"))("holamundo")
          , Success(ParsedElement(("hola", "mundo"), "")))
      }
    }
    "when parsing 'hola' concat with 'mundo' with 'holachau'" - {
      "should fail" in {

        assertExceptionFailureAndMessage(FunctionalParsers.<>(FunctionalParsers.stringParser("hola"), FunctionalParsers.stringParser("mundo"))("holachau")
          , Failure(new InvalidStringException("mundo")))
      }
    }

  }

  "RightmostCombinator" - {
    "when parsing 'hola' and  'mundo' rightmost with 'holamundo'" - {
      "should return success holamundo" in {

        assertParsesSucceededWithResult(FunctionalParsers.~>(FunctionalParsers.stringParser("hola"), FunctionalParsers.stringParser("holamundo"))("holamundo")
          , Success(ParsedElement("holamundo", "")))
      }
    }
  }

  "LeftmostCombinator" - {
    "when parsing 'hola' and  'mundo' leftmost with 'holamundo'" - {
      "should return success hola" in {

        assertParsesSucceededWithResult(FunctionalParsers.<~(FunctionalParsers.stringParser("hola"), FunctionalParsers.stringParser("holamundo"))("holamundo")
          , Success(ParsedElement("hola", "mundo")))
      }
    }
  }

  "SatifiesParser" - {
    "when parsing 'hola' stringParser with 'holamundo' and satifies contains hola" - {
      "should return original StringParser" in {

        val containsHola : String => Boolean=(parsed: String) => parsed.contains("hola")
        assertParsesSucceededWithResult(FunctionalParsers.satisfies(FunctionalParsers.stringParser("hola"),containsHola)("holamundo")
          , FunctionalParsers.stringParser("hola")("holamundo"))
      }
    }
    "when parsing 'hola' stringParser with 'holamundo' and satifies contains chau" - {
      "should return Failure" in {

        val containsChau : String => Boolean=(parsed: String) => parsed.contains("chau")
        assertExceptionFailureAndMessage(FunctionalParsers.satisfies(FunctionalParsers.stringParser("hola"),containsChau)("holamundo")
          , Failure(new NoSuchElementException("Predicate does not hold for ParsedElement(hola,mundo)")))
      }
    }
  }

}