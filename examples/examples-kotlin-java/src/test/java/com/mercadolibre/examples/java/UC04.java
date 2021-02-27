package com.mercadolibre.examples.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

class Collaborator04 {
	private String name;
	private String companyId;

	public String getName() {
		return name;
	}

	public String getCompanyId() {
		return companyId;
	}

	public Collaborator04(String name, String companyId) {
		this.name = name;
		this.companyId = companyId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Collaborator04 that = (Collaborator04) o;
		return Objects.equals(name, that.name) &&
				Objects.equals(companyId, that.companyId);
	}
}

class Office {
	private String name;
	private String companyId;

	public String getName() {
		return name;
	}

	public String getCompanyId() {
		return companyId;
	}

	public Office(String name, String companyId) {
		this.name = name;
		this.companyId = companyId;
	}

	@Override
	public String toString() {
		return "Office{" +
				"name='" + name + '\'' +
				", companyId='" + companyId + '\'' +
				'}';
	}
}

class Company {
	private String companyId;
	private List<String> collaborators;
	private List<String> offices;

	public Company(String companyId, List<String> collaborators, List<String> offices) {
		this.companyId = companyId;
		this.collaborators = collaborators;
		this.offices = offices;
	}

	public String getCompanyId() {
		return companyId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Company company = (Company) o;
		return Objects.equals(companyId, company.companyId) &&
				Objects.equals(collaborators, company.collaborators) &&
				Objects.equals(offices, company.offices);
	}
}

class InvalidCompanyException extends IllegalArgumentException {
	public InvalidCompanyException() {
		super("companyId is required");
	}
}

class OfficesService {
	public List<Company> compile(List<Collaborator04> collaborators, List<Office> offices) throws InvalidCompanyException {
		if (collaborators == null || offices == null) {
			return null;
		}
		validate(collaborators, offices);
		Map<String, List<Collaborator04>> collaboratorsCompanies = collaborators
				.stream()
				.collect(Collectors.groupingBy(Collaborator04::getCompanyId));
		Map<String, List<Office>> officesCompanies = offices
				.stream()
				.collect(Collectors.groupingBy(Office::getCompanyId));
		HashSet<String> companyIds = new HashSet<String>();
		companyIds.addAll(collaboratorsCompanies.keySet());
		companyIds.addAll(officesCompanies.keySet());
		return companyIds
					.stream()
					.map(it -> parseCompany(it, collaboratorsCompanies.get(it), officesCompanies.get(it)))
					.sorted(Comparator.comparing(Company::getCompanyId)).collect(Collectors.toList());
	}

	private Company parseCompany(String companyId, List<Collaborator04> collaborators, List<Office> offices) {
		return new Company(
			companyId,
			collaborators == null || collaborators.isEmpty() ? null : collaborators
				.stream()
				.map(Collaborator04::getName)
				.sorted()
				.collect(Collectors.toList()),
				offices == null || offices.isEmpty() ? null : offices
					.stream()
					.map(Office::getName)
					.sorted()
					.collect(Collectors.toList()));
	}

	private void validate(List<Collaborator04> collaborators, List<Office> offices) throws InvalidCompanyException {
		boolean collaboratorsAreValid = collaborators.stream().noneMatch(it -> it.getCompanyId() == null);
		boolean officesAreValid = offices.stream().noneMatch(it -> it.getCompanyId() == null);
		if (!collaboratorsAreValid || !officesAreValid) {
			throw new InvalidCompanyException();
		}
	}
}

public class UC04 {
	@Test
	public void UC04_01() throws InvalidCompanyException {
		List<Collaborator04> collaborators = Collections.singletonList(
			new Collaborator04("Maycow Antunes","Meli")
		);
		List<Office> offices = Collections.singletonList(new Office("Meli Floripa", "Meli"));
		OfficesService service = new OfficesService();
		List<Company> expected = List.of(
			new Company("Meli", List.of("Maycow Antunes"), List.of("Meli Floripa"))
		);
		Assertions.assertEquals(expected, service.compile(collaborators, offices));
	}

	@Test
	public void UC04_02() {
		List<Collaborator04> collaborators = Collections.singletonList(
			new Collaborator04("Maycow Antunes",null)
		);
		List<Office> offices = Collections.singletonList(new Office("Meli Floripa", "Meli"));
		OfficesService service = new OfficesService();
		Assertions.assertThrows(InvalidCompanyException.class, () -> service.compile(collaborators, offices));
	}

	@Test
	public void UC04_03() {
		List<Collaborator04> collaborators = Collections.singletonList(
				new Collaborator04("Maycow Antunes","Meli")
		);
		List<Office> offices = Collections.singletonList(new Office("Meli Floripa", null));
		OfficesService service = new OfficesService();
		Assertions.assertThrows(InvalidCompanyException.class, () -> service.compile(collaborators, offices));
	}

	@Test
	public void UC04_04() {
		List<Collaborator04> collaborators = Collections.singletonList(
			new Collaborator04("Maycow Antunes",null)
		);
		List<Office> offices = Collections.singletonList(new Office("Meli Floripa", null));
		OfficesService service = new OfficesService();
		Assertions.assertThrows(InvalidCompanyException.class, () -> service.compile(collaborators, offices));
	}

	@Test
	public void UC04_05() throws InvalidCompanyException {
		List<Collaborator04> collaborators = List.of(
			new Collaborator04("André Justi","Meli"),
			new Collaborator04("Wellington Macedo","Meli")
		);
		List<Office> offices = Collections.singletonList(new Office("Meli Floripa", "Meli"));
		OfficesService service = new OfficesService();
		List<Company> expected = List.of(
				new Company("Meli", List.of("André Justi", "Wellington Macedo"), List.of("Meli Floripa"))
		);
		Assertions.assertEquals(expected, service.compile(collaborators, offices));
	}

	@Test
	public void UC04_06() throws InvalidCompanyException {
		List<Collaborator04> collaborators = new ArrayList(Arrays.asList(
				new Collaborator04("Maycow Antunes","Meli"),
				new Collaborator04("Wellington Macedo","Meli"),
				new Collaborator04("Paulo Gustavo","Meli Envios")
			)
		);
		List<Office> offices = new ArrayList(Arrays.asList(
				new Office("Meli Floripa", "Meli"),
				new Office("Meli Cidade", "Meli")
			)
		);
		OfficesService service = new OfficesService();
		List<Company> expected = List.of(
			new Company(
				"Meli",
				List.of("Maycow Antunes", "Wellington Macedo"),
				List.of("Meli Cidade", "Meli Floripa")
			),
		new Company("Meli Envios", List.of("Paulo Gustavo"), null)
		);
		List<Company> c = service.compile(collaborators, offices);
		Assertions.assertEquals(expected, c);
	}

	@Test
	public void UC04_07() throws InvalidCompanyException {
		OfficesService service = new OfficesService();
		Assertions.assertNull(service.compile(null, null));
	}
}
