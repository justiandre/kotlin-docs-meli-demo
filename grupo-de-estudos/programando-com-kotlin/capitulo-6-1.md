# Capítulo 6

## Sumário

1. [O que são propriedades](#O-que-são-propriedades)
2. [Setter e Getter](#Setter-e-Getter)
3. [Visibilidade](#visibilidade)
4. [Inicialização tardia](#inicializacao-tardia)
5. [Propriedades delegadas](#propriedades-delegadas)

## O que são propriedades

Primeiro vamos entender o que são `fields` e `properties` em Java, considere a seguinte classe:

```java
class User {

    private int id;
    private String name;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

Os `fields` dessa class são o `id` e o `name` do User, que são o que definem o estado interno da classe. Porém quando você adiciona getter's e setter's para esse campos ele se tranformam propriedades, no qual o seu valor pode ser alterado ou recuperado através desses acessores.

Já em Kotlin não conseguimos declarar fields, toda declaração de membros que descrevem o estado de uma class em Kotlin são considerados propriedades. Considera agora a class `User` em Kotlin agora:

```kotlin
class User(val id: Int = 0, var name: String = "")
```

Se você tentar esse código a partir de uma class Java você vai perceber que por baixo dos panos o compilador de Kotlin gerou um getter para a propriedade `id`, pois foi declarada como `val`, e um getter e setter para a propriedade `name`, pois foi declarado como `var`.

## Setter e Getter

Como foi visto o Kotlin já gera um `getter` e `setter` padrão para as propriedades declaradas, porém é possível declarar um customizado caso queira. Primeiro vamos ver a sintaxe de uma proriedade:


```
var/val<propertyName>:<PropertyType>[=<property_initializer>]
[<getter>]
[<setter>]
```

Fica claro que não conseguimos declarar getter's e setter's a partir de uma propriedade que foi declarar diretamente no construtor default da class, a propriedade precisa ser declarada no corpo da função para que seja possível criar o getter e o setter personalizado:

```kotlin
class User {
    val id: Int = 0
    var name: String
    get() = name.toUpperCase()
    set(value) {
        name = value.toUpperCase()
    }
}
```

Agora criamos um getter e setter personalizado para a propriedade `name`, porém receberíamos um `StackOverFlow` ao chamar qualquer um dos dois. Consegue adivinhar o porquê?
Isso acontece pois ao acessarmos a prop `name` dentro do setter ou do getter estamos chamando novamente a função de setter ou getter gerando chamadas recursivas até estourar a exceção. Para resolver isso o Kotlin nos disponibiliza a palavra reservada `field`, que gera um campo de apoio no bytecode final:

```kotlin
class User {
    val id: Int = 0
    var name: String = ""
    get() = field.toUpperCase()
    set(value) {
        field = value.toUpperCase()
    }
}
```

Caso você prefira é possível utilizar uma propriedade de apoio para alcançar o mesmo resultado (não é muito idiomático):

```kotlin
class User {
    val id: Int = 0
    private var _name: String = "" // backing property
    var name : String
    get () = _name.toUpperCase()
    set (value) {
        _name = value
    }
}
```

## Visibilidade

O getter da propriedade sempre deve possuir a mesma visibilidade da própria propriedade, o código abaixo por exemplo não compila:

```kotlin
class User {
    val id: Int = 0
    private var name : String = ""
    public get () = field.toUpperCase() // O código não compila pois foi declarado uma visibilidade diferente da propriedade.
    set (value) {
        field = value.toUpperCase()
    }
```

Já para o setter é permitido declarar uma visibilidade mais restritiva que a visibilidade da propriedade em si:

```kotlin
class User {
    val id: Int = 0
    var name : String = ""
    get () = field.toUpperCase()
    private set (value) {
        field = value.toUpperCase()
    }
```

Ao sobrescrever uma propriedade via herança os acessores da classe pai não são herdados:

```kotlin
open class User {
    open var name: String = ""
        get() = field.toUpperCase()
        set(value) {
            field = value.toUpperCase()
        }
}

class SpecificUser : User() {
    override var name: String = ""
}

fun main() {
    val specificUser = SpecificUser()
    specificUser.name = "João"
    println(specificUser.name) // Imprime: João

    val user = User()
    user.name = "João"
    println(user.name) // Imprime: JOÃO
}
```

Porém conseguimos acessar os acessores da propriedade herdada via keyword `super`:

```kotlin
open class User {
    open var name: String = ""
        get() = field.toUpperCase()
        set(value) {
            field = value.toUpperCase()
        }
}

class SpecificUser : User() {
    override var name: String
        get() = super.name
        set(value) {
            super.name = value
        }
}

fun main() {
    val specificUser = SpecificUser()
    specificUser.name = "João"
    println(specificUser.name) // Imprime: JOÃO

    val user = User()
    user.name = "João"
    println(user.name) // Imprime: JOÃO
}
```

## Inicialização tardia

No Kotlin é possível implementar um projeto inteiro sem receber um `NullPointerException` utilizando o null-safety que vem built in da linguagem, porém no mundo da JVM existem inúmeros frameworks de injeção de depedência, no qual a depedência muitas vezes é injetada via reflection sem a necessidade da class possuir um construtor, então alguns podem se perguntar, como que conseguimos utilizar esses frameworks aliado ao `null-safety` do Kotlin? 

Para esse tipo de situação, e para outros no quais precisamos inicializar uma propriedade "mais tarde", que o Kotlin disponibiliza a palavra reservada `lateinit`:

```kotlin
class Carrier {
    lateinit var name: String

    fun initName(name: String) {
        this.name = name
    }
}

fun main() {
    val carrier = Carrier()
    carrier.initName("Correios")
    print(carrier.name) // Imprime Correios
}
```

Porém há algumas restrições relacionadas ao uso do `lateinit`:

- O tipo da propriedade não pode ser primitivo;
- A propriedade deve ser declarada como `var`;
- A propriedade não pode fazer uso de um setter ou getter personalizado;
- Tentar acessar a propriedade antes que seja inicializada resultará em uma `kotlin.UnitializedPropertyAccessException`

## Propriedades delegadas

Há alguns certos tipos de códigos que é muito comum implementarmos manualmente, que seria muito interessante implementarmos apenas uma vez e sempre que for necessárioa reutiliza-ló, em relação a propriedades. Alguns exemplos são:

- Lazy properties; o valor é computado apenas no primeiro acesso;
- Observable properties: `listeners` são notificados toda vez que essa propriedade é alterada;
- Guardar as propriedades em um map, em vez de declarar um campo para cada campo;

Para cobrir esses casos acima (e outros), Kotlin suporta propriedade delegadas (delegated properties):

```kotlin
class Example {
    var name: String by Delegate()
}
```

A sintaxe para usar uma propriedade é:

```
val/var <property name>: <Type> by <expression>
```

A expressão após o `by` é o delegate. O `Delegate` não necessita implementar nenhum tipo de interface, porém é requerido que seja declarada os operadores `getValue()`, e `setValue()` para propriedades `vars`. Por exemplo:

```kotlin
class Delegate {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return "$thisRef, thank you for delegating '${property.name}' to me!"
    }
 
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        println("$value has been assigned to '${property.name}' in $thisRef.")
    }
}
```

O Kotlin por padrão já oferece alguns delegates. A seguir vou mostrar o `lazy`, `observable` e `map` delegates.

### Lazy

```kotlin
val lazyValue: String by lazy {
    println("computed!")
    "Hello"
}

fun main() {
    println(lazyValue) // Imprime "computed \n "Hello""
    println(lazyValue) // Imprime "Hello"
}
```

### Observable

```kotlin
class User {
    var name: String by Delegates.observable("<no name>") {
        prop, old, new ->
        println("$old -> $new")
    }
}

fun main() {
    val user = User()
    user.name = "first"
    user.name = "second"
}
```

### Guardando as Propriedades em um Map

```kotlin
class User(val map: Map<String, Any?>) {
    val name: String by map // Aqui utilizamos `val`, porém poderíamos utilizar `var` com um MutableMap
    val age: Int     by map
}

fun main() {
    val user = User(mapOf(
        "name" to "John Doe",
        "age"  to 25
    ))
    println(user.name) // Imprime "John Doe"
    println(user.age)  // Imprime 25
}
```