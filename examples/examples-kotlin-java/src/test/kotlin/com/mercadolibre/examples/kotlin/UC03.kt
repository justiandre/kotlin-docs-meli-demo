package com.mercadolibre.examples.kotlin

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

data class Sector(val name: String)
data class Collaborator(val name: String, val salary: Double?, val sector: Sector? = null)
data class SectorSummary(val total: Double, val collaborators: List<Collaborator>)

class InvalidSalaryException : IllegalArgumentException("salary is required")
class InvalidSectorException : IllegalArgumentException("sector is required")

class Collaborators(private val collaborators: List<Collaborator>) {
	fun isSalariesValid() = collaborators.none { it.salary == null || it.salary <= 0 }
	fun isSectorsValid() = collaborators.none { it.sector == null }
	fun get() = collaborators
}

class SectorService {
	fun compile(collaborators: Collaborators): Map<String?, SectorSummary> {
		validateCollaborators(collaborators)
		return collaborators.get()
				.groupBy { it.sector?.name }
				.mapValues {  parseCollaboratorsToSectorSummary(it.value) }
	}

	private fun parseCollaboratorsToSectorSummary(collaborators: List<Collaborator>) = SectorSummary(
			total = collaborators.sumByDouble { it.salary!! },
			collaborators = collaborators.sortedByDescending { it.salary }
	)

	private fun validateCollaborators(collaborators: Collaborators) {
		if (!collaborators.isSalariesValid()) {
			throw InvalidSalaryException()
		}
		if (!collaborators.isSectorsValid()) {
			throw InvalidSectorException()
		}
	}
}

class UC03 {

	private val service = SectorService()

	@Test
	fun `UC03_01`() {
		val collaborators = Collaborators(emptyList())
		Assertions.assertTrue(service.compile(collaborators).isEmpty())
	}

	@Test
	fun `UC03_02`() {
		val collaborators = Collaborators(listOf(Collaborator("Paulo", -1.0, Sector("TTW01"))))
		Assertions.assertThrows(InvalidSalaryException::class.java) {
			service.compile(collaborators)
		}
	}

	@Test
	fun `UC03_03`() {
		val collaborators = Collaborators(listOf(Collaborator("Paulo", null, Sector("TTW01"))))
		Assertions.assertThrows(InvalidSalaryException::class.java) {
			service.compile(collaborators)
		}
	}

	@Test
	fun `UC03_04`() {
		val collaborators = Collaborators(listOf(Collaborator("Paulo", 10.0, null)))
		Assertions.assertThrows(InvalidSectorException::class.java) {
			service.compile(collaborators)
		}
	}

	@Test
	fun `UC03_05`() {
		val collaborators = listOf(
				Collaborator("Wellington Macedo", 300000.00, Sector("XTW04")),
				Collaborator("Paulo Gustavo", 500000.00, Sector("XTW04"))
		)
		val compiled = service.compile(Collaborators(collaborators))
		val expected = mapOf(
				"XTW04" to SectorSummary(800000.00, collaborators.sortedByDescending { it.salary })
		)
		Assertions.assertEquals(expected, compiled)
	}

	@Test
	fun `UC03_06`() {
		val collaborators = listOf(
				Collaborator("Wellington Macedo", 500000.00, Sector("XTW04")),
				Collaborator("Maycow Antunes", 100000.00, Sector("TTW01")),
				Collaborator("André Justi", 300000.00, Sector("XTW04"))
		)
		val compiled = service.compile(Collaborators(collaborators))
		val expected = mapOf(
				"XTW04" to SectorSummary(800000.00, collaborators
						.filter { it.sector?.name == "XTW04" }
						.sortedByDescending { it.salary }),
				"TTW01" to SectorSummary(100000.00, collaborators
						.filter { it.sector?.name == "TTW01" })
		)
		Assertions.assertEquals(expected, compiled)
	}

	@Test
	fun `UC03_07`() {
		val collaborators = listOf(
				Collaborator("André Justi", 500000.00, Sector("XTW04")),
				Collaborator("Paulo Gustavo", 100000.00, Sector("TTW01")),
				Collaborator("Wellington Macedo", 300000.00, Sector("XTW04")),
				Collaborator("Maycow Antunes", 300000.00, Sector("TTW01"))
		)
		val compiled = service.compile(Collaborators(collaborators))
		val expected = mapOf(
				"XTW04" to SectorSummary(800000.00, collaborators
						.filter { it.sector?.name == "XTW04" }
						.sortedByDescending { it.salary }),
				"TTW01" to SectorSummary(400000.00, collaborators
						.filter { it.sector?.name == "TTW01" }
						.sortedByDescending { it.salary })
		)
		Assertions.assertEquals(expected, compiled)
	}
}

