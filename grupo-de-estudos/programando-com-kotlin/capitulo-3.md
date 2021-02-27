# Capítulo 3 - Programação Orientada a Objetos em Kotlin

## Sumário

1. [Classes](#classes)
    - [Níveis de Acesso](#níveis-de-acesso)
    - [Classes Aninhadas](#classes-aninhadas)
    - [Classes de Dados](#classes-de-dados)
    - [Classes Enum](#classes-enum)
    - [Métodos Estáticos e Objetos Companheiros](#métodos-estáticos-e-objetos-companheiros)
2. [Interfaces](#interfaces)
3. [Herança](#herança)
4. [Modificadores de Visibilidade](#modificadores-de-visibilidade)
5. [Classes Abstratas](#classes-abstratas)
6. [Interface ou Classe Abstrata](#interface-ou-classe-abstrata)
7. [Polimorfismo](#polimorfismo)
8. [Regras para Sobrescrita](#regras-para-sobrescrita)
9. [Herança versus Composição](#herança-versus-composição)
10. [Delegação de Classe](#delegação-de-classe)
11. [Classes Seladas](#classes-seladas)

## Classes

Classes são as construções básicas de linguagens orientadas a objetos. Conceitualmente, permitem a criação de tipos customizados com métodos e atributos.

Em Kotlin, assim como em outras linguagens orientadas a objetos, a definição de uma classe é precedida pela palavra-chave `class`, da seguinte forma:

```kotlin
class MyClass {
    [...]
}
```

Assim como em Java, PHP, Ruby e outras, Kotlin permite incluir várias classe no mesmo arquivo.

A palavra chave `class`, por sua vez, pode ser precedida pelo nível de acesso e, se for omitido, o padrão será `public`:

```kotlin
private class MyClass {
    [...]
}
```

Diferentemente de Java, em que criamos um método com o mesmo nome da classe, em Kotlin o construtor é definido diretamente ao definir a classe:

```kotlin
class MyClass constructor(val intValue Int, val stringValue String) {
    [...]
}
```

Caso nós não precisemos incluir modificadores de acesso no método construtor, a palavra reservada `constructor` pode ser omitida:

```kotlin
class MyClass(val intValue Int, val stringValue String) {
    [...]
}
```

À esta definição do construtor diretamente na definição da classe chamamos `construtor primário`.

Caso precisemos fazer algum tratamento especial no construtor primário, implementamos o bloco `init`, que é executado juntamente ao construtor primário:

```kotlin
class MyClass(val intValue Int, val stringValue String) {
    init {
        require(intValue < 1) { "Invalid value!" }
    }
}
```

No exemplo acima, o método `require` irá disparar uma exceção `IllegalArgumentException` se o valor da expressão for `false`.

Caso precisemos criar outras possibilidades de parâmetros no construtor, podemos fazer isso através de `construtores secundários`:

```kotlin
class MyClass(val intValue Int, val stringValue String) {
    constructor(val stringValue) : this(intValue, stringValue) {
        this.intValue = 1
    }
}
```

E se nós tivéssemos definido um dos parâmetros como `nullable`, nós podemos definir o construtor secundário em apenas uma linha:

```kotlin
class MyClass(val intValue Int, val stringValue String?) {
    constructor(val intValue) : this(intValue, null)
}
```

Como citado acima, caso precisemos alterar a visibilidade do construtor da classe, podemos fazer através do construtor primário:

```kotlin
class MyClass internal constructor(val intValue Int, val stringValue String) {

}
```

Prefixando os argumentos do construtor com `val` ou `var` não é obrigatório, porém, não incluindo estas palavras reservadas, o compilador Kotlin não irá criar os getters nem os setters (para o caso de var) para os atributos definidos:

```kotlin
class MyClass(intValue Int, stringValue String) {

}
```

Em Java e em outras linguagens, quando vamos criar um objeto novo, incluímos a palavra reservada `new`. Em Kotlin, esta palavra reservada não é utilizada e a criação de objeto é semelhante à chamada de uma função:

```kotlin
val myObject = MyClass(10, "My String")
```

### Níveis de Acesso

Para classes em Kotlin, estão disponíveis os seguintes níveis de acesso:
- Internal (interno): É possível criar uma nova instância da classe em qualquer lugar dentro do módulo. É o equivalente a private, porém a nível de módulo
- Private (privado): A classe é visível somente no escopo do arquivo que a define
- Protected (protegido): É utilizado para subclasses

### Classes Aninhadas

O conceito de classes aninhadas existe também em Java e trata-se de uma classe definida dentro do corpo de outra classe. Em Kotlin, classes aninhadas são definidas da seguinte forma:

```kotlin
class FirstClass {
    class SecondClass {

    }
}
```

Classes aninhadas, assim como classes comuns, podem conter níveis de acesso, como por exemplo:

```kotlin
class FirstClass {
    private class SecondClass {

    }
}
```

No exemplo acima, a classe `SecondClass` poderá ser criada somente dentro de métodos da classe `FirstClass`.

Há dois tipos de classes aninhadas: Estáticas e não estáticas, também chamadas de classes internas. Por padrão, classes aninhadas são estáticas. Para definir classes internas, utilizamos a palavra reservada `inner`.

Os exemplos abaixo demonstram as diferenças entre os tipos de classes aninhadas:

- Classes Estáticas

```kotlin
class FirstClass {
    class SecondClass {
        fun master() {
            println("blaster")
        }
    }
}

// A "vantagem" de classes estáticas é que seus métodos podem ser acessados sem a criação de um objeto da classe "pai":
fun main(args: Array<String>) {
    FirstClass.SecondClass().master() // Irá imprimir "blaster"
}
```
- Classes Internas

```kotlin
class FirstClass {
    private val myValue = "test"
    // Classe Interna
    inner class SecondClass {
        fun master() {
            println(myValue)
        }
    }
}

// A "vantagem" de classes internas é que os métodos e atributos da classe "pai" podem ser acessados a partir da classe "filha":
fun main() {
    FirstClass().SecondClass().master() // Irá imprimir "test"
}
```

Caso existam atributos e métodos com o mesmo nome na classe "pai" e na classe aninhada, estes valores podem ser acessados através da expressão `this`. Kotlin possui uma versão "mais potente" do que outras linguagens:

```kotlin
class FirstClass {
    private val myValue = "first"
    // Classe Interna
    inner class SecondClass {
        private val myValue = "second"
        fun printValue() {
            println(this.myValue)
            println(this@FirstClass.myValue)
            println(this@SecondClass.myValue)
        }
    }
}

fun main() {
    FirstClass().SecondClass().printValue() // Irá imprimir "second" depois "first" depois "second" novamente
}
```

Classes aninhadas podem ser definidas anonimamente também, utilizando uma expressão de objeto (object expression):

```kotlin
window.addMouseListener(object : MouseAdapter() {
    override fun mouseClicked(e: MouseEvent) {
        [...]
    }
})
```

### Classes de Dados

Muitas vezes precisamos definir classes apenas para armazenar dados. Kotlin possui uma construção específica para isso, chamada `data class`. O compilador Kotlin cria para data classes, automaticamente, os métodos get, set, equals, entre outros.

Para definir data classes, utilizamos a palavra reservada `data` ao definir a classe:

```kotlin
data class User(val id: Int, val email: String, val password: String)
```

### Classes Enum

Enumerações são um tipo específico de classes, onde definimos um conjunto de constantes predefinidas. Para definir uma enumeração, utilizamos a palavra reservada `enum` antes de definir a classe:

```kotlin
enum class DeveloperActions { CODE, SLEEP, REPEAT }

fun main(args: Array<String>) {
    // Para pegar o valor da constante como string, utilizamos o método "valueOf":
    println(DeveloperActions.valueOf("CODE")) // Irá imprimir "CODE"
}
```

Assim como outros tipos de classes, é possível definir parâmetros de construtor para enumerações:

```kotlin
enum class Ammo(val ammoName: String) {
    BOW("Arrow"),
    PISTOL("Bullet")
}

fun main(args: Array<String>) {
    println(Ammo.BOW.ammoName) // Irá imprimir "Arrow"
}
```

Assim como qualquer outra classe, enumerações podem herdar de uma interface e implementá-la anonimamenta para cada valor da enum:

```kotlin
interface Codable {
    fun code(): String
}

enum class DeveloperType : Codable{ 
    BACKEND {
        override fun code(): String {
            return "Kotlin"
        }
    },
    FRONTEND {
        override fun code(): String {
            return "Javascript"
        }
    }
}

fun main(args: Array<String>) {
    println(DeveloperType.BACKEND.code()) // Irá imprimir "Kotlin"
}
```

### Métodos Estáticos e Objetos Companheiros

Diferentemente de Java, Kotlin não permite métodos estáticos em classes. Para isso, é aconselhável definir funções para os pacotes:

```kotlin
// mypackage.kt
package com.mypackage

fun myStaticFunction(): Unit {
    println("Printing statically!")
}

// main.kt
fun main(args: Array<String>) {
    com.mypackage.myStaticFunction()
}
```

Para tratar funções, o compilador Kotlin cria automaticamente uma classe e inclui as funções como métodos estáticos desta classe.

A construção `object expression` pode ser utilizada não só para criar classes anonimamente, mas também para criar objetos singleton, com métodos ditos "estáticos":

```kotlin
object Singleton {
    private var count = 0
    fun call(): Int {
        count++
        return count
    }
}

fun main(args: Array<String>) {
    println(Singleton.call()) // Irá imprimir 1
    println(Singleton.call()) // Irá imprimir 2
}
```

A estrutura object expression pode ser utilizada dentro do corpo de outras classes, utilizando a palavra reservada `companion`. Dessa forma, temos uma estrutura semelhante à de métodos estáticos de uma classe "comum".

```kotlin
class MyClass {
    companion object {
        fun staticFunction(): Unit {
            println("call static!")
        }
    }
}

fun main(args: Array<String>) {
    MyClass.staticFunction() // Irá imprimir "call static!"
}
```

Por baixo, o compilador Kotlin cria um atributo com o nome `companion` e uma classe com seus métodos definidos, permitindo, assim, herança:

```kotlin
interface Blaster {
    fun master(): Unit
}
class MyClass {
    companion object : Blaster {
        override fun master(): Unit {
            println("Master blaster")
        }
    }
}

fun main(args: Array<String>) {
    MyClass.master() // Irá imprimir "Master blaster"
}
```

## Interfaces

Interfaces por padrão são contratos a serem seguidos. Entretanto, em Kotlin, assim como em Java 8, interfaces podem conter não somente as declarações de métodos abstratos mas também implementações de métodos. Diferentemente de classes abstratas, interfaces não podem conter estados, porém podem definir atributos:

```kotlin
interface MyInterface {
    val myAttribute: String

    fun save(): Boolean
    fun printMyAttribute(): Unit {
        println(myAttribute)
    }
}

class MyClass(override val myAttribute: String) : MyInterface {
    override fun save(): Boolean {
        return true
    }
}
```

## Herança

Nos exemplos acima, utilizamos herança em vários momentos. Em Kotlin, a marcação de herança é feita através do sinal de dois pontos `:` na declaração da classe.

Diferentemente de Java, que utiliza os identificadores `extends` e `implements`, em Kotlin tanto para classes quanto para interfaces utilizamos os dois pontos e tratamos como herança de forma igual:

```kotlin
interface MyInterface {
    [...]
}

interface MyOtherInterface {
    [...]
}

abstract class MyParentClass {
    [...]
}

class MyChildClass : MyInterface, MyParentClass(), MyOtherInterface {
    [...]
}
```

Em Kotlin não há necessidade de que a classe esteja antes das interfaces na definição. A única restrição é que uma classe herde apenas de uma classe superior, porém, pode herdar de quantas interfaces forem necessárias. Ao herdar de classes, devemos sempre chamar o construtor daquela classe ao definir a classe derivada, por isso, no exemplo acima são incluídos os parênteses.

## Modificadores de Visibilidade

Assim como na definição de classes, quando é feita a declaração de atributos e métodos é possível utilizar modificadores de visibilidade. Assim como em classes, são quatro modificadores de visibilidade possíveis:

- Public (Público): O acesso pode ser feito de qualquer lugar. É o valor padrão
- Internal (Interno): O acesso pode ser feito de dentro do código do módulo
- Protected (Protegido): O acesso pode ser feito de dentro da classe que define o item e de qualquer classe derivada, que herde desta classe
- Private (Privado)

Em Kotlin, por padrão, todas as classes exceto classes abstratas são `final`, ou seja, não podem ser herdadas. Caso seja necessário herdar de uma classe, esta classe deve estar marcada como `open`, permitindo a herança. Isso vale também para seus respectivos métodos e atributos. A sobrecarga de métodos e atributos deve ser feita explicitamente utilizando a palavra reservada `override`. Quando fazemos a sobrecarga de um método podemos, se necessário, alterar o modificador de visibilidade deste item:

```kotlin
open class MyParentClass {
    protected open val myValue: String = "Some Value"
    
    protected open fun myFun(): String {
        return "Parent"
    }
}

class MyChildClass : MyParentClass() {
    public override val myValue: String = "Other Value"
    
    public override fun myFun(): String {
        return "Child"
    }
}


fun main(args: Array<String>) {
	val a = MyChildClass()
    println(a.myFun()) // Irá imprimir "Child"
    println(a.myValue) // Irá imprimir "Other Value"
}
```

## Classes Abstratas

Classes abstratas são classes parcialmente definidas. Por serem parcialmente definidas, não é possível criar instâncias de uma classe abstrata. Propriedades e métodos que não tenham implementação precisam ser implementados em classes derivadas. Classes abstratas podem herdar de classes abstratas, sendo necessário, por sua vez, que outra classe herde desta segunda classe e implemente os itens necessários de ambas as classes. Tanto as classes abstratas quanto os métodos a serem implementados utilizam o identificador `abstract`:

```kotlin
abstract class MyAbstractClass {
    abstract fun myAbstractFun()
}
```

É possível herdar de uma classe marcada como aberta e redefinir um método desta classe como abstrato, se necessário. Dessa forma, a classe derivada deve ser identificada como abstrata, por conter itens não implementados:

```kotlin
open class MyParentClass {
    open fun myFun(): String = "My Function Return"
}

abstract class MyChildClass : MyParentClass() {
    abstract override fun myFun(): String
}

class MyGrandChildClass : MyChildClass() {
    override fun myFun(): String = "My GrandChild Function Return"
}

fun main(args: Array<String>) {
    println(MyGrandChildClass().myFun()) // Irá imprimir "My GrandChild Function Return"
}
```

## Interface ou Classe Abstrata

- Is-a (É um): Quando ao avaliar um item a herdar pode ser feita a pergunta "Este item é um ...?" deve-se utilizar classes abstratas.
- Can-do (Pode fazer): Quando ao avaliar um item a herdar pode ser feita a pergunta "Este item pode fazer ...?" deve-se utilizar interfaces.

Classes abstratas tratam-se de tipos a serem herdados. Interfaces tratam-se de comportamentos variáveis.

Em Interfaces são considerados diferentes implementações para o mesmo contrato. Isso garante uma flexibilização das necessidades de implementação, garantindo que todo o "ecossistema" ao redor não seja impactado pelas alterações.

Entretanto, classes abstratas facilitam a reutilização de código, pois pode-se forçar a implementação novamente apenas de itens que de fato precisam ser implementados de forma diferente.

## Polimorfismo

O polimorfismo é um dos pilares da programação orientada a objetos, permitindo o desacoplamento de "o que" com o "como". Palavra de origem grega: polys significa muitos, e morphés significa formas. Existem diversas formas, no entanto o capítulo fala sobre runtime binding.

O polimorfismo permite o referenciamento de classes base para tratamento de eventos e algoritmos independente da implementação em runtime. Um exemplo disso poderia ser o tratamento de diferentes tipos de impressoras.

```kotlin
open class Printer {
    open fun print(content: String) {
        println("Printing")
    }
}

class ZplPrinter : Printer() {
    override fun print(content: String) {
        println("Printing zpl: ${content}")
    }
}

class RawPrinter : Printer() {
    override fun print(content: String) {
        println("Printing raw: ${content}")
    }
}

fun printContent(printer: Printer, content: String) {
    printer.print(content)
}

fun main(args: Array<String>) {
    val p1 = ZplPrinter()
    val p2 = RawPrinter()
    printContent(p1, "Some zpl content")
    printContent(p2, "Some raw content")
}
```

Ao compilar o código para bytecodes, é possível notar que existe uma chamada a um método virtual (invokevirtual). Por baixo dos panos o que ocorre é que na instanciação de um objeto, no momento em que a memória é alocada na heap, além de alocar memória para as propriedades do objeto (incluindo classes base), é sempre reservado um espaço extra que estará sempre no início do bloco (para fins de ganho de desempenho), que conterá uma referência às informações do dos metadados do tipo. Dentro dos metadados do tipo, existe a referência a uma tabela virtual (vtable), que por sua vez tem as referências a implementação de código nativo a ser executada.

Para o caso de referências a interfaces, o caminho é um pouco diferente. É necessário fazer a validação se a classe implementa mesmo o método e em caso afirmativo buscar o local onde estes métodos estão registrados. Não há maneira simples de garantir a ordem de métodos na vtable para classes distintas. Neste caso, é necessária uma busca pelas interfaces implementadas procurando o alvo. Depois de encontrada, é necessário uma busca na itable (tabela de métodos de interface), que é uma lista cuja estrutura de entradas é a mesma para cada classe que implementa a interface, para então chamar o método virtual. Isso ocorre porque uma interface X pode ser implementada por A e ter sido ou não sobrescrita na classe B que foi derivada de A.


## Regras para Sobrescrita

O contrário de java, onde por padrão as classes são extensíveis, em kotlin é necessário ser mais explícito. É necessário utilizar a palavra chave `open` para a classe e para propriedades ou métodos que podem ser sobrescritos e `override` ao sobrescrevê-los:

```kotlin
open class Shape {
    open val vertexCount: Int = 0
    open fun draw() {
        println("Drawing shape")
    }
}

class Rectangle() : Shape() {
    final override val vertexCount: Int = 4
    final override fun draw() { 
        println("Drawing rectangle")
    }
}

class Polygon : Shape {
    override var vertexCount: Int = 0  // Can be set to any number later
}
```

- É possível tornar uma propriedade ou método não extensível utilizando a palavra chave `final`.
- É possível transformar uma propriedade de `val` para `var` mas não o oposto.
- Existem situações onde é necessário derivar de uma classe e de uma interface onde ambas definem um método com a mesma assinatura. Nestes casos, é necessário que a classe derivada sobrescreva o método:

```kotlin
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

open class Image {
    open fun save(out: OutputStream) {
        println("logic to save image")
    }
}

interface VendorImage {
    fun save(out: OutputStream) {
        println("default vendor logic to save image")
    }
}

class PNGImage: Image(), VendorImage {
    override fun save(out: OutputStream) {
        super<VendorImage>.save(out)
        super<Image>.save(out)
    }
}

fun main(args: Array<String>) {
    val pngImage = PNGImage()
    val os = ByteArrayOutputStream()
    pngImage.save(os)
}
```

Caso não haja uma implementação padrão, a sobrescrita não é necessária.

## Herança versus Composição

Herança e composição são recursos fundamentais para POO.
Herança: relação de `is-a`. De classe ou interface.
Composição: relação de `has-a`. Significa que um objeto contém outro. Um exemplo poderia ser uma classe `DrawingBoard`, que irá conter uma série de `Shapes`.

```kotlin
class DrawingBoard {
    var shapes: Array<Shape>
}

class Shape (val x: Int, val y: Int){
}

```

Formas de associação:
- Agregação: associação com menor acoplamento, onde ambos objetos podem existir independentemente, como por exemplo `Professor` e `Departamento`. Um pode existir sem o outro.

- Composição: associação de maior acoplamento, onde um objeto não existe sem o outro, como por exemplo uma `Universidade` e `Departamento`. Os departamentos só existem se a universidade existir.

É possível e comum combinar associações e herança.

## Delegação de Classe

Design pattern onde a implementação é delegada de um objeto para outro. Kotlin permite a delegação de implementações de maneira prática utizando a palavra chave `by`. Isso fará com que o compilador gere automaticamente os métodos que delegam para esta determinada classe.

```kotlin
interface Base {
    fun printMessage()
    fun printMessageLine()
}

class BaseImpl(val x: Int) : Base {
    override fun printMessage() { print(x) }
    override fun printMessageLine() { println(x) }
}

class Derived(b: Base) : Base by b {
    override fun printMessage() { print("abc") }
}

fun main() {
    val b = BaseImpl(1000)
    Derived(b).printMessageLine()
    Derived(b).printMessage()
}
```

## Classes Seladas

São classes abstratas que só podem ser estendidas por classes aninhadas. Funciona de maneira parecida a uma Enum, com a diferença de que a Enum possui uma única instância para cada tipo, enquanto a classe selada pode possuir n instâncias. Isso permite utilizar esta hierarquia de classes dentro de expressões `when`, pois o compilador consegue cobrir todos os casos possíveis.

```kotlin
sealed class Optional<out V> {
    abstract fun isPresent(): Boolean
    
    class Some<out V>(val value: V) : Optional<V>() {
        override fun isPresent(): Boolean = true
    }
    
    class Empty<out V> : Optional<V>() {
        override fun isPresent(): Boolean = false
    }
}
 


fun optionalOfNullableString(v: String?): Optional<String> {
    if(v == null) {
        return Optional.Empty()
    }
    return Optional.Some(v)
}

fun printOptional(v: Optional<String>) {
    when(v) {
        is Optional.Some -> println("Content: ${v.value}")
        is Optional.Empty -> println("Content is empty")
        // else is not necessary, since all the options are covered
    }
}

fun main() {
    val opt = optionalOfNullableString(null)
    val opt2 = optionalOfNullableString("Awesome")
    printOptional(opt)
    printOptional(opt2)
}
```