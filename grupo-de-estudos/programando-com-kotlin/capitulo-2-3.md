# Capítulo 2 parte 2

## Sumário

1. [Controle de fluxo como expressões](#controle-de-fluxo-como-expressões)
2. [Sintaxe de null](#sintaxe-de-null)
3. [Verificação de tipos e casting](#verificação-de-tipos-e-casting)
4. [Casts inteligentes](#casts-inteligentes)
5. [Casting explícito](#casting-explícito)
6. [Evitando ClassCastException](#evitando-classcastexception)
7. [When](#when)
    - [Usando When para atribuir valor](#usando-when-para-atribuir-valor)
    - [Variações](#variações)
    - [Sem argumentos](#sem-argumentos)
8. [Retornos](#retornos)
9. [Hierarquia de tipos](#hierarquia-de-tipos)

## Controle de fluxo como expressões

Podemos utilizar as famosas keywords **if** e **else** para definir instruções em nosso código:

```kotlin
public boolean isZero(int x) {
    boolean isZero;
    if (x == 0) 
        isZero = true;
    else 
        isZero = false;
    return isZero;
}
```

No exemplo acima somos forçados a criar uma váriavel mútavel pois precisamos que a instrução seja executada para definir o valor da variável.

Como atribuir então o valor diretamente para isZero?

## **ternário**

Para a surpresa de muitos, Kotlin **não possui ternários**.

Porém **if...else e try** também podem ser tratados como expressões. Isso significa que o resultado pode ser diretamente atribuído a um valor ou devolvido por uma função.

```kotlin
val date = Date()
val today = if (date.year == 2020) true else false
console.log(today) // zoeira, não existe console.log kkk
```

> Assim como nos ternários, a presença do **else** também é obrigatória

> Podemos ter **else if**

O mesmo pode ser aplicado em blocos try..catch

```kotlin
val success = try {
    readFile()
    true
} catch (e: IOException) {
    false
}
```

## Sintaxe de null

Quem ai nunca fez um código top e recebeu de presente um **NullPointerException**? 

Em Kotlin para uma variável receber o valor **null** voce deve explicitamente  dizer ao compilador que a mesma aceita esse tipo de dado:

```kotlin
var str:String? = null
```

Caso não seja adicionado o operador **?**  não compilará.

```kotlin
var str:String = null // error
```

> Para mais detalhes, consultar estudo do [Capítulo 7: Null safety] 

## Verificação de tipos e casting

Fazendo uma breve comparação com java onde temos o operador **instanceOf**, em kotlin temos o operador **is**. Um pouco mais curto, não? 

```kotlin
fun isString(a: Any) Boolean {
    return if (a is String) true else false
}
```

## Casts inteligentes

Após verificar o tipo de um valor, devemos fazer um  cast explicito, o que resulta em um código duplicado

```java
if (obj instanceof String) {
    String str = (String) obj; // linha desnecessária
    str.length();
}
```

Em Kotlin podemos contar com a ajuda do compilador que fará o casting implicitamente da referência para o tipo mais especifico.

```kotlin
if (obj is String) {
    obj.length;
}
```

ou

```kotlin
fun isEmptyString(v: Any): Boolean {
    return v is String && v.lenth == 0;
}
```

e também:

```kotlin
fun isNotStringOrEmpty(v: Any): Boolean {
    return v !is String && v.lenth == 0;
} 
```

## Casting explícito

Em Kotlin também podemos explícitamente dizer de que tipo uma variável é utilizando o operador **as**

> A cada 10 minutos 1 operador novo é descoberto.

```kotlin
fun length(any: Any): Int {
    val v = any as String
    return v.length
}
```

## Evitando ClassCastException

Para evitar uma exception ao fazer um cast podemos usar o operador **?** para retornar null

```kotlin
val any = 1
val string: String? = any as? String //null
```

## When

Switch é coisa do passado, agora a moda é utilizar **When** programando [achar uma palavra que rime]

```kotlin
fun isZero(x) {
    when (x) {
        0 -> println("é zero")
        1 -> println("é 1")
        else -> println("vai além dos meus conhecimentos")
    }
}
```

> O When deve ser exaustivo e, desse modo, o compilador impõe que todas as condições devem ser atendidas e caso não for, a clausula **else** deve estar presente.

### Usando When para atribuir valor

```kotlin
fun isZero(x) {
    val isZero = when (x) {
        0 -> true
        else false
    }
}
```

> É possível retornar a resolução da expressão diretamente

### Variações

```kotlin
fun whatNumber(x: Int) {
    when (x) {
        1, 2 -> println("1 ou 2")
        in 3..8 -> println("entre 3 e 8")
        else -> println("não está mapeado")
    }
}
```

```kotlin
fun startsWithFoo(x: Any) {
    return when (x) {
        is String -> x.startsWith("Foo")
        else -> false
    }
}
```

### Sem argumentos

```kotlin
fun whenWithoutArgs(x: Int, y: Int) {
    when {
        x < y -> println("x é menor que y")
        x > y -> println("x é maior que y")
        else -> println("x é igual a y")
    }
}
```

## Retornos

O famoso e velho conhecido retorno

```kotlin
func soma(a: Int, b: Int) {
    return a + b
}
```

## Hierarquia de tipos

Em Kotlin, o tipo de nível mais alto se chama **Any**. É análogo ao tipo **Object** do Java.

Também temos o tipo **Unit**, que é análogo ao tipo **void** em java. 

> A declaração dele é opcional

E por ultimo o tipo **[Nothing]**.

Nothing em Kotlin significa um valor que não existe e pode ser usado para uma função que nunca termina de executar, como loops infinitos ou funções que disparam errors.

Vamos assumir como exemplo uma função que sempre dispara erro e vamos fazer uma análogia ao Java:

Java:
```java
void reportError() {
    throw new RuntimeException();
}
```

Kotlin:
```kotlin
fun reportError(): Nothing {
    throw RuntimeException()
}
```

O que acontece se chamarmos o código?

Java:
```java
int i = 0;
void exampleOne() {
    reportError(); // throws RuntimeException
    i = 1; Esse código não vai executar, mas não existem warnings informando isso.
}
```

Kotlin:
```kotlin
var i = 0;
fun exampleOne() {
    reportError(); // throws RuntimeException
    i = 1; // Teremos um warning de  'Unreachable code' aqui.
}
```

[Capítulo 7: Null safety]:(./capitulo-7-1)
[Nothing]:(https://medium.com/thoughts-overflow/kotlin-has-nothing-but-there-is-nothing-like-nothing-in-java-cab98e4f4d26)