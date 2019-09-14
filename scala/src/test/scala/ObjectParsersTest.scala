import ObjectParsers._
import ParsedModels.ParsedElement
import org.scalatest.FreeSpec

class ObjectParsersTest extends FreeSpec with ParserAssert {

  "AnyCharParser" - {
    "when parsing \"hello\"" - {
      "should return success with 'h' and \"ello\"" in {
        assertParsesSucceededWithResult(AnyCharParser("hello")
          , ParsedElement('h', "ello"))
      }
    }
    "when parsing an \"b\"" - {
      "should return success with 'b' and an empty string" in {
        assertParsesSucceededWithResult(AnyCharParser("b")
          , ParsedElement('b', ""))
      }
    }
    "when parsing an empty string" - {
      "should fail" in {
        assertParseFailed(AnyCharParser(""), new EmptyStringException)

      }
    }

  }

  "CharParser" - {
    "when parsing \"chau\"" - {
      "should return success with character 'c'" in {
        assertParsesSucceededWithResult(CharParser('c')("chau")
          , ParsedElement('c', "hau"))
      }
    }
    "when parsing \"hola\" and expect char 'c'" - {
      "should fail" in {
        assertParseFailed(CharParser('c')("hola"), new InvalidCharException('c'))
      }
    }
  }

  "VoidParser" - {
    "when parsing string \"hola\"" - {
      "should return success empty char " in {
        assertParsesSucceededWithResult(VoidParser("hola")
          , ParsedElement((), "ola"))
      }
    }
    "when parsing an empty string" - {
      "should fail" in {
        assertParseFailed(VoidParser(""), new EmptyStringException)

      }
    }
  }
  "LetterParser" - {
    "when parsing \"Hola\"" - {
      "should return success with character 'H'" in {
        assertParsesSucceededWithResult(LetterParser("Hola")
          , ParsedElement('H', "ola"))
      }
      "should return success with character 'h'" in {
        assertParsesSucceededWithResult(LetterParser("hola")
          , ParsedElement('h', "ola"))
      }
    }
    "when parsing \"1234\"" - {
      "should fail" in {
        assertParseFailed(LetterParser("12345"), new NotALetterException)
      }
    }
  }

  "DigitParser" - {
    "when parsing \"1234\"" - {
      "should return success with number 1" in {
        assertParsesSucceededWithResult(DigitParser("1234")
          , ParsedElement('1', "234"))
      }
    }
    "when parsing \"hola\"" - {
      "should fail" in {
        assertParseFailed(DigitParser("hola"), new NotADigitException)
      }
    }
  }

  "AlphaNumParser" - {
    "when parsing char 'S'" - {
      "should return success with character 'S'" in {
        assertParsesSucceededWithResult(AlphaNumParser("S")
          , ParsedElement('S', ""))
      }
    }
    "when parsing char 's'" - {
      "should return success with character 's'" in {
        assertParsesSucceededWithResult(AlphaNumParser("s")
          , ParsedElement('s', ""))
      }
    }
    "when parsing number 1" - {
      "should return success with num 1" in {
        assertParsesSucceededWithResult(AlphaNumParser("1")
          , ParsedElement('1', ""))
      }
    }
    "when parsing char '*' " - {
      "should fail" in {
        assertParseFailed(AlphaNumParser("*"), new NotAlphaNumException)
      }
    }
  }

  "StringParser" - {
    "when using string parser with \"hola\"" - {
      "should succeed with \"hola mundo!\"" in {
        assertParsesSucceededWithResult(StringParser("hola")("hola mundo!")
          , ParsedElement("hola", " mundo!"))
      }
      "should succeed with \"holanga!\"" in {
        assertParsesSucceededWithResult(StringParser("hola")("holanga!")
          , ParsedElement("hola", "nga!"))
      }
      "should succeed with \"hola\"" in {
        assertParsesSucceededWithResult(StringParser("hola")("hola")
          , ParsedElement("hola", ""))
      }
      "should fail with \"holgado\"" in {
        assertParseFailed(StringParser("hola")("holgado"), new InvalidStringException("hola"))
      }
      "should fail with an empty string" in {
        assertParseFailed(StringParser("hola")(""), new InvalidStringException("hola"))
      }
    }
  }

}