/**
 * The MIT License
 *
 * Copyright (C) 2022 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.comparator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * The unit test class for the class {@link SortOrderComparator}.
 */
public class SortOrderComparatorTest
{

	/**
	 * Two numbers, the comparator that compares them and the sign of the result
	 *
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            the comparator
	 * @param object
	 *            the first number
	 * @param compareWithObject
	 *            the number to compare with
	 * @param expectedSign
	 *            the sign of the result: -1, 0 or 1
	 */
	record NumberComparison(String description, Comparator<Integer> comparator, Integer object,
		Integer compareWithObject, int expectedSign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases of the order of a comparator in the given sort order: ascending puts the smaller
	 * number and null first, descending the greater number first and null last
	 *
	 * @param name
	 *            the name of the comparator
	 * @param comparator
	 *            the comparator
	 * @param sortOrder
	 *            the sort order the comparator was created with
	 * @return the cases
	 */
	static Stream<NumberComparison> order(final String name, final Comparator<Integer> comparator,
		final SortOrder sortOrder)
	{
		final boolean ascending = sortOrder == SortOrder.ASCENDING;
		final Integer number = 42;
		return Stream.of(
			new NumberComparison(
				name + ", 42 with the smaller 21: "
					+ (ascending ? "positive, 21 first" : "negative, 42 first"),
				comparator, number, 21, ascending ? 1 : -1),
			new NumberComparison(name + ", 42 with the same 42: zero", comparator, number, number,
				0),
			new NumberComparison(
				name + ", 42 with the greater 84: "
					+ (ascending ? "negative, 42 first" : "positive, 84 first"),
				comparator, number, 84, ascending ? -1 : 1),
			new NumberComparison(
				name + ", 42 with null: "
					+ (ascending ? "positive, null first" : "negative, null last"),
				comparator, number, null, ascending ? 1 : -1),
			new NumberComparison(
				name + ", null with 42: "
					+ (ascending ? "negative, null first" : "positive, null last"),
				comparator, null, number, ascending ? -1 : 1));
	}

	/**
	 * The cases of every constructor and factory method of {@link SortOrderComparator}: the
	 * constructor and the factory method without a sort order sort ascending
	 *
	 * @return the cases
	 */
	static Stream<NumberComparison> numberComparisons()
	{
		return Stream.of(
			order("new SortOrderComparator()", new SortOrderComparator<Integer>(),
				SortOrder.ASCENDING),
			order("new SortOrderComparator(ASCENDING)",
				new SortOrderComparator<Integer>(SortOrder.ASCENDING), SortOrder.ASCENDING),
			order("new SortOrderComparator(DESCENDING)",
				new SortOrderComparator<Integer>(SortOrder.DESCENDING), SortOrder.DESCENDING),
			order("of()", SortOrderComparator.of(), SortOrder.ASCENDING),
			order("of(ASCENDING)", SortOrderComparator.of(SortOrder.ASCENDING),
				SortOrder.ASCENDING),
			order("of(DESCENDING)", SortOrderComparator.of(SortOrder.DESCENDING),
				SortOrder.DESCENDING))
			.flatMap(Function.identity());
	}

	/**
	 * Test method for {@link SortOrderComparator#compare(Comparable, Comparable)} with every
	 * constructor and factory method: the smaller number and null first ascending, the greater
	 * number first and null last descending
	 *
	 * @param comparison
	 *            the numbers, the comparator and the sign of the result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("numberComparisons")
	public void testCompare(final NumberComparison comparison)
	{
		assertEquals(comparison.expectedSign(), Integer.signum(
			comparison.comparator().compare(comparison.object(), comparison.compareWithObject())));
	}
}
