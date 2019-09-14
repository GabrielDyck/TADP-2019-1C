class NotAStringException extends ParserException("Not a string.")

class EmptyStringException extends ParserException("Cant parse an empty string.")

class InvalidCharException(expectedChar: Char)
  extends ParserException("Character is not " + expectedChar.toString)

class NotALetterException
  extends ParserException("Character is not a letter.")

class NotADigitException
  extends ParserException("Character  is not a digit.")

class NotAlphaNumException
  extends ParserException("Character is not a alphaNum.")

class InvalidStringException(expectedString: String)
  extends ParserException("String doesnt start with " + expectedString)


class ORCombinatorException
  extends ParserException("Cannot parse any of parsers")