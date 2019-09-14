# Trabajo Práctico de paradigma híbrido funcional/objetos: Parsers - Parte I

El repositorio ya contiene código con una implementación de un parsers que recibe como entrada un `String` y retorna una lista de `Nota`s, donde lo que es una `Nota` es un ya tipo definido.

Hay tests mostrando el funcionamiento del parser hasta el momento, pero un ejemplo de como funciona es:

```scala
> new MusicParser("A B C D E F").parse()

#=> List(A, B, C, D, E, F)
```

Breve descripción de los archivos que hay en el repositorio:

- **Musica.scala**: es donde está definido que es una nota.
- **AudioPlayer.scala**: un objeto que wrappea a javax para dar una interfaz para reproducir música a partir de un `List[Nota]` o de un `String` (en este caso usando el parser).
- **PlayAudio.scala**: un objeto ejecutable (extiende de App) que está para que puedan probar el parseo haciendo que se escuche una melodía a partir del string que le pasan a AudioPlayer.
- **MusicParser**: la definición del parsers que convierte `String` a `List[Nota]`. Este archivo, y sus tests, son los únicos archivos que es necesario tocar para la entrega (PlayAudio se puede modifcar para probar diferentes canciones pero la idea no es commitear cambios a eso).

## Objetivo de la entrega

Al formato que creamos para escribir música le queremos agregar la posibilidad de repetir cierto patrón varias veces de la siguiente manera:

```scala
val notas = "3x(A E B) F C A"
```

que para el parser debería ser equivalente a esto que podemos escribir actualmente:

```scala
val notas = "A E B A E B A E B F C A"
```

Es decir, cuando tenemos un numero multiplicando a algo entre paréntesis, queremos que eso se repita la cantidad de veces indicada por el número.

Importante, el patrón podría anidarse las veces que se quiera.

```scala
> new MusicParser("2x(A B 3x(F G 2x(A))) F B E").parse()
#=> List(A, B, F, G, A, A, F, G, A, A, F, G, A, A, A, B, F, G, A, A, F, G, A, A, F, G, A, A, F, B, E)
```