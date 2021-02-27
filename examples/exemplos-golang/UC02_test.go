package exemplos_golang

import (
	"testing"

	"github.com/magiconair/properties/assert"
	"github.com/mercadolibre/fury_go-ucs-toolkit/pkg/errors"
)

func fibonnaci() func() int {
	a, b := 0, 1
	return func() int {
		a, b = b, a+b
		return a
	}
}

func fibonnaciUntil(boundary int) ([]int, error) {
	fib := fibonnaci()
	sequence := []int{0}
	for true {
		next := fib()
		if boundary > next {
			sequence = append(sequence, next)
			continue
		}
		if boundary == next {
			return sequence, nil
		}
		return nil, errors.New("you should inform a valid fibonacci sequence")
	}
	return sequence, nil
}

func Test_UC02(t *testing.T) {
	testCases := []struct {
		name             string
		boundary         int
		expectedSequence []int
		expectedError    error
	}{
		{
			name:             "01",
			boundary:         5,
			expectedSequence: []int{0, 1, 1, 2, 3},
		},
		{
			name:             "02",
			boundary:         8,
			expectedSequence: []int{0, 1, 1, 2, 3, 5},
		},
		{
			name:             "03",
			boundary:         13,
			expectedSequence: []int{0, 1, 1, 2, 3, 5, 8},
		},
		{
			name:             "04",
			boundary:         21,
			expectedSequence: []int{0, 1, 1, 2, 3, 5, 8, 13},
		},
		{
			name:             "05",
			boundary:         34,
			expectedSequence: []int{0, 1, 1, 2, 3, 5, 8, 13, 21},
		},
		{
			name:             "06",
			boundary:         55,
			expectedSequence: []int{0, 1, 1, 2, 3, 5, 8, 13, 21, 34},
		},
		{
			name:          "07",
			boundary:      4,
			expectedError: errors.New("you should inform a valid fibonacci sequence"),
		},
	}

	for _, tc := range testCases {
		t.Run(tc.name, func(t *testing.T) {
			actual, err := fibonnaciUntil(tc.boundary)
			assert.Equal(t, err, tc.expectedError)
			assert.Equal(t, actual, tc.expectedSequence)
		})
	}
}
