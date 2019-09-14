import Musica._
import org.scalatest.{FreeSpec, Matchers}

class MelodiaParserTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: T, expectedResult: T): Unit = {
    actualResult shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ T): Unit = {
    assertThrows[ParserException](actualResult)
  }
  //Redonda (1/1), Blanca (1/2), Negra (1/4), Corchea (1/8), SemiCorchea (1/16).

  "MelodiaParser" - {
    "cuando envio la melodia de feliz cumpleaños" - {
      "parsea toda la melodia y retorna una lista de sonidos" in {
        val melodia=MelodiaParser("4C1/4 4C1/4 4D1/2 4C1/4 4F1/2 4E1/2 4C1/8 4C1/4" +
          " 4D1/2 4C1/2 4G1/2 4F1/2 4C1/8 4C1/4 5C1/2 4A1/2 4F1/8 4F1/4 4E1/2 4D1/2")
        assertParsesSucceededWithResult(melodia.apply, List(
          Sonido(Tono(4,C),Negra),
          Sonido(Tono(4,C),Negra),
          Sonido(Tono(4,D),Blanca),
          Sonido(Tono(4,C),Negra),
          Sonido(Tono(4,F),Blanca),
          Sonido(Tono(4,E),Blanca),
          Sonido(Tono(4,C),Corchea),
          Sonido(Tono(4,C),Negra),
          Sonido(Tono(4,D),Blanca),
          Sonido(Tono(4,C),Blanca),
          Sonido(Tono(4,G),Blanca),
          Sonido(Tono(4,F),Blanca),
          Sonido(Tono(4,C),Corchea),
          Sonido(Tono(4,C),Negra),
          Sonido(Tono(5,C),Blanca),
          Sonido(Tono(4,A),Blanca),
          Sonido(Tono(4,F),Corchea),
          Sonido(Tono(4,F),Negra),
          Sonido(Tono(4,E),Blanca),
          Sonido(Tono(4,D),Blanca)))
      }
    }
    "cuando envio la melodia de extra bonus" - {
      "parsea toda la melodia y retorna una lista de sonidos" in {
        val melodia=MelodiaParser("4AM1/8 5C1/8 5C#1/8 5C#1/8 5D#1/8 5C1/8 4A#1/8 4G#1/2 " +
          "- 4A#1/8 4A#1/8 5C1/4 5C#1/8 4A#1/4 4G#1/2 5G#1/4 5G#1/4 5D#1/2")
        assertParsesSucceededWithResult(melodia.apply, List(
          Acorde(List(Tono(4,A), Tono(4,Cs), Tono(4,E)),Corchea),
          Sonido(Tono(5,C),Corchea),
          Sonido(Tono(5,C),Corchea),
          Sonido(Tono(5,C),Corchea),
          Sonido(Tono(5,D),Corchea),
          Sonido(Tono(5,C),Corchea),
          Sonido(Tono(4,A),Corchea),
          Sonido(Tono(4,G),Blanca),
          Silencio(Negra),
          Sonido(Tono(4,A),Corchea),
          Sonido(Tono(4,A),Corchea),
          Sonido(Tono(5,C),Negra),
          Sonido(Tono(5,C),Corchea),
          Sonido(Tono(4,A),Negra),
          Sonido(Tono(4,G),Blanca),
          Sonido(Tono(5,G),Negra),
          Sonido(Tono(5,G),Negra),
          Sonido(Tono(5,D),Blanca)))
      }
    }
  }
}