# Capítulo 4 - Funções em Kotlin

## Sumário

- [Capítulo 4 - Funções em Kotlin](#capítulo-4---funções-em-kotlin)
  - [Sumário](#sumário)
  - [Definição de funções](#definição-de-funções)
  - [Função como única expressão](#função-como-única-expressão)
  - [Funções-membro](#funções-membro)
  - [Funções locais](#funções-locais)
  - [Funções de nível superior](#funções-de-nível-superior)
  - [Parâmetros nomeados](#parâmetros-nomeados)
  - [Parâmetros default](#parâmetros-default)
  - [Funções de extensão](#funções-de-extensão)
  - [Precedência para funções de extensão](#precedência-para-funções-de-extensão)
  - [Funções de extensão em nulls](#funções-de-extensão-em-nulls)
  - [Funções-membro de extensão](#funções-membro-de-extensão)
  - [Sobreescrevendo funções-membro de extensão](#sobreescrevendo-funções-membro-de-extensão)
  - [Extensões em objetos companheiros](#extensões-em-objetos-companheiros)
  - [Múltiplos valores de retorno](#múltiplos-valores-de-retorno)
  - [Funções infixas](#funções-infixas)
  - [Operadores](#operadores)
    - [Sobrecarga de operadores](#sobrecarga-de-operadores)
    - [Operadores básicos](#operadores-básicos)
    - [in/contains](#incontains)
    - [get/set](#getset)
    - [invoke](#invoke)
    - [Comparação](#comparação)
    - [Atribuição](#atribuição)
    - [Interoperabilidade com Java](#interoperabilidade-com-java)
  - [Funções literais](#funções-literais)
  - [Funções tail recursive](#funções-tail-recursive)
  - [varargs](#varargs)
    - [Operador de spread](#operador-de-spread)
  - [Funções da Biblioteca padrão](#funções-da-biblioteca-padrão)
    - [apply](#apply)
    - [let](#let)
    - [with](#with)
    - [run](#run)
    - [lazy](#lazy)
    - [use](#use)
    - [repeat](#repeat)
    - [require/assert/check](#require/assert/check)
  - [Funções Genéricas](#funções-genéricas)
  - [Funções Puras](#funções-puras)


## Definição de funções

A ideia por trás das funções é bem simples: dividir um programa grande em partes menores que possam ser compreendidas mais facilmente e permitir reutilização de código a fim de evitar repetições.
As funções são definidas com a palavra **fun**, parâmetros opcionais e um valor de retorno. A lista de parâmetros deve estar sempre presente, mesmo se não houver nenhum parâmetro definido. Exemplo:
```java
fun hello() : String = "Dae raça"
```

Todo parâmetro deve estar no formato **name: type**. Exemplo:
```java
fun hello(name: String, location: String): String = "Dae $name, você está em $location?"
```

Se não for definido nenhum valor significativo de retorno da função, será definido Unit como retorno, porém o **Unit** pode ser omitido como pode ser visto no seguinte exemplo:
```java
fun print1(str: String): Unit {
    println(str)
}

fun print2(str: String) {
    println(str)
}
```


## Função como única expressão

Em geral, uma função deve declarar seu tipo de retorno, mas existe uma exceção somente para funções construídas de uma só expressão. Normalmente chamadas frde **one line** ou **single line** (função de uma só linha).

```java
fun square(k: Int) = k * k
````

Pode ser observado que o tipo de retorno **Int** não está declarado. Isso é inferido pelo compilador. É aconselhado descrever o tipo do retorno se isso for deixar o código mais claro.

```java
fun square(k: Int): Int = k * k
```

Funções com uma só expressão sempre podem ser escritas no estilo usual, se você quiser.
Obs:. As funções a seguir vão ser compiladas de modo a gerar o mesmo bytecode:

```java
fun concat1(a: String, b: String) = a + b
fun concat2(a: String, b: String): String {
    return a + b
}
```


## Funções-membro

Funções membros são definidas em uma classe, objeto ou interface. Uma função-membro é chamada usando o nome da classe ou da instância do objeto que contém, com um ponto seguido do nome da classe ou da instância do objeto que contém, com um ponto seguido do nome e os argumentos. Por exemplo:

```java
val String = "Olá"
val length = string.takes(5)
```

As funções-membro podem referenciar a si mesma e não precisa do nome da instância para faze-lo.

```java
object Rectangle {
    fun printArea(width: Int, height: Int): Unit {
        val area = calculateArea(width, height)
        println("This area is $area")
    }

    fun calculateArea(width: Int, height: Int): Int  {
        return width * height
    }
}
```


## Funções locais

Kotlin oferece suporte para funções declaradas em outras funções. São chamadas de **funções locais** ou **aninhadas** e podem ser aninhadas várias vezes.
O exemplo anterior que exibe a área pode ser escrito usando essa ideia.
```java
fun printArea(width: Int, height: Int): Unit {
    fun calculaArea(width: Int, height: Int): Int = width * height
    val area = calculateArea(width, height)
    println("The area is $area")
}
```

Funções locais pode acessar parâmetros e variáveis definidos no escopo mais externo:
```java
fun printArea(width: Int, height: Int): Unit {
    fun calculaArea(): Int = width * height
    val area = calculateArea()
    println("The area is $area")
}
```

Vamos trabalhar com um exemplo de função que poderia ser dividida usando funções locais:

```java
fun fizzbuzz(start: Int, end: Int): Unit {
    for (k in start..end) {
        if (k % 3 == 0 && k % 5 == 0)
            println("Fizz Buzz")
        else if (k % 3 == 0)
            println("Fizz")
        else if (k % 5 == 0)
            println("Buzz")
        else
            println(k)
    }
}
```
Esse é o famoso problema Fizz Buzz. Tem o objetivo de exibir os valors contidos entre os valores start e end. Entretanto, se o inteiro for multiplo de 3, deve aparecer "Fizz". Se for múltiplo de 5 deve exibir Buzz. Se for múltiplo de 3 e de 5 deve exibir "Fizz Buzz".

Podemos declarar uma função local para cada uma das verificações de módulo:
```java
fun fizzbuzz2(start: Int, end: Int): Unit {
    fun isFizz(k: Int): Boolean = k % 3 == 0
    fun isBuzz(k: Int): Boolean = k % 5 == 0
    for (k in start..end) {
        if (isFizz(k) && isBuzz(k))
            println("Fizz Buzz")
        else if (isFizz(k))
            println("Fizz")
        else if (isBuzz(k))
            println("Buzz")
        else
            println(k)
    }
}
```
Nesse caso, nossas ramificações de if...else agora chamam as funções aninhadas isFizz e isBuzz. **Podemos melhorar?**

```java
fun fizzbuzz3(start: Int, end: Int): Unit {
    for (k in start..end) {
        fun isFizz(): Boolean = k % 3 == 0
        fun isBuzz(): Boolean = k % 5 == 0
        if (isFizz() && isBuzz())
            println("Fizz Buzz")
        else if (isFizz())
            println("Fizz")
        else if (isBuzz())
            println("Buzz")
        else
            println(k)
    }
}
```
Passamos as funções para dento do for, assim podemos omitir as declarações de parâmetros e acessar K diretamente. **Podemos melhorar?**

```java
fun fizzbuzz4(start: Int, end: Int): Unit {
    for (k in start..end) {
        fun isFizz(): Boolean = k % 3 == 0
        fun isBuzz(): Boolean = k % 5 == 0
        when {
            isFizz() && isBuzz() -> println("Fizz Buzz")
            isFizz() -> println("Fizz")
            isBuzz() -> println("Buzz")
            else -> println(k)
        }
    }
}
```


**Dá para melhorar?**

Fica aqui um desafio para quem quiser trazer para a próxima apresentação, mas ficamos com essa solução final, que evita repetição de código e é mais legível que a interação inicial.


## Funções de nível superior

São funções que existem fora de qualquer classe, objeto ou interface e são definidas diretamente em um arquivo. O nome **"nível superior"** (top-level) vem do fato de as funções não estarem aninhadas em qualquer estrutura.

```java
fun foo(k: Int) {
    require(k > 10, {"k should be greater than 10"})
}
```


## Parâmetros nomeados

Os parâmetros nomeados nos permitem ser explícitos sobre a nomeação de argumentos quando estes são passados para uma função.
No exemplo a seguir, verificamos se a primeira string contém uma substring da segunda:
```java
val string = "Gato preto no quarto escuro"
string.regionMatches(14, "escritório escuro", 4, 6, true)
```

Para usar parâmetros nomeados colocamos o nome do parâmetro antes do valor do argumento:
```java
val string = "Gato preto no quarto escuro"
string.regionMatches(thisOffset = 14, other = "escritório escuro", otherOffset = 4, length = 6, ignoreCase = true)
```

Agora ficou mais extenso, porém mais legível porque conseguimos ver o que cada parâmetro significa.

Vamos comparar dois estilos diferentes de chamar a função:
```java
fun deleteFiles(filePattern: String, recursive: Boolean, ignoreCase: Boolean, deleteDirectories: Boolean): Unit

deleteFiles("*.jpg", true, true, false)
deleteFiles("*.jpg", recursive = true, ignoreCase = true, deleteDirectories = false)
```

Nem todos os parâmetros precisam ser nomeados, mas depois que um for, os sequintes devem ser nomeados.
Parâmetros nomeados também permitem que a ordem dos parâmetros seja alterada sem seguir a ordem que foi declarada.


## Parâmetros default

Em Kotlin, uma função pode definir que um ou mais de seus parâmetros tenham valores default, que serão usados caso os argumentos não sejam especificados.

```java
fun createConn(host: String, port: Int = 80, secure: Boolean = false)
````


A mesma função sendo chamada de duas formas corretamente:
```java
createConn("127.0.0.1", 8080, true)

createConn("127.0.0.1")
```

A mesma chamada sendo chamado COM ERRO
```java
createCon("127.0.0.1", true)
```
Uma vez que um parâmetro é omitido, todos os demais também devem ser.

Para resolver esse problema, podemos misturar parÂmetros nomeados com parâmetros default:

```java
createCon("127.0.0.1", secure = true)
```


## Funções de extensão

Funções estendidas do Kotlin oferecem a capacidade de adicionar novas funções a classes já existentes sem precisar estendê-la diretamente.

Em Java uma maneira de adicionar uma função a uma classe seria estender o tipo, criando um subtipo com a nova função.

```java
public class DroppableList<E> extends ArrayList<E> {
    private List<E> drop(Integer k) {
        [...]
    }
}
```

Entretando há situações onde isso não é possível, como classes que são definidas como final ou quando não se tem o controle da instância da classe não conseguindo substituir o tipo existente pelo subtipo. Sendo assim, uma maneira comum de fazer isso em Java seria criar uma classe utilitária con funções que recebem a instância como argumento, como acontece com a classe java.util.Collections.

```java
public class ListUtils {
    public static List<E> drop(Integer k, List<E> list) {
        [...]
    }
}
```

Em Kotlin uma função de extensão é declarada definindo uma função de nível superior, mas com o tipo desejado prefixado antes do nome da função.

```kotlin
fun <E> List<E>.drop(k: Int): List<E> {
    [...]
}
```

Em funções de extensão a palavra reservada this sempre irá a se referir ã instancia do receptor, por exemplo, na função "fun <E> List<E>.drop(k: Int): List<E>" o this irá se referir ao List<E>.

```kotlin
fun <E> List<E>.drop(k: Int): List<E> {
    val resultSize = size - k
    val list = ArrayList<E>(resultsize)
    for (index in k...size - 1) {
        list.add(this[index])
    }
    return list
}
```

Para usar uma função de extensão é necessário importá-la da mesma maneira que se faz com uma função de nível superior.
```kotlin
import com.mercadolibre.drop
val list = listOf(1,2,3)
val droppedList = list.drop(2)
```

## Precedência para funções de extensão

Não é possível sobreescrever funções declaradas em uma classe ou interface por uma função de extensão, se ela tiver a mesma assinatura o compilador nunca a chamará. O compilador irá primeiro buscar uma correspondência nas funções-membro, se houver alguma correspondência a vinculaçào será feita.

```kotlin
class Submarine {
    fun fire() {
        println("Firing torpedoes")
    }
    fun submerge() {
        println("Submerging")
    }
}

fun Submarine.fire() {
    println("Fire on board!")
}
fun Submarine.submerge(depth: Int) {
    println("Submerging to a depth of $depth fathoms")
}
```

Chamando as funções acima o resultado seria:

```kotlin
val sub = Submarine()
sub.fire()
sub.submerge()
sub.submerge(10)

Firing Torpedos
Submerging
Submerging to a depth of 10 fathoms
```

Vemos que como o funcão de extensão "fire()" foi definida com a mesma assinatura da função-membro, o compilador irá vincular a função membro, já a função "submerge(depth: Int)" que possui uma assinatura diferente pode ser usada.

## Funções de extensão em nulls

 As funções de extensões podem ser definidas com um tipo de receptor null. Essas extensões podem ser chamadas em uma variável de objeto, mesmo que seu valor seja nulo, e podem verificar this == nulo dentro do corpo da função. É isso que permite chamar toString() no Kotlin sem verificar por null: a verificação ocorre dentro da função de extensão.

 ```kotlin
 fun Any?.toString(): String {
    if (this == null) return "null"
    // after the null check, 'this' is autocast to a non-null type, so the toString() below
    // resolves to the member function of the Any class
    return toString()
}
```

## Funções-membro de extensão

Podemos definir uma função de extensão em classes como funções-membros, assim é possível restingir seu escopo.

```kotlin
class Mappings {
    private val map = hashMapOf<Int, String>()
    private fun String.stringAdd() {
        map.put(hashCode(), this)
    }
}
```
Neste exemplo podemos verificar como os receptores funcionam em funções-membro de extensão. A função hashCode é definida na interface Any, da qual Map e String herdam esta função. Quando hashCode é chamada há duas funções possíveis no escopo, a primeira na instância de Mappings é chamada de receptor de dispatch, já a segunda na instância de string é chamada de receptor de extensão. Por padrão o compilador, em funções-membro de extensão, dará preferência para o receptor de extensão. No exemplo anterior a função hasCode será o da instância de string, para usar o receptor de dispatch devemos usar um this qualificado:

```kotlin
class Mappings {
    private val map = hashMapOf<Int, String>()
    private fun String.stringAdd() {
        map.put(this@Mappings.hashCode(), this)
    }
}
```

## Sobreescrevendo funções-membro de extensão

Podemos sobreescrever funções-membro de extensão, para isso devemos declara-las como abertas(open).

```kotlin
open class Element(val name: String) {
    open fun Particle.react(name: String) {
        println("$name is reacting with a particle.")
    }
    fun react(particle: Particle) {
        particle.react(name)
    }
}

class NobleGas(name: String) : Element(name){
    override fun Particle.react(name: String) {
        println("$name is noble, it doesn`t react with particles.")
    }
    fun react(particle: Particle) {
        particle.react(name)
    }
}

fun main(args: Array<String>) {
    val selenium = Element("Selenium")
    selenium.react(Particle())

    val neon = NobleGas("Neon")
    neon.react(Particle())
}

Selenium is reacting with a particle.
Neon is noble, it doesn`t react with particles.
```

## Extensões em objetos companheiros

Se uma classe tiver um objeto companheiro definido, você também poderá definir funções de extensão para o objeto companheiro. Assim como os membro do objeto companheiro, eles podem ser chamados usando apenas o nome da classe como qualificador.

```kotlin
class MyClass {
    companion object { }
}

fun MyClass.Companion.printCompanion() { println("companion") }

fun main() {
    MyClass.printCompanion()
}
```

## Múltiplos valores de retorno

Em Kotlin é possível retornar mais de um valor em uma função, utilizando os tipos Pair e Triple.
O tipo Pair encapsula dois valores que são acessados via o primeiro e o segundo campo. O tipo triple tem o mesmo funcionando com a diferença de que retorna três valores.

```kotlin
fun roots(k : >= 0) {
    val root = Math.sqrt(k.Double())
    return Pair(root, -root)
}

fun main(args: Array<String>) {
    val (pos, neg) = root(16)
    val (a, b, c) = Triple(2, "x", listOf(null))
}
```

## Funções infixas

O Kotlin permite que algumas funções sejam chamadas sem usar ponto e colchetes. Estes são chamados funções infixas e seu uso pode resultar em um código que se parece muito mais com uma linguagem natural. A função infixa consiste em um operador que é colocado entre operandos ou argumentos.

```kotlin
val pair = "London" to "UK"
```
O código acima  mostra a função infixa to que recebe "London"e "UK" como parâmetros e devolve uma instância de Pair("London", "UK").

As funções-membro podem ser definadas como infixas, isso permite que sejam usadas no mesmo estilo. Como uma função infixa é colocada entre dois argumentos, todas as funções devem atuar em dois parâmetros. O primeiro é a instância na qual a função é chamada e o segundo é um parâmetro explícito da função.

```kotlin
infix fun concat(other: String): String {
    return this + other
}

fun main(args: Array<String>) {
    val concatString = "this" concat "other"
}
```

##  Operadores

Operadores são funções que utilizam um nome simbólico (syntax sugar). Em kotlin, muitos operadores embutidos são, na verdade, chamadas de função. 

``` kotlin
fun main() {
    val foo = listOf("a", "b", "c")
    val bar = listOf("d", "e", "f")
    val fooBar = foo + bar

    println(foo) // [a, b, c]
    println(bar) // [d, e, f]
    println(fooBar) // [a, b, c, d, e, f]
}
```

Implementação do método plus na estrutura de coleções em Kotlin:
``` kotlin
/**
 * Returns a list containing all elements of the original collection and then all elements of the given [elements] collection.
 */
public operator fun <T> Collection<T>.plus(elements: Iterable<T>): List<T> {
    if (elements is Collection) {
        val result = ArrayList<T>(this.size + elements.size)
        result.addAll(this)
        result.addAll(elements)
        return result
    } else {
        val result = ArrayList<T>(this)
        result.addAll(elements)
        return result
    }
}
```


###  Sobrecarga de operadores

Sobrecarga de operadores é a capacidade de definir funções que usem operadores através da palavra reservada *operator* como prefixo da declação de função, usando o termo equivalente em inglês como nome do método.

###  Operadores básicos

| Operação | Nome da Função |
|----------|----------------|
| a + b    |  a.plus(b)     |
| a - b    |  a.minus(b)    |
| a * b    |  a.times(b)    |
| a / b    |  a.div(b)      |
| a % b    |  a.rem(b)      |
| a..b     |  a.rangeTo(b)  |
| +a       |  a.unaryPlus() |
| -1       |  a.unaryMinus()|
| !1       |  a.not()       |

> a precedência de operadores é aplicada durante a execução das expressões

``` kotlin
data class CustomInt(val value: Int) {
    operator fun unaryMinus() = CustomInt(-value)

    operator fun plus(other: CustomInt) = CustomInt(value + other.value)
    operator fun plus(other: Int) = CustomInt(value + other)

    operator fun times(other: CustomInt) = CustomInt(value * other.value)
}

operator fun CustomInt.minus(other: Int): CustomInt = CustomInt(this.value - other)

fun main() {
    val a = CustomInt(2)
    val b = CustomInt(4)
    val c = CustomInt(6)

    println(-a) // CustomInt(value=-2)
    println(a + b) // CustomInt(value=6)
    println(a + b + 3) // 9
    println(a * b) // CustomInt(value=8)
    println(a - 2) // CustomInt(value=0)

    println(2 + 4 * 6) // 26
    println(a + b * c) // CustomInt(value=26)
    println(a.plus(b).times(c)) // CustomInt(value=36)
}
```

###  Atribuição

Para variáveis mutáveis, também podemos utilizar operadores de atribuição:

| Operação |  Nome da Função   |
|----------|-------------------|
| a += b   |  a.plusAssign(b)  |
| a -= b   |  a.minusAssign(b) | 
| a *= b   |  a.timesAssign(b) | 
| a /= b   |  a.divAssign(b)   | 
| a %= b   |  a.remAssign(b)   | 


Em geral, sobrecargas de operadores básicos devem ser funções puras (no side effects). Já para a sobrecarga de operadores de atribuição, a mudança é feita no próprio objeto e a função deve retornar Unit. 

```kotlin
data class CustomInt(var value: Int) {

    operator fun plus(other: CustomInt) = CustomInt(value + other.value)
//    operator fun plus(other: Int) = CustomInt(value + other) // erro com variáveis mutáveis

    operator fun plusAssign(other: Int): Unit {
        value += other
    }

    operator fun minusAssign(other: Int): Unit {
        value -= other
    }
}


fun main() {
    var a = CustomInt(2)
    val b = CustomInt(4)

    a += 1
    b -= 2
    val c = a + b

    println(a) // CustomInt(value=3)
    println(b) // CustomInt(value=2)
    println(c) // CustomInt(value=5)
}
```

###  in/contains

A palavra reservada *in* também pode ser sobrecarregada para utilização nas classes. O método a ser implementado é o *contains*.

``` kotlin
class AccessList(private val permissions: List<String>) {
    operator fun contains(permission: String): Boolean = permissions.contains(permission)
}

fun main() {
    val access = AccessList(listOf("OPEN_RESOURCE", "DELETE_RESOURCE", "IMPORT_RESOURCE"))

    println("OPEN_RESOURCE" in access) // true
    println("EXPORT_RESOURCE" in access) // false
    println(access.contains("DELETE_RESOURCE")) // true
}
```

###  get/set

É comum fazer o uso de colchetes para obter elementos dentro de um array ou coleção. 

```kotlin
val list = listOf(1, "2", "três", 4L)
println(list[0])
```
Internamente, isso é feito através das funções *get* e *set*.

Podemos sobrescrever esses métodos em nossas classes e fazer uso desse recurso. 

``` kotlin
class ChessBoard() {
    private val board = Array<Piece>(64) { Piece.Empty }

    operator fun get(rank: Int, file: Int): Piece = board[file * 8 + rank]

    operator fun set(rank: Int, file: Int, value: Piece): Unit {
        board[file * 8 + rank] = value
    }
}

fun main() {
    val board = ChessBoard()
    board[0, 4] = Piece.Queen 
    println(board[0, 4]) // Queen
    println(board[0, 0]) // Empty
}
```

### invoke

Podemos utilizar parênteses como operadores através da sobrescrita da função *invoke*, que pode ser sobrecarregada mais de uma vez, conforme o tipo e quantidade de parâmetros:

``` kotlin
class RandomLongs(seed: Long) {
    private val random = Random(seed)

    operator fun invoke(from: Long, until: Long): Long = random.nextLong(from, until)
    operator fun invoke(until: Long): Long = invoke(0, until)
    operator fun invoke(): Long = invoke(0, 1000)
}

fun main() {

    val random = RandomLongs(1591129064)
    val longs = listOf(random(), random(until = 20), random(100, 200))
    println(longs) // [754, 2, 197]
}
```


###  Comparação

Podemos definir em nossas classes o comportamento para atributos de comparação menor que, maior que, menor ou igual e maior ou igual. 

Isso é feito através da função compareTo, que deve ser compatível com a interface *Comparator* do Java. Ou seja:
- se A é menor que B, deve retornar um número inteiro negativo
- se A é igual a B, deve retornar zero.
- se A é maior que B, deve retornar um inteiro positivo


``` kotlin
class BingoNumber(val name: String, val number: Int) {
    operator fun compareTo(other: BingoNumber): Int {
        return when {
            number < other.number -> -1
            number > other.number -> 1
            else -> 0
        }
    }
}

fun main() {
    val a = BingoNumber("fim do primeiro tempo", 45)
    val b = BingoNumber("uma boa ideia", 51)

    println(a < b) // true
    println(b < a) // false
}
```

###  Interoperabilidade com Java

Apesar de Java não permitir sobrecarga de operadores, Kotlin permite que qualquer método Java cuja assinatura esteja correta seja utilizado como operador.

```java
public class Counter {
    private int value;

    public Counter(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public Counter plus(Counter other) {
        return new Counter(this.value + other.getValue());
    }

    @Override
    public String toString() {
        return "Counter{value=" + value + '}';
    }
}
```

``` kotlin
val foo = Counter(40)
val bar = Counter(2)

println(foo + bar) // Counter{value=42}
```

## Funções literais

Uma função literal é uma forma alternativa de definir uma função. Funções literais podem ser atribuidas a uma variável e ser executadas utilizando parênteses:

``` kotlin
fun main() {
    {
        println("literal function")
    }()

    val printMessage = { message: String -> println(message) }
    printMessage("bolinha")

    val elements = listOf("1", "two", "III")

    elements.forEach(printMessage)

    elements.forEach { println(it) }
}
```

## Funções tail recursive

Uma função recursiva é aquela que chama a si mesma para obter o resultado. 

``` kotlin 
fun sum(k: Int): Long {
    return if (k == 0) 0
    else sum(k - 1) + k
}

fun main() {
    println(sum(100))
    println(sum(1000))
    println(sum(10000))
    println(sum(100000))
}
```

Sempre que a função sum é chamada recursivamente, o runtime preserva as chamadas dentro do stack. Após a última execução, o resultado final é a soma dos valores na pilha. 

Caso a quantidade de chamadas recursivas for muito alta, o acúmulo de referências no stack pode gerar um erro de stack overflow.

``` log
5050
500500
50005000
Exception in thread "main" java.lang.StackOverflowError
	at AppKt.sum(app.kt:32)
	at AppKt.sum(app.kt:32)
	at AppKt.sum(app.kt:32)
    ...
```

Se uma chamada de uma função recursiva for a última operação em uma função e o resultado da chamada for simplesmente devolver o valor, o runtime não precisará manter o stack frame anterior ativo. A chamada da função pode ser utilizada como valor atual a cada "iteração".

Podemos utilizar a palavra reservada *tailrec* para garantir em tempo de compilação que a função recursiva seja a última operação.

``` kotlin
fun sum(k: Int): Long {
    tailrec fun sum(acc: Long, cur: Int): Long {
        return if (cur == 0) acc
        else sum(acc + cur, cur - 1)
    }

    return sum(0, k)
}

fun main() {
    println(sum(100)) // 5050
    println(sum(1000)) // 500500
    println(sum(10000)) // 50005000
    println(sum(100000)) // 5000050000
    println(sum(1000000)) // 500000500000
}
```

Internamente, durante a compilação, o código kotlin é otimizado para um laço.
``` java
public static final long sum(long acc, int cur) {
    while(cur != 0) {
        long var10000 = acc + (long)cur;
        --cur;
        acc = var10000;
    }

    return acc;
}
```

## varargs

Kotlin permite que funções sejam definidas de modo a aceitar um número variável de argumentos.

``` kotlin
fun multiprint(vararg strings: String): Unit {
    for (string in strings)
        println(string)
}

fun main() {
    multiprint("oi", "bolinha", "123")
    // oi
    // bolinha
    // 123
}
```

Uma função pode conter somente um parâmetro marcado como *vararg*, que geralmente é o último parâmetro da função, apesar de não ser uma regra. Se houver outros parâmetros depois de *vararg*, os argumentos deverão ser passados com parâmetros nomeados.

``` kotlin
fun multiprint(prefix: String, vararg strings: String, suffix: String): Unit {
    println(prefix)
    for (string in strings)
        println(string)
    println(suffix)
}

fun main() {
    multiprint("Start", "a", "b", "c", suffix = "End")
}
```

``` log
Start
a
b
c
End
```

###  Operador de spread

Um array pode ser utilizado como como parâmetro para um argumento do tipo *vararg* definido em uma função através do operador de spread *, que separa os elementos do array, passando-os como argumentos individuais.

``` kotlin
fun main() {
    val params = arrayOf("a", "b", "c", "d", "e")
    multiprint("Start", *params, suffix = "End")
}
```

``` log
Start
a
b
c
d
e
End
```

## Funções da Biblioteca Padrão

O Kotlin disponibiliza uma biblioteca-padrão cujo propósito não é substituir, mas expandir a biblioteca-padrão de Java.

### apply

apply() é uma função de extensão da biblioteca-padrão de Kotlin declarada em Any, portanto pode ser chamada em instâncias de todos os tipos.
    - Apply é uma função de extensão em um tipo.
    - Requer uma referência de objeto para executar em uma expressão.
    - Ele também retorna uma referência de objeto na conclusão.

```kotlin
data class Person(
    var name: String,
    var age: Int,
    var profession: String? = null,
    var nickname: String? = null
)

val person = Person("Tony Stark", 30)

person.apply {
    this.profession = "Iron Man"
    this.nickname = "Tony"
}
```

```kotlin
File(path).apply { mkdirs() }
```

```java
File makeDir(String path) {
  File result = new File(path);
  result.mkdirs();
  return result;
}
```

### let

let() é uma função de extensão da biblioteca-padrão de Kotlin, essencialmente semelhante a apply. A principal diferença é que ela devolve o valor da própria closure.

```kotlin
var str = "Hello World"
str.let { println("$it!!") }
println(str)
var strLength = str.let { "$it function".length }
```

```kotlin
val outputPath = Paths.get("/user/home").let {
    val path = it.resolve("output") 
    path.toFile().createNewFile()
    path
}
````

let() é útil para verificar propriedades Anuláveis. No exemplo abaixo, o código dentro da expressão let é executado apenas quando a propriedade não é nula. Assim, vamos nos salvar do verificador nulo if else. A função let facilita lidar com expressões nullable. Juntamente com um operador de chamada segura, ele permite avaliar se uma expressão, verificar se o resultado é null e armazená-lo em uma variável, tudo isso em uma única expressão concisa.

```kotlin
var name : String? = "Kotlin let null check"
name?.let { println(it) } //prints Kotlin let null check
name = null
name?.let { println("${it}") } 
```

```kotlin 
DbConnection.getConnection().let { connection -> ...}
// connection is no longer visible here
```
### with

with()é conveniente quando você precisa chamar vários métodos diferentes no mesmo objeto. Em vez de repetir a variável que contém esse objeto em cada linha, você pode "fatorá-lo" com uma chamada with. A última expressão de with function retorna um resultado.

```kotlin
val w = Window()
with(w) {
  setWidth(100)
  setHeight(200)
  setBackground(RED)
}

var xyz = with(person) {
    name = "Sem Nome"
    tutorial = "Kotlin estudos"
    val xyz = "Atribuicao realizada"
    xyz
}
```

### run
run é uma função de extensão que combina os casos de uso de with e let, closure é passada para run, que tem a instância como receptor. O valor de retorno da closure é usado como valor de retorno do próprio run:
A principal diferença entre let e run é que com run, o receptor é a instância, enquanto em let, o argumento da closure é a instância.

```kotlin
var mensagem = "Nova mensagem"
mensagem = run {
    val mensagem = "Treinamento de Kotlin - topico funcao RUN"
    mensagem
}
````
Combinando let com run
```kotlin
var p : String? = "teste"
p?.let { println("p is $p") } ?: run {
    println("p was null. Setting default value to: ")
    p = "Kotlin"
}
```

### lazy
lazy é outra função útil que encapsula uma chamada de função custosa de modo que será chamada quando for necessária pela primeira vez. A ideia do lazy é permitir a inicialização de property na primeira vez que for utilizada, e então, nas próximas vezes de uso, o valor atribuído é devolvido imediatamente, como se fosse um cache.

```kotlin
fun readStringFromDatabase():String = "" // operação custosa
val lazyString = lazy { readStringFromDatabase() }
val string = lazyString.value
```

### use 
use é semelhante à instrução try com recursos (try-with-resources) existentes em Java e a using do C#. use chamará a função de forma segura, fechando o recurso após a função ter concluído, independentemente de uma exceção ter sido ou não lançada:

```kotlin
val input = Files.newInputStream(Paths.get("input.txt"))
val byte = input.use({ input.read() })
```

Combinando apply com use.

```kotlin
fun readProperties() = Properties().apply {
    FileInputStream("config.properties").use { fis ->
        load(fis)
    }
}
```

### repeat
Como o nome indica, repeat aceita uma função literal e um inteiro k. A função literal será chamada k vezes.
É uma função bem simples cuja finalidade é evitar a necessidade de um bloco for para operações simples:

```kotlin
repeat(10, { println("Hello") })
```

### require/assert/check
Kotlin disponibiliza algumas funcoes para que possamos adcionar uma quantidade de especificacoes formais em nosso programa. Essas funções são todas muito semelhantes. A principal diferença está no tipo de exceção lançada.

require lança uma exceção IllegalArgumentException e é usada para garantir que os argumentos correspondam às condições de entrada:

```kotlin
fun factorial(n: Long): Long {
    require(n >= 0) { "Number must no be negative" }
    return n
}
```

assert lança uma exceção AssertionException e é usada para garantir que nosso estado interno seja consistente;

```kotlin
fun activate(index: Int) {
  assert(pump[index].isActive) { "Failed to activate pump index=$index" }
}
```

check lança uma exceção IllegalStateException e é usada também para consistência do estado interno.

```kotlin
class Socket {
    var isConnected: Boolean = false
    var connectedHost: String? = null

    fun connect(to: String, result: (isConnected: Boolean) -> Void) {
        // Starting State Assumption: |this| is not already connected.
        check(!isConnected) {
            "|Socket.connect| cannot be called after a successful call to |Socket.connect|. " +
                    "socket=$this to=$to connectedHost=$connectedHost"
        }
    }
}
```

## Funções Genéricas

Podemos ter casos que precisamos escrever funções para um tipo, e então precisamos escrever para outro tipo. Para evitar um caso assim, as funções podem ser genéricas quanto aos tipos que usam. Esse recurso permite que uma função seja escrita de modo a funcionar com qualquer tipo, em vez de servir somente para um tipo específico.

```kotlin
fun <T> choose(t1: T, t2: T, t3: T): T {
    return when (Random().nextInt(3)) {
        0 -> t1
        1 -> t2
        else -> t3
    }
}
```

## Funções Puras

kotlin tem o suporte nativo a programação funcional e uma caracteristica é a imutabilidade. Na programação funcional, uma função pura é aquela que tem as duas propriedades.A função deve sempre devolver a mesma saída para a mesma entrada. A função não deve gerar efeitos colaterais. Um exemplo simples é abs, e as funções matematicas.
Quando escrevemos códigos orientados a objeto é muito fácil criar funções impuras (com efeito colateral) sem perceber. Veja o exemplo abaixo, onde temos um data class Shape com uma função slice.

```kotlin
data class Shape(var width: Float, var height: Float) {
    fun slice(times: Int): Float {
        return (this.width * this.height) / times
    }
```

Para tornar essa função pura, devemos remover todas as dependências a propriedades mutáveis externas. No caso da classe Shape, podemos mover a função slice para fora da classe, nos obrigando a definir width e height como parâmetros da função, tornando-os dependências externas que, uma vez que são passadas, não podem ser alteradas internamente

```kotlin
data class Shape(var width: Float, var height: Float)

fun slice(width: Float, height: Float, times: Int): Float {
    return (width * height) / times
}
```

 Essa técnica pode parecer simples para esses algoritmos, mas imagine o quanto poderíamos ganhar em velocidade em programas que possuem funções com execuções complexas.
