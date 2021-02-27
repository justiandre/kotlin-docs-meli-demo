# Capítulo 5 - Funções de ordem superior e programação funcional

## Sumário

- [Capítulo 5 - Funções de ordem superior e programação funcional](#capítulo-5---funções-de-ordem-superior-e-programação-funcional)
  - [Sumário](#sumário)
  - [Funções de ordem superior](#funções-de-ordem-superior)
    - [Devolvendo uma função](#devolvendo-uma-funçõ)
    - [Atribuição de função](#atribuição-de-função)
  - [Closures](#closures)
  - [Funções anônimas](#funções-anônimas)
  - [Referências a funções](#referência-a-funções)
    - [Referências a funções de nível superior](#referências-a-funções-de-nível-superior)
    - [Referências a funções-membro e a funções de extensão](#referência-a-funções-membro-e-a-funções-de-extensão)
    - [Referências vinculadas](#referências-vinculadas)
  - [Receptores de funções literais](#receptores-de-funções-literais)
  - [Composição de funções](#composição-de-funções)
  - [Funções inline](#funções-inline)
    - [Noinline](#noinline)
  - [Currying e aplicação parcial](#currying-e-aplicação-parcial)
    - [Currying em ação](#currying-em-ação)
    - [Acrescentando suporte a currying](#acrescentando-suporte-a-currying)
  - [Memoização](#memoização)
    - [Implementando a memoização](#implementando-a-memoização)
  - [Alias de tipo](#alias-de-tipo)
  - [Either](#either)
    - [Fold](#fold)
    - [Projeção](#projeção)
    - [Outras funções de projeção](#outras-funções-de-projeção)
  - [DSLs](#dsls-personalizadas)


##  Funções de ordem superior
Uma função de ordem superior (higher order function) simplesmente aceita outra função como parâmetro, devolve uma função como seu valor de retorno, ou ambos.

Primeiro Exemplo:
```kotlin
fun foo(str: String, fn: (String) -> String): Unit {
    val applied = fn(str)
    println(applied)
}

fun main() {
   foo("hello", {it.reversed()})

   foo("hello") {it.reversed()}
}
```

*Lembre-se de que as função literal que tenha apenas um argumento pode usar it como atalho para evitar ter que o nomear explicitamente.

Segundo Exemplo:
```kotlin
fun main() {
    val ints = listOf(1, 2, 3, 4, 5, 6)

    val evens = ints.filter { it % 2 == 0 }
    val odds = ints.filter { it % 2 == 1 }
}
```

*Coleções e as funções de ordem superior disponíveis para elas serão discutidas de forma abrangente no Capítulo 10, coleções.

Vantagens: 
    - Reutilização de código
    - Rápido de escrever
    - Fácil de ler

###  Devolvendo uma função

Vamos agora retornar à definição de uma função de ordem superior. Lembre-se de que dissemos uma função que devolve outra função também é considerada uma função de ordem superior válida:

```kotlin
fun main() {
    val reversi = bar()

    reversi("hello")
    reversi("world")
}

fun bar(): (String) -> String = {str -> str.reversed()}
```
*Para devolver uma função, usamos um sinal de igualdade depois do tipo de retorno e colocamos a função entre chaves.

Na execução, atribuimos a função bar a uma variável chamada reversi antes de chamá-la com dois valores diferentes.
A utilizada dessa técnica poderá ser percebida quando tivermos uma função que aceite outros valores e devolva uma função que utilize as entradas da função original.

```kotlin
fun modulo(k: Int): (Int) -> Boolean = { it % k == 0 }

fun main() {
    val ints = listOf(1, 2, 3, 4, 5, 6)

    ints.filter (modulo(1))
    ints.filter (modulo(2))
    ints.filter (modulo(3))
}
```
*Neste caso, não usamos chaves porque se o fizéssemos, definiríamos outra função que chamaria a função modulo, e teríamos uma função de função.

###  Atribuição de função

As funções também podem ser atribuídas a variáveis para ser mais fácil passá-las por aí como parâmetros:

```kotlin
fun main() {
        val isEven: (Int) -> Boolean = modulo(2)
        
        listOf(1, 2, 3, 4).filter (isEven)
        listOf(5, 6, 7, 8).filter (isEven)
}

fun modulo(k: Int): (Int) -> Boolean = { it % k == 0 }
```

Também podemos atribuir funções literais a variáveis:
```kotlin   
    val isEven: (Int) -> Boolean = { it % 2 == 0}
```
Ou assim
```kotlin
    val isEven: (Int) -> Boolean = { k : Int -> k % 2 == 0}
```

##  Closures

Na programação funcional, closure é uma função que tem acesso a variáveis e parâmetros definidos em escopos mais externos. 
Dizemos que há um "close over" (fechamento ou abrangência) dessa varáveis - daí o nome closure.

Vamos considera um exemplo em que desejamos carregar nomes de um banco de dados e filtrá-los de modo que incluam apenas aqueles que antederem a alguns critérios de pesquisa.
Usaremos nosso bom e velho amigo, o método filter:

```kotlin
fun main() {
    val result = studentsByLastName("Silva")
    print(result)
}

fun studentsByLastName(lastNameToMatch: String): List<Student> {
    return loadStudents().filter {
        it.lastName == lastNameToMatch
    }
}

fun loadStudents() = listOf(Student("João", "Silva"), Student("Maria", "Antonia"), Student("Franciso", "Seila"), Student("Fran", "Silva"))
class Student(val firstName: String, val lastName: String)
```

Observe que a função literal passada para o método filter utiliza o parâmetro da função mais externa. 
Esse parâmetro está definido em um escopo externo à função, portanto a função faz um close over do parâmetro.

As closures podem acessar variáveis locais também:
```kotlin
    val counter = AtomicInteger(0)
    val cores = Runtime.getRuntime().availableProcessors()
    val threadPool = Executors.newFixedThreadPool(cores)

    threadPool.submit {
        println("I am task number ${counter.incrementAndGet()}")
    }    
```

Nesse exemplo, submetemos uma série de tarefas a um pool de threads. Como podemos ver, cada tarefa tem acesso a um contador compartilhado (usando AtomicInteger para segurança de thread).


As closures também podem alterar variáveis closed-over:
```kotlin
fun main() {
    var containsNegative = false
    val ints = listOf(0, 1, 2, 3, 4, 5, -1)

    ints.forEach{
        if (it < 0){
            containsNegative = true
        }
    }
}
```

##  Funções anônimas

Com frequência, quando usamos funções de ordem superior, nós as chamamos usando funções literais, especialmente se a função for pequena:
```kotlin
    listOf(1, 2, 3).filter { it > 1}
```

Como podemos ver, não há motivo para definir a função passada em nenhum lugar. Quando usamos literais desse modo, não podemos especificar o valor de retorno.
Em geral, isso não é um problema, pois o compilador de Kotlin inferirá o tipo de retorno para nós.

Às vezes, porém, pode ser que desejamos ser explícitos quanto ao tipo de retorno.
Nesses casos, podemos usar o que chamamos de função anônima. 
É uma função definida de modo parecido com uma função usual, exceto pelo fato de seu nome ser omitido:

```kotlin
    fun(a: String, b: String): String = a + b
```

A função pode ser usada assim:
```kotlin
fun main() {
    val ints = listOf(1, 2, 3)
    val evens = ints.filter(fun(k: Int) = k % 2 == 0)

    println(evens)
}
```

Se o tipo do parêmetro também puder ser inferido, essa informação podera ser omitida:

```kotlin
fun main() {
    val ints = listOf(1, 2, 3)
    val evens = ints.filter(fun(k) = k % 2 == 0)

    println(evens)
}
```
##  Referências a funções

Até agora neste capítulo, vimos como passar funções como parâmetros. 
Os modos como fizemos isso até o momento são criando uma função literal ou usando uma função que devolva outra função.

###  Referências a funções de nível superior

O que faríamos se tivéssemos uma função de nível superior e quiséssemos usá-la?
Podemos encapsular a função em outra função, é claro:
```kotlin
fun isEven(k: Int): Boolean = k % 2 == 0

fun main() {
    val ints = listOf(1, 2, 3, 4, 5)
    ints.filter { isEven(it) }
}
```

A alternativa é usar o que chamamos de referência a uma função. Ao utilizar a mesma definição isEven, podemos escrever um código assim:
```kotlin
fun isEven(k: Int): Boolean = k % 2 == 0

fun main() {
    val ints = listOf(1, 2, 3, 4, 5)
    ints.filter(::isEven)
}
```
Observe que a sintaxe :: é usada antes do nome da função.

###  Referências a funções-membro e a funções de extensão

Referências a funções podem ser usadas para funções de extensão e funções-membro, prefixando-as com o nome da classe.
Vamos definir uma função de extensão para interios cujo nome é isOdd, assim:
```kotlin
fun main() {
    fun Int.isOdd(): Boolean = this % 2 == 1

    val ints = listOf(1, 2, 3, 4, 5)
    ints.filter { it.isOdd() }
}
```

Como alternativa, também poderíamos utilizar uma referência:
```kotlin
fun main() {
    fun Int.isOdd(): Boolean = this % 2 == 1

    val ints = listOf(1, 2, 3, 4, 5)
    ints.filter(Int::isOdd)
}
```

*Uma referência de função a uma função-membro ou uma função de extensão tem um parâmetro extra: a instância ou o receptor sobre o qual a função é chamada.
Referência a funções podem parecer simplesmente outra maneira de fazer basicamente o mesmo, mas cosidere um caso em que uma função aceite vários parâmetros:

```kotlin
    fun foo(a: Double, b: Double, f: (Double, Double) -> Double) = f(a, b)
```

Nesse caso, foo chamará o parâmetro da função com as entradas a e b. Para fazer essa chamada, podemos, é claro, passar uma função literal:

```kotlin
    foo(1.0, 2.0, {a, b -> Math.pow(a, b) })
```

Math.pow é uma função-membro; como sabemos aceita dois doubles e devolve outro double, podemos utilizar uma referência de função.
Ela terá uma assinatura de função coincidente e, desse modo, permitirá a redução de código boilerplate:

```kotlin
    foo(1.0, 2.0, Math::pow)
```
###  Referências vinculadas

Em Kotlin 11, podemos ter referências de função vinculadas a uma instância em particular.
Isso signigica que podemos coloca ruma expressão antes do operador ::. 
A referência é, então, associada a essa instância específica, o que significa que de modo diferente das referências não vinculadas, a aridade da função devolvida não é incrementada;

Primeiro exemplo de referências não vinculadas:
```kotlin
fun String.equalsIgnoreCase(other: String) = this.toLowerCase() == other.toLowerCase()

fun main() {
    val result = listOf("foo", "moo", "boo", "Bar").filter {
        (String::equalsIgnoreCase)("bar", it)
    }

    println(result)
}
```

Temos uma função simples para testar igualdade sem cosiderar diferenças entre letras maiúsculas e minúsculas, mas quando criamos uma referência de função para ela, a assinatura será (String, String) -> Boolean.
O primeiro argumento é o receptor. Isso significa que não podemos simplesmente passar a referência para a função filter na lista, mas devemos encapsulá-la em outra função literal.

Agora um exemplo de referências vinculadas:
```kotlin
fun String.equalsIgnoreCase(other: String) = this.toLowerCase() == other.toLowerCase()

fun main() {
    val result = listOf("foo", "moo", "boo", "Bar").filter ("bar"::equalsIgnoreCase)

    println(result)
}
```

##  Receptores de funções literais

Com base no capítulo 4 sobre funções, lembre-se de que o receptor de uma função é a instância que corresponde à palavra reservada this quando estamos no corpo da função.
Em Kotlin, parâmetros de função podem ser definidos para aceitar um receptor quando são chamados.

Quando chamamos a função fn no corpo da função foo, precisamos chamá-la em uma instância de string, como podemos ver se completarmos a implementação de foo:

```kotlin
fun foo(fn: String.() -> Boolean): Unit {
    val result = "string".fn()
    println(result)
}

fun main() {
    foo { this.isEmpty() }
}
```

Esse recurso também funciona com funções anônimas:
```kotlin
fun main() {
     val subString = fun String.(substr: String): Boolean = this.contains(substr)
     val result = "hello".subString("ello")
   
     println(result)
}
```
Talvez você prefira a sintaxe de função anônima se quiser atribuir uma função a uma variável, como fizemos antes. Isso porque um receptor não pode ser especificado com uma função literal.

Receptores de função úteis quando escrevemos DSLs personalizadas. Discutiremos esse assunto em detalhes em uma seção mais adiante.


##  Composição de funções

De modo diferente de outras linguagens, Kotlin não tem suporte incluído para composição de funções, porém com os recursos que a linguagem oferece é possível criar algo semelhante:
```kotlin
fun <A,B,C> compose(fn1: (A) -> B, fn2: (B) -> C): (A) -> C {
    return { a ->
        val b = fn1(a)
        val c = fn2(b)
        c
    }
}
fun main() {
    val f = String::length
    val g = Any::hashCode
    val fog = compose(f, g)
    println(fog("what is the hash of my length?"))
}
```

É possível ainda utilizar composição de função juntamente com sintaxe infix.
```kotlin
infix fun <P1, R, R2> Function1<P1, R>.compose(fn: (R) -> R2): (P1) -> R2 = {
  fn(this(it))
}

fun infixexample() {
  val f = String::length
  val g = Any::hashCode
  val fog = f compose g
}

operator infix fun <P1, R, R2> Function1<P1, R>.times(fn: (R) -> R2): (P1) -> R2 = {
  fn(this(it))
}

fun opexample() {
  val f = String::length
  val g = Any::hashCode
  val fog = f * g
}
```

##  Funções inline

Kotlin nos permite evitar um overhead quando utilizamos funções usando a palavra reservada **inline**. Tal palavra informa ao compilador que a função marcada como inline, assim como seus parâmetros, 
dever ser expandida e gerada de forma inline no local da chamada.

```kotlin
fun multiplyByTwo(num: Int, 
                  lambda: (result: Int) -> Unit) : Int {
    val result = num * 2
    lambda.invoke(result)
    return result
}
fun main() {
    multiplyByTwo(5) { println("Result is: $it") }
}

// JAVA compilado
public static final void main(@NotNull String[] args) {
   multiplyByTwo(5, (Function1)null.INSTANCE);
}

public static final int multiplyByTwo(int num, Function1 lambda) {
   int result = num * 2;
   lambda.invoke(result);
   return result;
}

inline fun multiplyByTwo(num: Int, 
                         lambda: (result: Int) -> Unit) : Int {
    val result = num * 2
    lambda.invoke(result)
    return result
}

fun main() {
    multiplyByTwo(5) { println("Result is: $it") }
}

// JAVA compilado
public static final void main(@NotNull String[] args) {
   int num$iv = 5;
   int result$iv = num$iv * 2;
   String var4 = "Result is: " + result$iv;
   System.out.println(var4);
}

public static final int multiplyByTwo(int num, @NotNull Function1 lambda) {
   int result = num * 2;
   lambda.invoke(result);
   return result;
}
``` 

O uso desse recurso deve ser cuidadosamente avaliado. O volume de código gerado pode aumentar, mas se isso significa evitar alocações em um laço, valerá a pena fazê-lo.

###  Noinline

As lambdas inline só podem ser chamadas dentro das funções **inline** ou passadas como argumentos inline, mas as **noinline** podem ser manipuladas da forma que quisermos: armazenadas em campos, passadas em volta, etc.
```kotlin
fun main(args: Array<String>) {
    multiplyByTwo(5,
            { println("Result is: $it") },
            { println("Goodbye") })
}

inline fun multiplyByTwo(num: Int,
                         lambda1: (result: Int) -> Unit,
                         noinline lambda2: () -> Unit): Int {
    val result = num * 2
    lambda1.invoke(result)
    lambda2.invoke()
    return result
}

public static final void main(@NotNull String[] args) {
   byte num$iv = 5;
   Function0 lambda2$iv = (Function0)null.INSTANCE;
   int result$iv = num$iv * 2;
   String var5 = "Result is: " + result$iv;
   System.out.println(var5);
   lambda2$iv.invoke();
}
```
A função lambda1 era inline, mas a lambda2 não era.

##  Currying e aplicação parcial

É uma técnica comum em programação funcional é o conceito de currying. Trata-se do processo de transforma uma função que aceite vários parâmentros em um série de funções, em que cada uma aceitará uma única função.
```kotlin
fun foo(a: String, b: Int): Boolean
```
A Forma com currying terá o seguinte aspecto:
```kotlin
fun foo(a: String): (Int) -> Boolean
```

Currying está relacionado à ideia de aplicação parcial. É o processo pelo qual alguns parâmetros de uma função, mas não todos, são especificados previamente, devolvendo uma nova função que aceite os parâmetros que estiverem faltando.

###  Currying em ação

```kotlin
fun addition(a: Int, b: Int) = a + b

val addThree = { b: Int -> addition(3, b) }

fun main() {
    // Returns lambda (Int) -> Int
    addThree
    // Returns 8
    addThree(5)
}
```

###  Acrescentando suporte a currying

O primeiro passo é definir funções de extensão em FunctionN, que devolverão as funções com currying ou usar uma implementação pronta:

```kotlin
lib -> implementation "org.funktionale:funktionale-currying:1.2"


fun <P1, P2, R> Function2<P1, P2, R>.curried(): (P1) -> (P2) -> R = { p1 ->
    { p2 ->
        this(p1, p2)
    }
}

val add = { x: Int, y: Int -> x + y}.curried() // Now the type of this val is (Int) -> (Int) -> Int
val add3 = add(3)
val add5 = add(5)
val add100 = add(100)
println(add3(3)) // 6
println(add100(-10)) // 90
```

##  Memoização

A memoização (memoization) é uma técnica para agilizar chamadas de função fazendo caching e reutilizando a saída, em vez de refazer o processamento para um dado conjunto de entradas.
Essa técnica oferece um custo-benefício entre memória e velocidade.

Exemplo dado um função de Fibonacci:
```kotlin
fun fib(k: Int): Long = when (k){
    0 -> 1
    1 -> 1
    else -> fib(k -1) + fib(k -2)
}
```

Observe que quando chamamos fib(k), precisamos chamar fib(k-1) e fib(k-2). Como resultado, faremos muitas chamadas duplicadas com o mesmo valor. Por exemplo, para fib(5), chamaremos fib(1) cinco vezes separadamente.
Eis alguns tempos relativos ao chamar Fibonacci para diversos valores:

| função   | tempo     |
|----------|-----------|
| fib(5)   |  1 ms     |
| fib(10)  |  1 ms     |
| fib(15)  |  1 ms     |
| fib(20)  |  1 ms     |
| fib(25)  |  2 ms     |
| fib(30)  |  5 ms     |
| fib(35)  |  54 ms    |
| fib(40)  |  667 ms   |
| fib(45)  |  6349 ms  |
| fib(50)  |  69102 ms |

Podemos implementar um cache simples, por conta própria, usando um mapa:
```kotlin
val map = mutableMapOf<Int, Long>()
fun memfib(k: Int): Long {
    return map.getOrPut(k) {
        when (k) {
            0 -> 1
            1 -> 1
            else -> memfib(k - 1) + memfib(k - 2)
        }   
    } 
}
``` 

###  Implementando a memoização

Poderiamos tornar esse processo automático para qualquer função, mas somente para funções não recursivas. Uma função de propósito geral que utilize os valores de entrada como chaves em um cache para procurar o resultado armazenado:
```kotlin
fun <A, R> memoize(fn: (A) -> R): (A) -> R {
  val map = ConcurrentHashMap<A, R>()
  return { a ->
    map.getOrPut(a) {
      fn(a)
    }
  }
}

val memquery = memoize(::query)
```

Para melhorar mais ainda, podemos definir memoize como uma função de extensão em Function1, o que nos permitirá chamá-la usando a sintaxe de ponto:
```kotlin
fun <A, R> Function1<A, R>.memoize(): (A) -> R {
  val map = ConcurrentHashMap<A, R>()
  return { a ->
    map.getOrPut(a) {
      this.invoke(a)
    }
  }
}

val memquery = ::query.memoize()
```

##  Alias de tipo

Kotlin 1.1 introduziu um novo recurso para fazer referência a tipos verbosos, conhencido como aliases (apelidos) de tipos. Como o nome sugere, um alias de tipo nos permite declarar um novo tipo que é simplesmente um alizas de um tipo existente.
```kotlin
typealias Cache = HashMap<String, Boolean>
```

Um typealias não gera nenhum overhead nem vantagem para o runtime. O alias será simplesmente substituído pelo compilador. Isso significa que novos tipos não serão criados nem alocados.

```kotlin
typealias String1 = String
typealias String2 = String

fun printString(str: String1): Unit = println(str)

val a: String2 = "I am a String"
printString(a) 
```

##  Either

Na maioria das linguagens de programação funcional, há um tipo chamado **Either**. O tipo Either é usado para representar um valor que possa ter dois tipos possíveis.
```kotlin
sealed class Either<out L, out R>

class Left<out L>(val value: L) : Either<L, Nothing>()
class Right<out R>(val value: R) : Either<Nothing, R>()
```

###  Fold

A função **fold** aceitará duas funções. A primeira será aplicada se Either for uma instância do tipo Left, enquanto a segunda será aplicada se Either for do tipo Right.
```kotlin
sealed class Either<out L, out R> {
  fun <T> fold(lfn: (L) -> T, rfn: (R) -> T): T = when (this) {
    is Left -> lfn(this.value)
    is Right -> rfn(this.value)
  }
}
```

Veremos como esse código pode ser usado. Inicialmente criaremos algumas classes básicas que serão utilizadas no restante dos exemplos:

```kotlin
class User(val name: String, val admin: Boolean)
class ServiceAccount
class Address(val town: String, val postcode: String)
```

Então, vamos supor que tivéssemos uma função que devolvesse o usuário atual e outra que devolvesse os endereços de um usuário em particular:

```kotlin
fun getCurrentUser(): Either<ServiceAccount, User> = TODO()
fun getUserAddresses(user: User): List<Address> = TODO()
```

Observe que a função **getCurrentUser** devolve um Either que contém dois tipos de usuário. Um deles é um usuário regular e o outro, um **ServiceAccount**. Então, podemos usar esse Either para obter os endereços do usuário:
```kotlin
val addresses = getCurrentUser().fold({ emptyList<Address>() }, { getUserAddresses(it) })
```

###  Projeção

É comum ver funcionalidades em um Either que nos permitam mapear, filtrar, obter o valor, e assim por diante. Essas funções são definidas para que se apliquem somente a um dos tipos,
e haverá no-ops no outro caso. 

O modo como decidiremos fazer essa implementação consiste em criar duas subclasses de projeção: uma **ValueProjection** e um **EmptyProjection**. **ValueProjection** implementará as funções e **EmptyProjection** implementará no-ops.
A classe Either, então, conterá funções para obter uma projeção para o lado que for solicitado.

```kotlin
sealed class Projection<out T> {
    abstract fun <U> map(fn: (T) -> U): Projection<U>
}
```

**map** transformará o valor se for uma projeção no qual estamos interessados, e **getOrElse**, que devolverá o valor os aplicará um função default. O próximo passo será implementar isso para as duas classes:
```kotlin
class ValueProjection<out T>(val value: T) : Projection<T>() {
    override fun <U> map(fn: (T) -> U): Projection<U> = ValueProjection(fn(value))
}

class EmptyProjection<out T> : Projection<T>() {
    override fun <U> map(fn: (T) -> U): Projection<U> = EmptyProjection<U>()
}

fun <T> Projection<T>.getOrElse(or: () -> T): T = when (this) {
    is EmptyProjection -> or()
    is ValueProjection -> this.value
}
```

O último passo será atualizar nossa classe Either para que devolve essas projeções quando for solicitada:
```kotlin
sealed class Either<out L, out R> {
    fun <T> fold(lfn: (L) -> T, rfn: (R) -> T): T = when (this) {
        is Left -> lfn(this.value)
        is Right -> rfn(this.value)
    }

    fun leftProjection(): Projection<L> = when (this) {
        is Left -> ValueProjection(this.value)
        is Right -> EmptyProjection<L>()
    }

    fun rightProjection(): Projection<R> = when (this) {
        is Left -> EmptyProjection<R>()
        is Right -> ValueProjection(this.value)
    }
}
```

Agora, podemos usar esse código assim:
```kotlin
val postcodes = getCurrentUser().rightProjection()
                                .map { getUserAddresses(it) }
                                .map { addresses.map { it.postcode } }
                                .getOrElse { emptyList() }
```

Observe como podemos continuar a mapear os resultados e, em seguida, aplicar um default no final. Se o Either devolvido não for um valor Right, os mapas não terão nenhum efeito.

###  Outras funções de projeção

Prosseguiremos, adicionando mais funções de projeção, que são: exists, filter, toList e orNull.

A função **exists** aceitará uma função e, se a projeção tiver um valor, a função será aplicada e um resultado Boolean será devolvido. Se a projeção for vazia, ela devolverá false:
```kotlin
abstract fun exists(fn: (T) -> Boolean): Boolean
```

**filter** executará uma operação de filtro na projeção. A projeção de um valor aplicará a função e uma projeção vazia será devolvida se a função filter devolver false:
```kotlin
abstract fun filter(fn: (T) -> Boolean): Projection<T>
```

**toList** devolverá uma lista de valores ou uma lista vazia se a projeção for vazia:
```kotlin
abstract fun toList(): List<T>
```

**orNull** devolverá o valor ou null se projeção for vazia:
```kotlin
abstract fun orNull(): T?
```

O nosso tipo Either com mais algumas funções que nos permitirão inspecionar o tipo:
```kotlin
sealed class Either<out L, out R> {
    fun <T> fold(lfn: (L) -> T, rfn: (R) -> T): T = when (this) {
        is Left -> lfn(this.value)
        is Right -> rfn(this.value)
    }

    fun leftProjection(): Projection<L> = when (this) {
        is Left -> ValueProjection(this.value)
        is Right -> EmptyProjection<L>()
    }

    fun rightProjection(): Projection<R> = when (this) {
        is Left -> EmptyProjection<R>()
        is Right -> ValueProjection(this.value)
    }

    fun isLeft() = when (this) {
        is Left -> true
        is Right -> false
    }

    fun isRight() = when (this) {
        is Left -> false
        is Right -> true
    }
}

class Left<out L>(val value: L) : Either<L, Nothing>()
class Right<out R>(val value: R) : Either<Nothing, R>()

sealed class Projection<out T> {
    abstract fun <U> map(fn: (T) -> U): Projection<U>
    abstract fun exists(fn: (T) -> Boolean): Boolean
    abstract fun filter(fn: (T) -> Boolean): Projection<T>
    abstract fun toList(): List<T>
    abstract fun orNull(): T?
}

class EmptyProjection<out T> : Projection<T>() {
    override fun <U> map(fn: (T) -> U): Projection<U> = EmptyProjection<U>()
    override fun exists(fn: (T) -> Boolean): Boolean = false
    override fun filter(fn: (T) -> Boolean): Projection<T> = this
    override fun toList(): List<T> = emptyList()
    override fun orNull(): T? = null
}

class ValueProjection<out T>(val value: T) : Projection<T>() {
    override fun <U> map(fn: (T) -> U): Projection<U> = ValueProjection(fn(value))
    override fun exists(fn: (T) -> Boolean): Boolean = fn(value)
    override fun filter(fn: (T) -> Boolean): Projection<T> = when (fn(value)) {
        true -> this
        false -> EmptyProjection()
    }

    override fun toList(): List<T> = listOf(value)
    override fun orNull(): T? = value
}
```

Agora, podemos executar um código como este:
```kotlin
val service: ServiceAccount? = getCurrentUser().leftProjection().orNull()
val usersWithMultipleAddresses = getCurrentUser().rightProjection().filter { getUserAddresses(it).size > 1 }
val isAdmin = getCurrentUser().rightProjection().exists { it.admin }
```

## DSLs personalizadas

### Conceito

Diferente das linguagens de programação que se propõem à resolverem conjuntos genéricos de problemas, a [DSL](https://en.wikipedia.org/wiki/Domain-specific_language "DSL") `Domain Specific Language` foi criada para resolver um problema específico ou um conjunto de problemas que fazem parte de um mesmo domínio.

Um exemplo de `DSL`, é a linguagem utilizada nos scripts de [Gradle](https://gradle.org/ "Gradle"), que serve para configurar o build e dependências de um projeto [JVM](https://en.wikipedia.org/wiki/Java_virtual_machine "JVM"). Que é feito em [Groovy](https://groovy-lang.org/ "Groovy") ou [Kotlin](https://kotlinlang.org/ "Kotlin"), percebemos que por mais que tenhamos uma linguagem de programação para fazer isso, a sintaxe se mostra bem diferente e orientada à resolver um problema específico. Isso acontece porque Gradle possui uma `DSL` para escrever seus scripts, denominada [Gradle Build Language](https://docs.gradle.org/current/userguide/writing_build_scripts.html#sec:the_gradle_build_language "Gradle Build Language"). Outro exemplo bastante utilizado de `DSL` é o [Graphql](https://graphql.org/ "Graphql").

Um dos autores mais famosos sobre o assunto, [Martin Fowler](https://www.martinfowler.com/bliki/DomainSpecificLanguage.html "Martin Fowler"), considera [CSS](https://en.wikipedia.org/wiki/Cascading_Style_Sheets "CSS") e [SQL](https://en.wikipedia.org/wiki/SQL "SQL") como `DSL`. A partir disto, pode-se dizer que as `DSL's` podem ou não serem consideradas linguagens de programação.

**Podemos classificá-las de duas formas:**
- **Internas**: São aquelas que utilizam uma linguagem já existente como base, podendo alterar a sintaxe para resolver um problema de domínio específico, como por exemplo a `DSL` em `Kotlin` (*que será apresentada posteriormente*);
- **Externas**: São aquelas que utilizam sintaxes próprias e que, geralmente, necessitam da criação de um parser para o processamento das mesmas (*exemplo: `GraphQL`, `CSS`, `SQL`, dentre outras*).
 
Sabendo disso, veremos como podemos criar `DSL` internas para resolver problemas específicos em `Kotlin`.

Alguns exemplos das funcionalidades do `Kotlin` que auxiliam na criação de `DSLs`:

- **Higher Order Functions**: São funções que suportam uma função como argumento ou retornam uma função.

```kotlin
// Higher Order Function
fun logIfTrue(condition: Boolean, value: () -> String) {
  if (condition) {
    print(value())
  }
}

fun main() {
  val age = 30 // input
  // Chamada
  logIfTrue(age >= 18) { "A pessoa é maior de idade, e possui $age anos" }
}

```

- **Extension Functions**: É a possibilidade de adicionar funções à classes já existente.

```kotlin
// Extension Function
fun LocalDate.printPtBr() {
  println(this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
}

fun main() {
  // Chamada
  LocalDate.now().printPtBr() // print: 17/06/2020
  LocalDate.of(1988, 11, 3).printPtBr() // print: 03/11/1988
}
```

- **Infix Functions**: O modificador infix na assinatura de uma função permite que a mesma seja invocada em um objeto sem a necessidade de parênteses ou da inserção de um ponto antes do seu nome.

```kotlin
// Infix Function
infix fun Boolean.logIfTrue(message: String) {
  if (this) {
    println(message)
  }
}

fun main() {
  val age = 30 // input
  // Chamada
  (age >= 18) logIfTrue "A pessoa é maior de idade, e possui $age anos"
}
```

### Receptores de funções 

**O que é um lambda com um receptor ([receiver](http://esug.org/data/Old/ibm/tutorial/OOP.HTML))?**

Um lambda com um receptor permite chamar métodos de um objeto no corpo de uma lambda sem nenhum qualificador.

**Motivação**

Além do açúcar sintático e da concisão, as lambdas com receptores permitem o uso de `APIs` expressivas adequadas para `DSLs` internas. Eles são bons para esse propósito, porque as `DSLs` são linguagens estruturadas e o lambda com receptores fornece facilmente essa capacidade de estruturar `APIs` e a capacidade de representar estruturas aninhadas.

### Exemplos

#### Exemplo de uso

```kotlin
fun result() =
  html {
    head {
      title {+"XML encoding with Kotlin"}
    }
    body {
      h1 {+"XML encoding with Kotlin"}
      p  {+"this format can be used as an alternative markup to XML"}

      // an element with attributes and text content
      a(href = "http://kotlinlang.org") {+"Kotlin"}

      // mixed content
      p {
        +"This is some"
        b {+"mixed"}
        +"text. For more see the"
        a(href = "http://kotlinlang.org") {+"Kotlin"}
        +"project"
      }
      p {+"some text"}

      // content generated by
      p {
        for (arg in args)
          +arg
      }
    }
  }
```

**Nota**: Esse exemplo está disponível na [documentação oficial do Kotlin](https://kotlinlang.org/docs/reference/lambdas.html#function-literals-with-receiver "documentação oficial do Kotlin").


#### Exemplo de criação

```kotlin
/**
 * Calls the specified function [block] with `this` value as its receiver and returns `this` value.
 *
 * For detailed usage information see the documentation for [scope functions](https://kotlinlang.org/docs/reference/scope-functions.html#apply).
 */
@kotlin.internal.InlineOnly
public inline fun <T> T.apply(block: T.() -> Unit): T {
  // Impl...
}
```

**Nota**: Código da função `apply` disponível em todos objetos.

#### Exemplo prático

```kotlin
import org.junit.Assert
import org.junit.Test
import java.time.LocalDate

// Classe com o conteúdo dos erros de validação.
data class ValidationErrorItem(
   var field: String? = null,
   var message: String? = null
)

// Exception que é gerada ao executar as validações.
data class ValidationException(val items: Collection<ValidationErrorItem>) : RuntimeException("Erro de validação")

// Classe responsável por gerenciar o tratamento de erros e a criação da DSL feita para isso.
class ValidateHandler {

   companion object {
      fun context(context: ValidateHandler.() -> Unit) = ValidateContext(ValidateHandler().apply(context))
   }

   private val items: ArrayList<ValidationErrorItem> = arrayListOf()

   infix fun add(item: ValidationErrorItem) = items.add(item)

   infix fun Boolean?.isTrue(item: ValidationErrorItem.() -> Unit) = addIfTrue((this != null && !this), item)

   infix fun LocalDate?.isInPast(item: ValidationErrorItem.() -> Unit) = this?.run {
      val dateCompare = LocalDate.now()
      val isInPass = this == dateCompare || this.isAfter(dateCompare)
      addIfTrue(isInPass, item)
   }

   infix fun Any?.required(item: ValidationErrorItem.() -> Unit) {
      val isEmpty = when (this) {
         null -> true
         is String -> this.isBlank()
         is Iterable<*> -> this.toList().isEmpty()
         is Map<*, *> -> this.isEmpty()
         else -> false
      }
      addIfTrue(isEmpty, item)
   }

   private fun addIfTrue(condition: Boolean?, item: ValidationErrorItem.() -> Unit) = condition
      ?.takeIf { condition }
      ?.apply { add(ValidationErrorItem().apply(item)) }

   class ValidateContext(private val handler: ValidateHandler) {

      fun hasValidationError() = items().isNotEmpty()

      fun items() = handler.items

      fun validate() {
         if (hasValidationError()) {
            throw ValidationException(items())
         }
      }
   }
}

// Classe Model/Entidade com os dados de negócio e as suas regras, no caso aqui a utilização das DLS criada para validação.
data class Company(
   val name: String? = null,
   val sector: String? = null,
   val document: String? = null,
   val foundation: LocalDate? = null,
   val hasGain: Boolean = false,
   val collaborators: List<String>? = null
) {

   // Função com as regras de validação utilizando a DLS criada.
   fun validate() = ValidateHandler.context {
      name required {
         field = "name"
         message = "O nome é obrigatório"
      }
      sector required {
         field = "sector"
         message = "O setor é obrigatório"
      }
      document required {
         field = "document"
         message = "O documento é obrigatório"
      }
      foundation isInPast {
         field = "foundation"
         message = "A fundação deve estar no passado"
      }
      hasGain isTrue {
         field = "gain"
         message = "Deve possuir ganhos"
      }
      collaborators required {
         field = "collaborators"
         message = "É necessário ter ao menos um colaborador"
      }
   }.validate()
}

// Classe com os testes unitários que garantem o funcionamento da DLS.
class Test {

   @Test
   fun `with errors`() {
      val company = Company(
         name = "  ",
         sector = "",
         document = null,
         foundation = LocalDate.now().plusDays(1),
         hasGain = false,
         collaborators = listOf()
      )
      try {
         company.validate()
         Assert.fail("Deveria gerar erro de validação")
      } catch (validationException: ValidationException) {
         val expectErrors = arrayOf(
            ValidationErrorItem("name", "O nome é obrigatório"),
            ValidationErrorItem("sector", "O setor é obrigatório"),
            ValidationErrorItem("document", "O documento é obrigatório"),
            ValidationErrorItem("gain", "Deve possuir ganhos"),
            ValidationErrorItem("foundation", "A fundação deve estar no passado"),
            ValidationErrorItem("collaborators", "É necessário ter ao menos um colaborador")
         )
         assertValidationException(expectErrors, validationException)
      }
   }

   @Test
   fun `without errors`() {
      val company = Company(
         name = "Mercado Livre",
         sector = "TI/E-Commerce",
         document = "123456789",
         foundation = LocalDate.of(1999, 8, 2),
         hasGain = true,
         collaborators = listOf("André Justi", "Wellington Gustavo")
      )
      try {
         company.validate()
      } catch (validate: ValidationException) {
         Assert.fail("Não deveria gerar erro de validação")
      }
   }

   fun assertValidationException(expectErrors: Array<ValidationErrorItem>, validationException: ValidationException) {
      val errors = validationException.items.toTypedArray()
      val validationErrorItemSort: (ValidationErrorItem) -> String? = ValidationErrorItem::message
      errors.sortBy(validationErrorItemSort)
      expectErrors.sortBy(validationErrorItemSort)
      Assert.assertArrayEquals("Erros não esperados", expectErrors, errors)
   }
}
```
