package com.mercadolibre.examples.java;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UC02 {

    static class InvalidFibonacciSequenceException extends IllegalArgumentException {

        InvalidFibonacciSequenceException() {
            super("you should inform a valid fibonacci sequence");
        }
    }

	static IntStream fibonacci() {
		return Stream.iterate(new int[]{0, 1}, t -> new int[]{t[1], t[0] + t[1]})
				.map(t -> t[0])
				.mapToInt(Integer::intValue);

	}

	static List<Integer> fibonacciUntil(Integer boundary) {
		return fibonacci()
				.takeWhile(sequence -> {
					if (boundary > sequence) return true;
					else if (boundary == sequence) return false;
					else throw new InvalidFibonacciSequenceException();
				})
				.boxed()
				.collect(toList());
	}

	static class Expect {
		private Integer boundary;

		Expect(Integer boundary) {
			this.boundary = boundary;
		}

		void expect(Integer...expected) {
			assertEquals(asList(expected), fibonacciUntil(boundary));
		}
	}

	static Expect whenFibonacciUntil(Integer boundary) {
		return new Expect(boundary);
	}

	@Test
	public void UC02_01() {
		whenFibonacciUntil(5).expect(0, 1, 1, 2, 3);
	}

	@Test
	public void UC02_02() {
        whenFibonacciUntil(8).expect(0, 1, 1, 2, 3, 5);
	  }

	@Test
	public void UC02_03() {
        whenFibonacciUntil(13).expect(0, 1, 1, 2, 3, 5, 8);
	}

	@Test
	public void UC02_04() {
        whenFibonacciUntil(21).expect(0, 1, 1, 2, 3, 5, 8, 13);
	}

	@Test
	public void UC02_05() {
        whenFibonacciUntil(34).expect(0, 1, 1, 2, 3, 5, 8, 13, 21);
	}

	@Test
	public void UC02_06() {
        whenFibonacciUntil(55).expect(0, 1, 1, 2, 3, 5, 8, 13, 21, 34);
	}

	@Test
	public void UC02_07() {
        assertThrows(InvalidFibonacciSequenceException.class, () -> fibonacciUntil(4));
	}
}