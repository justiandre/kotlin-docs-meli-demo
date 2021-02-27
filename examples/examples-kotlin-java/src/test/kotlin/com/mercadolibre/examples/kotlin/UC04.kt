package com.mercadolibre.examples.kotlin

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

data class Collaborator04(val name: String, val companyId: String?)
data class Office(val name: String, val companyId: String?)
data class Company(val companyId: String, val collaborators: List<String>?, val offices: List<String>?)

class InvalidCompanyException : IllegalArgumentException("companyId is required")


class CompanyService() {

	fun compile(collaborators: List<Collaborator04>, offices: List<Office>): List<Company> {
		validate(collaborators, offices)
		val collaboratorsCompanies = collaborators.groupBy(Collaborator04::companyId)
		val collaboratorsOffices = offices.groupBy(Office::companyId)
		return (collaboratorsCompanies.keys + collaboratorsOffices.keys)
				.distinct()
				.mapNotNull { parseCompany(it!!, collaboratorsCompanies[it], collaboratorsOffices[it]) }
				.sortedBy(Company::companyId)
	}

	private fun parseCompany(company: String, collaborators: List<Collaborator04>?, offices: List<Office>?) = Company(
			companyId = company,
			collaborators = collaborators?.map(Collaborator04::name)?.sorted(),
			offices = offices?.map(Office::name)?.sorted()
	)
}

private fun validate(collaborators: List<Collaborator04>, offices: List<Office>) {
	val collaboratorsAreValid = collaborators?.none { it.companyId == null }
	val officesAreValid = offices?.none { it.companyId == null }
	if (!collaboratorsAreValid || !officesAreValid) {
		throw InvalidCompanyException()
	}
}

class UC04 {

	private val companyService = CompanyService()

	@Test
	fun `UC04_01`() {
		val collaborators = listOf(Collaborator04("Maycow Antunes", "Meli"))
		val offices = listOf(Office("Meli Floripa", "Meli"))
		val companies = companyService.compile(collaborators, offices)
		val expected = listOf(
				Company("Meli", listOf("Maycow Antunes"), listOf("Meli Floripa"))
		)
		assertCompanies(expected, companies)
	}

	@Test
	fun `UC04_02`() {
		val collaborators = listOf(Collaborator04("Paulo Gustavo", null))
		val offices = listOf(Office("Meli Floripa", "Meli"))
		Assertions.assertThrows(InvalidCompanyException::class.java) {
			companyService.compile(collaborators, offices)
		}
	}

	@Test
	fun `UC04_03`() {
		val collaborators = listOf(Collaborator04("André Justi", null))
		val offices = listOf(Office("Meli Floripa", null))
		Assertions.assertThrows(InvalidCompanyException::class.java) {
			companyService.compile(collaborators, offices)
		}
	}

	@Test
	fun `UC04_04`() {
		val collaborators = listOf(Collaborator04("Paulo Gustavo", null))
		val offices = listOf(Office("Meli Floripa", null))
		Assertions.assertThrows(InvalidCompanyException::class.java) {
			companyService.compile(collaborators, offices)
		}
	}

	@Test
	fun `UC04_05`() {
		val collaborators = listOf(
				Collaborator04("André Justi", "Meli"),
				Collaborator04("Wellington Macedo", "Meli")
		)
		val offices = listOf(
				Office("Meli Floripa", "Meli")
		)
		val companies = companyService.compile(collaborators, offices)
		val expected = listOf(
				Company("Meli", listOf("André Justi", "Wellington Macedo"), listOf("Meli Floripa"))
		)
		assertCompanies(expected, companies)
	}

	@Test
	fun `UC04_06`() {
		val collaborators = listOf(
				Collaborator04("Maycow Antunes", "Meli"),
				Collaborator04("André Justi", "Meli"),
				Collaborator04("Paulo Gustavo", "Meli Envios")
		)
		val offices = listOf(
				Office("Meli Floripa", "Meli"),
				Office("Meli Cidade", "Meli")
		)
		val companies = companyService.compile(collaborators, offices)
		val expected = listOf(
				Company("Meli Envios", listOf("Paulo Gustavo"), null),
				Company("Meli", listOf("André Justi", "Maycow Antunes"), listOf("Meli Cidade", "Meli Floripa"))
		)
		assertCompanies(expected, companies)
	}

	@Test
	fun `UC04_07`() {
		val collaborators = emptyList<Collaborator04>()
		val offices = listOf<Office>()
		val companies = companyService.compile(collaborators, offices)
		assertCompanies(listOf(), companies)
	}

	private fun assertCompanies(expected: List<Company>, contains: List<Company>) {
		Assertions.assertTrue(expected.all { contains.contains(it) })
	}
}
