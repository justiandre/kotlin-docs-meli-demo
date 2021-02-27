package exemplos_golang

import (
	"sort"
	"testing"

	"github.com/stretchr/testify/assert"
)

type State struct {
	Name string
}

type City struct {
	Name       string
	Population int
	State      *State
}

type Address struct {
	Street string
	City   *City
}

type Person struct {
	Name    string
	Age     int
	Address *Address
}

const (
	AgeLimitFilter        = 18
	PopulationLimitFilter = 100000
)

func getStates(persons []Person) (states []string) {
	for _, person := range persons {
		if !isPersonOlderThanLimit(person) {
			continue
		}
		stateName := getStateNameByPersonAndPopulationGreaterLimit(person)
		if stateName==""{
			continue
		}
		if !containsElementInSlice(states, stateName){
			states = append(states, stateName)
		}
	}
	sort.Strings(states)
	return states
}

func containsElementInSlice(slices []string, element string) bool{
	for _,v := range slices{
		if v==element{
			return true
		}
	}
	return false
}

func isPersonOlderThanLimit(person Person) bool {
	return person.Age >= AgeLimitFilter
}

func getStateNameByPersonAndPopulationGreaterLimit(person Person) string {
	address := person.Address
	if address == nil {
		return ""
	}
	city := address.City
	if city == nil || city.Population < PopulationLimitFilter {
		return ""
	}
	if city.State == nil  {
		return ""
	}
	return city.State.Name
}

func Test_UC01_01(t *testing.T) {
	persons := []Person{
		{Name: "André Justi"},
	}
	states := getStates(persons)
	assert.Empty(t, states)
}

func Test_UC01_02(t *testing.T) {
	persons := []Person{
		{Name: "André Justi"},
		{Name: "Maycow Antunes"},
	}
	states := getStates(persons)
	assert.Empty(t, states)
}

func Test_UC01_03(t *testing.T) {
	persons := []Person{
		{Name: "Paulo Gustavo", Age: AgeLimitFilter - 1, Address: &Address{Street: "Doutor Carlos Maia", City: &City{Name: "Cosmopolis", Population: PopulationLimitFilter + 1, State: &State{Name: "SC"}}}},
	}
	states := getStates(persons)
	assert.Empty(t, states)
}

func Test_UC01_04(t *testing.T) {
	persons := []Person{
		{Name: "Wellington Macedo", Age: AgeLimitFilter + 1, Address: &Address{Street: "Miguel Dutra", City: &City{Name: "Palhoça", Population: PopulationLimitFilter - 1, State: &State{Name: "SC"}}}},
	}
	states := getStates(persons)
	assert.Empty(t, states)
}

func Test_UC01_05(t *testing.T) {
	persons := []Person{
		{Name: "André Justi", Age: AgeLimitFilter + 10, Address: &Address{Street: "Padre Chagas", City: &City{Name: "Florianópolis", Population: PopulationLimitFilter + 1, State: &State{Name: "SC"}}}},
	}
	states := getStates(persons)
	assert.Equal(t, []string{"SC"}, states)
}

func Test_UC01_06(t *testing.T) {
	persons := []Person{
		{Name: "André Justi", Age: AgeLimitFilter + 10, Address: &Address{Street: "Padre Chagas", City: &City{Name: "Florianópolis", Population: PopulationLimitFilter + 1, State: &State{Name: "SC"}}}},
		{Name: "Wellington Macedo", Age: AgeLimitFilter + 1, Address: &Address{Street: "Miguel Dutra", City: &City{Name: "Palhoça", Population: PopulationLimitFilter - 1, State: &State{Name: "SC"}}}},
		{Name: "Paulo Gustavo", Age: AgeLimitFilter + 1, Address: &Address{Street: "Jose Angelo Peti", City: &City{Name: "Druta", Population: PopulationLimitFilter - 1, State: &State{Name: "SP"}}}},
		{Name: "Maycow Antunes", Age: AgeLimitFilter + 1, Address: &Address{Street: "Dona Ema", City: &City{Name: "Rio Negrinho", Population: PopulationLimitFilter + 1, State: &State{Name: "RJ"}}}},
	}
	states := getStates(persons)
	assert.Equal(t, []string{"RJ", "SC"}, states)
}

func Test_UC01_07(t *testing.T) {
	var persons []Person
	states := getStates(persons)
	assert.Empty(t, states)
}
