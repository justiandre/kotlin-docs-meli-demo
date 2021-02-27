package com.mercadolibre.examples.java;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

class InvalidSalaryException extends Exception {
    public InvalidSalaryException(String message) {
        super(message);
    }
}

class InvalidSectorException extends Exception {
    public InvalidSectorException(String message) {
        super(message);
    }
}

class Sector {
    private String name;

    public Sector(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Collaborator {
    private String name;
    private Double salary;
    private Sector sector;

    public Collaborator(String name, Double salary, Sector sector) {
        this.name = name;
        this.salary = salary;
        this.sector = sector;
    }

    public Double getSalary() {
        return salary;
    }

    public Sector getSector() {
        return sector;
    }

    @Override
    public String toString() {
        return "Collaborator{" +
                "name='" + name + '\'' +
                '}';
    }
}

class SectorSummary {
    private Double total;
    private List<Collaborator> collaborators;

    public SectorSummary(Double total, List<Collaborator> collaborators) {
        this.total = total;
        this.collaborators = collaborators;
    }

    @Override
    public String toString() {
        return "SectorSummary{" +
                "total=" + total +
                ", collaborators=" + collaborators +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SectorSummary that = (SectorSummary) o;
        return Objects.equals(total, that.total) &&
                Objects.equals(collaborators, that.collaborators);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, collaborators);
    }
}

class Collaborators {
    private List<Collaborator> collaborators;

    public Collaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public boolean isSalariesValid() {
        return this.collaborators.stream().noneMatch(it -> it.getSalary() == null || it.getSalary() < 0);
    }

    public boolean isSectorsValid() {
        return this.collaborators.stream().noneMatch(it -> it.getSector() == null);
    }

    public List<Collaborator> get() {
        return this.collaborators;
    }
}

class SectorService {
    public Map<String, SectorSummary> compile(Collaborators collaborators) throws InvalidSalaryException, InvalidSectorException {
        validateCollaborators(collaborators);
        return collaborators.get()
                .stream()
                .collect(Collectors.groupingBy(c -> c.getSector().getName()))
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> parseCollaboratorsToSectorSummary(e.getValue())));
    }
    
    private SectorSummary parseCollaboratorsToSectorSummary(List<Collaborator> collaborators) {
        return new SectorSummary(
                collaborators
                        .stream()
                        .mapToDouble(Collaborator::getSalary).sum(),
                collaborators
                        .stream()
                        .sorted(Comparator.comparingDouble(Collaborator::getSalary))
                        .collect(Collectors.toList())
        );
    }

    private void validateCollaborators(Collaborators collaborators) throws InvalidSalaryException, InvalidSectorException {
        if (!collaborators.isSalariesValid()) {
            throw new InvalidSalaryException("salary is required");
        }
        if (!collaborators.isSectorsValid()) {
            throw new InvalidSectorException("sector is required");
        }
    }
}

public class UC03 {

    private SectorService service = new SectorService();

    @Test
    public void UC03_01() throws InvalidSalaryException, InvalidSectorException {
        Assertions.assertTrue(service.compile(new Collaborators(Collections.emptyList())).isEmpty());
    }

    @Test
    public void UC03_02() {
        List<Collaborator> collaborators = Collections.singletonList(
                new Collaborator("Paulo Gustavo", -1.0, new Sector("TTW01"))
        );
        Assertions.assertThrows(InvalidSalaryException.class, () -> {
            service.compile(new Collaborators(collaborators));
        });
    }

    @Test
    public void UC03_03() {
        List<Collaborator> collaborators = Collections.singletonList(
                new Collaborator("Paulo Gustavo", null, new Sector("TTW01"))
        );
        Assertions.assertThrows(InvalidSalaryException.class, () -> {
            service.compile(new Collaborators(collaborators));
        });
    }

    @Test
    public void UC03_04() {
        List<Collaborator> collaborators = Collections.singletonList(
                new Collaborator("Paulo Gustavo", 10.0, null)
        );
        Assertions.assertThrows(InvalidSectorException.class, () -> {
            service.compile(new Collaborators(collaborators));
        });
    }

    @Test
    public void UC03_05() throws InvalidSalaryException, InvalidSectorException {
        List<Collaborator> collaborators = Arrays.asList(
                new Collaborator("Wellington Macedo", 300000.0, new Sector("XTW04")),
                new Collaborator("Paulo Gustavo", 500000.0, new Sector("XTW04"))
        );
        Map<String, SectorSummary> compiled = service.compile(new Collaborators(collaborators));
        Map<String, SectorSummary> expected = Map.of("XTW04", new SectorSummary(
                800000.0,
                collaborators.stream()
                        .sorted(Comparator.comparingDouble(Collaborator::getSalary))
                        .collect(Collectors.toList())
        ));
        Assertions.assertEquals(expected.get("XTW04"), compiled.get("XTW04"));
    }

    @Test
    public void UC03_06() throws InvalidSalaryException, InvalidSectorException {
        List<Collaborator> collaborators = Arrays.asList(
                new Collaborator("Wellington Macedo", 500000.0, new Sector("XTW04")),
                new Collaborator("Maycow Antunes", 100000.0, new Sector("TTW01")),
                new Collaborator("André Justi", 300000.0, new Sector("XTW04"))
        );
        Map<String, SectorSummary> compiled = service.compile(new Collaborators(collaborators));
        Map<String, SectorSummary> expected = Map.of(
                "XTW04", new SectorSummary(
                        800000.0,
                        collaborators.stream()
                                .filter(it -> it.getSector().getName().equals("XTW04"))
                                .sorted(Comparator.comparingDouble(Collaborator::getSalary))
                                .collect(Collectors.toList())
                ),
                "TTW01", new SectorSummary(
                        100000.0,
                        collaborators.stream()
                                .filter(it -> it.getSector().getName().equals("TTW01"))
                                .sorted(Comparator.comparingDouble(Collaborator::getSalary))
                                .collect(Collectors.toList())));

        Assertions.assertEquals(expected.get("XTW04"), compiled.get("XTW04"));
        Assertions.assertEquals(expected.get("TTW01"), compiled.get("TTW01"));
    }

    @Test
    public void UC03_07() throws InvalidSalaryException, InvalidSectorException {
		List<Collaborator> collaborators = Arrays.asList(
				new Collaborator("André Justi", 500000.0, new Sector("XTW04")),
				new Collaborator("Paulo Gustavo", 100000.0, new Sector("TTW01")),
				new Collaborator("Wellington Macedo", 300000.0, new Sector("XTW04")),
				new Collaborator("Maycow Antunes", 300000.0, new Sector("TTW01"))
		);
		Map<String, SectorSummary> compiled = service.compile(new Collaborators(collaborators));
		Map<String, SectorSummary> expected = Map.of(
				"XTW04", new SectorSummary(
						800000.0,
						collaborators.stream()
								.filter(it -> it.getSector().getName().equals("XTW04"))
								.sorted(Comparator.comparingDouble(Collaborator::getSalary))
								.collect(Collectors.toList())
				),
				"TTW01", new SectorSummary(
						400000.0,
						collaborators.stream()
								.filter(it -> it.getSector().getName().equals("TTW01"))
								.sorted(Comparator.comparingDouble(Collaborator::getSalary))
								.collect(Collectors.toList())));

		Assertions.assertEquals(expected.get("XTW04"), compiled.get("XTW04"));
		Assertions.assertEquals(expected.get("TTW01"), compiled.get("TTW01"));
    }
}
