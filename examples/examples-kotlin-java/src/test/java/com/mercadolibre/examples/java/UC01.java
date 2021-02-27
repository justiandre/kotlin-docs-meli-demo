package com.mercadolibre.examples.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

class State {

	private final String name;


	public State(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}

class City {

	private final String name;
	private final Integer population;
	private final State state;

	public City(String name, Integer population, State state) {
		this.name = name;
		this.population = population;
		this.state = state;
	}

	public String getName() {
		return name;
	}

	public Integer getPopulation() {
		return population;
	}

	public State getState() {
		return state;
	}
}

class Address {

	private final String street;
	private final City city;

	public Address(String street, City city) {
		this.street = street;
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public City getCity() {
		return city;
	}
}

class Person {

	private final String name;
	private final Integer age;
	private final Address address;

	public Person(String name, Integer age, Address address) {
		this.name = name;
		this.age = age;
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public Address getAddress() {
		return address;
	}

	public Integer getAge() {
		return age;
	}
}

class PersonService {

	public static final Integer AGE_LIMIT_FILTER = 18;
	public static final Integer POPULATION_LIMIT_FILTER = 100000;

	public Collection<String> getStates(Collection<Person> persons) {
		if (persons == null) {//VER PARA FAZER COM OPTIONAL
			return new ArrayList<>();
		}
		return persons.stream()
			.filter(person -> person.getAge() != null && person.getAge() >= AGE_LIMIT_FILTER)
			.map(Person::getAddress)
			.map(Address::getCity)
			.filter(city -> city.getPopulation() != null && city.getPopulation() >= POPULATION_LIMIT_FILTER)
			.map(City::getState)
			.map(State::getName)
			.distinct()
			.sorted()
			.collect(Collectors.toList());
	}
}


public class UC01 {

	private PersonService personService = new PersonService();

	@Test
	public void UC01_01() {
		Collection<Person> persons = Arrays.asList(
			new Person("André Justi", null, null)
		);
		Collection<String> states = personService.getStates(persons);
		Assertions.assertTrue(states.isEmpty());
	}

	@Test
	public void UC01_02() {
		Collection<Person> persons = Arrays.asList(
			new Person("André Justi", null, null),
			new Person("Maycow Antunes", null, null)
		);
		Collection<String> states = personService.getStates(persons);
		Assertions.assertTrue(states.isEmpty());
	}

	@Test
	public void UC01_03() {
		Collection<Person> persons = Arrays.asList(
			new Person("Paulo Gustavo", PersonService.AGE_LIMIT_FILTER - 1, new Address("Doutor Carlos Maia", new City("Cosmopolis", PersonService.POPULATION_LIMIT_FILTER + 1, new State("SC"))))
		);
		Collection<String> states = personService.getStates(persons);
		Assertions.assertTrue(states.isEmpty());
	}

	@Test
	public void UC01_04() {
		Collection<Person> persons = Arrays.asList(
			new Person("Wellington Macedo", PersonService.AGE_LIMIT_FILTER + 1, new Address("Miguel Dutra", new City("Palhoça", PersonService.POPULATION_LIMIT_FILTER - 1, new State("SC"))))
		);
		Collection<String> states = personService.getStates(persons);
		Assertions.assertTrue(states.isEmpty());
	}


	@Test
	public void UC01_05() {
		Collection<Person> persons = Arrays.asList(
			new Person("André Justi", PersonService.AGE_LIMIT_FILTER + 10, new Address("Padre Chagas", new City("Florianópolis", PersonService.POPULATION_LIMIT_FILTER + 1, new State("SC"))))
		);
		Collection<String> states = personService.getStates(persons);
		Assertions.assertEquals(Arrays.asList("SC"), states);
	}

	@Test
	public void UC01_06() {
		Collection<Person> persons = Arrays.asList(
			new Person("André Justi", PersonService.AGE_LIMIT_FILTER + 10, new Address("Padre Chagas", new City("Florianópolis", PersonService.POPULATION_LIMIT_FILTER + 1, new State("SC")))),
			new Person("Wellington Macedo", PersonService.AGE_LIMIT_FILTER + 1, new Address("Miguel Dutra", new City("Palhoça", PersonService.POPULATION_LIMIT_FILTER - 1, new State("SC")))),
			new Person("Paulo Gustavo", PersonService.AGE_LIMIT_FILTER + 1, new Address("Jose Angelo Peti", new City("Druta", PersonService.POPULATION_LIMIT_FILTER - 1, new State("SP")))),
			new Person("Maycow Antunes", PersonService.AGE_LIMIT_FILTER + 1, new Address("Dona Ema", new City("Rio Negrinho", PersonService.POPULATION_LIMIT_FILTER + 1, new State("RJ"))))
		);
		Collection<String> states = personService.getStates(persons);
		Assertions.assertEquals(Arrays.asList("RJ", "SC"), states);
	}

	@Test
	public void UC01_07() {
		Collection<Person> persons = Arrays.asList();
		Collection<String> states = personService.getStates(persons);
		Assertions.assertTrue(states.isEmpty());
	}
}
