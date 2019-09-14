import Musica._
import ObjectParsers.{AlphaNumParser, CharParser, DigitParser, LetterParser}
import ParsedModels.ParsedElement
import scala.util.{Failure, Success, Try}

case class MelodiaParser(melodia: String){
  def apply :List[Tocable] = {

    val silenciosParsers = CharParser('-').<|>(CharParser('_')).<|>(CharParser('~'))
    val coreParser = AlphaNumParser.<|>(CharParser('#')).<|>(silenciosParsers).<|>(CharParser('/')).*
    // Primero debe haber al menos una melodia, ParsedElement(List[List[Char]],List[Char], String)
    val melodiaParseada = coreParser.<~(CharParser(' ')).*.<>(coreParser)(melodia)

    if(melodiaParseada.isFailure) {
      throw MelodiaInvalidaException("Unexpected error", new Exception("Failed creating core melody."))
    }

    // Mapeo List[List[Char]] => List[String] y List[Char] => String y los concateno
    val pentagrama : List[String] = melodiaParseada.get.parsedElement._1.map(_.mkString)
      .:+(melodiaParseada.get.parsedElement._2.mkString)
    pentagrama.map(string =>
      parseTocable(string) match {
        case Success(tocable: Tocable) => tocable
        case Failure(e: Exception) => throw MelodiaInvalidaException(melodia,e)
      })
  }

  def parseFigura(figure: List[Char]): Figura = {
    Try(Musica.figuraMapper(figure)) match {
      case Success(figura : Figura) => figura
      case Failure(e: Exception) =>  throw e
    }
  }

  def parseTocable(string: String): Try[Tocable]= {
    string match {
      case "-" | "_" | "~" => Success(Musica.findSilencio(string))
      case _ =>
        val parserTocable = DigitParser.<>(LetterParser.+.<>(CharParser('#').opt))
          .<>(DigitParser.+.sepBy(CharParser('/')))(string)
        parserTocable match {
          case Success(ParsedElement(((numero, letra), figura: List[List[Char]]), "")) =>
            val figuraParseada = this.parseFigura(figura.flatten)
            Success(buildTocable(numero, letra, figuraParseada))
          case Failure(e: Exception) => Failure(e)
        }

    }
  }

  private def buildTocable(numero: Char,letras:(List[Char],Any), figura: Figura) :Tocable= {
    val octava= numero.asDigit
    Musica.findNota(letras,octava,figura)match {
      case nota: Nota => Sonido(Tono(octava,nota),figura)
      case acorde: Acorde => acorde
    }
  }

  case class MelodiaInvalidaException(string : String, e :Exception)
    extends Exception(s"La melodia ${string} no se pudo parsear. Razon: ${e.getMessage}")
}
