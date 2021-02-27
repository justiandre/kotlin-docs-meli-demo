- [Classes de dados](#classes-de-dados)
  - [Criação automática de getters e setters](#criação-automática-de-getters-e-setters)
  - [Método copy](#método-copy)
  - [toString automático](#tostring-automático)
  - [Métodos hashCode e equals gerados para você](#métodos-hashcode-e-equals-gerados-para-você)
  - [Declarações desestruturadas](#declarações-desestruturadas)
  - [Desestruturando tipos](#desestruturando-tipos)
  - [Desestruturando tipos "de terceiros"](#desestruturando-tipos-de-terceiros)
  - [Propriedades declaradas dentro da classe](#propriedades-declaradas-dentro-da-classe)
  - [Regras para definição de classe de dados](#regras-para-definição-de-classe-de-dados)
  - [Classes de dados padrão](#classes-de-dados-padrão)

## Classes de dados

Frequentemente precisamos de classes que possuem o propósito principal de armazenar dados. Essas classes possuem um "contrato padrão" para inicialização, métodos acessores e modificadores, *toString*, *hashCode*, etc. 

```java
public class BlogEntryJ {
    private String title;
    private String description;
    private final DateTime publishTime;
    private final Boolean approved;
    private final DateTime lastUpdated;
    private final URI url;
    private final Integer commentCount;
    private final List<String> topTags;
    private final String email;
    // ... getters, setters, constructors, toString, hashCode, equals 
}
```
Em Kotlin, essas classes são chamadas de *data classes*, e são marcadas com *data* na declaração. Classes de dados foram criadas para tipos cujo propósito é não servir para nada além de serem contêineres de dados.

```kotlin
data class BlogEntry(
    var title: String,
    var description: String,
    val lastUpdated: DateTime,
    val url: URI,
    val email: String,
    val relatedTags: List<String> = emptyList(),
    val approved: Boolean = false,
    val publishTime: DateTime?,
    val commentCount: Int = 0
)
```
### Criação automática de getters e setters
Para atributo declarado como *var*, são gerados os *getter* e os *setters* automaticamente.
```java
@NotNull
public final String getTitle() {
  return this.title;
}

public final void setTitle(@NotNull String var1) {
  Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
  this.title = var1;
}
```

Para atributo do tipo *val* é gerado somente o *getter*.
```java
@NotNull
public final String getTitle() {
  return this.title;
}

public final void setTitle(@NotNull String var1) {
  Intrinsics.checkParameterIsNotNull(var1, "<set-?>");
  this.title = var1;
}
```

### Método copy
Obtido automaticamente ao utilizar uma classe de dados, permite criar uma nova instância de seu tipo, ao mesmo tempo que possibilita selecionar os campos que você quer alterar.
```kotlin
val post = BlogEntry(
    title = "Destructuring Declarations",
    description = "...",
    url = URI.create("/docs/multi-declarations.html"),
    lastUpdated = DateTime.parse("2020-06-30T12:00-03:00"),
    publishTime = null,
    email = "docs@kotlinlang.com"
)

val publishedPost = post.copy(approved = true, publishTime = DateTime.now())

println(post)
println(publishedPost)
```
```log
BlogEntry(title=Destructuring Declarations, description=..., lastUpdated=2020-06-30T12:00:00.000-03:00, url=/docs/multi-declarations.html, email=docs@kotlinlang.com, relatedTags=[], approved=false, publishTime=null, commentCount=0)

BlogEntry(title=Destructuring Declarations, description=..., lastUpdated=2020-06-30T12:00:00.000-03:00, url=/docs/multi-declarations.html, email=docs@kotlinlang.com, relatedTags=[], approved=true, publishTime=2020-07-12T18:00:03.988-03:00, commentCount=0)
```

Internamente:

```java
BlogEntry publishedPost = BlogEntry.copy$default(post, (String)null, (String)null, (DateTime)null, (URI)null, (String)null, (List)null, true, DateTime.now(), 0, 319, (Object)null);

@NotNull
public final BlogEntry copy(@NotNull String title, @NotNull String description, @NotNull DateTime lastUpdated, @NotNull URI url, @NotNull String email, @NotNull List relatedTags, boolean approved, @Nullable DateTime publishTime, int commentCount) {
  Intrinsics.checkParameterIsNotNull(title, "title");
  Intrinsics.checkParameterIsNotNull(description, "description");
  Intrinsics.checkParameterIsNotNull(lastUpdated, "lastUpdated");
  Intrinsics.checkParameterIsNotNull(url, "url");
  Intrinsics.checkParameterIsNotNull(email, "email");
  Intrinsics.checkParameterIsNotNull(relatedTags, "relatedTags");
  return new BlogEntry(title, description, lastUpdated, url, email, relatedTags, approved, publishTime, commentCount);
}
```

> Providing explicit implementations for the componentN() and copy() functions is not allowed.

### toString automático

Ao definir um tipo, as melhores práticas determinam que você deve fornecer uma versão sobrecarregada do método *toString*, que deve retornar uma string que descreva a instância.

Na maioria dos casos, a implementação do método *toString* retorna uma string contendo todos os atributos da classe concatenados como *chave=valor*. Adicionar ou remover atributos da classe requer a atualização "manual" do método.

Ao declarar uma *data class* em kotlin, o método *toString* é gerado automaticamente durante a compilação, reduzindo *boilerplate* em nossos códigos.

```java
@NotNull
public String toString() {
  return "BlogEntry(title=" + this.title + ", description=" + this.description + ", publishTime=" + this.publishTime + ", approved=" + this.approved + ", lastUpdated=" + this.lastUpdated + ", url=" + this.url + ", commentCount=" + this.commentCount + ", topTags=" + this.topTags + ", email=" + this.email + ")";
}
```

### Métodos hashCode e equals gerados para você
Em kotlin, todo tipo é derivado de *Any*, que tem a declaração do método *hashCode*, equivalente ao presente na classe 
*Object* de Java.

> Suponha que você esteja implementando uma agenda de telefones. Você colocará todo nome que comece com A na seção A, todo nome que comece com B na seção B, e assim sucessivamente. Essa abordagem simples permite fazer consultas mais rápidas quando você estiver procurando algum registro. É assim que coleções baseadas em hash, como *HashMap* e HashSet, são implementadas.

Os métodos hashCode e equals são gerados automaticamente pelo compilador, considerando a soma de hashCodes dos atributos utilizados na declaração da classe de dados. É possível sobrescrever a implementação, desde que seguindo as regras:
* Quando executado no mesmo objeto mais de uma vez, deve retornar de forma consistente o mesmo valor, desde que o objeto não tenha sido modificado.
* Se, comparando dois objetos, o método *equals()* retornar verdadeiro, executar *hashCode()* nos dois objetos também deverá retornar o mesmo valor inteiro.

### Declarações desestruturadas
Kotlin possui um syntax sugar que permite a declaração de variáveis através da desestruturação do tipo, extraindo os atributos da classe em novas variáveis.

```kotlin
val post = BlogEntry(
    title = "Last time to win",
    description = "A brief description",
    url = URI("/last-time-to-win"),
    lastUpdated = DateTime.now(),
    publishTime = null,
    email = "publisher@website.com"
)

val (title, description, lastUpdated) = post
println("$title - $description - $lastUpdated")
```
```log
Last time to win - A brief description - 2020-07-12T18:29:03.942-03:00
```

Durante a compilação é gerado um método *componentN* para cada atributo declarado na classe de dados, onde *N* representa a ordem do atributo na declaração da classe:
```java
@NotNull
public final String component1() {
  return this.title;
}
@NotNull
public final String component2() {
  return this.description;
}
// ... 
public final int component9() {
  return this.commentCount;
}
```

```kotlin
println(post.component1())
println(post.component2())
println(post.component4())
```
```log
Last time to win
A brief description
/last-time-to-win
```


Assim, o compilador kotlin transforma o acúcar sintático da desestruturação em chamadas para o método *componentN* correspondente:
```java
String title = post.component1();
String description = post.component2(); 
DateTime lastUpdated = post.component3();
```
Um detalhe do processo de desestruturação de Kotlin é o de que é apenas um syntax sugar. Em tempo de compilação, as variáveis assumem o valor de retorno dos métodos componentN, na ordem de declaração dos atributos na classe de dados, ou seja, [não há extração por correspondência de nome das variáveis:](https://discuss.kotlinlang.org/t/position-based-declaration-destructuring/3787/6), como temos em Javascript ou Scala:

```kotlin
val (title, description, email) = post
println("$title - $description - $email")
```
Variável *email* declarada com o valor de *lastUpdate*:
```
Last time to win - A brief description - 2020-07-12T18:46:27.421-03:00
```
*É possível configurar linters para avisar sobre esse comportamento. O intellij já possui esse tipo de aviso por padrão para classes de dados.*

Desde a versão 1.1, foi adicionada a possibilidade de utilizar underscores para ignorar as variáveis não desejadas na desestruturação:
```kotlin
val (title, description, _, _, email) = post
println("$title - $description - $email")
```
```log
Last time to win - A brief description - publisher@website.com
```

### Desestruturando tipos

Utilizando classes de dados, ganhamos automaticamente a desestruturação, pois os métodos componentN são gerados durante a compilação. 
Porém, também podemos utilizar a desestruturação para os outros tipos de classes, através do uso de *operator functions*
```kotlin
class Vector3(val x: Double, val y: Double, val z: Double) {
    operator fun component1() = x
    operator fun component2() = y
    operator fun component3() = z
}
// using a normal class with custom componentN functions
val coordinates = listOf(Vector3(0.2, 0.1, 0.5), Vector3(-12.0, 3.145, 5.100))
for ((x, y, z) in coordinates) {
    println("Coordinates: x=$x, y=$y, z=$z")
}
```
```log
Coordinates: x=0.2, y=0.1, z=0.5
Coordinates: x=-12.0, y=3.145, z=5.1
```

### Desestruturando tipos "de terceiros" 
Supondo o caso onde temos uma classe Java ou Kotlin que não temos o controle do código fonte, mas desejamos utilizar desestruturação de tipos, podemos fazer uso de funções de extensão:

```java
public class Sensor {
    private final String id;
    private final double value;

    public String getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public Sensor(String id, double value) {
        this.id = id;
        this.value = value;
    }
}
```

```kotlin
operator fun Sensor.component1() = this.id
operator fun Sensor.component2() = this.value

val sensors = listOf(Sensor("DS18B20", 29.2), Sensor("DS18B21", 32.1))
for ((sensorID, value) in sensors) {
    println("Sensor $sensorID reading is $value Celsius")
}
```
```log
Sensor DS18B20 reading is 29.2 Celsius
Sensor DS18B21 reading is 32.1 Celsius
```

### Propriedades declaradas dentro da classe
O compilador utiliza somente os atributos definidos no construtor da classe de dados para a geração automática dos métodos toString(), equals(), hashCode(), and copy().
```kotlin
data class Person(val name: String) {
    var age: Int = 0
}

val p1 = Person("john")
p1.age = 32
println("p1 $p1 - ${p1.age}")

val p2 = Person("john")
println("p2 $p2 - ${p2.age}")
p2.age = 50
println("p2 $p2 - ${p2.age}")

println(p1 == p2)
```
```log
p1 Person(firstName=john) - 32
p2 Person(firstName=john) - 0
p2 Person(firstName=john) - 50
true
```

### Regras para definição de classe de dados

Ao definir uma classe de dados, existem algumas regras:
- o construtor primário deve ter pelo menos um parâmetro.
- todos os parâmetros do construtor primário devem estar marcados como val ou var.
- classes de dados não podem ser abstratas(abstract), abertas(open), seladas nem internas(inner)

[A partir da versão 1.1 do kotlin](https://github.com/Kotlin/KEEP/blob/master/proposals/data-class-inheritance.md), foram removidas algumas das restrições extensão de classes de dados. Sendo também possível extender de classes seladas no mesmo arquivo:

```kotlin
sealed class Expr

data class Const(val number: Double) : Expr()
data class Sum(val e1: Expr, val e2: Expr) : Expr()
object NotANumber : Expr()

fun eval(expr: Expr): Double = when (expr) {
    is Const -> expr.number
    is Sum -> eval(expr.e1) + eval(expr.e2)
    NotANumber -> Double.NaN
}
val e = eval(Sum(Const(1.0), Const(2.0)))
````

Graças aos recursos da linguagem Kotlin que oferecem null-safety, ao inicializar uma classe de dados, devemos informar no construtor o valor de todos os atributos. 

Porém, pode ser necessário fornecer um construtor vazio (alguns frameworks Java exigem que a classe forneça um construtor default sem parâmetros, apesar de ser possível resolver isso também através de [anotações](https://kotlinexpertise.com/hibernate-with-kotlin-spring-boot/) ou [plugin](https://stackoverflow.com/questions/32038177/kotlin-with-jpa-default-constructor-hell/58410514#58410514)).

Para obter a inicialização de classes de dados com um construtor vazio, basta fornecer um valor padrão para cada parâmetro, permitindo assim a inicialização com o construtor vazio:
```kotlin
data class Email(var to: String = "", var subject: String = "", var content: String = "")
```

### Classes de dados padrão
Kotlin disponibiliza na biblioteca padrão duas classes de dados prontas chamadas Pair e Triple, já citadas em encontros anteriores.
Permitem, por exemplo, o uso de *destrutores*:
```kotlin
val keys = listOf(Pair("site_id", "MLB"), Pair("service_id", 157861))
for ((key, value) in keys) {
    println("$key=$value")
}
```

```log
site_id=MLB
service_id=157861
```

> In most cases, though, named data classes are a better design choice, because they make the code more readable by providing meaningful names for properties.


**links interessantes:**

- https://stackoverflow.com/questions/11537005/why-chose-31-to-do-the-multiplication-in-the-hashcode-implementation
- https://kotlinexpertise.com/hibernate-with-kotlin-spring-boot/
- https://github.com/Kotlin/KEEP/blob/master/proposals/data-class-inheritance.md

