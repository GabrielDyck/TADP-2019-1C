import org.scalatest.{FreeSpec, Matchers}

class FirstTest extends FreeSpec with Matchers {
  def assertParsesSucceededWithResult[T](actualResult: T, expectedResult: T): Unit = {
    actualResult shouldBe expectedResult
  }

  def assertParseFailed[T](actualResult: ⇒ T): Unit = {
    assertThrows[ParserException](actualResult)
  }

  "CustomMusicParser" - {
    "when fed empty text" - {
      "parses an empty list of notes" in {
        assertParsesSucceededWithResult(new MusicParser("").parse(), List())
      }
    }
  }
}
