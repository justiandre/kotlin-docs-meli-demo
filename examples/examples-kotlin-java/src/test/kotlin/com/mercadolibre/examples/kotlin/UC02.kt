package com.mercadolibre.examples.kotlin

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.lang.IllegalArgumentException

class InvalidFibonacciSequenceException : IllegalArgumentException("you should inform a valid fibonacci sequence")

fun fibonacci() = sequence {
	var terms = Pair(0, 1)
	while (true) {
		yield(terms.first)
		terms = Pair(terms.second, terms.first + terms.second)
	}
}

fun fibonacciUntil(boundary: Int) =
		fibonacci()
				.takeWhile {
					when {
						boundary > it -> true
						boundary == it -> false
						else -> throw InvalidFibonacciSequenceException()
					}
				}
				.toList()

class Expect(private val boundary: Int) {
	fun expect(vararg expected: Int) = assertEquals(expected.asList(), fibonacciUntil(boundary))
}

fun whenFibonacciUntil(boundary: Int) = Expect(boundary)

class UC02 {

	@Test
	fun `UC02 01`() {
		whenFibonacciUntil(5).expect(0, 1, 1, 2, 3)
	}

	@Test
	fun `UC02 02`() {
		whenFibonacciUntil(8).expect(0, 1, 1, 2, 3, 5)
	}

	@Test
	fun `UC02 03`() {
		whenFibonacciUntil(13).expect(0, 1, 1, 2, 3, 5, 8)
	}

	@Test
	fun `UC02 04`() {
		whenFibonacciUntil(21).expect(0, 1, 1, 2, 3, 5, 8, 13)
	}

	@Test
	fun `UC02 05`() {
		whenFibonacciUntil(34).expect(0, 1, 1, 2, 3, 5, 8, 13, 21)
	}

	@Test
	fun `UC02 06`() {
		whenFibonacciUntil(55).expect(0, 1, 1, 2, 3, 5, 8, 13, 21, 34)
	}

	@Test
	fun `UC02 07`() {
		assertThrows<InvalidFibonacciSequenceException> { fibonacciUntil(4) }
	}
}

