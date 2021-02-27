# Algumas Features

Será demonstrado aqui algumas das principais features do Kotlin, e na maioria dos casos também será colocado o mesmo código em Java, ou seja o que é necessário fazer em Java para o mesmo. 

## Interoperabilidade 100% Java

Uma das melhores características da linguagem de programação Kotlin é sua total interoperabilidade com Java, que atrai mais desenvolvedores de Java para aprender Kotlin e também sua baixa curva de aprendizado para desenvolvedores Java. Com essa total interoperabilidade conseguimos usar todas as ferramentas, frameworks e utilitários feitos em Java.

**Código Java**

```java
public class Driver {

   private final String name;
   private final String lastname;

   public Driver(String name, String lastname) {
      this.name = name;
      this.lastname = lastname;
   }

   public String getFullName() {
      return name + " " + lastname;
   }
}
```

**Código de exemplo Kotlin que chama o código Java**

```kotlin
fun main() {
   val driver = Driver(
      "Arthur", "Fleck"
   )
   println("Driver: " + driver.fullName)
   // Imprime: "Driver: Arthur Fleck"
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/java-interop.html).

## Data class

O escopo do recurso e a necessidade de uma classe são sempre discutidos pelos designers de linguagem de programação. Uma classe de dados típica em Java tem toneladas de código padrão que é necessário pular enquanto descobre o uso real dessa classe. No entanto, no Kotlin, você pode escrever o equivalente do mesmo código Java de uma maneira muito simples e economizar toneladas de digitação e esforço mental. 

**Código de exemplo Kotlin**

```kotlin
data class Person(val name: String, val lastName: String, val age: Int)
```

**Código de exemplo Java equivalente**

```java
public class Person {

   private final String name;
   private final String lastName;
   private final int age;

   public Person(String name, String lastName, int age) {
      this.name = name;
      this.lastName = lastName;
      this.age = age;
   }

   public String getName() {
      return name;
   }

   public String getLastName() {
      return lastName;
   }

   public int getAge() {
      return age;
   }
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/data-classes.html).

## Default value

Os parâmetros padrão no Kotlin são bastante úteis quando você passa os argumentos por nome, em vez de índice. Outra grande vantagem também quando é necessário adicionar um parâmetro em uma função já existente e não gostaríamos de quebrar quem já utiliza essa função. 

**Código de exemplo Kotlin**

```kotlin
fun saveEvent(type: String, description: String = "undefined", created: LocalDateTime = LocalDateTime.now()) {
   //Regra
}

fun exec() {
   //Exemplo Uso
   saveEvent("induced")
}
```

**Código de exemplo Java equivalente**

```java
public void saveEvent(String type, String description, LocalDateTime created) {
    if (description != null) {
        description = "undefined";
    }
    if (created != null) {
        created = LocalDateTime.now();
    }
    // Regra
}

public void exec() {
    // Exemplo Uso
    saveEvent("induced", null, null);
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/functions.html#default-arguments).

## Named arguments

Argumentos padrão se tornam mais poderosos em combinação com argumentos nomeados. também melhoram a leitura do código de quem está chamando a função.

**Código de exemplo Kotlin**

```kotlin
fun saveEvent(type: String, description: String = "undefined", created: LocalDateTime = LocalDateTime.now()) {
   //Regra
}

fun exec() {
   //Exemplo Uso
   saveEvent(type = "rejected", created = LocalDateTime.now(ZoneOffset.UTC))
}
```

**Não existe código Java equivalente para isso**

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/functions.html#named-arguments).

## String Template

As String em Kotlin podem conter expressões de modelo. A expressão de modelo é um pedaço de código que é avaliado e seu resultado é concatenado em string. Começa com um cifrão **$** e consiste em um nome de variável, caso queira executar algo dentro disso é só adicionar **${logica}**

**Código de exemplo Kotlin**

```kotlin
fun main() {
   val name = "Arthur"
   val lastname = "Fleck"
   val birthDate = LocalDate.of(1988, Month.NOVEMBER, 3)

   println("Full name: $name $lastname - birth date: ${DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)}")
   // Print: "Full name: Arthur Fleck - birth date: 3 de novembro de 1988"
}
```

**Código de exemplo Java equivalente**

```java
public static void main(String[] args) {
   String name = "Arthur";
   String lastname = "Fleck";
   LocalDate birthDate = LocalDate.of(1988, Month.NOVEMBER, 03);

   // Forma 01
   System.out.println(String.format("Full name: %s %s - birth date: %s", name, lastname, birthDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))));
   // Print: "Full name: Arthur Fleck - birth date: 3 de novembro de 1988"

   // Forma 02
   System.out.println("Full name: " + name + " " + lastname + " - birth date: " + birthDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)));
   // Print: "Full name: Arthur Fleck - birth date: 3 de novembro de 1988"
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/basic-syntax.html#using-string-templates).

## Null safety

O sistema de tipos da linguagem Kotlin visa eliminar os perigos de referências nulas do código, que é frequentemente chamado de erro de bilhão de dólares. Por exemplo, em Java, o acesso a um membro de referência nula resulta em uma exceção de referência nula, já no Go um panic. Kotlin não compila código que atribui ou retorna um nulo. Isso é visto como uma das características mais importantes do Kotlin.

**Código de exemplo Kotlin**

```kotlin
fun main() {
   //   var name: String? = "possiblyNull"
   //   val nameLength = name.length // Nao compila
   var name2: String = "NotPossiblyNull"
   val nameLength2 = name2.length // Compila, Funciona
   var name3: String? = "possiblyNull"
   val nameLength3 = name3?.length // Compila, Funciona com uso de Elvis Operator
   var name4: String? = null
   val nameLength4 = name4!!.length // Compila, Mas ao executar gera um kotlin.KotlinNullPointerException
}
```

**Não existe código Java equivalente para isso**

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/null-safety.html).

## Elvis Operator

Com Elvis Operator podemos trabalhar com variáveis nulas de maneira muito mais eficiente, também deixando o código muito mais limpo e legível.

**Código de exemplo Kotlin**

```kotlin
data class Country(val name: String)
data class State(val name: String, var country: Country?)
data class City(val name: String, var state: State?)
data class Person(val name: String, val cityBirth: City?)

fun getPerson() = Person(name = "Arthur", cityBirth = null)

fun main() {
   val countryBirth = getPerson().cityBirth?.state?.country?.name ?: "undefined"
   println(countryBirth)
   // Imprime: "undefined"
}
```

**Código de exemplo Java equivalente**

```java
class Country {

   private final String name;

   public Country(String name) {
      this.name = name;
   }

   public String getName() {
      return name;
   }
}

class State {

   private final String name;
   private final Country country;

   public State(String name, Country country) {
      this.name = name;
      this.country = country;
   }

   public String getName() {
      return name;
   }

   public Country getCountry() {
      return country;
   }
}

class City {

   private final String name;
   private final State state;

   public City(String name, State state) {
      this.name = name;
      this.state = state;
   }

   public String getName() {
      return name;
   }

   public State getState() {
      return state;
   }
}

class Person {

   private final String name;
   private final City cityBirth;

   public Person(String name, City cityBirth) {
      this.name = name;
      this.cityBirth = cityBirth;
   }

   public String getName() {
      return name;
   }

   public City getCityBirth() {
      return cityBirth;
   }
}

public class Main {

   public static Person getPerson() {
      return new Person("Arthur", null);
   }

   public static void main(String[] args) {
      Person person = getPerson();
      String countryBirthName = "undefined";
      // Existem varias formas de fazer esse codigo, mas essa foi usada só para esclarecer o problema
      if (person != null) {
         City cityBirth = person.getCityBirth();
         if (cityBirth != null) {
            State stateBirth = cityBirth.getState();
            if (stateBirth != null) {
               Country countryBirth = stateBirth.getCountry();
               if (countryBirth != null) {
                  String name = countryBirth.getName();
                  if (name != null) {
                     countryBirthName = name;
                  }
               }
            }
         }
      }
      System.out.println(countryBirthName);
      // Print: "undefined"
   }
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/null-safety.html#elvis-operator).

## Extension function

Graças às funções de extensão do Kotlin, você pode adicionar métodos às classes sem fazer alterações no código fonte. Embora as funções de extensão sejam frequentemente criticadas, as vezes é muito útil.

As extensões nos permitem resolver um método / propriedade estático em um objeto / classe que já existe. Ou seja, chamamos o objeto como se tivesse chamando o método ou classe de origem.

**Código de exemplo Kotlin**

```kotlin
fun LocalDate.formatPtBr(): String = this.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) // Inline

fun LocalDate.printPtBr() {
   println(this.formatPtBr())
}

fun main() {
   val date = LocalDate.of(1988, Month.NOVEMBER, 3)
   println(date.formatPtBr())
   // Print "03/11/1988"
   date.printPtBr()
   // Print "03/11/1988"
}
```

**Código de exemplo Java equivalente**

**Não existe código Java equivalente para isso**

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/extensions.html).

## Delegation

O [padrão Delegation](https://en.wikipedia.org/wiki/Delegation_pattern) provou ser uma boa alternativa à herança de implementação, e o Kotlin o suporta nativamente, exigindo zero código [padrão](https://en.wikipedia.org/wiki/Delegation_pattern), isso é muito útil em casos de uso, qual usamos uma estrutura de camada onde Endpoint > Service > Dão e normalmente em alguns métodos o service apenas delega a chamada para o Dao, porque o mesmo não possui nenhuma lógica.

**Código de exemplo Kotlin**

```kotlin
interface Dao {
   fun func1(): String

   fun func2(): String

   fun func3(): String
}

open class DaoImpl : Dao {
   override fun func1() = "Dao - func1"

   override fun func2() = "Dao - func2"

   override fun func3() = "Dao - func3"
}

class ServiceImpl(dao: Dao) : Dao by dao {
   override fun func3() = "Service - func3 - Override"
}

fun main() {
   val service = ServiceImpl(DaoImpl())

   println(service.func1())
   // Imprime: "Dao - func1"
   println(service.func2())
   // Imprime: "Dao - func2"
   println(service.func3())
   // Imprime: "Service - func3 - Override"
}
```

**Código de exemplo Java equivalente**

```java
package features.delegation;

interface Dao {

   String func1();

   String func2();

   String func3();
}

class DaoImpl implements Dao {

   public String func1() {
      return "Dao - func1";
   }

   public String func2() {
      return "Dao - func2";
   }

   public String func3() {
      return "Dao - func3";
   }
}

class ServiceImpl {

   private Dao dao;

   public ServiceImpl(Dao dao) {
      this.dao = dao;
   }

   public String func1() {
      return dao.func1();
   }

   public String func2() {
      return dao.func2();
   }

   public String func3() {
      return "Service - func3 - Override";
   }
}

public class Main {

   public static void main(String[] args) {
      ServiceImpl service = new ServiceImpl(new DaoImpl());

      System.out.println(service.func1());
      // Imprime: "Dao - func1"

      System.out.println(service.func2());
      // Imprime: "Dao - func2"

      System.out.println(service.func3());
      // Imprime: "Service - func3 - Override"
   }
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/delegation.html).

## Lambdas

Expressões lambda e funções anônimas são 'literais de função', isto é, funções que não são declaradas, mas são transmitidas imediatamente como expressão.

**Código de exemplo Kotlin**

```kotlin
fun main() {
   val types = listOf("induced", "diverted", "rejected")
   types.map { "TYPE: ${String::toString}" }.forEach { println(it) }
   // Imprime:
   //  "TYPE: INDUCED"
   //  "TYPE: DIVERTED"
   //  "TYPE: REJECTED"
}
```

**Código de exemplo Java equivalente**

```java
public static void main(String[] args) {
   List<String> types = List.of("induced", "diverted", "rejected");
   types.stream().map(type -> String.format("TYPE: %s", type.toUpperCase())).forEach(System.out::println);
   // Imprime:
   //  "TYPE: INDUCED"
   //  "TYPE: DIVERTED"
   //  "TYPE: REJECTED"
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/lambdas.html#lambda-expressions-and-anonymous-functions).

## Scope Function

A biblioteca padrão Kotlin contém várias funções cujo único objetivo é executar um bloco de código no contexto de um objeto. Quando você chama essa função em um objeto com uma expressão lambda fornecida, ela forma um escopo temporário. Nesse escopo, você pode acessar o objeto sem seu nome. Tais funções são chamadas de funções de escopo . Há vários deles, como: let, run, with, apply, e also.

Basicamente, essas funções fazem o mesmo: execute um bloco de código em um objeto. O que é diferente é como esse objeto fica disponível dentro do bloco e qual é o resultado de toda a expressão.

**Código de exemplo Kotlin**

```kotlin
fun main() {
   // Exemplo 01
   val cities = mutableSetOf("Florianópolis", "São Paulo", "Rio de Janeiro")

   // Scope function
   cities.apply {
      add("Curitiba")
      add("Porto Alegre")
      add("Belo Horizonte")
   }

   // Forma tradicional
   cities.add("Curitiba")
   cities.add("Porto Alegre")
   cities.add("Belo Horizonte")

   println(cities)
   // Imprime "[Florianópolis, São Paulo, Rio de Janeiro, Curitiba, Porto Alegre, Belo Horizonte]"

   // -------------------

   // Exemplo 02
   data class Person(var name: String? = null, var lastName: String? = null, var age: Int? = null)
   val person = Person()

   // Scope function
   person.apply {
      name = "Arthur"
      lastName = "Fleck"
      age = 44
   }

   // Forma tradicional
   person.name = "Arthur"
   person.lastName = "Fleck"
   person.age = 44

   println(person)
   // Imprime "Person(name=Arthur, lastName=Fleck, age=44)"
}
```

**Não existe código Java equivalente para isso**

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/scope-functions.html).

## Sealed classes

Classes seladas são usadas para representar hierarquias de classe restritas, quando um 
valor pode ter um dos tipos de um conjunto limitado, mas não pode ter nenhum 
outro tipo. Elas são, de certa forma, uma extensão de classes de enum: o conjunto de 
valores para um tipo de enum também é restrito, mas cada constante de enum
existe apenas como uma única instância, enquanto uma subclasse de uma classe 
selada pode ter várias instâncias que podem conter Estado.

**Código de exemplo Kotlin**

```kotlin
sealed class Payment

data class CashPayment(val amount: Int, val orderId: Int): Payment()
data class BankPayment(val amount: Int, val bankName: String): Payment()

fun processPayment(payment: Payment) {
    when (payment) {
        is CashPayment -> {
            println("Pagamento no valor de ${payment.amount} na comanda ${payment.orderId}")
        }
        is BankPayment -> {
            println("Transferencia de ${payment.amount} para o banco ${payment.bankName}")
        }
    }
}

fun main() {
    processPayment(CashPayment(20, 2))
    processPayment(BankPayment(20, "Nubank"))
}
```

**Código de exemplo Java equivalente**

```java
public abstract class Payment {

    public final static class CardPayment extends Payment {

        private Integer amount;
        private Integer orderId;

        public CardPayment(Integer amount, Integer orderId) {
            this.amount = amount;
            this.orderId = orderId;
        }

        public Integer getAmount() {
            return amount;
        }

        public Integer getOrderId() {
            return orderId;
        }
    }

    public final static class BankPayment extends Payment {

        private Integer amount;
        private String bankName;

        public BankPayment(Integer amount, String bankName) {
            this.amount = amount;
            this.bankName = bankName;
        }

        public Integer getAmount() {
            return amount;
        }

        public String getBankName() {
            return bankName;
        }
    }
}

public class SealedDemo {
    public static void process(Payment payment) {
        if (payment instanceof Payment.CardPayment) {
            Payment.CardPayment cardPayment = (Payment.CardPayment) payment;
            System.out.println(String.format("Pagamento no valor de %s na comanda %s", cardPayment.getAmount(), cardPayment.getOrderId()));
        } else if (payment instanceof Payment.BankPayment) {
            Payment.BankPayment bankPayment = (Payment.BankPayment) payment;
            System.out.println(String.format("Transferencia de %s para o banco %s", bankPayment.getAmount(), bankPayment.getBankName()));
        }
    }

    public static void main(String[] args) {
        Payment cardPayment = new Payment.CardPayment(100, 10);
        process(cardPayment);
        Payment bankPayment = new Payment.BankPayment(10, "Nubank");
        process(bankPayment);
    }
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/sealed-classes.html).


## Expressão when

A expressão `when` substitui o famoso `switch` operator de linguagens baseadas em C. `when` checa o argumento de entrada contra todas as opções sequencialmente até que alguma seja verdadeira. `when` pode ser usado ou como uma expressão ou como uma declaração.

A opção `else` é executada se nenhuma das outras opções são verdadeiras. Se `when` é utilizado como uma expressão, a opção `else` é obrigatório, a não ser que o compilador tenha certeza que todos os casos estão cobertos pela opção (como por exemplo com um enum)

**Código de exemplo Kotlin**

```kotlin
fun main() {
    // Exemplo #1
    val result01 = when (10) {
        0, 1, 2 -> "number 0, 1 or 2"
        in 3..11 -> "num is between 3 and 11"
        else -> "none of above"
    }
    println(result01)

    // Exemplo #2
    val x = 1
    val s = "1"
    when (x) {
        parseInt(s) -> println("s encodes x")
        else -> println("s does not encode x")
    }

    // Exemplo #3
    when {
        x > 0 -> println("x is greater than 0")
        x > 10 -> print("x is greater than 10")
        x > 20 -> print("x is greater than 20")
        else -> println("x is a big number")
    }
}
```

**Código de exemplo Java equivalente**

```java
public static void main(String[] args) {                                                                                   
    // Exemplo #1                                                                                                          
    var num = 10;                                                                                                          
    var result = switch (num) {                                                                                            
        case 0, 1, 2 -> "number 0, 1 or 2";                                                                                
        case 3, 4, 5, 6, 7, 8, 9, 10, 11 -> "num is between 3 and 11"; // não há nada em java para validar ranges no switch
        default -> "none of above";                                                                                        
    };                                                                                                                     
    out.println(result);                                                                                                   
                                                                                                                           
    // Exemplo #2 - Não conseguimos usar expressões ou váriaveis no switch do Java                                         
    var x = 1;                                                                                                             
    var s = "1";                                                                                                           
    if (parseInt(s) == x) {                                                                                                
        out.println("s encodes x");                                                                                        
    } else {                                                                                                               
        out.println("s does not encode x");                                                                                
    }                                                                                                                      
                                                                                                                           
    // Exemplo #3 - Não conseguimos usar o switch do java sem passar um valor.                                             
    if (x > 0) {                                                                                                           
        out.println("x is greater than 0");                                                                                
    } else if (x > 10) {                                                                                                   
        out.println("x is greater than 10");                                                                               
    } else if (x > 20) {                                                                                                   
        out.println("x is greater than 20");                                                                               
    } else {                                                                                                               
        out.println("x is a big number");                                                                                  
    }                                                                                                                      
}                                                                                                                          
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/control-flow.html#when-expression).

## Smart cast

Há duas formas de fazer casting de valores de variáveis em Kotlin: explicitamente e de forma automática. O compilador Kotlin buca a palavra reservada "is" e identifica valores imutáveis para fazer o casting automaticamente.

**Código de exemplo Kotlin**

```kotlin
   // Podemos utilizar o tipo "Any" quando não sabemos o tipo da variável:
   fun printLength(data: Any) {
      if (data is String) {
         // O Compilador Kotlin converte a variável automaticamente em string
         println(data.length)
      }
   }

   // A função acima pode também ser escrita da seguinte forma:
   fun printLength(data: Any) {
      if (data !is String) return
      // O Compilador Kotlin converte a variável automaticamente em string
      println(data.length)
   }


   // ...Ou talvez dessa forma
   fun printLength(data: Any) {
      // O smart cast pode ser utilizado diretamente em condições, com "&&" e "||" 
      if (data !is String || data.length == 0) return
      println(data.length)
   }

   // Podemos utilizar o smart cast diretamente em uma instrução "when"
   fun printNumber(data: Any) {
      when (data) {
         is Int -> println(data)
         is String -> println(data.length)
         is CharArray -> println(data.size)
         is IntArray -> println(data.sum())
      }
   }
}
```

**Código de exemplo Java equivalente**

```java
   // Não há funções em Java, sempre é necessário implementar métodos em uma classe
   public class Casting {
      public void printLength(Object data) {
         // Em Java há o teste de tipo de dados, porém, não há casting automático
         if (data instanceof String) {
               System.out.println(((String) data).length());
         }
      }

      public void printNumber(Object data) {
         // Não é possível utilizar switch case sem condição inicial, sendo necessário utilizar if-else
         if (data instanceof Integer) {
               System.out.println(data);
         } else if (data instanceof String) {
               System.out.println(((String) data).length());
         } else if (data instanceof char[]) {
               System.out.println(((char[]) data).length);
         } else if (data instanceof int[]) {
               int sum = 0;
               // Em adição, não há um método que soma todos os elementos de um array de inteiros em Java
               for (int item : (int[]) data) {
                  sum += item;
               }
               System.out.println(sum);
         }
      }
   }
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/typecasts.html#smart-casts).

## Lazy initialization 

Às vezes, precisamos construir objetos que tenham um processo de inicialização complicado. Além disso, muitas vezes não podemos 
ter certeza de que o objeto pelo qual pagamos o custo da inicialização no início de 
nosso programa será usado em nosso programa.

Pensando nisso a equipe do Kotlin desenvolveu o conceito de "inicialização lenta" 
para impedir a inicialização desnecessária de objetos.


**Código de exemplo Kotlin**

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

**Código de exemplo Java equivalente**

```java

public class BookManager {
    public static List<String> loadBooks(String name) {
        System.out.println("Load books for " + name);
        return Arrays.asList("Book1", "Book2");
    }
}

public class Person {
    private List<String> books;
    private String name;

    public Person(String name) {
        this.books = Collections.emptyList();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public List<String> getBooks() {
        if (this.books.isEmpty()) {
            this.books = BookManager.loadBooks(this.name);
        }
        return books;
    }
}

public class LazyDemo {
    public static void main(String[] args) {
        Person person = new Person("Paulo Gustavo");
        System.out.println(person.getName()); // do not load books yet
        System.out.println(person.getBooks()); // loading books
        System.out.println(person.getBooks()); // getting books from cache
    }
}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/delegated-properties.html#lazy).

## Operator overloading

O compilador Kotlin permite a sobrecarga de operadores, permitindo a implementação de operações customizadas em tipos definidos pelo usuário. Os operadores possuem uma representação simbólica, como "+", "-", "*", "/", "!", "%", etc e uma precedência de execução fixa, porém a implementação pode ficar à cargo da pessoa que está desenvolvendo.
É possível implementar as operações matemáticas (unárias e binárias), de atribuição ("+=", "-=", etc) de comparação e equalidade (">", "<", "==", "!=", etc) e também com o operador "in" para ranges, além de poder ser utilizado para invocação de funções e acesso indexado:

**Código de exemplo Kotlin**

```kotlin
   data class Reverse(val value: Int)

   // Irá substituir uma soma por uma subtração
   operator fun Reverse.plus(increment: Int) = Reverse(value - increment)

   // Irá substituir uma subtração por uma soma
   operator fun Reverse.minus(decrement: Int) = Reverse(value + decrement)

   // Irá substituir um incremento por um decremento
   operator fun Reverse.inc() = Reverse(value - 1)

   // Irá substituir um decremento por um incremento
   operator fun Reverse.dec() = Reverse(value + 1)

   fun main() {
      // Lembrando que o valor 10 será passado para todas as funções 
      var r = Reverse(10)
      println(r + 3) // Irá imprimir 7
      println(r++) // Irá imprimir 9
      println(r--) // Irá imprimir 11
      println(r - 3) // Irá imprimir 13
   }

   // Sobrescrevendo o método invoke:
   enum class DeveloperActions {
      EAT, CODE, REPEAT
   }
   class Developer(val name:String) {
      operator fun invoke(action: DeveloperActions) = when (action) {
         DeveloperActions.CODE -> "$name is coding"
         DeveloperActions.EAT -> "$name is eating"
         DeveloperActions.REPEAT -> "$name is starting again!"
      }
   }
   
   fun main() {
      val dev = Developer("Bill Gates")

      println(dev(DeveloperActions.EAT)) // dev.invoke(DeveloperActions.CODE)
      println(dev(DeveloperActions.CODE)) // dev.invoke(DeveloperActions.CODE)
      println(dev(DeveloperActions.REPEAT)) // dev.invoke(DeveloperActions.CODE)

      // Irá imprimir:
      // Bill Gates is eating
      // Bill Gates is coding
      // Bill Gates is starting again!
   }
```

**Código de exemplo Java equivalente**

```java
    // Não há sobrecarga de operadores em Java, por definição dos desenvolvedores da linguagem
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/operator-overloading.html).

## Espaços em nomes de funções

Em Kotlin temos a possibilidade de escrever os nomes das funções de qualquer forma desde que esteja dentro de crases: 

**Código de exemplo Kotlin**

```kotlin
fun `do some thing`() {}

func main() {
    `do some thing`()
}
```

**Código de exemplo Java equivalente**

```java
public void doSomeThing() {}
// Geralmente usado em testes
public void do_some_thing() {}
```


**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/coding-conventions.html#function-names).

## Infix functions

Funções marcadas com o keyword `infix` também podem ser chamadas usando a notação `infix` (omitindo o ponto e os parênteses na chamada). Funções infix devem satisfazer os seguintes requerimentos:

- Devem ser métodos ou [`extension functions`](#extension-function);
- Devem conter apenas um único parâmetro;
- O parâmetro não pode ser um [`vararg`](https://kotlinlang.org/docs/reference/functions.html#variable-number-of-arguments-varargs) and não pode conter um [`default value`](#default-value);

**Código de exemplo Kotlin**

```kotlin
class MyList<T> {

    val values = ArrayList<T>()

    infix fun add(v: T) = values.add(v)

    override fun toString(): String {
        return values.toString()
    }
}

infix fun Int.mais(x: Int): Int = this + x

fun main() {
    // Exemplo #1
    val num = 1
    println(num mais 2)  // Imprime 3
    println(num.mais(2)) // Imprime 3

    // Exemplo #2
    val fruits = MyList<String>()
    fruits add "apple"
    fruits add "orange"
    fruits.add("pineapple")
    println(fruits.toString()) // Imprime [apple, orange, pineapple]
}
```

**Não existe código Java equivalente para isso**

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/functions.html#infix-notation).

## Inline functions

Kotlin permite a utilização de high order functions, possibilitando a utilização de funções como parâmetro e retorno de outras funções. Porém, estas funções e lambdas são armazenadas internamente em objetos, podendo causar uma sobrecarga de memória - cada função é armazenada em um objeto anônimo diferente, alocando espaço em memória para cada um deles. Uma forma de evitar o problema de sobrecarga de memória é declarando a função que irá receber outras funções como `inline`. Declarando funções como inline irá fazer com que esta função e as funções passadas como parâmetro e retorno serão compiladas como uma só, alocando memória de uma só vez. Em contrapartida, inline functions aumentam o bytecode gerado e seu uso deveria ser evitado com funções grandes.

**Código de exemplo Kotlin**

```kotlin
   fun MyParameterFunction(value: String): String {
      return value + value
   }

   inline fun MyInlineFunction(value: String, func: (String) -> String): String {
      return value + func(value)
   }

   main() {
      // Irá imprimir "testeteste2teste2"
      println(MyInlineFunction("teste", {MyParameterFunction("teste2")}))
   }
```

**Código de exemplo Java equivalente**

```java
    // Em Java não há o conceito de inline functions
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/functions.html#inline-functions).

## Programação funcional

Funções em Kotlin são `first-class-values`, elas podem ser asignadas para variáveis e passadas como parâmetros.
Falando de imutabilidade toda coleção em Kotlin possuí sua versão mutável e imutável, além de nos permitir declarar váriaveis e campos `read-only` com a keyword `val`. Existe também uma grande gama de operações imutáveis que podemos realizar nas nossas coleções.
Resumindo o Kotlin disponibiliza as ferramentas necessárias para programarmos ou de uma maneira totalmente funcional ou orientada a objetos ou misturando os dois paradigmas.

**Código de exemplo Kotlin**

```kotlin
class Student(
  val name: String,
  val surname: String,
  val passing: Boolean,
  val averageGrade: Double
)

val students = arrayListOf(
  Student(name = "Bruce", surname = "Wayne", passing = true, averageGrade = 9.0),
  Student(name = "Tim", surname = "Drake", passing = true, averageGrade = 6.5),
  Student(name = "Jason", surname = "Todd", passing = false, averageGrade = 3.5),
  Student(name = "Dick", surname = "Grayson", passing = true, averageGrade = 9.5),
  Student(name = "Clark", surname = "Kent", passing = false, averageGrade = 3.5),
  Student(name = "Barry", surname = "Allen", passing = true, averageGrade = 7.5)
)

fun main() {
  // Exemplo #1
  val topThree = students
    .filter { it.passing }
    .sortedBy { it.averageGrade }
    .take(3)
    .sortedWith(compareBy({ it.surname }, { it.name }))
    .joinToString(separator = "\n") {
      "Student: ${it.surname}, ${it.name} | Passing: ${it.passing} | Average grade: ${it.averageGrade}"
    }
  println(topThree) // Imprime: Student: Allen, Barry | Passing: true | Average grade: 7.5
                    //          Student: Drake, Tim   | Passing: true | Average grade: 6.5
                    //          Student: Wayne, Bruce | Passing: true | Average grade: 9.0

  // Exemplo #2
  fun safeDivide(numerator: Int, denominator: Int) = if (denominator == 0) 0.0 else numerator.toDouble() / denominator
  val f: (Int, Int) -> Double = ::safeDivide
  println(f(3, 0)) // Imprime 0.0

  // Exemplo #3
  val sum = { x: Int, y: Int -> x + y }
  println(sum(1, 2)) // Imprime 3

  // Exemplo #4
  fun callAndPrint(function: (Int, Int) -> Int, v1: Int, v2: Int) { println(function(v1, v2)) }
  callAndPrint(sum, 10, 10) // Imprime 20

  // Exemplo #5
  val someNumber = 10
  print(someNumber.takeIf { it > 5 } ?.let { "Number is greater than 5" }) // Imprimi "Number is greater than 5"
}
```

**Código de exemplo Java equivalente**

```java
static class Student {                                                          
	                                                                            
	private String name;                                                        
	private Boolean passing;                                                    
	private String surname;                                                     
	private Double averageGrade;                                                
                                                                                
	Double getAverageGrade() {                                                  
		return averageGrade;                                                    
	}                                                                           
                                                                                
	Boolean getPassing() {                                                      
		return passing;                                                         
	}                                                                           
                                                                                
	String getName() {                                                          
		return name;                                                            
	}                                                                           
                                                                                
	String getSurname() {                                                       
		return surname;                                                         
	}                                                                           
                                                                                
	Student(String name, String surname, Boolean passing, Double averageGrade) {
		this.name = name;                                                       
		this.surname = surname;                                                 
		this.passing = passing;                                                 
		this.averageGrade = averageGrade;                                       
	}                                                                           
}                                                                                                                                                                                                                         
                                                                                                                                            
static Double safeDivider(Integer numerator, Integer denominator) {                                                                         
	return denominator == 0 ? 0.0 : numerator / denominator;                                                                                 
}                                                                                                                                           
                                                                                                                                            
interface CallAndPrintInterface {                                                                                   
	void call(BinaryOperator<Integer> function, Integer v1, Integer v2);                                                                    
}                                                                                                                                           
                                                                                                                                            
                                                                                                                                            
public static void main(String[] args) {                                                                                                    
	// example #1                                                                                                                           
	var students = Arrays.asList(                                                                                                           
		new Student("Bruce", "Wayne", true, 9.0),                                                                                           
		new Student("Tim", "Drake", true, 6.5),                                                                                             
		new Student("Jason", "Todd", false, 3.5),                                                                                           
		new Student("Dick", "Grayson", true, 9.5),                                                                                          
		new Student("Clark", "Kent", false, 3.5),                                                                                           
		new Student("Barry", "Allen", true, 7.5)                                                                                            
	);                                                                                                                                      
	var topThree = students
         .stream()                                           
         .filter(Student::getPassing)                                           
         .sorted(comparing(Student::getAverageGrade))                           
         .limit(3)                                                              
         .sorted(comparing(Student::getSurname).thenComparing(Student::getName))
         .reduce("", (v, s) ->                 
               v += String.format("Student: %s, %s | Passing: %s | Average grade: %s", s.surname, s.name, s.passing, s.averageGrade) + "\n"
         , String::concat);       
	System.out.println(topThree); // Imprime: Student: Allen, Barry | Passing: true | Average grade: 7.5
                                 //          Student: Drake, Tim   | Passing: true | Average grade: 6.5
                                //           Student: Wayne, Bruce | Passing: true | Average grade: 9.0  /                                                                                                           
                                                                                                                                            
	// Em java as funções não são consideradas first-class-values,                                                                 
	// então sempre precisamos declarar interfaces com apenas um método                                                                  
	// para ser possível utilizar lambdas. Isso em java é chamado de SAM.                                                                   
                                                                                                                                            
	// Exemplo #2                                                                                                                           
	BiFunction<Integer, Integer, Double> f = FunctionalProgramming::safeDivider;                                                            
	System.out.println(f.apply(3, 0)); // Imprime 0.0                                                      
                                                                                                                                            
	// Exemplo #3                                                                                                                           
	BinaryOperator<Integer> sum = (Integer x, Integer y) -> x + y;                                                                          
	System.out.println(f.apply(1, 2)); // Imprime 3                                                                        
                                                                                                                                            
	// Exemplo #4                                                                                                                           
	CallAndPrintInterface callAndPrint = BiFunction::apply;                                                                                 
	callAndPrint.call(sum, 10, 10); // Imprime 20                                                                  
                                                                                                                                            
	// Exemplo #5 - Não há nada equivalente em java.                                                                                        
}                                                                                                                                           
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/tutorials/kotlin-for-py/functional-programming.html).

## Returns

Kotlin possui três expressões que permitem pular dentro do código:
- "return", utilizado geralmente para retornar para o ponto o qual a função foi chamada;
- "break", que finaliza o loop mais próximo do ponto atual;
- "continue", que segue para a próxima iteração do loop mais próximo do ponto atual;

Diferente de outras linguagens, podemos definir `labels` as quais servirão como marcadores de pontos a retornar. Labels em Kotlin são definidas com o sinal de `@` após o identificador, como por exemplo `blocoA@`. Qualquer expressão pode conter uma label, permitindo as seguintes possibilidades: 

**Código de exemplo Kotlin**

```kotlin
   // Sem a label, a expressão "break" iria parar somente o loop for (j), com a label, irá parar o loop for (i):
   loop@ for (i in 1..100) {
      for (j in 1..100) {
         if (j == 50) break@loop
      }
   }

   // Return por padrão retorna para o nível acima, com a label irá retornar ao bloco superior:
   fun printExcept(array: List<String>, value: String) {
      array.forEach {
         if (it == value) return@forEach // Irá retornar ao bloco que está chamando o lambda, nesse caso o loop forEach
         print(it)
      }
   }

   main() {
      printExcept(listOf("A","B","C"), "B") // Irá imprimir "AC"
   }
```

**Código de exemplo Java equivalente**

```java
   // Em Java há também o conceito de labels, mas a utilização é um pouco diferente 
   // e seu funcionamento é limitado a loops:
   public static void main(String[] args) {
      // Definimos a label em um nível acima, não na mesma linha da expressão do bloco
      loop:
         for (int i = 1; i <= 100; i++) {
               for (int j = 1; j <= 100; j++) {
                  if (j == 50) {
                     // O break retorna para a label
                     break loop;
                  }
               }
         }
    }
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/returns.html).

## Generics

Permite criar códigos genéricos que aceitam tipos de dados diferentes.

**Código de exemplo Kotlin**

```kotlin
data class Person(val name: String)

class MyArrayImpl<T> {
    var items = arrayListOf<T>()

    fun addItem(item: T) {
        items.add(item)
    }

    fun removeItem(item: T) {
        items.remove(item)
    }

    fun <R> map(mapper: (item: T) -> R): MyArrayImpl<R> {
        val m = MyArrayImpl<R>()
        m.items.addAll(items.map(mapper))
        return m
    }

    override fun toString(): String {
        return items.toString()
    }
}

fun main() {
    val list = MyArrayImpl<Person>()
    list.addItem(Person("Paulo"))
    list.addItem(Person("Gustavo"))

    val newList = list.map { it.name + " Mapped" }
    println(newList)

}
```

**Código de exemplo Java equivalente**

```java
public class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

public interface Mapper<T, R> {
    R map(T item);
}

public class MyArrayImpl<T> {
    private List<T> items;

    public MyArrayImpl() {
        this.items = new ArrayList<T>();
    }

    public void addItem(T item) {
        this.items.add(item);
    }

    public void removeItem(T item) {
        this.items.remove(item);
    }

    public <R> MyArrayImpl<R> map(Mapper<T, R> mapper) {
        MyArrayImpl<R> m = new MyArrayImpl<>();
        this.items.stream().map(mapper::map);
        m.items.addAll(this.items.stream().map(mapper::map).collect(Collectors.toList()));
        return m;
    }

    @Override
    public String toString() {
        return items.toString();
    }
}

public class GenericsExample {

    public static void main(String[] args) {
        MyArrayImpl<Person> persons = new MyArrayImpl();
        persons.addItem(new Person("Paulo"));
        persons.addItem(new Person("Gustavo"));

        System.out.println(persons);

        MyArrayImpl<String> newList = persons.map(item -> item.getName() + " mapped");

        System.out.println(newList);
    }

}
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/generics.html).

## Type alias

Há casos em que precisamos criar nomes alternativos para tipos já existentes. Isso pode acontecer caso o nome é muito grande e vamos utilizar muito durante nossa implementação, por exemplo. Kotlin permite criar alias para tipos utilizando a palavra chave `typealias`:

**Código de exemplo Kotlin**

```kotlin

   // Isso pode ser feito para tipos...
   typealias MyHashMap = HashMap<String, String>

   // ...Mas também pode ser utilizado para funções...
   typealias MyFunction = (String, Int, Boolean) -> Unit

   // ...E para classes aninhadas!
   class MyClass {
      inner class MyInnerClass
   }
   typealias InnerClass = MyClass.MyInnerClass
```

**Código de exemplo Java equivalente**

```java
   // Em Java não há type alias, é necessário extender a classe a qual se quer criar um alias para implementar 
   // com um nome diferente ou mais simples
   public class TheNewType extends HashMap<String, String> {
      public TheNewMyHashMapType() {
         super();
      }
   }
```

**Obs.:** Isso pode ser visto em mais detalhes [aqui](https://kotlinlang.org/docs/reference/type-aliases.html).

## Coroutines

`kotlinx.coroutines` é uma biblioteca para [coroutines](https://en.wikipedia.org/wiki/Coroutine) desenvolvida pela própria JetBrains. 

Você pode pensar em uma coroutine como uma thread mais leve. Como threads, coroutines podem ser executadas em paralelo, esperar uma por a outra e comunicarem-se entre si. A maior diferença é que coroutines são muito mais baratas, quase de graça: nós podemos criar milhares delas e pagar um preço muito baixo em termos de perfomance. Enquanto threads, por outro lado, são caras para iniciar e manter. Milhares de threads podem ser um verdadeiro desafio para máquinas modernas.

**Código de exemplo Kotlin**

```kotlin
import kotlinx.coroutines.*

fun main() = runBlocking {
    launch { countUntil100("coroutine 1") }
    launch { countUntil100("coroutine 2") }
    println("Final do bloco main")
}

suspend fun countUntil100(name: String) {
    IntRange(start = 0, endInclusive = 100)
        .forEach {
            delay(1) // Delay serve para suspender essa coroutine e dessa forma deixar uma coroutine rodar.
            print(name, it)
        }
}

fun print(name: String, num: Int) {
    println("[$name] number: $num")
}
```

**Código de exemplo Java equivalente**

**OBS: Não existem coroutines em java. Você consegue reproduzir o mesmo comportamento com threads**

```java
public class ThreadExample {

	public static void main(String[] args) {
		countUntil100("thread 1");
		countUntil100("thread 2");
	}

	static void countUntil100(String name) {
		new Thread(() -> IntStream.range(0, 100).forEach(num -> print(name, num))).start();
	}

	static void print(String name, Integer number) {
		System.out.printf("[%s] number: %s \n", name, number);
	}
}
```
