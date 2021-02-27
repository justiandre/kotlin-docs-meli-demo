# Capítulo 2 parte 2

## Sumário

1. [Pacotes](#pacotes)
2. [Importações](#importações)
    * [Importações com caractere-curinga](#importações-com-caractere-curinga)
    * [Renomeando na importação](#renomeando-na-importação)
3. [Template String](#template-string)
4. [Intervalos](#intervalos)
5. [Laços](#laços)
6. [Tratamento de exceções](#tratamento-de-exceções)
7. [Instanciando classes](#instanciando-classes)
8. [Igualdade referencial e igualdade estrutural](#igualdade-referencial-e-igualdade-estrutural)
9. [Modificadores de visibilidade](#modificadores-de-visibilidade)

## Pacotes

Os pacotes permitem separar o seu código em diferentes namespace:

```kotlin
package br.com.company.myproject.math

fun add(x: Int, y: Int) = a + y
```

Caso você não declare o pacote de um arquivo todas as declarações contidas nele ficarão disponíveis sem a necessidade de importação:

Suponha que temos o arquivo `foo.ks` dentro do dir `src/com/br/company/foo`

```kotlin
fun foo() {}
```

Note que não declaramos o package dp arquivo `foo.ks`. Agora vamos supor que temos o arquivo `boo.ks` dentro do dir `src/com/br/company/boo` e queremos usar a função `foo`:

```kotlin
package br.com.company.myproject.boo

fun boo() {
    foo()
}
```

A boa prática aqui é todo novo arquivo fazer parte de um pacote sempre relacionado a hierarquia do pacote que ele está.

## Importações

Permite que seja importado artefatos que foram declarados fora do pacote atual, como por exemplo classes, interface ou funções: 

```kotlin
package br.com.company.myproject.mydate

import java.time.LocalDate

fun now() = LocalDate.now()
```

No exemplo acima importei do namespace `br.com.company.myproject.LocalDate` a classe `LocalDate` para ser utilizada dentro do pacote `br.com.company.myproject.mydate`.

### Importações com caractere-curinga

Esse tipo de importação nos possibilita importar todos os artefatos declarados em um pacote, utilizando o operador *. Vamos supor que tenho um arquivo para mapear os keycodes do meu teclado:

```kotlin
package br.com.company.myproject.keycodes

val SPACE = 32
val ENTER = 13
val BACKSPACE = 8
```

E agora quero utiliza-lós em um pacote diferente. Segue exemplo sem utilizar o operador curinga:

```kotlin
package br.com.company.myproject

import br.com.company.myproject.keycodes.BACKSPACE
import br.com.company.myproject.keycodes.ENTER
import br.com.company.myproject.keycodes.SPACE

fun main() {
    println(BACKSPACE)
    println(SPACE)
    println(ENTER)
}
```

E agora com o operador curinga:

```kotlin
package br.com.company.myproject

import br.com.company.myproject.keycodes.*

fun main() {
    println(BACKSPACE)
    println(SPACE)
    println(ENTER)
}
```

A importação com caractere-curinga elimina a repetição na hora de importar diversos artefatos de um mesmo pacote.

### Renomeando na importação

Se dois pacotes possuem o mesmo nome, podemos utilizar a palavra reservada `as` para criar um apelido para um dos pacotes e assim evitar a colisão de nome entre os dois:

```kotlin

import br.com.company.myproject.mydate.now
import br.com.company.myotherproject.mydate.now as now2

fun main() {
    val n = now()
    val n2 = now2()
}
```

## Template String

Permite interpolar valores na construção de string, desenvolvedores javascript terão familiaridade com a sintaxe. 
O uso de templates é simples, necessitamos apenas prefixar um valor com o símbolo de cifrão ($) em uma string:

```kotlin
val theBestLanguage = "Kotlin"
println("The best language is $theBestLanguage !!")
// Vai imprimir: "The best language is Kotlin !!"
```

Também podemos interpolar expressões colocando-as entre chaves ({}) após o cifrão ($):

```kotlin
class Person(val firstName: String, val lastName: String)

fun main() {
    val joaozinho = Person("Joazinho", "Da Silva")
    println("The fullname is: ${joaozinho.firstName} ${joaozinho.lastName}")
}
```

## Intervalos

Um intervalo é definido por um valor inicial e final, qualquer tipo que seja comparável pode ser utilizado para criar um intervalo, no qual é feito com o operador `..`:

```kotlin
val aToZ = "a".."z"
val oneToTen = 1..10
```

É possível testar se um valor existe em um determinado intervalo utilizado o operador `in`:

```kotlin
println("c" in "a".."z") // true
println(5 in 1..10) // true
```

Basicamente qualquer tipo que seja compáravel pode ser utilizado para criar um intervalo. Você também pode criar intervalos personalizados sobrescrevendo o operador `rangeTo` em uma classe (veremos isso mais a fundo nos próximos grupos de estudo).

Existem também funções da biblioteca para criar intervalos, no qual cobre casos que o operador `..` não consegue:

```kotlin
val threeToZero = 3.downTo(0) // 3, 2, 1, 0
val oneToTen = 1.rangeTo(10) // O mesmo que fazer 1..10
val oddNumbers = oneToTen.step(2) // 1, 3, 5, 7, 9
val countingDownOddNumbers = oddNumbers.reversed() //  9, 7, 5, 3, 1
```

## Laços

Kotlin disponibiliza para construção de laços de repetição: o láco `while` e o laço `for`. Quem já trabalhou com uma linguagem que herda o estilo da linguagem C praticamente já conhece o laço `while` do kotlin, pois é exatamente igual:

```kotlin
while(true) {
    println("This will print out for a long time!")
}
```

Já o laço `for` em Kotlin aceita qualquer objeto que implemente o operator `iterator`, pode ser tanto uma função membro quanto uma funcão de extensão, e devolva uma instância de objeto que disponibilize as seguintes funções:

```kotlin
fun hasNext(): Boolean
fun next(): T
```

Quem já programou em Java vai perceber que são as mesmas funções que compõe a interface `Iterator` em Java. Segue um exemplo abaixo:

```kotlin
class Person(val name: String, val lastName: String) {

    operator fun iterator(): Iterator<String> {
        return arrayOf(name, lastName).iterator()
    }
}

fun main() {
    for (name in Person("Lionel", "Ronaldo")) {
        print("$name ")
    } 
    // Vai imprimir ao final do programa: Lionel Ronaldo
}
```

Note que utilizamos o operador `in` aqui também para iteramos no objeto `Person` dentro do laço `for`.

Todas as coleções em Kotlin implementam o operador `iterator`, e também disponibilizam uma forma de capturar tanto o índice quanto o valor, segue alguns exemplos abaixo:

```kotlin
fun main() {
    // array - apenas valores
    val heroes = arrayOf("batman", "superman")
    for (value in heroes) {
        println("$value, ")
    }

    println()

    // array - índice e valor
    for ((index, value) in heroes.withIndex()) {
        println("index: $index | value: $value, ")
    }

    println()

    // mapas
    for ((key, value) in mapOf(1 to "batman", 2 to "superman")) {
        println("key: $key | value: $value, ")
    }

    // fori tradicional em kotlin
    for (i in 1..10) {
        println(i)
    }
}
```

**Importante** ressaltar que tanto para laços de intervalos quanto para laços de Array o compilador do Kotlin irá compilar para um laço `for` usual bsaseado em índice, evitando qualquer penalidade no desempenho.

## Tratamento de exceções

O tratamento de exceções do Kotlin é basicamente igual ao JAVA, exceto que em Kotlin todas as exceções são não verificadas (unchecked). Para quem não conhece JAVA as exceções checked são aquelas exceções que são obrigadas a serem tratadas, caso isso não seja feito ocorrerá um erro de compilação. 

Segue abaixo um exemplo de tratamento de exceção em Kotlin:

```kotlin
fun main() {
    val name = "Wesli"
    var isNameValid = true
    try {
        validate(name)
    } catch (e: RuntimeException) {
        isNameValid = false
    } finally {
        println("Nome válido: $isNameValid")
    }
}
```

No exemplo acima estamos chamando a função `validate` dentro de forma segura, pois se caso ela lançar qualquer exception igual ou filha de `Runtimexception` será capturado no bloco `catch`, caso contrário não será capturada. O bloco `finally` sempre é chamado, independentemente se a exceção foi capturada ou não. Você é obrigado a declarar pelo menos um `finally` ou um `catch` junto com o `try` senão um erro de compilação ocorrerá.

## Instanciando classes

É igual a chamada de uma função normal, Kotlin dispensa totalmente a palavra `new` que é utilizada em JAVA para instanciar classes:

```kotlin
class User(val name: String)

fun main() {
    val pessoa = User("Topperson")
    println(pessoa.name) // Imprime: Topperson
}
```

## Igualdade referencial e igualdade estrutural

A igualdade referencial serve para testar se dois objetos apontam para a mesma referência. Para testar a igualdade referencial é utilizado o operador `===` ou `!==` para negação:

```kotlin
class User(val name: String)

fun main() {
    val topA = User("Topperson")
    val topB = User("Topperson")
    println(topA === topB) // Imprime false
    println(topA !== topB) // Imprime true
}
```
Já a igualdade estrutural é utilizada para testar se dois objetos tem o mesmo valor, por baixo dos panos a função `equals` é chamada quando estamos testando uma igualdade estrutural. Para testar a igualdade estrutural utilizamos o operador `==` ou `!=` para negação:

```kotlin
class Usuario(val name: String) {
    override fun equals(other: Any?): Boolean {
        if (other is Usuario) {
            return other.name == name
        }
        return false
    }
}

fun main() {
    val topA = Usuario("Topperson")
    val topB = Usuario("Topperson")
    println(topA == topB) // Imprime true
    println(topA != topB) // Imprime false
}
```

**OBS:** o operador == é null safe, isso quer dizer que não precisamos nos preocupar se estamos testando uma referência nula, o compilador já acrescenta essa verificação para nós.

## Modificadores de visibilidade

Kotlin com quatro modificadores de visibilidade, public, internal, protected e private.

**public:** Public será o modificador de visibilidade default caso você não declare um implicitamente. Todo artefato que for público estará disponível para que qualquer parte do código a use.

```kotlin
fun sayHello() { // Implicitamente público. Posso chamar essa função de qualquer parte do código.
    println("Hello")
}
```

**private:** Qualquer funçao, classe ou interface de alto nível definida como private só poderá ser acessada no mesmo arquivo. Já os membros de uma classe/objeto/interface, como funções ou propriedades, definidas como private só poderão ser acessadas de dentro da classe/objeto/interface:

```kotlin
class Employee(private val name: String)

class Company(private val employees: List<Employee>) {
                                                   
    private fun getEmployeeNames() = employees.map{ employee -> employee.name } 
    // Erro de compilação, pois a propriedade name é declarada como privada na class Employee
}
```

**protected:** Apenas propriedades de classe/objeto/interface podem ser declarados como protected. Toda propriedade e função de declarada como protected será vísivel a partir da própria classe/objeto/interface e para suas subclasses.

```kotlin
open class User(protected val name: String)

class SuperUser(name: String) : User(name) {

    fun sayName() {
        println(name)
    }
}

fun main() {
    val superUser = SuperUser("Ronaldo")
    println(superUser.sayName())
}
```

**internal:** É como o modificador public porém a nível de módulo. Um módulo é definidio como um módulo Maven ou Gradle ou um módulo Intellij.