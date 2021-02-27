# Capítulo 6

## Sumário

1. [Inicializações preguiçosas](#inicializações-preguiçosas)
2. [Lateinit vs lazy](#Lateinit-vs-lazy)
3. [Observáveis](#Observáveis)
4. [Delegação de uma propriedade diferente de null](#Delegação-de-uma-propriedade-diferente-de-null)

## Inicializações preguiçosas

Às vezes, precisamos construir objetos que tenham um processo de inicialização complicado. Além disso, muitas vezes não podemos ter certeza de que o objeto pelo qual pagamos o custo da inicialização no início de nosso programa será usado em nosso programa.

Pensando nisso a equipe do Kotlin desenvolveu o conceito de "inicialização preguiçosa" para impedir a inicialização desnecessária de objetos.

```kotlin
class BookManager {
    fun loadBooks(person: Person): List<String> {
        println("Load books for ${person.name}")
        return listOf("Book1", "Book2")
    }
}

data class Person(val name: String) {
    val books by lazy { BookManager().loadBooks(this) }
}

fun main(args: Array<String>) {
    val person = Person("Paulo Gustavo")
    println(person.name) // ainda não carregou os livros
    println(person.books) // carregou os livros
    println(person.books) // pegou os livros já carregados
}
```

Por default, a inicialização é thread-safe, o sistema usa locks para garantir que uma única thread possa inicializar a instância de [Lazy].

Podemos também optar por mudar a implementação default utilizando a assinatura:

```kotlin
fun <T> lazy(mode: LazyThreadSafetyMode, initializer: () -> T): Lazy<T>
```

Além do `SYNCHRONIZED` (default), temos também:

- PUBLICATION: significa que a função de inicialização pode ser chamada várias vezes em acessos concorrentes para um valor não inicializado da instância de [Lazy], mas somente o primeiro valor devolvido será usado como valor dessa instância.
- NONE: significa que nenhum lock é usado para sincronizar o acesso ao valor da instância de [Lazy]; se a instância for acessada de várias threads, seu comportamento será indefinido. É recomendado utilizar este apenas em casos onde o desempenho é crucial e a instância não for inicializada por N threads

## Lateinit vs lazy

À primeira vista, um var lateinit e lazy {...} parecem muito semelhatens. Porém há diferenças significativas entre ambos:

- A delegação com `lazy` {...} só pode ser usada para propriedades val; `lateinit` só pode ser usada para propriedades var.
- Uma propriedade `var lateinit` não pode ser compilada em um campo final; portanto, não será possível ter imutabilidade.
- `lateinit` não precisa ser inicializado no momento de sua declaração. Porém se tentarmos usar sem inicializar receberemos a seguinte exception: `Caused by: kotlin.UninitializedPropertyAccessException : lateinit property someText has not been initialized`
- `lateinit` não funciona com tipos primitivos
- `lateinit` só pode ser utilizado com propriedades que não possuem um getter/setter customizados.
- `lateinit` precisa que o usuário inicialize de forma correta em ambientes multi-threaded. Inicializações `by lazy {...}` por default são thread-safe.

## Observáveis

Uma propriedade `observable` é basicamente uma propriedade regular, com um valor inicial e uma função de retorno de chamada que é chamada sempre que seu valor é alterado. **Detalhe importante**: a função é chamada após a atribuição ter sido realizada.

Podemos criar um observable utilizando a seguinte fun:

```kotin
fun <T> observable(initialValue: T, crossinline OnChange: (property: KProperty<*>, oldValue: T, newValue: T) -> Unit): ReadWriteProperty<Any?, T>
```

```kotlin
class QualquerCoisa {
    var value: Int by Delegates.observable(0) {
        prop, old, newVal -> onValueChanged()
    }

    private fun onValueChange() {
        println("value has changed:$value")
    }
}
```

Podemos utilizar outra implementação observável que nos permite rejeitar o novo valor dependendo do contexto:

```kotlin
class ApenasPositivo {
    var value: Int by Delegates.vetoable(0) {prop, old, newVal -> newVal >= 0 }
}
```

## Delegação de uma propriedade diferente de null

Sabemos que o kotlin não nos permite declarar uma váriavel que não pode aceitar null sem inicializar ela ou coloca-la como `lateinit`:

```kotlin
class Abc {

    var test: String // Property must be initialized or be abstract
}
```

Também não queremos inicializar ela com um valor default. O que fazer então?

Podemos utilizar o notNul delegate, que nos permite declarar a váriavel dizendo ao compilador que ela não poderá receber null e não precisamos inicializar no momento da criação.

```kotlin

class NonNullProp {

    var value: String by Delegates.notNull<String>()
    var value2: String by Delegates.notNull<String>()
}

val n = NonNullProp()
n.value = "Paulo"

println(n.value) // "paulo"
printlnt(n.value2) // Error: Property test should be initialized before get.

n.value2 = null //não compila

```

## Propriedades ou métodos

Quando utilizar uma propriedade e quando utilizar um método? Siga as seguintes diretrizes para decidir o que é apropriado:

- Um método de classe representa uma ação.
- Uma propriedade representa um dado.
- Evite ter um código complexo no corpo de um getter.
- Ler uma propriedade não deverá causar nenhum efeito colateral; evite até mesmo lançar excpetions a partir de um getter
- Se o resultado for uma cópia do estado interno do seu objeto, crie um método, semelhante ao que temos em java como clone.
- Se chamar o código da propriedade produzir resultados diferente toda vez, utilize um método

