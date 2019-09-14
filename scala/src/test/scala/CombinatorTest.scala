import ParsedModels.ParsedElement
import ObjectParsers._
import org.scalatest.FreeSpec

class CombinatorTest extends FreeSpec with ParserAssert {

  "OR Combinator" - {
    "when using char parsers with 't' or with 's'" - {
      val tos = CharParser('t') <|> CharParser('s')

      "should return success when parsing \"tree\"" in {
        assertParsesSucceededWithResult(tos("tree"), ParsedElement('t', "ree"))
      }
      "should return success when parsing \"sun\"" in {
        assertParsesSucceededWithResult(tos("sun"), ParsedElement('s', "un"))
      }
      "should fail when parsing \"moon\"" in {
        assertParseFailed(tos("moon"), new ORCombinatorException)
      }

    }

    "when parsing 'a' OR 'b''" - {
      val aob = CharParser('a').<|>(CharParser('b'))
      "should return when parsing 'arbol' success with char 'a'" in {
        assertParsesSucceededWithResult(aob("arbol")
          , ParsedElement('a', "rbol"))
      }
      "should return success when parsing 'bort' with char 'b'" in {
        assertParsesSucceededWithResult(aob("bort")
          , ParsedElement('b', "ort"))
      }
      "should return failure when parsing 'chau'" in {
        assertParseFailed(aob("chau")
          , new ORCombinatorException)
      }
    }

    "when using char parser with 'c' or void parser" - {
      val tov = CharParser('c') <|> VoidParser

      "should return success when parsing \"car\"" in {
        assertParsesSucceededWithResult(tov("car"), ParsedElement('c', "ar"))
      }
      "should return success when parsing \"c\"" in {
        assertParsesSucceededWithResult(tov("c"), ParsedElement('c', ""))
      }
      "should return success when parsing \"bike\"" in {
        assertParsesSucceededWithResult(tov("bike"), ParsedElement((), "ike"))
      }
      "should fail when parsing an empty string" in {
        assertParseFailed(tov(""), new ORCombinatorException)
      }
    }
  }

  "And Combinator" - {
    "when using char parsers with \"hola\" or with \"mundo\"" - {
      val holaMundo = StringParser("hola") <> StringParser("mundo")

      "should return success when parsing \"holamundo\"" in {
        assertParsesSucceededWithResult(holaMundo("holamundo"), ParsedElement(("hola", "mundo"), ""))
      }
      "should return success when parsing \"holamundoasd\"" in {
        assertParsesSucceededWithResult(holaMundo("holamundoasd"), ParsedElement(("hola", "mundo"), "asd"))
      }
      "should fail when parsing \"holachau\"" in {
        assertParseFailed(holaMundo("holachau"))
      }
    }
  }

  "~> Combinator" - {
    "when using char parsers with 't' or with 's'" - {
      val tos = CharParser('t') ~> CharParser('s')

      "should return success when parsing \"tsarbol\"" in {
        assertParsesSucceededWithResult(tos("tsarbol"), ParsedElement('s', "arbol"))
      }
      "should return success when parsing \"ts\"" in {
        assertParsesSucceededWithResult(tos("ts"), ParsedElement('s', ""))
      }
      "should fail when parsing \"arbol\"" in {
        assertParseFailed(tos("arbol"))
      }
    }
    "when parsing 'hola' and  'mundo' rightmost with 'holamundo'" - {
      "should return success holamundo" in {

        assertParsesSucceededWithResult(StringParser("hola").~>(StringParser("mundo"))("holamundo")
          , ParsedElement("mundo", ""))
      }
    }
  }

  "<~ Combinator" - {
    "when using char parsers with 't' or with 's'" - {
      val tos = CharParser('t') <~ CharParser('s')

      "should return success when parsing \"tsarbol\"" in {
        assertParsesSucceededWithResult(tos("tsarbol"), ParsedElement('t', "arbol"))
      }
      "should return success when parsing \"ts\"" in {
        assertParsesSucceededWithResult(tos("ts"), ParsedElement('t', ""))
      }
      "should fail when parsing \"salsa\"" in {
        assertParseFailed(tos("salsa"))
      }
    }

    "when parsing 'hola' and  'mundo' leftmost with 'holamundo'" - {
      "should return success hola" in {
        assertParseFailed(StringParser("hola").<~(StringParser("holamundo"))("holamundo"))
      }
    }
  }

  "satisfies Combinator" - {
    "when using digit parser with less than 8 condition " - {
      val lessThanEight = DigitParser satisfies { c: Char => c.asDigit < 8}

      "should return success when parsing \"7asd\"" in {
        assertParsesSucceededWithResult(lessThanEight("7asd"), ParsedElement('7', "asd"))
      }
      "should return success when parsing \"4\"" in {
        assertParsesSucceededWithResult(lessThanEight("4"), ParsedElement('4', ""))
      }
      "should fail when parsing \"9arbol\"" in {
        assertParseFailed(lessThanEight("salsa"))
      }
    }
    "when parsing 'hola' stringParser with 'holamundo' and satifies contains hola" - {
      "should return original StringParser" in {

        val containsHola: String => Boolean = (parsed: String) => parsed.contains("hola")
        assertParsesSucceededWithResult(StringParser("hola").satisfies(containsHola)("holamundo")
          , StringParser("hola")("holamundo").get)
      }
    }
    "when parsing 'hola' stringParser with 'holamundo' and satifies contains chau" - {
      "should return Failure" in {

        val containsChau: String => Boolean = (parsed: String) => parsed.contains("chau")
        assertParseFailed(StringParser("hola").satisfies(containsChau)("holamundo")
          , new NoSuchElementException("Predicate does not hold for ParsedElement(hola,mundo)"))
      }
    }
  }

  "opt Combinator" - {
    "when using string parser with \"in\" as optional " - {
      val talVezIn: Parser[Any] = StringParser("in").opt
      val precedencia = talVezIn <> StringParser("fija")

      "should return success when parsing \"infija\"" in {
        assertParsesSucceededWithResult(precedencia("infija"), ParsedElement(("in", "fija"), ""))
      }
      "should return success when parsing \"fija\"" in {
        assertParsesSucceededWithResult(precedencia("fija"), ParsedElement(((),"fija"), ""))
      }
      "should return success when parsing \"fijain\"" in {
        assertParsesSucceededWithResult(precedencia("fijain"), ParsedElement(((),"fija"), "in"))
      }
      "should fail when parsing \"casa\"" in {
        assertParseFailed(precedencia("casa"))
      }
    }
  }

  "KleneClosure" - {
    "when parsing 'aaa'" - {
      "should return List('a' 'a' 'a')" in {
        val klene = StringParser("a").*
        assertParsesSucceededWithResult(klene("aaa"), ParsedElement(List("a", "a", "a"), ""))
      }
    }
    "when parsing 'aaab'" - {
      "should return List('a' 'a' 'a')" in {
        val klene = StringParser("a").*
        assertParsesSucceededWithResult(klene("aaab"), ParsedElement(List("a", "a", "a"), "b"))
      }
    }

    "when parsing 'baaab'" - {
      "should return List('')" in {
        val klene = StringParser("a").*
        assertParsesSucceededWithResult(klene("baaab"), ParsedElement(List(), "baaab"))
      }
    }
    "when parsing ''" - {
      "should return List('')" in {
        val klene = StringParser("a").*
        assertParsesSucceededWithResult(klene(""), ParsedElement(List(), ""))
      }
    }

  }

  "KleneClosure+" - {
    "when parsing 'aaa'" - {
      "should return List('a' 'a' 'a')" in {
        val klene = StringParser("a").+
        assertParsesSucceededWithResult(klene("aaa"), ParsedElement(List("a", "a", "a"), ""))
      }
    }
    "when parsing 'aaab'" - {
      "should return List('a' 'a' 'a')" in {
        val klene = StringParser("a").+
        assertParsesSucceededWithResult(klene("aaab"), ParsedElement(List("a", "a", "a"), "b"))
      }
    }

    "when parsing 'baaab'" - {
      "should fail" in {
        val klene = StringParser("a").+
        assertParseFailed(klene("baaab"), new ParserException("No kleene plus"))
      }
    }
    "when parsing ''" - {
      "should return List('')" in {
        val klene = StringParser("a").+
        assertParseFailed(klene(""),  new ParserException("No kleene plus"))
      }
    }
  }

  "SepBy" - {
    "when parsing '1234-5678'" - {
      "should return List('1','2','3','4','5','6','7','8')" in {
        //Funciona bien pero falla el assert. Ver mensaje de error. Ver luego

        val sepBy = DigitParser.sepBy(CharParser('-'))("1234-5678")
        assertParsesSucceededWithResult(sepBy, ParsedElement(List('1','2','3','4','5','6','7','8'),""))
      }
    }

    "when parsing '1234 5678'" - {
      "should return fail" in {
        val sepBy = DigitParser.sepBy(CharParser('-'))
        assertParseFailed(sepBy("1234 5678"),  new ParserException("Invalid separator"))
      }
    }
    "when parsing '1234'" - {
      "should return List('1','2','3','4')" in {
        //Funciona bien pero falla el assert. Ver mensaje de error. Ver luego

        val sepBy = DigitParser.sepBy(CharParser('-'))
        assertParsesSucceededWithResult(sepBy("1234"), ParsedElement(List('1','2','3','4'),""))
      }
    }
    "when parsing '-1234'" - {
      "should fail" in {
        val sepBy = DigitParser.sepBy(CharParser('-'))
        //Deberia enviar ese mensaje
        assertParseFailed(sepBy("-1234"), new ParserException("Content must be present 1 time"))
      }
    }

    "when parsing ''" - {
      "should fail" in {
        val sepBy = DigitParser.sepBy(CharParser('-'))
        assertParseFailed(sepBy(""),  new ParserException("Content must be present 1 time"))
      }
    }  }

  "ConstParser" - {
    "when parsing 'true' and const boolean true" - {
      "should return (true,'true')" in {
        val const = StringParser("true").const(true)("true")
        assertParsesSucceededWithResult(const, ParsedElement(true, "true"))
      }
    }
    "when parsing 'true' and const integer 1" - {
      "should return (1,'true')" in {
        val const = StringParser("true").const(1)("true")
        assertParsesSucceededWithResult(const, ParsedElement(1, "true"))
      }
    }
    "when parsing 'false' and const integer 1" - {
      "should return failure" in {
        val const = StringParser("true").const(1)("false")
        assertParseFailed(const, new ParserException("Not the constant"))
      }
    }


    "MapParser" - {
      "when parsing 'Nombre' with StringParser and map 'Nombre' into Persona" - {
        "should return (Persona('Nombre'),'')" in {
          case class Persona(nombre: String, apellido: String)
          val personaParser = (AlphaNumParser.* <> (CharParser(' ') ~> AlphaNumParser.*))
            .map { case (nombre:List[Char],apellido:List[Char])=> Persona(nombre.mkString, apellido.mkString) }("Nombre Apellido")

         assertParsesSucceededWithResult(personaParser,ParsedElement(Persona("Nombre","Apellido"),""))
        }
      }

    }

  }
}
