# Capítulo 7 - Null safety, reflexão e anotações - Parte 1

## Sumário

1. [Tipos nullable](#tipos-nullable)
2. [Cast inteligente](#cast-inteligente)
3. [Acesso seguro de null](#acesso-seguro-de-null)
4. [Operador force](#operador-force)
5. [Operador Elvis](#operador-elvis)
6. [Casting seguro](#casting-seguro)
7. [Opcionais](#opcionais)
    - [Criando e devolvendo um Optional](#criando-e-devolvendo-um-optional)
    - [Usando um Optional](#usando-um-optional)

## Tipos nullable

Kotlin permite o controle de valores nulos na utilização de tipos. Por padrão, uma variável não pode ter um valor nulo:

```kotlin
val myApp: String = null // Dispara erro de compilação 
```

O mesmo vale para `var`:

```kotlin
var myApp: String = "My Kotlin App"
myApp = null // Dispara erro de compilação
```

Para permitir uma variável conter um valor nulo, utilizamos o sufixo `?` ao definir o tipo de dado:

```kotlin
val myApp: String? = null // Permite a compilação
```

Ou, para os casos de variáveis alteráveis (`val`):

```kotlin
var myApp: String? = "My Kotlin App"
myApp = null // Dispara erro de compilação
```

O mesmo vale para parâmetros e retornos em funções:

```kotlin

fun printVal(value: String?) {
    println(value)
}

printVal("master") // Imprime "master"
printVal(null) // Imprime "null"

###################################################

fun printValNotNull(value: String) {
    println(value)
}

printValNotNull("master") // Imprime "master"
printValNotNull(null) // Dispara erro de compilação

###################################################

fun returnValue(): String? {
    return null
}

println(returnValue()) // Imprime "null"

###################################################

fun returnValueNotNull(): String {
    return null // Dispara erro de compilação
}

```

## Cast inteligente

Vimos como declaramos variáveis nuláveis, mas como utilizamos seus valores?

A forma mais simples é através de um simples `if`:

```kotlin
fun printLength(value: String?) {
    if (value != null) {
        println(value.length)
    }
}
```

O compilador Kotlin irá verificar se o valor da variável é diferente de nulo e, se sim, todas as operações disponíveis para o valor poderão ser utilizadas.

## Acesso seguro de null

Como vimos, podemos incluir condições `if` para identificar quando um valor é diferente de nulo. Porém, conforme temos operações aninhadas, o código tende a ficar bem poluído:

```kotlin
class User(val name: String, val profile: Profile?)
class Profile(val name: String, val group: Group?)
class Group(val name: String, val company: Company?)
class Company(val name: String)

fun getUserCompany(user: User?): String? {
    var companyName: String? = null
    if (user != null) {
        val profile = user.profile
        if (profile != null) {
            val group = profile.group
            if (group != null) {
                val company = group.company
                if (company != null) {
                    companyName = company.name
                }
            }
        }
    }
    return companyName
}
```

Para evitar a utilização de ifs aninhados Kotlin possui um operador para acesso seguro à valores que podem ser nulos, com uma notação semelhante à utilizada na definição dos tipos, com o ponto de interrogação: `?`. Este operador é utilizado antes do ponto ao chamar valores que podem ser nulos:

```kotlin
fun getUserCompany(user: User?): String? {
    return user?.profile?.group?.company?.name
}
```

## Operador force

Quando queremos ignorar as verificações de nulo do compilador e forçar um valor não nulo em um tipo nulável utilizamos o operador `!!`:

```kotlin
val myValue: String? = "Kotlin is so cool"
val myValueNotNull: String = myValue!!

println(myValueNotNull) // Imprimie "Kotlin is so cool"

val myValue: String? = null
val myValueNotNull: String = myValue!!

println(myValueNotNull) // Não imprime nada!
```

## Operador Elvis

Há vários casos em que precisamos de um valor padrão quando o valor é nulo. Podemos fazer um if/else para isso, porém não é tão visualmente bonito nem tampouco prático. Para facilitar o desenvolvimento, Kotlin possui uma construção semelhante à um `if` ternário, chamado de Operador Elvis: `?:`. O nome Elvis vem do fato de que quando observada de lado, o operador "parece" com o topete do cantor Elvis Presley. A utilização do Operador Elvis é a seguinte:

```kotlin
val myValue: String? = "Teste"
val myNotNullValue = myValue ?: "Valor Padrão"

println(myNotNullValue) // Imprime "Teste"

val myValue: String? = null
val myNotNullValue = myValue ?: "Valor Padrão"
println(myNotNullValue) // Imprime "Valor Padrão"
```

## Casting seguro

Quando utilizamos valores não determinados - em Kotlin, usando o tipo `Any`, é necessário fazermos um casting. Para isso, Kotlin possui o operador `as`. Quando tratamos de tipos nuláveis, utilizamos o operador seguro `as?`:

```kotlin
val myValue: Any = "Kotlin Yeah"
val safeStringValue: String? = myValue as? String
val safeIntValue: Int? = myValue as? Int

println(safeStringValue) // Imprime "Kotlin Yeah"
println(safeIntValue) // Imprime "null", pois não consegui converter pra Int
```

## Opcionais

A partir de Java 8 é possível utilizar o tipo de dados `Optional`. Este tipo é semelhante aos tipos `Maybe` de Haskell e `Option` de Scala. Kotlin, mesmo já possuindo os tipos nuláveis, permite a utilização do modelo do java também.

### Criando e devolvendo um Optional

Para encapsular valores em um `Optional` utilizamos o método estático `of`:

```kotlin
val optionalValue: Optional<String> = Optional.of("My Value")
```

Se vamos criar com um valor vazio, podemos utilizar o método estático `empty`:

```kotlin
val optionalValue: Optional<String> = Optional.empty()
```

Se vamos encapsular a partir de um valor possívelmente nulo, utilizamos o método `ofNullable`:

```kotlin
val myValue: String? = "Valor possívelmente nulo"
val optionalValue: Optional<String> = Optional.ofNullable(myValue)
```

O mesmo vale para retornos:

```kotlin
// Kotlin-style
fun getOptionalValue(): String? {
    [...]
}

// Java com Optional
fun getOptionalValue(): Optional<String> {
    [...]
}
```

### Usando um Optional

A utilização de valores do tipo `Optional` é semelhante aos valores nullable do Kotlin, porém difere quando vamos extrair o valor que foi encapsulado, através dos métodos `get` e `orElse`, o segundo, um equivalente ao Operador Elvis padrão do Kotlin (`?:`):

```kotlin
val optionalValue: Optional<String> = Optional.of("My Value")

println(optionalValue.get()) // Imprime "My Value"

###################################################

val optionalValue: Optional<String> = Optional.empty()

println(optionalValue.get()) // Não imprime nada

###################################################

val optionalValue: Optional<String> = Optional.empty()

println(optionalValue.orElse("Valor Padrão")) // Imprime "Valor Padrão"
```

Podemos também utilizar mapas e `flatMap` para extrair e transformar os valores de optionals:

```kotlin
fun optionalFunction(): Optional<String> {
    return Optional.of("Teste")
}

fun optionalLength(value: String): Optional<Int> {
    return Optional.of(value.length)
}

val value = optionalFunction().flatMap(::optionalLength).orElse(0)
println(value) // Imprime 5

###################################################

fun optionalFunction(): Optional<String> {
    return Optional.empty()
}

fun optionalLength(value: String): Optional<Int> {
    return Optional.of(value.length)
}

val value = optionalFunction().flatMap(::optionalLength).orElse(0)
println(value) // Imprime 0
```

Para tipos nullable de Kotlin não é necessário utilizar esta funcionalidade, pois podemos passar de forma transparente os valores.