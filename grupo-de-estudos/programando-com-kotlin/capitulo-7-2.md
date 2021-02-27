# Capítulo 7 - Null safety, reflexão e anotações - Parte 2

## Sumário

1. [Reflexão](#reflexão)
    - [KClass](#kclass)
    - [Instanciação usando reflexão](#instanciação-usando-reflexao)
2. [Construtores](#construtores)
    - [Instanciação com callBy](#instanciação-com-callby)
3. [Objetos e companheiros](#objetos-e-companheiros)
4. [Propriedades úteis de KClass](#propriedades-úteis-de-kclass)
5. [Funções e propriedades reflexivas](#funções-e-propriedades-reflexivas)
    - [Chamando uma função de modo reflexivo](#chamando-uma-função-de-modo-reflexivo)
    - [Variantes declaradas e não declaradas](#variantes-declaradas-e-não-declaradas)

## Reflexão

Reflexão ou reflection permite a inspeção de código em tempo de execução ao invés de fazer em tempo de compilação. Em Kotlin, não faz parte da stdlib, sendo necessário importar a dependência kotlin-reflect.

### KClass

KClass é o tipo principal usado para reflexão em Kotlin. Pode ser obtido através da instância, da classe ou pelo full qualified name (FQN).

```kotlin

// pela instância
val foo = "bar"
val kclass: KClass<String> = foo::class

// pela classe
val kclass2: KClass<String> = String::class

// FQN (utiliza api de reflexão do java)
val kclass3: KClass<Baz> = Class.forName("com.mercadolibre.Baz").kotlin

```

### Instanciação usando reflexão

É possível criar instâncias de tipos sem conhecimento em tempo de execução através do método `createInstance`. Importante observar que este método só serve para classes com construtores sem parâmetros ou com todos os parâmetros opcionais.

```kotlin

interface Job {
    fun execute(): Unit
}

val props = Properties()
props.load(Files.newInputStream(Paths.get("/path/to/jobs.props")))
val fqnClassNames = (props.getProperty("jobs") ?: "").split(',')

val jobs = fqnClassNames.map {
    Class.forName(it).kotlin.createInstance() as Job
}

jobs.forEach { it.execut() }

```

## Construtores

É possível inspecionar os construtores através da propriedade `constructors` e com isso recuperar informações como parâmetros e tipos dos parâmetros através da propriedade `parameters`. Com a referência a um construtor, é possível instanciar objetos utilizando `call` e `callBy`, onde o primeiro aceita uma lista de vargars que deve estar na ordem declarada do construtor e o segundo aceita um mapa de parâmetros.

### Instanciação com call
```kotlin
class Gate(siteId: String, facilityId: String, openOnSunday: Boolean) {
    constructor(siteId: String, facilityId: String) : this(siteId, facilityId, false)
}

fun createGate(siteId: String, facilityId: String, openOnSunday: Boolean) {
    val constructor = Gate::class.constructoris.find {
        it.parameters.size === 3
    } ?: throw RuntimeException("No compatible constructors")
    return constructor.call(siteId, facilityId, openOnSunday)
}

```

### Instanciação com callBy

Ao buscar informações de parameters de um construtor, recebemos uma lista de `KParameter`, contendo o nome, tipo do parâmetro, se é vargargs, inline ou opcional. Com essas definições, é possível mapear os parâmetros do construtor para instanciação do objeto.

```kotlin
interface Plugin {
    fun configure(): Unit
}

class OraclePlugin(conn: Connection) {
    fun configure(): Unit = ...
}

fun createPlugin(className: String): Plugin {
    val kclass = Class.forName(className).kotlin
    assert(kclass.constructors.size == 1, { "Forneça apenas plugins com um único construtor" })
    val constructor = kclass.constructors.first()
    assert(constructor.parameters.size == 1, { "Forneça apenas plugins com um parâmetro" })
    val parameter: KParameter = constructor.parameters.first()
    val paramMap = when(parameter.type.jvmErasure) {
        java.sql.Connection::class -> {
            val conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/mysqljdbc")
            mapOf(parameter to conn)
        }
        java.util.Properties::class -> {
            val props = Properties()
            mapOf(parameter to props)
        }
        java.nio.file.FileSystem::class -> {
            val fs = FileSystems.getDefault()
            mapOf(parameter to fs)
        }
        else -> throw RuntimeException("Tipo não suportado")
    }
    return constructor.callBy(paramMap) as Plugin
}
```

## Objetos e companheiros

É possível obter referências a objetos ou objetos `companion` através de reflexão. Também é possível obter referencia de uma instância de um objeto companheiro, como segue.

```kotlin
class Aircraft(name: String, manufacturer: String, capacity: Int){
    companion object {
        fun boeing(name: String, capacity: Int) = Aircraft(name, "Boeing", capacity)
    }
}

val kclass = Aircraft::class
// Obtendo referência ao objeto companion
val companionKClass = kclass.companionObject
// Obtendo referência a instância
val companionInst = kclass.companionObjectInstance as Aircraft.Companion // cast feito para Aircraft.Companion pois o mesmo não tem nome
companionInst.boeing("747", 999)
```

Se tivermos uam `KClass` representando um objeto singleton, é possível recuperar a instância através da propriedade `objectInstance`.

```kotlin
object PizzaOven {
    fun cook(flavor: String): Pizza = Pizza(flavor)
}

valkclass = PizzaOven::class
val oven: PizzaOven = kclass.objectInstance as PizzaOven
```

Também seria possível fazer através do full qualified name.

```kotlin
val fqn = "com.example.PizzaOven"
val kclass: KClass = Class.forName(fqn).kotlin
val oven: PizzaOven = kclass.objectInstance as PizzaOven
```

## Propriedades úteis de KClass

Uma KClass descreve completamente uma classe específica, incluindo parâmetros, superclasses, funções, construtores, anotações e propriedades. Algumas propriedades úteis:

- `typeParameters`
    Define os tipos do parâmetro que a classe declara.

    ```kotlin
    class Sandwich<F1, F2>()

    val types = Sandwich::class.typeParameters
    // imprimindo os nomes dos parâmetros e limites caso tenham sido definidos (do contrário, será Any)
    types.forEach {
        println("Tipo ${it.name} tem limite superior ${it.upperBounds}")
    }
    ```
- `superclasses` e `allSuperClasses`
    Retornam respectivamente a listagem das superclasses imediatas e todas as superclasses.

    ```kotlin
    class Foo: Serializable, Closeable
    val directSuperclasses = Foo::class.superclasses // Serializable, Closeable
    val allSuperClasses = Foo::class.allSuperclasses // Serializable, Closeable, Any
    ```

## Funções e propriedades reflexivas

A reflexão em kotlin não é limitada só em classes e objetos. É possível também acessar funções e propriedades. Para listar os nomes de funções definidas em uma classe, é possível utilizar a propriedade `memberFunctions`. Esta, porém, não lista funções de extensão. Para listar funções de extensão, é possível chamar `memberExtensionFunctions`. Existe ainda, uma terceira propriedade, `functions`, que traz o combinado das anteriores.

```kotlin
class Rocket() {
    var lat: Double = 0
    var long: Double = 0

    fun explode() {
        println("Boom")
    }

    fun setCourse(lat: Double, long: Double) {
        require(lat.isValid())
        require(long.isValid())
        this.lat = lat
        this.long = long
    }

    fun Double.isValid() = Math.abs(this) <= 180
}

fun <T: Any> printFunctions(KClass<T> kclass) {
    kclass.memberFunctions.forEach {
        println(it.name)
    }
    kclass.memberExtensionFunctions.forEach {
        println(it.name)
    }
    // Ou poderia utilizar apenas functions
}

fun main(args: Array<String>) {
	val kclass = Rocket::class
    printFunctions(kclass)
}

```

As funções são representadas por `KFunction`, que contém funções e propriedades que permitem saber se a função é inline, se é um operador, se é infixa, tipo de retorno, params, etc. Da mesma maneira que para funções, existem propriedades na api de reflexão para listar as propriedades da classe: `memberProperties` e `memberExtensionProperties`. Propriedades por sua vez são representadas por `KProperty`.

### Chamando uma função de modo reflexivo

É possível chamar funções específicas de uma classe através de uma instância de `KFunction` utilizando o método `call`, que receberá a instância da classe que deve ser utilizada na invocação (uma vez que a instância de `KFunction` não está vinculada a uma instância da classe em particular) e um vararg com os parâmetros.

```kotlin
val function = Rocket::class.functions.find { it.name == "explode" }
val rocket = Rocket()
function?.call(rocket) // neste caso não são necessários parâmetros além do objeto utilizado, pois explode não declara nenhum
```

### Variantes declaradas e não declaradas

- Variantes não declaradas incluem propriedades e funções declaradas no tipo referenciado pela `KClass` e classes-pai e interfaces. São os exemplos vistos até então.
- Variantes declaradas incluem apenas funções e propriedades declaradas no próprio tipo. Neste caso, são como os exemplos anteriores mas com `declared` como prefixo: `declaredMemberFunctions`, `declaredMemberExtensionFunctions`, etc.