

package object Musica {
  object Nota {
    def notas = List(C, Cs, D, Ds, E, F, Fs, G, Gs, A, As, B)

  }

  trait Nota {
    def notas = Nota.notas

    def sostenido: Nota = notas((notas.indexOf(this) + 1) % notas.size)

    def bemol: Nota = notas((notas.indexOf(this) - 1) % notas.size)

    def acordeMenor(octava: Int, figura: Figura): Acorde =
      Acorde((this :: this + 3 :: this + 7 :: Nil).map(Tono(octava, _)), figura)

    def acordeMayor(octava: Int, figura: Figura): Acorde =
      Acorde((this :: this + 4 :: this + 7 :: Nil).map(Tono(octava, _)), figura)

    def +(cantidadDeSemitonos: Int): Nota = (1.to(cantidadDeSemitonos)).foldLeft(this) {
      case (nota, _) ⇒ nota.sostenido
    }
  }

  case object C extends Nota
  case object Cs extends Nota
  case object D extends Nota
  case object Ds extends Nota
  case object E extends Nota
  case object F extends Nota
  case object Fs extends Nota
  case object G extends Nota
  case object Gs extends Nota
  case object A extends Nota
  case object As extends Nota
  case object B extends Nota

  abstract class Figura(val duracion: Int)
  case object Redonda extends Figura(1500)
  case object Blanca extends Figura(Redonda.duracion / 2)
  case object Negra extends Figura(Blanca.duracion / 2)
  case object Corchea extends Figura(Negra.duracion / 2)
  case object SemiCorchea extends Figura(Corchea.duracion / 2)

  case class Tono(octava: Int, nota: Nota)

  trait Tocable
  case class Sonido(tono: Tono, figura: Figura) extends Tocable
  case class Silencio(figura: Figura) extends Tocable
  case class Acorde(tonos: List[Tono], figura: Figura) extends Tocable

  type Melodia = List[Tocable]

  def figuraMapper[T](equivalencia: List[T]): Figura ={
    equivalencia match{
      case List('1','1') => Redonda
      case List('1','2') => Blanca
      case List('1','4')=> Negra
      case List('1','8')=> Corchea
      case List('1',List('1','6'))=> SemiCorchea
      case _ => throw new Exception("No existe ninguna figura para parsear con equivalencia = " + equivalencia)
    }
  }
  def findNota(tupla : (List[Char],Any), octava: Int, figura: Figura): Any ={

    tupla._1 match {
      case List(letra:Char) =>getNota(letra)
      case List(letra:Char,'m') => getNota(letra).acordeMenor(octava, figura)
      case List(letra:Char,'M') => getNota(letra).acordeMayor(octava, figura)
      case List(letra:Char) if tupla._2== '#' => getNota(letra).sostenido

      case _ => throw new Exception(s"No existe ninguna Nota de la forma${tupla.toString()}" )
    }

  }

  def findSilencio(string: String): Tocable = {
    string match {
      case "_" => Silencio(Blanca)
      case "-" => Silencio(Negra)
      case "~" => Silencio(Corchea)
      case _ => throw new Exception(s"No existe ningun Silencio de la forma${string}" )
    }
  }
  def getNota (nota: Char )={
    Nota.notas.find(next => next.toString == nota.toString) getOrElse( throw new Exception(s"No existe ninguna Nota de la forma${nota.toString}" ))
  }
}