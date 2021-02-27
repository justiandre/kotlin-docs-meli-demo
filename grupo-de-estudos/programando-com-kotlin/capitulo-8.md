# Capítulo 8 - Generics

## Sumário

1. [Funções Parametrizadas](#funcões-parametrizadas)
2. [Tipos Parametrizados](#tipos-parametrizados)
3. [Polimorfismo Restrito](#polimorfismo-restrito)
   - [Limites Superiores](#limites-superiores)
   - [Vários Limites](#vários-limites)
4. [Variância de Tipos](#variância-de-tipos)
   - [Invariância](#invariância)
   - [Covariância](#covariância)
   - [Tipo de Retorno Covariante](#tipo-de-retorno-covariante)
   - [Contravariância](#contravariância)
5. [Tipo Nothing](#tipo-nothing)
6. [Projeção de Tipos](#projeção-de-tipos)
7. [Apagamento de Tipo](#apagamento-de-tipo)
8. [Reificação de Tipos](#reificação-de-tipos)
9. [Limites de Tipos Recursivos](#limites-de-tipos-recursivos)
10. [Tipos de Dados Algébricos](#tipos-de-dados-algébricos)

## Funções Parametrizadas

A função a seguir, random(), devolve aleatoriamente um elemento dos passados a ela por parâmetro. Não precisamos saber quais são os tipos dos elementos, pois não os usaremos, só precisamos selecionar um para ser devolvido. Quando abstraimos um tipo desta maneira chamamos de **parâmetro de tipo**.

```kotlin
fun random(one: Any, two: Any, three: Any): Any
```

Desta maneira a função random() é genérica pois aceita qualquer instância, porém o tipo devolvido será sempre um Any o que nos obriga a fazer um cast de volta ao tipo original, ficando suscetível a erros e nada elegante.

Para melhorarmos essa função vamos reescrevê-la usando um parâmetro de tipo, permitindo que o compilador infira corretamente o tipo de retorno.

```kotlin
fun <T> random(one: T, two: T, three: T): T
val randomInt: Int = random(10, 20, 30)
```

Também podemos passar diferentes tipos para a função random(), nesse caso, o tipo inferido será o supertipo comum de nível mais baixo comum entre eles.

```kotlin
val any: Any = random("a", 1, false)
```

Podemos ter mais de um parâmetro de tipo em funções genéricas.

```kotlin
fun <K,V>put(key: k, value: V): Unit
```

## Tipos Parametrizados

Assim como as funções, tipos também podem ser parametrizados.

```kotlin
class Sequence<T>
```

Quando for declarado, o tipo deve ser instanciado com um tipo concreto.

```kotlin
val seq = Sequence<Boolean>()
```

## Polimorfismo Restrito

O polimorfismo restrito nos permite escrever funções genéricas para tipos que compartilham uma característica em comum, por exemplo, podemos escrever uma função que apenas aceite tipos que extendam o tipo Comparable.

### Limites Superiores

O limite do tipo superior restringe o parâmetro de tipo fornecido para função àqueles que são subclasses do limite. Assim, se declararmos um função que aceite apenas tipos que extendem Comparable, poderemos usar esta função com tipos como String e Int, mas não com Any, pois este não é uma subclasse de Comparable.

```kotlin
fun <T: Comparable<T>> min(first: T, second: T): T {
    val k = first.compareTo(second)
    return if (k <= 0) first else second
}

val a: Int = min(4,5)
val b: String = min ("a", "b")
```

> Sempre que um parâmetro de tipo for usado sem um limite superior explícito, o compilador usrá Any como limite superior

### Vários Limites

Podemos declarar uma função genérica contendo vários limites superiores, por exemplo, se refatorarmos a função min() para que além de Comparable obrigue os tipos a serem Serializable, teremos:

```kotlin
fun <T> min(first: T, second: T): T
where T: Comparable<T>,T : Serializable {
   val k = first.compareTo(second)
    return if (k <= 0) first else second
}
```

Observe que os limites superiores foram listados após a cláusula where, Comparable e Serializable.

Classes também podem definir vários limites superiores.

```kotlin
class MultipleBoundedClass<T> where T: Compareble<T>, T: Serialiable
```

## Variância de Tipo

O conceito de variância aparece quando pensamos em classes genéricas. A variância se relaciona a como a herança de uma classe genérica se coordena com a herança da classe secundária. Se tivermos uma classe B que herda de A o que podemos dizer sobre a relação de uma classe genérica Class\<A> e Class\<B>?

Dependendo do tipo de variância Class\<B> poder ser um subtipo de Class\<A>, um supertipo ou nenhum deles.

### Invariância

Parâmetros de tipo são invariantes pos padrão em Kotlin, não há nenhum relacionamento de subtipos entre os tipos. Tomando por exemplo:

```kotlin
class Fruit
class Apple: Fruit()
class Orange: Fruit()

fun foo(crate: Crate<Fruit>): Unit {
   crate.add(Apple()) // não compila
}
```

Isso equivale a dizer que Crate\<Apple> não é um subtipo nem um supertipo de Crate\<Orange>, idenpendentemente de haver uma relação entre Apple e Orange, pois ambas extendem Fruit.

### Covariância

Como por padrão os tipos são invariantes, se tentarmos algo como o exemplo abaixo termos um erro de compilação.

```kotlin
open class Fruit {
   fun isSafeToEat(): Boolean = ...
}

class Orange: Fruit()

fun isSafe(crate: Crate<Fruit>): Boolean = crate.elements.all(
   it.isSafeToEat()
)

val oranges = Crate(mutableListOf(Orange(), Orange()))
isSafe(oranges)
```

Mesmo que Orange extenda Fruit, Crate\<Orange> e Crate\<Fruit> são invariantes, o compilador não deixará que uma Crate\<Orange> ser usado em isSafe(), mesmo que em termos de lógica seria possível fazer esse uso.

Para que possamos usar Crate\<Orange> em isSafe(), precisamos dizer ao compilador que Crate\<Orange> e Crate\<Fruit> são covariantes, assim permitindo que a relação entre Fruit e Orange se extenda ao parâmetro de tipo em Crate. Para isso usamos a termo **out**:

```kotlin
class Crate<out T>(val elements: List<T>)
```

Assim dizemos ao compilador que qualquer subtipo de T é compatível, Crate\<Orange> e Crate\<Fruit> agora são covariantes.

Quando marcamos um parâmetro de tipo como covariante, o compilador restringe o uso do parâmetro de tipo somente para valores de retorno.

### Tipo de retorno covariante

Em Kotlin por padrão tipos de retornos são covariantes, se um subtipo quiser deveolver um tipo mais específico, será permitido.

```kotlin
open class Animal

class Sheep: Animal()

abstract class Farm {
   abstract fun get(): Animal
}

abstract class SheepFarm(): Farm(){
   abstract override fun get(): Sheep
}

val farm: Farm = SheepFarm()
val animal1 = farm.get()

val sheepFarm = SheepFarm()
val animal2 = sheepFarm.get()
```

### Contravariância

É o oposto de covariância, quando marcamos um tipo como contravariante invertemos o relacionamento entre os tipos, isso quer dizer que um Box\<String> se torna um supertipo de uma Box\<Any>.

Utilzamos a palavra reservada **in** para marcar um parâmetro de tipo como contravariante.

```kotlin
class Crate<in T>(val elements: List<T>)
```

No exemplo acima estamos dizendo ao compilador que uma Crate\<Orange> é um supertipo de uma Crate\<Fruit>

Quando marcamos um parâmetro de tipo como contravariante, o compilador restringe o uso do parâmetro de tipo somente para parâmetros de entrada.

## Tipo Nothing

Nothing é um tipo que se caracteriza por ser subtipo de todos os outros demais tipos. A idéia de nothing é comum na programação funcional. Um dos casos de uso é quando não queremos retornar nada em uma função, no exemplo abaixo a função error() usa Nothing como retorno e lança uma exceção.

```kotlin
inline fun error(message: Any): Nothing = throw IllegalStateException(message.toString())
```

Também pode ser usado para informar ao compilador que uma função jamais termina normalmente, como em um loop infinito.

Porém seu principal uso é como um parâmetro de tipo, podemos criar um tipo covariante que seja compatível com todos os supertipos.

```kotlin
class Box<out T>
Box<Nothing>()
```

## Projeção de Tipos

A projeção de tipo nos permite modificar uma função para que ela seja convariante ou contravariante. Ao usar a projeção de tipo iremos restringir o parâmetro de tipo para que seja possível torná-lo covariante ou contravariante. O compilador irá permitir que usemos apenas as funções em que o parâmetro de tipo esteja na posição permitida, ou seja, na projeção covariante podemos chamar apenas as funções que devolvam T, e na contravariante só poderemos chamar as funções que aceitem T.

Seu uso é muito útil quando queremos mudar o comportamento de uma classe definida por outra pessoa.

```kotlin
open class Fruit {
   fun isSafeToEat(): Boolean = ...
}

class Orange: Fruit()

fun isSafe(crate: Crate<Fruit>): Boolean = crate.elements.all(
   it.isSafeToEat()
)

val oranges = Crate(mutableListOf(Orange(), Orange()))
isSafe(oranges)
```

No exemplo acima teremos um erro em tempo de compilação, pois Crate\<Orange> e Crate\<Fruit> são invariantes. Para podermos usar Crate\<Orange> nesta função, podemos na definição da função alterar seu tipo para que seja covariante usando a palavra reservada **out**. Caso queiramos torná-la contravariante devemos usar a **in**.

```kotlin
fun isSafe(crate: Crate<out Fruit>): Boolean = crate.elements.all(
   it.isSafeToEat()
)
```

## Apagamento de Tipo

Como Kotlin foi projetada para rodar na JVM, está suscetível a suas restrições. Quando Java foi criado não havia genéricos, só foram inseridos no versão 1.5. Como esta versão deveria ser compatível com as anteriores foi usada uma técnica chamada apagamento(erasure). Apagamento é o processo onde o compilador remove parâmetros  de tipo durante a compilação. Em Java uma classe definida como List\<T> no código-fonte seria compilada para List ou List\<Object>. Como Kotlin tem a JVM também terá as seguintes limitações:

- Funções com os mesmos nomes e os mesmos parâmetros apagados entrarão em conflito.

```kotlin
fun print(list: List<String>)
fun print(list: List<Int>)
```

- Em tempo de execução, não é possível ver os parâmetros de tipo usados na instanciação de um objeto.

- Não é permitido testar se uma instância é do tipo T

- Não é permitido testar se uma instância é de um tipo parametrizado

- As classes que utilizam um parâmetro de tipo tem esse parâmetro substituido por um objeto ou pelo limite superior.

Há duas maneiras de contornar isso, uma é usar a anotação @JvmName para marcar as funções que tenham a mesma assinatura apagada com um nome diferente. A outra é usar uma forma limitada de reificação.

## Reificação de Tipos

Um tipo reificável é o nome dado a um tipo quando suas informações podem ser inspecionadas em tempo de execução. Um tipo não reificável é aquele sofreu o efeito de apagamento de tipo, de modo que suas informações foram perdidas em tempo de execução.

Para contornar esse problema, em Kotlin podemos usar as Reificação de Tipos, permitindo que as informações de tipos sejam mantidas em tempo de execução para funções inline.

Para usar esse recurso basta acrescentar a palavra reservada **reified** antes do parâmetro de tipo.

```kotlin
inline fun <reified T>runtimeType(): Unit {
   println("My type parameter is " + T::class::qualifiedName)
}
```

## Limites de Tipos Recursivos

Podemos adicionar como limite superior de um parâmetro outro parâmetro com um limite superior, chamamos isto de limite de tipo recursivo.

```kotlin
interface Account<E: Account<E>>: Comparable<E> {
   ...
}
```

## Tipos de Dados Algébricos

Tipos de dados algébricos é um conceito de programação funcional, refere-se ao fato de a álgebra ser definida como um conjunto de itens a as operações permitidas nesses itens. Por exemplo, em matemática, o operador + é definido em inteiros e devolve a soma deles. 

Assim, uma álgebra para um tipo define operações ou funções neste tipo.

Podemos definir tipos de dados algébricos em Kotlin usando a palavra reservada **sealed**.

```kotlin
sealed class DeliveryStatus {
  
  object Preparing : DeliveryStatus()
  
  data class Dispatching(
    val trackingNumber: String
  ) : DeliveryStatus()
  
  object Delivered : DeliveryStatus()
}
```

Definimos uma sealed class, isso significa que nenhuma outra classe fora do arquivo kotlin onde esta classe está definida pode extendê-la, assim o compilador conhece cada possível subtipo de DeliveryStatus.

Agora podemos usar DeliveryStatus desta maneira:

```kotlin
fun showDeliveryStatus(status: DeliveryStatus) {
  return when (status) {
    is Preparing -> showPreparing()
    is Dispatched -> showDispatched(it.trackingNumber)
  }
}
```