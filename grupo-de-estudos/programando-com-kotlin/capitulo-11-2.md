# Capítulo 11 - Testes em Kotlin

## Introdução

- [Capítulo 11 - Testes em Kotlin](#capítulo-11---testes-em-kotlin)
    - [Config do projeto](#config-do-projeto)
    - [Testes de propriedade](#testes-de-propriedade)
        - [Especificando um gerador](#especificando-um-gerador)
    - [Testes orientados a tabela](#testes-orientados-a-tabela)
        - [Testando um código não determinístico](#testando-um-código-não-determinístico)
    - [Tags, condições e config](#tags,-condições-e-config)
        - [Config](#config)
        - [Condições](#condições)
        - [Tags](#tags)
        - [Recursos](#recursos)
     -  [Resumo](#resumo)



## Config do projeto
Às vezes, talvez, você queira executar algum código antes que qualquer teste seja executado ou depois que todos os testes tiverem sido concluídos. Isso pode ser feito com o uso da classe abstrata
**ProjectConfig**. Para usá-la, basta criar um objeto que estenderá essa classe abstrata e garantir que ela esteja no classpath. Então, KotlinTest a encontrará automaticamente e a chamará:

```kotlin
object MyProjectConfig : ProjectConfig() {

  var server: HttpServer? = null

  override fun beforeAll() {
    val addr = InetSocketAddress(8080)
    val server = HttpServer.create(addr, 0)
    server.executor = Executors.newCachedThreadPool()
    server.start()
    println("Server is listening on port 8080")
  }

  override fun afterAll() {
    server!!.stop(0)
  }
}
```

## Testes de propriedade
Um método alternativo de teste, os quais visam testar uma única propriedade de uma função cada vez. Considerando que vamos testar se uma propriedade se mantém para diversos valores de entrada,
segue daí que desejaremos usar o máximo possível de valores diferentes. Por esse motivo, testes baseados em propriedades muitas vezes são associados à geração automática de valores. Em kotlinTest, esses
valores são fornecidos por meio de recursos devidamente chamados de geradores (generators).

Para usar um gerador para testes de propriedade, devemos utilizar uma chamada em estilo de inspetor para a qual passaremos outra função de teste. Essa função de teste deve ter os tipos de parâmetros especificados,
pois o compilador não será capaz de inferi-los. A mesma função deve devolver um booleano e esse valor sinalizará se a propriedade se mantém para os valores de entrada.

```kotlin
 "String.size" {
      forAll({ a: String, b: String ->
        (a + b).length == a.length + b.length
      })
    }
```

### Especificando um gerador
KotlinTest tem vários geradores embutidos, e às vezes, porém, não queremos deixar a cargo dele escolher qual é o melhor para se utilizar nos testes, mas podemos dizer qual ele deve utilizar:

```kotlin
"squareRoot" {
      forAll(Gen.int(), { k ->
        val square = squareRoot(k)
        square * square == k
      })
    }
```

Às vezes, queremos especificar totalmente nossos próprios intervalos ou valores de entrada, quando os geradores embutidos não são suficientes.
Por exemplo, podemos criar um gerador que devolva um elemento aleatório de uma coleção cada vez que for chamado:

```kotlin
"using a collection picker" {
      val values = listOf("pick", "one", "of", "these")
      forAll(Gen.oneOf(values), { element ->
        // test logic
        values.contains(element)
      })
    }
```

De modo alternativo, se os geradores auxiliares embutidos não forem suficiente, é sempre possível criar nossos próprios geradores do zero. Tudo que precisamos fazer é estender a interface **Generator<T>**,
em que **T** é o tipo devolvido, e implementar a função **generate**

```kotlin
fun evenInts() = object : Gen<Int> {
  override fun generate(): Int {
    while (true) {
      val next = Random.default.nextInt()
      if (next % 2 == 0)
        return next
    }
  }
}

forAll(evenInts(), { k -> 
    val square = squareRoot(k)
    square * square == k
})
```

## Testes orientados a tabela
A ideia por trás dos testes orientados a tabela é semelhante aos testes baseados em propriedade. Nesse caso, a diferença é que vez de os geradores fornecerem valores aleatórios, o conjunto
de valores de entrada é manualmente especificado.

```kotlin
class TableExample : StringSpec() {
    init {

        "example of booleans" {

            val table = table(
                headers("a", "b", "c"),
                row(true, true, false),
                row(true, false, true),
                row(true, false, false)
            )

            forAll(table) { a, b, c ->
                a shouldBe true
                if (b)
                    c shouldBe true
            }
        }
    }
}
```

### Testando um código não determinístico
Ao testar um código não determinístico - como futuros(furures), atores(actors) ou repositórios de dados consistentes, é conveniente ser capaz de garantir que, em algum ponto, o teste passe, mesmo que tenha falhado inicialmente.
O que realmente queremos é uma forma de fazer um teste esperar enquanto uma condição é **false** e, então, terminar o teste assim que ela mudar para **true**. KotlinTest executa essa truque introduzindo
um recurso interessante chamando **Eventually**, que foi inspirado em um funcionalidade semelhante de ScalaTest.
Para usar o recurso **eventually**, devemos inicialmente estender a interface **Eventually**, que provê a funcionalidade:

```kotlin
class EventuallyExample : WordSpec(), Eventually
```

Então devemos chamar a função eventually, passando uma duração em primeiro lugar e uma função literal para ser executada em segundo. A função será executada repetidamente até que a duração expire ou a função literal tenha sido concluída com sucesso.

```kotlin
class FileCreateWithEventually : ShouldSpec(), Eventually {

  init {
    should("create file") {
      eventually(60.seconds) {
        createFile("/home/davidcopperfield.txt")
      }
    }
  }
}
```

## Tags, condições e config
Nesta seção, será abortada as diversas opções de configuração que podem ser usadas para controlar como os testes são executados e quais são executados.

### Config
Cada caso de teste disponibiliza uma função config, que pode ser usada para definir configurações específicas para esse teste, como threading, tags e se o teste está ativo ou não.

```kotlin
should("run multiple times") {
      // test logic
    }.config(invocations = 5, threads = 4)
```

### Condições
As condições são uma maneira simples de ativar ou desativar um teste com base na avaliação em tempo de execução. O bloco de configuração contém uma propriedade enabled que é chamada antes da execução de um teste,
a fim de verificar se esse teste deverá ser executado ou ignorado.

```kotlin
 should("be disabled") {
      // test logic
    }.config(enabled = false)
```

Podemos estender isso para que uma consulta seja feita em tempo de execução, definindo uma função no lugar de um valor fixo.

```kotlin
fun isMultiCore(): Boolean = Runtime.getRuntime().availableProcessors() > 1

should("only run on multicore machines") {
  // test logic
}.config(enabled = isMultiCore())
```
### Tags
De modo semelhante às condições, as tags permitem ter uma forma de agrupar testes, de modo que possam ser ativados ou desativados em tempo de execução.

```kotlin
object ElasticSearch : Tag()
object Windows : Tag()

should("this test is tagged") {
      // test logic
}.config(tags = setOf(ElasticSearch, Windows))
```

Se estivéssemos usando Gradle, poderíamos executar somente esses testes usando o comando a seguir:

```bash
gradle test -DincludeTags=Windows,ElasticSearch
```

Se quisermos excluir qualquer teste que exija Windows porque queremos executar um job construído para Linux, utilizaremos o seguinte comando:

```bash
gradle test -DexcludeTags=Windows
```
### Recursos
Um último recurso interessante de KotlinTest é a capacidade de fechar recuros automaticamente. depois que todos os testes forem concluídos.

```kotlin
class ResourceExample : StringSpec() {

  val input = autoClose(javaClass.getResourceAsStream("/orders.csv"))

  init {
    "your test case" {
      // use input stream here
    }
  }
}
```

O uso é simples. Basta encapsular o recurso, por exemplo, o stream de entrada, com a função autoClose. Independentemente do resultado dos testes, os recursos serão devidamente encerrados.




