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
package io.github.astrapi69.comparator.object;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.comparator.SortOrder;

/**
 * The unit test class for the class {@link StringLengthComparator}.
 */
public class StringLengthComparatorTest
{

	/**
	 * Two strings, the comparator that compares them and the sign of the result
	 *
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            the comparator
	 * @param object
	 *            the first string
	 * @param compareWithObject
	 *            the string to compare with
	 * @param expectedSign
	 *            the sign of the result: -1, 0 or 1
	 */
	record LengthComparison(String description, Comparator<String> comparator, String object,
		String compareWithObject, int expectedSign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/** A string of four characters. */
	static final String ALEX = "Alex";

	/** A string of four characters, after {@link #ALEX} in the natural order. */
	static final String LEON = "Leon";

	/** A string of five characters, longer than {@link #ALEX} and {@link #LEON}. */
	static final String BILLY = "Billy";

	/**
	 * The cases of the order of a comparator in the given sort order: the longer string first,
	 * whatever the sort order, and the sort order between strings of equal length
	 *
	 * @param name
	 *            the name of the comparator
	 * @param comparator
	 *            the comparator
	 * @param sortOrder
	 *            the sort order the comparator was created with
	 * @return the cases
	 */
	static Stream<LengthComparison> order(final String name, final Comparator<String> comparator,
		final SortOrder sortOrder)
	{
		final boolean ascending = sortOrder == SortOrder.ASCENDING;
		// another instance, equal to ALEX, so that the comparison does not end at the same instance
		final String otherAlex = new String(ALEX);
		return Stream.of(
			new LengthComparison(name + ", Alex with the longer Billy: positive, the longer first",
				comparator, ALEX, BILLY, 1),
			new LengthComparison(name + ", Billy with the shorter Alex: negative, the longer first",
				comparator, BILLY, ALEX, -1),
			new LengthComparison(
				name + ", Alex with Leon of equal length: "
					+ (ascending
						? "negative, Alex first, the natural order"
						: "positive, Leon first, the natural order reversed"),
				comparator, ALEX, LEON, ascending ? -1 : 1),
			new LengthComparison(
				name + ", Leon with Alex of equal length: "
					+ (ascending
						? "positive, Alex first, the natural order"
						: "negative, Leon first, the natural order reversed"),
				comparator, LEON, ALEX, ascending ? 1 : -1),
			new LengthComparison(name + ", Alex with an equal Alex: zero", comparator, ALEX,
				otherAlex, 0));
	}

	/**
	 * The cases of null with a comparator of a factory method, which the flag nullIsGreaterThan
	 * places and not the sort order
	 *
	 * @param name
	 *            the name of the comparator
	 * @param comparator
	 *            the comparator
	 * @param nullIsGreaterThan
	 *            the flag the comparator was created with
	 * @return the cases
	 */
	static Stream<LengthComparison> nulls(final String name, final Comparator<String> comparator,
		final boolean nullIsGreaterThan)
	{
		return Stream.of(
			new LengthComparison(
				name + ", null with Alex: "
					+ (nullIsGreaterThan ? "positive, null last" : "negative, null first"),
				comparator, null, ALEX, nullIsGreaterThan ? 1 : -1),
			new LengthComparison(
				name + ", Alex with null: "
					+ (nullIsGreaterThan ? "negative, null last" : "positive, null first"),
				comparator, ALEX, null, nullIsGreaterThan ? -1 : 1));
	}

	/**
	 * The cases of every constructor and factory method of {@link StringLengthComparator}: the
	 * longer string comes first in both sort orders, so that {@link SortOrder#DESCENDING} does not
	 * put the shorter string first, and the sort order decides between strings of equal length only
	 * (#25)
	 *
	 * @return the cases
	 */
	static Stream<LengthComparison> lengthComparisons()
	{
		return Stream.of(
			order("new StringLengthComparator()", new StringLengthComparator(),
				SortOrder.ASCENDING),
			order("new StringLengthComparator(ASCENDING)",
				new StringLengthComparator(SortOrder.ASCENDING), SortOrder.ASCENDING),
			order("new StringLengthComparator(DESCENDING)",
				new StringLengthComparator(SortOrder.DESCENDING), SortOrder.DESCENDING),
			order("of()", StringLengthComparator.of(), SortOrder.ASCENDING),
			nulls("of()", StringLengthComparator.of(), true),
			order("of(false)", StringLengthComparator.of(false), SortOrder.ASCENDING),
			nulls("of(false)", StringLengthComparator.of(false), false),
			order("of(ASCENDING)", StringLengthComparator.of(SortOrder.ASCENDING),
				SortOrder.ASCENDING),
			nulls("of(ASCENDING)", StringLengthComparator.of(SortOrder.ASCENDING), true),
			order("of(DESCENDING)", StringLengthComparator.of(SortOrder.DESCENDING),
				SortOrder.DESCENDING),
			nulls("of(DESCENDING)", StringLengthComparator.of(SortOrder.DESCENDING), true),
			order("of(DESCENDING, false)", StringLengthComparator.of(SortOrder.DESCENDING, false),
				SortOrder.DESCENDING),
			nulls("of(DESCENDING, false)", StringLengthComparator.of(SortOrder.DESCENDING, false),
				false))
			.flatMap(Function.identity());
	}

	/**
	 * Test method for {@link StringLengthComparator#compare(String, String)} with every constructor
	 * and factory method: the longer string first in both sort orders, the sort order between
	 * strings of equal length only, and null where the flag nullIsGreaterThan puts it
	 *
	 * @param comparison
	 *            the strings, the comparator and the sign of the result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("lengthComparisons")
	public void testCompareOrder(final LengthComparison comparison)
	{
		assertEquals(comparison.expectedSign(), Integer.signum(
			comparison.comparator().compare(comparison.object(), comparison.compareWithObject())));
	}

	/**
	 * A comparator and the order it sorts the strings {@code Leon, Alex, Billy} in
	 *
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            the comparator
	 * @param sortedStrings
	 *            the strings sorted with the comparator
	 */
	record SortedStrings(String description, Comparator<String> comparator,
		List<String> sortedStrings) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The orders of the factory methods in both sort orders: the longer string first, then the sort
	 * order between the strings of equal length (#25)
	 *
	 * @return the comparators and the orders they sort in
	 */
	static Stream<SortedStrings> sortedStrings()
	{
		return Stream.of(
			new SortedStrings("of(): Billy, Alex, Leon, the longer first, then the natural order",
				StringLengthComparator.of(), List.of(BILLY, ALEX, LEON)),
			new SortedStrings(
				"of(DESCENDING): Billy, Leon, Alex, the longer first, then the natural order reversed",
				StringLengthComparator.of(SortOrder.DESCENDING), List.of(BILLY, LEON, ALEX)));
	}

	/**
	 * Test method for {@link StringLengthComparator#compare(String, String)} in a sort: the order
	 * the comparator sorts the strings {@code Leon, Alex, Billy} in
	 *
	 * @param sorted
	 *            the comparator and the order it sorts in
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("sortedStrings")
	public void testSort(final SortedStrings sorted)
	{
		final List<String> strings = new ArrayList<>(List.of(LEON, ALEX, BILLY));

		strings.sort(sorted.comparator());

		assertEquals(sorted.sortedStrings(), strings);
	}

}
