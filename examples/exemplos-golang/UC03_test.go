package exemplos_golang

import (
	"sort"
	"testing"

	"github.com/mercadolibre/fury_go-ucs-toolkit/pkg/errors"
	"github.com/stretchr/testify/assert"
)

type Sector struct {
	Name string
}

type Collaborator struct {
	Name   string
	Salary *float64
	Sector *Sector
}

type Collaborators []Collaborator

func (c Collaborators) isSalariesValid() bool {
	for _, v := range c {
		if v.Salary == nil {
			return false
		}
		salary := *v.Salary
		if salary < 0 {
			return false
		}
	}
	return true
}

func (c Collaborators) isSectorsValid() bool {
	for _, v := range c {
		if v.Sector == nil {
			return false
		}
	}
	return true
}

type SectorSummary struct {
	Total         float64
	Collaborators []Collaborator
}

type SectorService struct{}

func (s *SectorService) Compile(collaborators Collaborators) (result map[string]SectorSummary, err error) {
	if !collaborators.isSalariesValid() {
		return result, errors.New("salary is required")
	}
	if !collaborators.isSectorsValid() {
		return result, errors.New("sector is required")
	}
	result = make(map[string]SectorSummary)
	for _, v := range collaborators {
		if _, ok := result[v.Sector.Name]; !ok {
			result[v.Sector.Name] = SectorSummary{}
		}
		summary := result[v.Sector.Name]
		summary.Total += *v.Salary
		summary.Collaborators = append(summary.Collaborators, v)

		result[v.Sector.Name] = summary
	}
	s.sortSectorSummaryBySalary(result)
	return
}

func (*SectorService) sortSectorSummaryBySalary(result map[string]SectorSummary) {
	for _, v := range result {
		sort.SliceStable(v.Collaborators, func(i, j int) bool {
			is := *v.Collaborators[i].Salary
			js := *v.Collaborators[j].Salary
			return is > js
		})
	}
}

// --------------------------------- test content -------------------------------------

var (
	negativeSalary = float64(-1)
	salary100000   = float64(100000)
	salary300000   = float64(300000)
	salary500000   = float64(500000)
	TTW01          = Sector{Name: "TTW01"}
	XTW04          = Sector{Name: "XTW04"}
)

func Test_UC03_01(t *testing.T) {
	service := SectorService{}
	result, _ := service.Compile(Collaborators{})
	assert.Equal(t, 0, len(result))
}

func Test_UC03_02(t *testing.T) {
	service := SectorService{}
	_, err := service.Compile(Collaborators{{Salary: &negativeSalary}})
	assert.EqualError(t, errors.New("salary is required"), err.Error())
}

func Test_UC03_03(t *testing.T) {
	service := SectorService{}
	_, err := service.Compile(Collaborators{{Salary: nil}})
	assert.EqualError(t, errors.New("salary is required"), err.Error())
}

func Test_UC03_04(t *testing.T) {
	service := SectorService{}
	_, err := service.Compile(Collaborators{{Salary: &salary100000}})
	assert.EqualError(t, errors.New("sector is required"), err.Error())
}

func Test_UC03_05(t *testing.T) {
	service := SectorService{}
	collaborators := Collaborators{
		{Name: "Wellington Macedo", Salary: &salary300000, Sector: &XTW04},
		{Name: "Paulo Gustavo", Salary: &salary500000, Sector: &XTW04},
	}
	compiled, err := service.Compile(collaborators)
	expected := map[string]SectorSummary{"XTW04": {
		Total:         800000,
		Collaborators: collaborators,
	}}
	sortDesc(expected)
	assert.NoError(t, err)
	assert.Equal(t, expected, compiled)
}

func Test_UC03_06(t *testing.T) {
	service := SectorService{}
	collaborators := Collaborators{
		{Name: "Wellington Macedo", Salary: &salary500000, Sector: &XTW04},
		{Name: "Maycow Antunes", Salary: &salary100000, Sector: &TTW01},
		{Name: "André Justi", Salary: &salary300000, Sector: &XTW04},
	}
	compiled, err := service.Compile(collaborators)
	expected := map[string]SectorSummary{
		"XTW04": {
			Total:         800000,
			Collaborators: filterCollaborators(collaborators, "XTW04"),
		},
		"TTW01": {
			Total:         100000,
			Collaborators: filterCollaborators(collaborators, "TTW01"),
		}}
	sortDesc(expected)
	assert.NoError(t, err)
	assert.Equal(t, expected, compiled)
}

func Test_UC03_07(t *testing.T) {
	service := SectorService{}
	collaborators := Collaborators{
		{Name: "André Justi", Salary: &salary500000, Sector: &XTW04},
		{Name: "Paulo Gustavo", Salary: &salary100000, Sector: &TTW01},
		{Name: "Wellington Macedo", Salary: &salary300000, Sector: &XTW04},
		{Name: "Maycow Antunes", Salary: &salary300000, Sector: &TTW01},
	}
	compiled, err := service.Compile(collaborators)
	expected := map[string]SectorSummary{
		"XTW04": {
			Total:         800000,
			Collaborators: filterCollaborators(collaborators, "XTW04"),
		},
		"TTW01": {
			Total:         400000,
			Collaborators: filterCollaborators(collaborators, "TTW01"),
		}}
	sortDesc(expected)
	assert.NoError(t, err)
	assert.Equal(t, expected, compiled)
}

func filterCollaborators(c Collaborators, sector string) (filtered Collaborators) {
	for _, v := range c {
		if v.Sector.Name == sector {
			filtered = append(filtered, v)
		}
	}
	return
}

// Função para auxiliar no teste
func sortDesc(result map[string]SectorSummary) {
	for _, v := range result {
		sort.SliceStable(v.Collaborators, func(i, j int) bool {
			is := *v.Collaborators[i].Salary
			js := *v.Collaborators[j].Salary
			return is > js
		})
	}
}
