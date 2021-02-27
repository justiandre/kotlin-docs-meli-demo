# Capítulo 10 - Coleções

## Sumário

1. [Hierarquia de classes](#Hieraquia-de-classes)
2. [Arrays](#Arrays)
3. [Listas](#Listas)
4. [Mapas](#Mapas)
5. [Conjuntos](#conjuntos)
6. [Visões somente de leitura](#Visões-somente-de-leitura)
7. [Acesso indexado](#Acesso-indexado)
8. [Sequências](#Sequências)

## Hierarquia de classes
kotlin distingue mutáveis e imutáveis. Interface Collection oferece suporte a métodos somente de leitura, enquanto a Mutable Collection oferece suporte a métodos de leitura/gravação.

```kotlin
val theList = listOf("one", "two", "three")    
 
val theMutableList = mutableListOf("one", "two", "three")
```
Todas as coleções estão no namespace kotlin.collectionse e todos os tipos mutáveis podem ser facilmente identificados, pois têm o prefixo Mutable.

Em kotlin que todas as interfaces somente de leitura são covariantes.
Covariante é um termo que se refere à capacidade de mudar o argumento de tipo genérico de uma classe por um argumento de seus pais.

Isso significa que você pode tomar uma List<String> e atribuí-la a List<Any>, pois a classe Any é pai de String.
Ver mais sobre variance - https://kotlinlang.org/docs/reference/generics.html#variance

Percorrendo as interfaces e suas implementações. Temos no topo a interface Iterable com uma simples definição. 

```kotlin 
public interface Iterable<out T> { 
    public abstract operator fun iterator(): Iterator<T> 
}
```

**Collection< T >** é a raiz da hierarquia de coleção. Essa interface representa o comportamento comum de uma coleção somente leitura, estende de Iterable. Collections define operadores de consulta para uma colecao: tamanho, se é vazio, se um elemento esta presente.

```kotlin
fun printAll(strings: Collection<String>) {
    for(s in strings) print("$s ")
    println()
}
    
fun main() {
    val stringList = listOf("one", "two", "one")
    printAll(stringList)
    
    val stringSet = setOf("one", "two", "three")
    printAll(stringSet)
}
```

**MutableCollection** é um Collection com operações de gravação, como add e remove.

```kotlin
fun List<String>.getShortWordsTo(shortWords: MutableList<String>, maxLength: Int) {
    this.filterTo(shortWords) { it.length <= maxLength }
    // throwing away the articles
    val articles = setOf("a", "A", "an", "An", "the", "The")
    shortWords -= articles
}

fun main() {
    val words = "A long time ago in a galaxy far far away".split(" ")
    val shortWords = mutableListOf<String>()
    words.getShortWordsTo(shortWords, 3)
    println(shortWords)//[ago, in, far, far]
}
```

**List< < T >** armazena elementos em uma ordem especificada e fornece acesso indexado a eles. Os índices começam do zero e vão até (list.size - 1). 

```kotlin 
val numbers = listOf("one", "two", "three", "four")
println("Number of elements: ${numbers.size}")
println("Third element: ${numbers.get(2)}")
println("Fourth element: ${numbers[3]}")
println("Index of element \"two\" ${numbers.indexOf("two")}")
```

**MutableList < T >** é um List com operação de gravação específica, por exemplo, para adicionar ou remover um elemento em uma posição específica.

```kotlin
val numbers = mutableListOf(1, 2, 3, 4)
numbers.add(5)
numbers.removeAt(1)
numbers[0] = 0
numbers.shuffle()
println(numbers)
```

As listas são muito semelhantes ao Arrays. No entanto, há uma diferença importante: o tamanho de uma matriz é definido na inicialização e nunca é alterado; por sua vez, uma lista não tem um tamanho predefinido; o tamanho de uma lista pode ser alterado como resultado de operações de gravação: adição, atualização ou remoção de elementos.

No Kotlin, a implementação padrão List é a ArrayList que você pode considerar como uma Array redimensionável.

**Set< T >** armazena elementos únicos, sua ordem é geralmente indefinida. Set pode conter apenas um null.

```kotlin 
val numbers = setOf(1, 2, 3, 4)
println("Number of elements: ${numbers.size}")
if (numbers.contains(1)) println("1 is in the set")

val numbersBackwards = setOf(4, 3, 2, 1)
println("The sets are equal: ${numbers == numbersBackwards}")
```
**MutableSet** é um Set com operações de gravação de MutableCollection.

```kotlin
val set = mutableSetOf(1, 2, 3)
println(set) // [1, 2, 3]

set.remove(3)
set += listOf(4, 5)
println(set) // [1, 2, 4, 5]
```

A implementação padrão de Set- LinkedHashSet- preserva a ordem de inserção dos elementos. Portanto, as funções que dependem da ordem, como first() ou last(), retornam resultados previsíveis.

```kotlin
val numbers = setOf(1, 2, 3, 4)  // LinkedHashSet is the default implementation
val numbersBackwards = setOf(4, 3, 2, 1)

println(numbers.first() == numbersBackwards.first())
println(numbers.first() == numbersBackwards.last())
```

**Map < K, V >** não é um herdeiro da interface Collection; no entanto, também é um tipo de coleção Kotlin. Map armazena pares de valores-chave (ou entradas ); chaves são únicas, mas chaves diferentes podem ser combinadas com valores iguais. Map interface fornece funções específicas, como acesso ao valor por chave, pesquisa de chaves e valores e etc.

```kotlin
val numbersMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key4" to 1)

println("All keys: ${numbersMap.keys}")

println("All values: ${numbersMap.values}")

if ("key2" in numbersMap) println("Value by key \"key2\": ${numbersMap["key2"]}")    

if (1 in numbersMap.values) println("The value 1 is in the map")

if (numbersMap.containsValue(1)) println("The value 1 is in the map") // same as previous
```

**MutableMap** é um mapa com operações de gravação de mapa. 

 ```kotlin
val numbersMap = mutableMapOf("one" to 1, "two" to 2)
numbersMap.put("three", 3)
numbersMap["one"] = 11

println(numbersMap)
 ```

**Array** -  isolado na hieraquia temos uma classe Array. Armazena um número fixo de valores de um dado tipo. Seu tamanho é definido na criação, e não pode ser alterado. Arrays podem ser criados usando as funções da biblioteca padrão construtor, arrayOf , arrayOfNulls e emptyArray 

 ```kotlin
 val nums1 = arrayOf(1, 2, 3, 4, 5)
 val nums2 = IntArray(5, {i -> i * 2 + 3})
 var nullArray= arrayOfNulls<String>(5)
 ```

Temos ainda na hierarquia um espaço para os iteradores, kotlin tem para os mutáveis e imutáveis. 

```kotlin
public interface Iterator<out T> { 
    public operator fun next(): T 
    public operator fun hasNext(): Boolean 
}
```

```kotlin
public interface MutableIterator<out T> : Iterator<T> {
    public fun remove(): Unit 
}
```
Um Iterator faz a leitura somente para frente. Isso significa que não podemos retroceder para o elemento visitado antes. Para aceitar essa funcionalidade, a biblioteca disponibiliza ListIterator e MutableListIterator. 

**Sequencias**
Juntamente com as coleções, a biblioteca padrão Kotlin contém outro tipo de contêiner - sequências ( Sequence<T>). As seqüências oferecem as mesmas funções, Iterable mas implementam outra abordagem para o processamento em várias etapas.


```kotlin
    val numbersSequence = sequenceOf("four", "three", "two", "one")
```

Se você já possui um Iterable objeto (como Listou ou Set), pode criar uma sequência a partir dele chamando asSequence().

```kotlin
    val numbers = listOf("one", "two", "three", "four")
    val numbersSequence = numbers.asSequence()
```

Sequencia infinita chamamos generateSequence()
```kotlin
val seqFromFunction = generateSequence(Instant.now()) {it.plusSeconds(1)}
```

Como o Stream em Java, o Sequence no Kotlin é executado preguiçosamente. A diferença é que, se usarmos uma sequência para processar uma coleção usando várias operações, não obteremos um resultado intermediário no final de cada etapa. 
Sequence não introduz uma nova coleção após o processamento de cada etapa, tem um enorme potencial para aumentar o desempenho do aplicativo enquanto trabalha com grandes coleções. Por outro lado, há uma sobrecarga para sequências ao processar pequenas coleções.

```kotlin
val withoutSequence = (1..10).filter{it % 2 == 1}.map { it * 2 }.toList()

val withSequence = (1..10).asSequence().filter{it % 2 == 1}.map { it * 2 }.toList()
```

## Arrays
Inicialização: 

```kotlin
val intArray = arrayOf(1, 2, 3, 4)

val stringArray = arrayOfNulls<String>(3)

val studentArray = Array<Student>(2) { index -> 
    when (index) { 
        0 -> Student(1, "Alexandra", "Brook")
        1 -> Student(2, "James", "Smith")
        else ->throw IllegalArgumentException("Too many") 
    } 
}
```

OBS: arrays na JVM recebe um tratamento especial, entao os arrays em kotlin teram um bytecode muito semelhante. 

A biblioteca padrão do kotlin oferece suporte pronto para arrays tipos primitivos: intArrayOf, longArrayOf, charArrayOf, doubleArrayOf

```kotlin
val ints = intArrayOf(1,2,3, 4, 5, 6, 7, 8, 9, 10)
```
Obs: Garanta  que sempre usará ***ArrayOf quando lidar com tipos primitivos. JVM possuii otimizacões. 

Para percorrer um array nenhum iterador é usado. 

```kotlin
    val countries = arrayOf("UK", "Germany", "Italy")

    for (country in countries) {
        print("$country ")
    }
```
Mesmo principio se da para acessar e alterar os valores, de um array onde nao se utiliza os metodos set e get. 
Na biblioteca-padrão de Kotlin em kotlin.collections, há uma classe chamada ArraysKt. Nessa classe, temos muitas funções auxiliares (métodos de extensão).

Exemplo 
```kotlin
val ints = intArrayOf(1,2,3, 4, 5, 6, 7, 8, 9, 10)
println("Pegue elementos menores que 5 do IntArray:${ ints.takeWhile { it <5 }.joinToString(",") }")
println("Tome cada terceiro elemento do IntArray:: ${ints.filterIndexed { index, element -> index % 3 == 0 }.joinToString(",")}")
```

A biblioteca padrão Kotlin fornece um conjunto de funções de extensão para transformações de coleção. E de exemplo temos os métodos de extensão map e flatMap.

A transformação de mapeamento cria uma coleção a partir dos resultados de uma função nos elementos de outra coleção.

```kotlin
val ints = intArrayOf(1,2,3, 4, 5, 6, 7, 8, 9, 10)
val strings = ints.map { element ->"Item " + element.toString() }
```

```kotlin
val numbers = setOf(1, 2, 3)
println(numbers.map { it * 3 })
println(numbers.mapIndexed { idx, value -> value * idx })
```

Exemplo de flatMap - Retorna uma lista única de todos os elementos gerados pelos resultados da aplicação de determinada condição em cada elemento da coleção original.

```kotlin
data class NamesList(val names: List<String>) {}
    
val containers = listOf(
    NamesList(listOf("name one", "name two", "name three")),
    NamesList(listOf("name four", "name five", "name six")),
    NamesList(listOf("name seven", "name eight"))
)
println(containers.flatMap { it.names }) //[name one, name two, name three, name four, name five, name six, name seven, name eight]

```

Combinando os dois. 

```kotlin
data class MotorVehicle(
    val name: String,
    val model: Int,
    val manufacturer: String
)

val cars = listOf(
    MotorVehicle("Swift", 2016, "Maruti"),
    MotorVehicle("Altroz", 2020, "Tata"),
    MotorVehicle("Verna", 2019, "Hyundai")
)

val bikes = listOf(
    MotorVehicle("R-15", 2018, "Yamaha"),
    MotorVehicle("Gixxer", 2017, "Suzuki")
)

val vehicles = listOf(cars, bikes)

val manufacturerList = vehicles.flatMap {
     it
 }.map {
     it.manufacturer
 }
```

Outras funcoes que temos **zip(), associateWith(), flatten(), joinsToString()** entre outras. 

flatten - Achatamento
```kotlin
val numberSets = listOf(setOf(1, 2, 3), setOf(4, 5, 6), setOf(1, 2))
println(numberSets.flatten())
```
associate
```kotlin
val numbers5 = listOf("one", "two", "three", "four")
println(numbers5.associateWith { it.length })//{one=3, two=3, three=5, four=4}
```
zip
```kotlin
val colors = listOf("red", "brown", "grey")
val animals = listOf("fox", "bear", "wolf")
println(colors zip animals)
```

A API da biblioteca-padrão oferece muitos métodos que permitem converter um array em um tipo de coleção diferente.

```kotlin
val longs = longArrayOf(1, 2, 1, 2, 3, 4, 5) 
val hashSet: HashSet<Long> = longs.toHashSet() 
```

## Listas

As listas são coleções ordenadas.Podemos criar uma lista somente leitura simples usando o método **listOf ()** e MutableList de leitura e gravação usando **mutableListOf ()** :

```kotlin
val theList = listOf("one", "two", "three")    
val theMutableList = mutableListOf("one", "two", "three")
```


```kotlin
val numbers = listOf(1, 2, 3, 4)
println(numbers.get(0))
println(numbers[0])
//numbers.get(5)                         // exception!
println(numbers.getOrNull(5))             // null
println(numbers.getOrElse(5, {it}))        // 5
```

Sublist

```kotlin
val numbers = (0..13).toList()
println(numbers.subList(3, 6))
```

```kotlin
val numbers = mutableListOf(1, 2, 3, 4)
println(numbers.indexOfFirst { it > 2})
println(numbers.indexOfLast { it % 2 == 1})
```


**fill()** simplesmente substitui todos os elementos da coleção pelo valor especificado.
```kotlin
val numbers = mutableListOf(1, 2, 3, 4)
numbers.fill(3)
println(numbers)
```

Para remover um elemento em uma posição específica de uma lista, use a removeAt()
```kotlin
val numbers = mutableListOf(1, 2, 3, 4, 3)    
numbers.removeAt(1)
println(numbers)
```

```kotlin
data class Planet(val name: String, val distance:Long)

val planets = listOf( Planet("Mercury", 57910000),
    Planet("Venus", 108200000), Planet("Earth", 149600000),
    Planet("Mars", 227940000), Planet("Jupiter", 778330000),
    Planet("Saturn", 1424600000), Planet("Uranus", 2873550000),
    Planet("Neptune", 4501000000), Planet("Pluto", 5945900000))

println(planets.last())  //Pluto
println(planets.first())  //Mercury
println(planets.get(4))  //Jupiter
println(planets.isEmpty()) //false
println(planets.isNotEmpty())  //true
println(planets.asReversed())  //"Pluto", "Neptune"
println(planets.elementAtOrNull(10)) //Null
```

```kotlin
val numbers = mutableListOf("one", "two", "three", "four")

numbers.sort()
println("Sort into ascending: $numbers")
numbers.sortDescending()
println("Sort into descending: $numbers")

numbers.sortBy { it.length }
println("Sort into ascending by length: $numbers")
numbers.sortByDescending { it.last() }
println("Sort into descending by the last letter: $numbers")

numbers.sortWith(compareBy<String> { it.length }.thenBy { it })
println("Sort by Comparator: $numbers")

numbers.shuffle()
println("Shuffle: $numbers")

numbers.reverse()
println("Reverse: $numbers")
```

## Mapas

Uma coleção de mapa, como o nome implica, permite associar um objeto (chave) a outro (valor).

```kotlin
val theMap = mapOf(1 to "one", 2 to "two", 3 to "three")
val theMutableMap = mutableMapOf(1 to "one", 2 to "two", 3 to "three")
```

```kotlin
val carsMap: Map<String, String> = mapOf("a" to "aston martin", "b" to "bmw", "m" to "mercedes", "f" to "ferrari")
println("cars[${carsMap.javaClass.canonicalName}:$carsMap]")
println("car maker starting with 'f':${carsMap.get("f")}")  //Ferrari
println("car maker starting with 'X':${carsMap.get("X")}")  //null

val states: MutableMap<String, String> = mutableMapOf("AL" to "Alabama", "AK" to "Alaska", "AZ" to "Arizona")
states += ("CA" to "California")
println("States [${states.javaClass.canonicalName}:$states")
println("States keys:${states.keys}")  //AL, AK, AZ,CA
println("States values:${states.values}")  //Alabama, Alaska, Arizona, California
```

Para recuperar um valor de um mapa, podemos passar a chave para o get(), ou a sintexe abreviada [key]. 
Tem tambem como chamar um getValue(), que caso ele nao encontre ele retorna uma exceçao diferente dos anteriores que retornam null. 

getOrElse() funciona da mesma maneira que para listas: os valores para chaves inexistentes são retornados da função lambda especificada.
getOrDefault() retorna o valor padrão especificado se a chave não for encontrada.

```kotlin 
val numbersMap = mapOf("one" to 1, "two" to 2, "three" to 3)
println(numbersMap.get("one"))
println(numbersMap["one"])
println(numbersMap.getOrDefault("four", 10))
println(numbersMap["five"])               // null
//numbersMap.getValue("six")      // exception!
```
Exemplo de metodos de extensao para map: 

```kotlin 
val states: MutableMap<String, String> = mutableMapOf("AL" to "Alabama", "AK" to "Alaska", "AZ" to "Arizona")
states += ("CA" to "California")
states.filterNot { it.value.startsWith("C") } //AL=Alabama, AK=Alaska, AZ=Arizona

val numbersMap = mapOf("key1" to 1, "key2" to 2, "key3" to 3, "key11" to 11)
val filteredMap = numbersMap.filter { (key, value) -> key.endsWith("1") && value > 10}
println(filteredMap)
```

Plus e Minus 
```kotlin
val numbersMap = mapOf("one" to 1, "two" to 2, "three" to 3)
println(numbersMap + Pair("four", 4))
println(numbersMap + Pair("one", 10))
println(numbersMap + mapOf("five" to 5, "one" to 11))

println(numbersMap - "one")
println(numbersMap - listOf("two", "four"))
```

## Conjuntos
Um conjunto (set) é uma coleção que não contém itens duplicados. também para nulo, – não podemos ter mais de um item nulo

```kotlin
val theSet = setOf("one", "two", "three")  
val theMutableSet = mutableSetOf("one", "two", "three")
```

Um ponto a ser observado é que os métodos plus e minus não alteram a coleção. Tais métodos de extensão estão definidos na interface imutável Set e, desse modo, acabarão gerando uma nova coleção imutável.

Para encontrar uma interseção entre duas coleções (elementos presentes em ambas), use intersect(). Para encontrar elementos de coleção que não estão presentes em outra coleção, use subtract(). Ambas as funções também podem ser chamadas no formato infix, por exemplo a intersect b.

```kotlin
val numbers = setOf("one", "two", "three")

println(numbers union setOf("four", "five"))
println(setOf("four", "five") union numbers)

println(numbers intersect setOf("two", "one"))
println(numbers subtract setOf("three", "four"))
println(numbers subtract setOf("four", "three")) // same output
```

Observe que as operações definidas também são suportadas List. No entanto, o resultado de operações definidas nas listas ainda é um Set.

## Visões somente de leitura
Em kotlin temos o conceito de uma visao somente de leitura para uma coleção mutavel. 

```kotlin
val carManufacturers: MutableList<String> = mutableListOf("Masserati", "Aston Martin","McLaren","Ferrari","Koenigsegg")
val carsView: List<String> = carManufacturers
carManufacturers.add("Lamborghini")
println("Cars View:$carsView")  //Cars View: Masserati, Aston Martin, McLaren, Ferrari, Koenigsegg, Lamborghini
```

## Acesso indexado
Kotlin facilita acessar os elementos de uma lista ou devolver os valores para uma chave trata de um mapa. Não é necessário empregar a sintaxe get(index) ou get(key) em estilo Java, mas você pode simplesmente usar a indexação em estilo de array para obter seus itens:

```kotlin
val capitals = listOf("London", "Tokyo", "Instambul", "Bucharest") 
capitals[2]  //Tokyo
capitals[100] java.lang.ArrayIndexOutOfBoundException 
val countries = mapOf("BRA" to "Brazil", "ARG" to "Argentina", "ITA" to "Italy") countries["BRA"] //Brazil
countries["UK"]  //null

```

## Sequências
Sequências são ótimas para cenários em que o tamanho da coleção não é conhecido com antecedência. As sequências sao equivalente em Kotlin aos tipos Stream. Infelizmente, a biblioteca de Kotlin não inclui suporte para processamento paralelo de sequências.

```kotlin
val setSequence: Sequence<String> = setOf("Anna","Andrew", "Jack", "Laura","Anna").asSequence()
val intSeq = sequenceOf(1, 2, 3, 4, 5)
```
OBS: Todos os tipos de coleção que vimos até agora podem ser convertidos em uma sequência.

Há uma função que permite produzir elementos de sequência um por um ou por pedaços de tamanhos arbitrários. 

```kotlin
val oddNumbers = sequence {
    yield(1)
    yieldAll(listOf(3, 5))
    yieldAll(generateSequence(7) { it + 2 })
}

println(oddNumbers.take(5).toList())
```

## Operações Comunns
Transformations 

Filtering

plus and minus operators

Grouping

Retrieving collection parts

Retrieving single elements

Ordering

Aggregate operations

Para saber mais - https://kotlinlang.org/docs/reference/collection-operations.html

