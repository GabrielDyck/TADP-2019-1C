import ParsedModels._
import org.scalatest.Matchers
import org.scalatest.TryValues._

import scala.reflect.ClassTag
import scala.util.{Failure, Success}

trait ParserAssert extends Matchers{

  def assertParsesSucceededWithResult[T](actualResult: ParsedResult[T], expectedResult: ParsedElement[T]): Unit = {
    actualResult match{
      case Success(value: ParsedElement[T]) => value shouldBe expectedResult
      case Failure(exception) => exception shouldBe expectedResult
    }
  }

  def assertParseFailedWithMessage[T](parsed: ParsedResult[T], expectedMessage: String): Unit = {
    parsed.failure.exception should have message expectedMessage
  }

  def assertParseFailed[T](parsed: ParsedResult[T]): Unit = {
    parsed.failure.exception shouldBe a [ParserException]
  }

  def assertParseFailed[T, K: ClassTag](parsed: ParsedResult[T], exception: K): Unit = {
    parsed.failure.exception shouldBe a [K]
  }

}
