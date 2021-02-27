package exemplos_golang

import (
	"errors"
	"github.com/stretchr/testify/assert"
	"reflect"
	"sort"
	"testing"
)

type Collaborator04 struct {
	Name string
	CompanyId string
}

type Office struct {
	Name string
	CompanyId string
}

type OfficesService struct {}

type Company struct {
	CompanyId string
	Collaborators []string
	Offices []string
}

func validateCollaborators(collaborators []*Collaborator04) bool {
	for _, collaborator := range collaborators {
		if collaborator.CompanyId == "" {
			return false
		}
	}
	return true
}

func validateOffices(offices []*Office) bool {
	for _, office := range offices {
		if office.CompanyId == "" {
			return false
		}
	}
	return true
}

func (o *OfficesService) Compile(collaborators []*Collaborator04, offices []*Office) ([]*Company, error) {
	var companies []*Company
	if collaborators == nil && offices == nil {
		return companies, nil
	}
	if err := validate(collaborators, offices); err != nil {
		return nil, err
	}

	companyNames := unique(append(getCollaboratorsCompanies(collaborators), getOfficesCompanies(offices)...))
	for _, companyId := range companyNames {
		companies = append(companies, &Company{
			CompanyId: companyId,
			Collaborators: getCompanyCollaborators(companyId, collaborators),
			Offices: getCompanyOffices(companyId, offices),
		})
	}
	return companies, nil
}

func validate(collaborators []*Collaborator04, offices []*Office) error {
	if !validateCollaborators(collaborators) || !validateOffices(offices) {
		return errors.New("companyId is required")
	}
	return nil
}

func getCollaboratorsCompanies(collaborators []*Collaborator04) []string {
	var companies []string
	for _, item := range collaborators {
		companies = append(companies, item.CompanyId)
	}
	return companies
}

func getOfficesCompanies(offices []*Office) []string {
	var companies []string
	for _, item := range offices {
		companies = append(companies, item.CompanyId)
	}
	return companies
}

func getCompanyCollaborators(companyId string, collaborators []*Collaborator04) []string {
	var companyCollaborators []string
	for _, item := range collaborators {
		if item.CompanyId == companyId {
			companyCollaborators = append(companyCollaborators, item.Name)
		}
	}
	return companyCollaborators
}

func getCompanyOffices(companyId string, offices []*Office) []string {
	var companyOffices []string
	for _, item := range offices {
		if item.CompanyId == companyId {
			companyOffices = append(companyOffices, item.Name)
		}
	}
	return companyOffices
}

func unique(items []string) []string {
	keys := make(map[string]bool)
	list := []string{}
	for _, entry := range items {
		if _, value := keys[entry]; !value {
			keys[entry] = true
			list = append(list, entry)
		}
	}
	return list
}

func Equal(a, b []string) bool {
	if len(a) != len(b) {
		return false
	}
	sort.Strings(a)
	sort.Strings(b)

	for i, v := range a {
		if v != b[i] {
			return false
		}
	}
	return true
}

func Test_UC04_01(t *testing.T) {
	var collaborators = []*Collaborator04{
		{
			Name: "Maycow Antunes",
			CompanyId: "Meli",
		},
	}
	var offices = []*Office{
		{
			Name: "Meli Floripa",
			CompanyId: "Meli",
		},
	}
	var expected = []*Company{
		{
			CompanyId: "Meli",
			Collaborators: []string{ "Maycow Antunes" },
			Offices: []string{ "Meli Floripa" },
		},
	}
	var service = &OfficesService{}
	var companies, err = service.Compile(collaborators, offices)
	assert.Nil(t, err)
	assert.Equal(t, expected, companies)
}

func Test_UC04_02(t *testing.T) {
	var collaborators = []*Collaborator04{
		{
			Name: "Maycow Antunes",
		},
	}
	var offices = []*Office{
		{
			Name: "Meli Floripa",
			CompanyId: "Meli",
		},
	}
	var service = &OfficesService{}
	var companies, err = service.Compile(collaborators, offices)
	assert.EqualError(t, errors.New("companyId is required"), err.Error())
	assert.Empty(t, companies)
}

func Test_UC04_03(t *testing.T) {
	var collaborators = []*Collaborator04{
		{
			Name: "Maycow Antunes",
			CompanyId: "Meli",
		},
	}
	var offices = []*Office{
		{
			Name: "Meli Floripa",
		},
	}
	var service = &OfficesService{}
	var companies, err = service.Compile(collaborators, offices)
	assert.EqualError(t, errors.New("companyId is required"), err.Error())
	assert.Empty(t, companies)
}

func Test_UC04_04(t *testing.T) {
	var collaborators = []*Collaborator04{
		{
			Name: "Maycow Antunes",
		},
	}
	var offices = []*Office{
		{
			Name: "Meli Floripa",
		},
	}
	var service = &OfficesService{ }
	var companies, err = service.Compile(collaborators, offices)
	assert.EqualError(t, errors.New("companyId is required"), err.Error())
	assert.Empty(t, companies)
}

func Test_UC04_05(t *testing.T) {
	var collaborators = []*Collaborator04{
		{
			Name: "André Justi",
			CompanyId: "Meli",
		},
		{
			Name: "Wellington Macedo",
			CompanyId: "Meli",
		},
	}
	var offices = []*Office{
		{
			Name: "Meli Floripa",
			CompanyId: "Meli",
		},
	}
	var expected = []*Company{
		{
			CompanyId: "Meli",
			Collaborators: []string{ "André Justi", "Wellington Macedo", },
			Offices: []string{ "Meli Floripa" },
		},
	}
	var service = &OfficesService{}
	var companies, err = service.Compile(collaborators, offices)
	assert.Nil(t, err)
	assert.Equal(t, expected, companies)
}

func Test_UC04_06(t *testing.T) {
	var collaborators = []*Collaborator04{
		{
			Name: "Maycow Antunes",
			CompanyId: "Meli",
		},
		{
			Name: "André Justi",
			CompanyId: "Meli",
		},
		{
			Name: "Paulo Gustavo",
			CompanyId: "Meli Envios",
		},
	}
	var offices = []*Office{
		{
			Name: "Meli Floripa",
			CompanyId: "Meli",
		},
		{
			Name: "Meli Cidade",
			CompanyId: "Meli",
		},
	}
	var expected = []*Company{
		{
			CompanyId: "Meli",
			Collaborators: []string{ "Maycow Antunes", "André Justi", },
			Offices: []string{ "Meli Cidade", "Meli Floripa" },
		},
		{
			CompanyId: "Meli Envios",
			Collaborators: []string{ "Paulo Gustavo", },
		},
	}
	var service = &OfficesService{}
	var companies, err = service.Compile(collaborators, offices)
	assert.Nil(t, err)
	assert.Equal(t, len(expected), len(companies))
	for i, item := range expected {
		assert.True(t, reflect.DeepEqual(item.CompanyId, companies[i].CompanyId))
		assert.True(t, Equal(item.Offices, companies[i].Offices))
		assert.True(t, Equal(item.Collaborators, companies[i].Collaborators))
	}
}

func Test_UC04_07(t *testing.T) {
	var service = &OfficesService{}
	var companies, err = service.Compile(nil, nil)
	assert.Nil(t, err)
	assert.Empty(t, companies)
}
