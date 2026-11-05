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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.comparator.SortOrder;

/**
 * The unit test class for the class {@link StringComparator}
 */
public class StringComparatorTest
{

	/** A string, before {@link #BILLY} and {@link #LEON} in the natural order. */
	static final String ALEX = "Alex";

	/** A string, after {@link #ALEX} and before {@link #LEON} in the natural order. */
	static final String BILLY = "Billy";

	/** A string, after {@link #ALEX} and {@link #BILLY} in the natural order. */
	static final String LEON = "Leon";

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
	record StringComparison(String description, Comparator<String> comparator, String object,
		String compareWithObject, int expectedSign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases of the order of a comparator in the given sort order: the natural order of the
	 * strings ascending, its reverse descending
	 *
	 * @param name
	 *            the name of the comparator
	 * @param comparator
	 *            the comparator
	 * @param sortOrder
	 *            the sort order the comparator was created with
	 * @return the cases
	 */
	static Stream<StringComparison> order(final String name, final Comparator<String> comparator,
		final SortOrder sortOrder)
	{
		final boolean ascending = sortOrder == SortOrder.ASCENDING;
		// another instance, equal to ALEX, so that the comparison does not end at the same instance
		final String otherAlex = new String(ALEX);
		return Stream.of(
			new StringComparison(
				name + ", Alex with Leon: "
					+ (ascending ? "negative, Alex first" : "positive, Leon first"),
				comparator, ALEX, LEON, ascending ? -1 : 1),
			new StringComparison(
				name + ", Leon with Alex: "
					+ (ascending ? "positive, Alex first" : "negative, Leon first"),
				comparator, LEON, ALEX, ascending ? 1 : -1),
			new StringComparison(
				name + ", Alex with Billy: "
					+ (ascending ? "negative, Alex first" : "positive, Billy first"),
				comparator, ALEX, BILLY, ascending ? -1 : 1),
			new StringComparison(name + ", Alex with an equal Alex: zero", comparator, ALEX,
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
	static Stream<StringComparison> nulls(final String name, final Comparator<String> comparator,
		final boolean nullIsGreaterThan)
	{
		return Stream.of(
			new StringComparison(
				name + ", null with Billy: "
					+ (nullIsGreaterThan ? "positive, null last" : "negative, null first"),
				comparator, null, BILLY, nullIsGreaterThan ? 1 : -1),
			new StringComparison(
				name + ", Billy with null: "
					+ (nullIsGreaterThan ? "negative, null last" : "positive, null first"),
				comparator, BILLY, null, nullIsGreaterThan ? -1 : 1),
			new StringComparison(name + ", null with null: zero", comparator, null, null, 0));
	}

	/**
	 * The cases of every constructor and factory method of {@link StringComparator}: the natural
	 * order ascending, its reverse descending, and with a factory method null where the flag
	 * nullIsGreaterThan puts it, last if it is not given
	 *
	 * @return the cases
	 */
	static Stream<StringComparison> stringComparisons()
	{
		return Stream.of(
			order("new StringComparator()", new StringComparator(), SortOrder.ASCENDING),
			order("new StringComparator(ASCENDING)", new StringComparator(SortOrder.ASCENDING),
				SortOrder.ASCENDING),
			order("new StringComparator(DESCENDING)", new StringComparator(SortOrder.DESCENDING),
				SortOrder.DESCENDING),
			order("of()", StringComparator.of(), SortOrder.ASCENDING),
			nulls("of()", StringComparator.of(), true),
			order("of(false)", StringComparator.of(false), SortOrder.ASCENDING),
			nulls("of(false)", StringComparator.of(false), false),
			order("of(true)", StringComparator.of(true), SortOrder.ASCENDING),
			nulls("of(true)", StringComparator.of(true), true),
			order("of(ASCENDING)", StringComparator.of(SortOrder.ASCENDING), SortOrder.ASCENDING),
			nulls("of(ASCENDING)", StringComparator.of(SortOrder.ASCENDING), true),
			order("of(DESCENDING)", StringComparator.of(SortOrder.DESCENDING),
				SortOrder.DESCENDING),
			nulls("of(DESCENDING)", StringComparator.of(SortOrder.DESCENDING), true),
			order("of(DESCENDING, false)", StringComparator.of(SortOrder.DESCENDING, false),
				SortOrder.DESCENDING),
			nulls("of(DESCENDING, false)", StringComparator.of(SortOrder.DESCENDING, false), false))
			.flatMap(Function.identity());
	}

	/**
	 * Test method for {@link StringComparator#compare(String, String)} with every constructor and
	 * factory method: the natural order ascending, its reverse descending, and null where the flag
	 * nullIsGreaterThan puts it
	 *
	 * @param comparison
	 *            the strings, the comparator and the sign of the result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("stringComparisons")
	public void testCompareOrder(final StringComparison comparison)
	{
		assertEquals(comparison.expectedSign(), Integer.signum(
			comparison.comparator().compare(comparison.object(), comparison.compareWithObject())));
	}

	/**
	 * A comparator and the order it sorts the strings {@code Leon, Alex, Billy, null, null} in
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
	 * The orders of the factory methods with both flags and in both sort orders: the flag places
	 * the nulls, the sort order the strings
	 *
	 * @return the comparators and the orders they sort in
	 */
	static Stream<SortedStrings> sortedStrings()
	{
		return Stream.of(
			new SortedStrings("of(false): the nulls first, then Alex, Billy, Leon",
				StringComparator.of(false), Arrays.asList(null, null, ALEX, BILLY, LEON)),
			new SortedStrings("of(true): Alex, Billy, Leon, then the nulls",
				StringComparator.of(true), Arrays.asList(ALEX, BILLY, LEON, null, null)),
			new SortedStrings("of(DESCENDING, false): the nulls first, then Leon, Billy, Alex",
				StringComparator.of(SortOrder.DESCENDING, false),
				Arrays.asList(null, null, LEON, BILLY, ALEX)),
			new SortedStrings("of(DESCENDING): Leon, Billy, Alex, then the nulls",
				StringComparator.of(SortOrder.DESCENDING),
				Arrays.asList(LEON, BILLY, ALEX, null, null)));
	}

	/**
	 * Test method for {@link StringComparator#compare(String, String)} in a sort: the order the
	 * comparator sorts the strings {@code Leon, Alex, Billy, null, null} in
	 *
	 * @param sorted
	 *            the comparator and the order it sorts in
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("sortedStrings")
	public void testSort(final SortedStrings sorted)
	{
		final List<String> strings = new ArrayList<>(Arrays.asList(LEON, ALEX, BILLY, null, null));

		strings.sort(sorted.comparator());

		assertEquals(sorted.sortedStrings(), strings);
	}

}
