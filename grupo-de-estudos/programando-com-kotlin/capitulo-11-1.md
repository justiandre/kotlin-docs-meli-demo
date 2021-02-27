# Capítulo 11 - Testes em Kotlin

## Introdução

- [Capítulo 11 - Testes em Kotlin](#capítulo-11---testes-em-kotlin)
    - [Introdução](#Introdução)
    - [Selecinando uma spec](#selecinando-uma-spec)
    - [Matchers](#matchers)
        - [Matchers de string](#matchers-de-string)
        - [Matchers de coleção](#matchers-de-coleção)
        - [Matchers de ponto flutuante](#matchers-de-ponto-flutuante)
        - [Esperando exceções](#esperando-exceções)
        - [Combinando matchers](#combinando-matchers)
        - [Matchers personalizados](#matchers-personalizado)
    - [Inspetores](#inspetores)
    - [Interceptadores](#interceptadores)
        - [Interceptador de caso de teste](#interceptador-de-caso-de-teste)
        - [Interceptador de spec](#interceptador-de-spec)


## Introdução
    Escrever seu primeiro teste com KotlinTest é bem simples, Inicialmente, a dependência de KotlinTest deverá ser adiciona à sua construção.
    A maneira mais fácil de fazer isso, se você estiver usando Gradle ou Maven, é procurar a io.kotest na central do Maven - basta acessar http://search.maven.org e obter a versão mais recente.
    será necessário acresncetar essa informação em sua contrução com Grandle usando o seguinte:
    
<details open>
<summary>Groovy (build.gradle)</summary>

```groovy
test {
  useJUnitPlatform()
}

dependencies {
  testImplementation 'io.kotest:kotest-runner-junit5-jvm:<version>' // for kotest framework
  testImplementation 'io.kotest:kotest-assertions-core-jvm:<version>' // for kotest core jvm assertions
  testImplementation 'io.kotest:kotest-property-jvm:<version>' // for kotest property test
}
```
</details>

De modo alternativo, para Maven, utilize o código a seguir:

#### Maven

Para o maven, você deve configurar o plugin surefire para junit tests.
    
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
</plugin>
```

E adicione o runner Kotest JUnit5 à sua compilação para usar o framework product.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-runner-junit5-jvm</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

Para usar as asserções jvm do kotest core, adicione a seguinte configuração.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-assertions-core-jvm</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

E para usar o kotest property testing, adicione a seguinte configuração.

```xml
<dependency>
    <groupId>io.kotest</groupId>
    <artifactId>kotest-property-jvm</artifactId>
    <version>{version}</version>
    <scope>test</scope>
</dependency>
```

Nos exemplos utilizei a versão 4.1.2. 

Em seguida, crie a pasta com o código-fonte dos testes: geralmente será src/test/kotlin, caso ainda não exista. 
Vamos escrever um teste de unidade para a classe String da biblioteca-padrão. Crie um arquivo chamado StringTest.kt.
Nele, crie uma única classe chamada StringTest, que deve estender FunSpec. O conteúdo do arquivo deverá ter um aspecto semelhante a este:

```kotlin
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class FunSpecTest : FunSpec() {
    init {
        test("String.startswith should be true for a prefix"){
            "helloworld".startsWith("hello") shouldBe true
        }
    }
}
```

Para escrever um teste de unidade, chame uma função de nome test, que aceita dois parâmetros.
O primeiro é uma descrição do teste, enquanto o segundo é uma função literal contendo o corpo do teste.
A descrição do teste, isto é, seu nome, aparecerá na saída, de modo que saberemos quais testes falharam e quais passaram.

Em nosso primeiro teste, verificaremos se a função startWith definida em String deve devolver true para prefixos válidos.
Cada teste individual é simplesmente inserido em um bloco init{} no corpo da classe.

## Selecinando uma spec

No primeiro teste que escrevemos, estendemos uma classe chamada FunSpec, que é apenas um exemplo do que KotlinTest chama de spec.
Uma spec, ou estido, é simplesmente a maneira pela qual os teste são dispostos nos arquivos de classe.

| Test Style | Inspired By |
| --- | --- |
| [Fun Spec](#fun-spec) | ScalaTest |
| [Describe Spec](#describe-spec) | Javascript frameworks and RSpec |
| [Should Spec](#should-spec) | A Kotest original |
| [String Spec](#string-spec) | A Kotest original |
| [Behavior Spec](#behavior-spec) | BDD frameworks |
| [Free Spec](#free-spec) | ScalaTest |
| [Word Spec](#word-spec) | ScalaTest |
| [Feature Spec](#feature-spec) | Cucumber |
| [Expect Spec](#expect-spec) | A Kotest original |
| [Annotation Spec](#annotation-spec) | JUnit |


Exemplos de Specs do livro na versão utilizada.

WordSpec que utiliza a palavra should:
```kotlin
import io.kotest.core.spec.style.WordSpec
import io.kotest.matchers.shouldBe

class WordSpecTest : WordSpec() {
    init {
        "String.length" should {
            "return the length of the string" {
                "hello".length shouldBe 5
                "".length shouldBe 0
            }
        }
    }
}
```

ShouldSpec é quase igual à classe FunSpec. 
Porém, há uma diferença: o nome da função é should em vez de test.

```kotlin
class ShouldSpecTest : ShouldSpec() {
    init {
        context("String.length") {
            should("return the length of the string") {
                "hello".length shouldBe 5
            }
            
            should("support empty strings") {
                "".length shouldBe 0
            }
        }
    }
}
```
*Os testes também podem ser aninhados em um ou mais blocos de contexto.
Esses testes serão agrupados pelo seu namespace-pai, isso significa que serão agrupados em uma hierarquia.

BehaviorSpec inspirado no estilo de testes de BFF (behavior-driven development) com Given, When e Then
```kotlin
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.util.*

class BehaviorSpecTest : BehaviorSpec() {
    init {
        Given("a stack") {
            val stack = Stack<String>()
            When("an item is pushed") {
                stack.push("kotlin")
                Then("the stack should not be empty") {
                    stack.isEmpty() shouldBe false
                }
            }

            When("the stack is popped") {
                stack.pop()
                Then("it should be empty") {
                    stack.isEmpty() shouldBe true
                }
            }
        }
    }
}
```

FeatureSpec utiliza as palavras reservadas feature e scenario:

```kotlin
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import java.util.*

class FeatureSpecTest : FeatureSpec() {
    init {
        feature("a stack") {
            val stack = Stack<String>()
            scenario("should be non-empty when an item is pushed") {
                stack.push("kotlin")
                stack.isEmpty() shouldBe false
            }

            scenario("should be empty when the item is popped") {
                stack.pop()

                stack.isEmpty() shouldBe true
            }
        }
    }
}
```

Se você estiver migrando da JUnit, AnnotationSpec é uma especificação que usa anotações como a JUnit 4/5. Basta adicionar a anotação @Test a qualquer função definida na classe spec.
Você também pode adicionar anotações para executar algo antes dos testes / especificações e depois dos testes / especificações, da mesma forma que o JUnit

Embora essa especificação não ofereça nenhuma vantagem sobre o uso do próprio JUnit, ela permite migrar mais rapidamente, pois normalmente você só precisa ajustar as importações.

@BeforeAll / @BeforeClass
@BeforeEach / @Before
@AfterAll / @AfterClass
@AfterEach / @After
Se você deseja ignorar um teste, use @Ignore.

```kotlin
import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

class AnnotationSpecTest : AnnotationSpec() {

    @BeforeEach
    fun beforeTest() {
        println("Before each test")
    }

    @Test
    fun test1() {
        1 shouldBe 1
    }

    @Test
    fun test2() {
        3 shouldBe 3
    }
}
```

## Matchers
### Matchers de string
Um dos conjuntos mais comuns de matchers, sem dúvidas, são os matcher de String. 
A tabela a seguir lista os matchers mais comuns de string: 

```kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.*

class StringMatchersTest : StringSpec({
    "should return true when string start with the prefix" {
        "hello world" should startWith("he")
    }

    "should return true when string contains the substring" {
        "hello" should include("ell")
    }

    "should return true when string end with the suffix" {
        "hello" should endWith("ello")
    }

    "should return true when string has five length" {
        "hello" should haveLength(5)
    }

    "should return true when there is a match with a regex" {
        "hello" should match("he...")
    }
})
```

### Matchers de coleção
O conjunto mais util de matchers atua em coleções, incluindo listas, conjuntos, mapas, e...

```kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.*
import io.kotest.matchers.collections.contain
import io.kotest.matchers.maps.contain
import io.kotest.matchers.maps.haveKey
import io.kotest.matchers.maps.haveValue
import io.kotest.matchers.should
import io.kotest.matchers.shouldBe

class CollectionsMatchersTest : StringSpec({
    "CollectionContain" {
        val col = listOf("one", "two", "three")
        col should contain("three")
    }

    "HaveSize" {
        val col = listOf("one", "two", "three")
        col should haveSize(3)
    }

    "Sorted" {
        val col = listOf(1, 2, 3)
        col shouldBe sorted<Int>()
    }

    "SingleElement" {
        val col = listOf(3)
        col shouldBe singleElement(3)
    }

    "CollectionContainsAll" {
        val col = listOf("one", "two", "three")
        col should containAll(listOf("one", "three"))
    }

    "BeEmpty" {
        val col = listOf<String>()
        col should beEmpty()
    }

    "HaveKey" {
        val mapa = mapOf(1 to "x", 2 to "y", 3 to "w")
        mapa should haveKey(3)
    }

    "HaveValue" {
        val mapa = mapOf(1 to "x", 2 to "y", 3 to "w")
        mapa should haveValue("w")
    }

    "MapContain" {
        val mapa = mapOf(1 to "x", 2 to "y", 3 to "w")
        mapa should contain(3, "w")
    }
})
```

### Matchers de ponto flutuante

Ao testar igualdade entre doubles, não devemos usar uma igualdade simples. 
Isso se deve à natureza imprecisa da armazenagem de alguns valores, principalmente de decimais que se repetem em base 2 (assim como um terço não pode ser representado exatamente na base 10)

A maneira mais segura e correta de fazer comparações entre número de ponto flutuante é verificar se a diferença entre dois números está abaixo de algum valor. O valor escolhido é a tolerância, que deverá ser baixa o suficiente a fim de satisfazer seus critérios para considerar os número iguais.
KotlinTest tem suporte pronto para isso:

```kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.plusOrMinus
import io.kotest.matchers.shouldBe

class FloatingPointTest : StringSpec({
    "FloatingPoint"{
        val input = 10.0
        input shouldBe 10.0
    }

    "FloatingPoint with delta"{
        val input = 10.001
        input shouldBe (10.0 plusOrMinus 0.001)
    }

    "FloatingPoint with delta - Second test"{
        val input = 9.999
        input shouldBe (10.0 plusOrMinus 0.001)
    }
})
```
 
### Esperando exceções

```kotlin
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.should
import io.kotest.matchers.string.endWith
import kotlin.math.sqrt

class ExpectingExceptionsTest : StringSpec() {
    init {
        "ExpectedThrowException"{
            shouldThrow<IllegalArgumentException> {
                squareRoot(-1)
            }
        }

        "ExpectedThrowException check message"{
            val exception = shouldThrow<IllegalArgumentException> {
                squareRoot(-1)
            }

            exception.message should endWith("not pass")
        }
    }

    private fun squareRoot(k: Int): Int {
        if(k < 0){
            throw IllegalArgumentException("This should not pass")
        }

        return sqrt(k.toDouble()).toInt()
    }
}
```

### Combinando matchers

Utilização dos operadores lógicos booleanos AND e OR.

```kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.string.haveLength
import io.kotest.matchers.string.include
import io.kotest.matchers.should

class CombiningTest : StringSpec({

    "Combining Matcher with AND" {
        "Hello world" should (haveLength(11) and include("llo wo"))
    }

    "Combining Matcher with OR" {
        "Hello world" should (haveLength(11) or include("bye")) 
    }

})
```

### Matchers personalizados

```kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.shouldBe
import java.lang.IllegalArgumentException


class AvengersTest : StringSpec() {
    init {
        "testing isLive Custom Test"{
            "Spider-Man" shouldBe isLive(2020)
        }

    }

    private fun isLive(year: Int) = object : Matcher<String> {
        private val avengers = setOf("Captain America", "Thor Odinson", "Hawkeye", "War Machine",
                "Scarlet Witch", "Falcon", "Spider-Man", "Ant-Man", "Nebula", "Rocket")

        override fun test(value: String): MatcherResult {
            if (year != 2020){
                throw IllegalArgumentException()
            }

            return MatcherResult(avengers.contains(value), "Deceased", "Live - Test passed")
        }

    }
}
```

## Inspetores

Os Inspetores (inspetors) de KotlinTest são uma maneira fácil de testar o conteúdo de coleções.
As vezes, você quer garantir que apenas alguns elementos de uma coleção devem passar por uma asserção.
Em  outras ocasições, talvez você queira que nenhum elemento passe pela asserção, ou que apenas um passe, ou dois, eassim por diante.
É claro que podemos fazer isso por conta própria, simplesmente iterando pela coleção e mantendo o controle de quantos itens passaram pelas asserções, porém os inspetores podem fazer isso para nós.


```kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.inspectors.forAll
import io.kotest.inspectors.forAtLeastOne
import io.kotest.inspectors.forOne
import io.kotest.inspectors.forSome
import io.kotest.matchers.should
import io.kotest.matchers.string.endWith
import io.kotest.matchers.string.startWith

class InspectorsTest : StringSpec({
    val kings = listOf("Stephen I", "Henry I", "Henry II", "Henry III", "William I", "William II")

    "all kings should have a regal number" {
        kings.forAll {
            it should endWith("I")
        }
    }

    //forOne Somente um caso deve passar
    "only one king has the name Stephen" {
        kings.forOne {
            it should startWith("Stephen")
        }
    }

    //forSome Verifica se pelo menos um elemento não todos eles, passou no teste.
    "some kings have regal number II" {
        kings.forSome {
            it should endWith("II")
        }
    }

    //forAtLeastOne Verifica se um elemento passou no test
    "at least one King has the name Henry" {
        kings.forAtLeastOne {
            it should startWith("Henry")
        }
    }
})
```

## Interceptadores (utiliza a versão do livro, diferente da kotest 4.1.2)

Quando vamos além do escopo dos testes de unidade independentes e entramos nos testes que exigem recursos, com freguência precisamos configurar esses recursos antes de um teste ou liberalos posteriormente.
Por exemplo, como uma conexão com um banco de dados talvez precise ser iniciada para ser utilizada em teste e, então deve ser devidamente encerrado depois que o teste terminar.
Podemos fazer isso manualmente em um teste, mas se tivermos uma suite de testes, isso se torna trabalhoso.

Não seria melhor se pudéssemos definir uma função uma só vez e, então faze-lá executar antes e depois de cada teste ou de cada suite de testes?
Essa funcionalidade está presente em KotlinTest e se chama interceptadores (interceptors).
Cada tipo de interceptador é definido para que seja executado antes e depois de o código ser testado. 
Vamos discutir os diferentes tipos de interceptadores.

### Interceptador de caso de teste

No exemplo a seguir, definiremos um interceptador que exibirá o tempo gasto para executar um teste:
```kotlin
val myinterceptor: (TestCaseContext, () -> Unit) -> Unit = {
     context, test ->
     val start = System.currentTimeMillis()
     test()
     val end = System.currentTimeMillis()
     val duration = end - start
     println("This test took $duration millis")
}
```
Observe como o teste é chamado dentro do interceptador. O próximo passo é adicionar o interceptador em qualquer teste para o qual queiramos calcular o tempo gasto:

```kotlin
"this test has an interceptor" {
 // test logic here
}.config(interceptors = listOf(myinterceptor))
 "so does this test" {
 // test logic here
}.config(interceptors = listOf(myinterceptor))
```

Observe que cada caso de teste aceita uma lista de interceptadores. 
Embora, nesse caso, tenhamos utilizado apenas um, podemos adicionar um número arbitrário deles.


### Interceptador de spec
O interceptador de spec é muito parecido com o interceptador de caso de teste; a única diferença é que o contexto do caso de teste é substituido pelo contexto da spec.
Assim como o interceptador anterior, devemos chamar a função fornecida; caso contrário, toda a spec será ignorada. 
Esse recurso, então, oferece a você a capacidade de utilizar uma lógica personalizada para determinar se um spec será executada ou não:

```kotlin
val mySpecInterceptor: (Spec, () -> Unit) -> Unit = {
    spec, tests ->
    val start = System.currentTimeMillis()
    tests()
    val end = System.currentTimeMillis()
    val duration = end - start
    println("The spec took $duration millis")
}
```

Nesse exemplo, implementamos novamente o nosso interceptador de tempo, dessa vez para calcular o tempo para toda a spec.
Para usá-los, sobreescreva uma propriedade chamada specInterceptors, fornecendo uma lista de interceptadores.

```kotlin
override val specInterceptors: List<(Spec, () -> Unit) -> Unit> =
listOf(mySpecInterceptor)
```

É tudo muito semelhante ao exemplo com caso de teste.

## Listeners (extra livro, utilizando kotest 4.1.2)

É um requisito comum executar código antes ou depois de testes ou especificações. 
Por exemplo, para iniciar (ou redefinir) e encerrar um banco de dados.

TestListener
------------

A interface principal é _TestListener_. As seções a seguir descrevem os retornos de callbacks disponíveis nesta interface.

|Callback|Description|
|--------|-----------|
|beforeTest|Invoked directly before each test is executed with the `TestCase` instance as a parameter. If the test is marked as ignored / disabled / inactive, then this callback won't be invoked.|
|afterTest|Invoked immediately after a `TestCase` has finished with the `TestResult` of that test. If a test case was skipped (ignored / disabled / inactive) then this callback will not be invoked for that particular test case.<br/><br/>The callback will execute even if the test fails.
|beforeSpec|Invoked after the Engine instantiates a spec to be used as part of a test execution.<br/><br/>The callback is provided with the `Spec` instance that the test will be executed under.<br/><br/>If a spec is instantiated multiple times - for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, then this callback will be invoked for each instance created, just before the first test (or only test) is executed for that spec.<br/><br/>This callback should be used if you need to perform setup each time a new spec instance is created.<br/><br/>If you simply need to perform setup once per class file, then use prepareSpec. This callback runs before any `beforeTest` functions are invoked.<br/><br/> When running in the default `SingleInstance` isolation mode, then this callback and `prepareSpec` are functionally the same since all tests will run in the same spec instance.|
|afterSpec|Is invoked after the `TestCase`s that are part of a particular spec instance have completed.<br/><br/>If a spec is instantiated multiple times - for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, then this callback will be invoked for each instantiated spec, after the tests that are applicable to that spec instance have returned.<br/><br/>This callback should be used if you need to perform cleanup after each individual spec instance. If you need to perform cleanup once per class file, then use `finalizeSpec.`<br/><br/>This callback runs after any `afterTest` callbacks have been invoked.<br/><br/>When running in the default `SingleInstance` isolation mode, then this callback and `finalizeSpec` are functionally the same since all tests will run in the same spec instance.|
|prepareSpec|Called once per spec, when the engine is preparing to execute the tests for that spec. The `KClass` instance of the spec is provided as a parameter.<br/><br/>Regardless of how many times the spec is instantiated, for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, this callback will only be invoked once. If there are no active tests in a spec, then this callback will still be invoked.<br/><br/>When running in the default `SingleInstance` isolation mode, then this callback and `beforeSpec` are functionally the same since all tests will run in the same spec instance.|
|finalizeSpec|Called once per `Spec`, after all tests have completed for that spec.<br/><br/>Regardless of how many times the spec is instantiated, for example, if `InstancePerTest` or `InstancePerLeaf` isolation modes are used, this callback will only be invoked once.<br/><br/>The results parameter contains every `TestCase`, along with the result of that test, including tests that were ignored (which will have a `TestResult` that has `TestStatus.Ignored`).<br/><br/>When running in the default `SingleInstance` isolation mode, then this callback and `afterSpec` are functionally the same since all tests will run in the same spec instance.|
|beforeInvocation|Invoked before each 'run' of a test, with a flag indicating the iteration number. This callback is useful if you have set a test to have multiple invocations via config and want to do some setup / teardown between runs.<br/><br/>If you are running a test with the default single invocation then this callback is effectively the same as `beforeTest`.<br/><br/>_Note: If you have set multiple invocations _and_ multiple threads, then these callbacks will be invoked concurrently._|
|afterInvocation|Invoked after each 'run' of a test, with a flag indicating the iteration number. This callback is useful if you have set a test to have multiple invocations via config and want to do some setup / teardown between runs.<br/><br/>If you are running a test with the default single invocation then this callback is effectively the same as `afterTest`.<br/><br/>_Note: If you have set multiple invocations _and_ multiple threads, then these callbacks will be invoked concurrently._|

### Listeners de caso de teste
Existem várias maneiras de usar os métodos em um listener.

Métodos DSL 

A primeira e mais simples é usar os métodos DSL disponíveis em uma Spec que criam e registram um TestListener para você. 
Por exemplo, podemos invocar beforeTest ou afterTest (e outros) diretamente ao lado de nossos testes.

```kotlin
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe


class ListenersTest : StringSpec({
    var start = 0L
    beforeTest {
        start = System.currentTimeMillis()
        println("Starting a test $it")
    }

    afterTest { (test, result) ->
        val end = System.currentTimeMillis()
        val duration = end - start

        println("Finished test with result $result\n This test took $duration millis")
    }

    "this test"{
        "be alive".length shouldBe 8
    }

    "this test2"{
        "be alive".length shouldBe 8
        "be alive2".length shouldBe 9
    }
})
```

Por tráz, esses métodos DSL criarão uma instância do TestListener, substituindo as funções apropriadas e garantindo que esse Listener de teste esteja registrado para execução.

