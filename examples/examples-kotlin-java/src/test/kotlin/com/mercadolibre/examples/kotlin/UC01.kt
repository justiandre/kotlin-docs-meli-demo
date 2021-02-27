package com.mercadolibre.examples.kotlin

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

data class State(val name: String? = null)
data class City(val name: String? = null, val population: Int? = null, val state: State? = null)
data class Address(val street: String? = null, val city: City? = null)
data class Person(val name: String? = null, val age: Int? = null, val address: Address? = null)

class PersonService {

	companion object {
		const val AGE_LIMIT_FILTER = 18
		const val POPULATION_LIMIT_FILTER = 100000
	}

	fun getStates(persons: Collection<Person>) = persons
		.filter { it.age ?: 0 >= AGE_LIMIT_FILTER }
		.mapNotNull(Person::address)
		.mapNotNull(Address::city)
		.filter { it.population ?: 0 >= POPULATION_LIMIT_FILTER }
		.mapNotNull(City::state)
		.mapNotNull(State::name)
		.distinct()
		.sorted()
}


class UC01 {

	private val personService = PersonService()

	@Test
	fun `UC01_01`() {
		val persons = listOf(
			Person(name = "André Justi", age = null, address = null)
		)
		val states = personService.getStates(persons)
		Assertions.assertTrue(states.isEmpty())
	}

	@Test
	fun `UC01_02`() {
		val persons = listOf(
			Person(name = "André Justi", age = null, address = null),
			Person(name = "Maycow Antunes", age = null, address = null)
		)
		val states = personService.getStates(persons)
		Assertions.assertTrue(states.isEmpty())
	}

	@Test
	fun `UC01_03`() {
		val persons = listOf(
			Person(name = "Paulo Gustavo", age = PersonService.AGE_LIMIT_FILTER - 1, address = Address(street = "Doutor Carlos Maia", city = City(name = "Cosmopolis", population = PersonService.POPULATION_LIMIT_FILTER + 1, state = State(name = "SC"))))
		)
		val states = personService.getStates(persons)
		Assertions.assertTrue(states.isEmpty())
	}

	@Test
	fun `UC01_04`() {
		val persons = listOf(
			Person(name = "Wellington Macedo", age = PersonService.AGE_LIMIT_FILTER + 1, address = Address(street = "Miguel Dutra", city = City(name = "Palhoça", population = PersonService.POPULATION_LIMIT_FILTER - 1, state = State(name = "SC"))))
		)
		val states = personService.getStates(persons)
		Assertions.assertTrue(states.isEmpty())
	}


	@Test
	fun `UC01_05`() {
		val persons = listOf(
			Person(name = "André Justi", age = PersonService.AGE_LIMIT_FILTER + 10, address = Address(street = "Padre Chagas", city = City(name = "Florianópolis", population = PersonService.POPULATION_LIMIT_FILTER + 1, state = State(name = "SC"))))
		)
		val states = personService.getStates(persons)
		Assertions.assertEquals(listOf("SC"), states)
	}

	@Test
	fun `UC01_06`() {
		val persons = listOf(
			Person(name = "André Justi", age = PersonService.AGE_LIMIT_FILTER + 10, address = Address(street = "Padre Chagas", city = City(name = "Florianópolis", population = PersonService.POPULATION_LIMIT_FILTER + 1, state = State(name = "SC")))),
			Person(name = "Wellington Macedo", age = PersonService.AGE_LIMIT_FILTER + 1, address = Address(street = "Miguel Dutra", city = City(name = "Palhoça", population = PersonService.POPULATION_LIMIT_FILTER - 1, state = State(name = "SC")))),
			Person(name = "Paulo Gustavo", age = PersonService.AGE_LIMIT_FILTER + 1, address = Address(street = "Jose Angelo Peti", city = City(name = "Druta", population = PersonService.POPULATION_LIMIT_FILTER - 1, state = State(name = "SP")))),
			Person(name = "Maycow Antunes", age = PersonService.AGE_LIMIT_FILTER + 1, address = Address(street = "Dona Ema", city = City(name = "Rio Negrinho", population = PersonService.POPULATION_LIMIT_FILTER + 1, state = State(name = "RJ"))))
		)
		val states = personService.getStates(persons)
		Assertions.assertEquals(listOf("RJ", "SC"), states)
	}

	@Test
	fun `UC01_07`() {
		val persons = emptyList<Person>()
		val states = personService.getStates(persons)
		Assertions.assertTrue(states.isEmpty())
	}
}

