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
import java.util.Date;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.meanbean.test.BeanTester;

import io.github.astrapi69.comparator.object.DateComparator;
import io.github.astrapi69.comparator.object.DescendingDateComparator;
import io.github.astrapi69.comparator.object.StringComparator;
import io.github.astrapi69.test.object.Person;

/**
 * The unit test class for the class {@link ComparatorExtensions}.
 */
public class ComparatorExtensionsTest
{

	/**
	 * A pair of persons, which compare by their names, and what
	 * {@link ComparatorExtensions#isGreater(Comparable, Comparable)},
	 * {@link ComparatorExtensions#isSmaller(Comparable, Comparable)} and
	 * {@link ComparatorExtensions#isSame(Comparable, Comparable)} answer for it
	 *
	 * @param description
	 *            the name of the case
	 * @param object
	 *            the first person
	 * @param compareWithObject
	 *            the person to compare with
	 * @param greater
	 *            the answer of isGreater
	 * @param smaller
	 *            the answer of isSmaller
	 * @param same
	 *            the answer of isSame
	 */
	record ComparedPair(String description, Person object, Person compareWithObject,
		boolean greater, boolean smaller, boolean same) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The pairs on both sides of the boundary of two equal persons, and with null, which
	 * {@link ComparatorExtensions#compare(Comparable, Comparable)} orders before every object
	 *
	 * @return the pairs
	 */
	static Stream<ComparedPair> comparedPairs()
	{
		final Person al = Person.builder().name("al").build();
		final Person otherAl = Person.builder().name("al").build();
		final Person bert = Person.builder().name("bert").build();
		return Stream.of(new ComparedPair("the first smaller", al, bert, false, true, false),
			new ComparedPair("the first greater", bert, al, true, false, false),
			new ComparedPair("equal, two instances", al, otherAl, false, false, true),
			new ComparedPair("the same instance", al, al, false, false, true),
			new ComparedPair("the first null", null, al, false, true, false),
			new ComparedPair("the second null", al, null, true, false, false),
			new ComparedPair("both null", null, null, false, false, true));
	}

	/**
	 * Test for the methods {@link ComparatorExtensions#isGreater(Comparable, Comparable)},
	 * {@link ComparatorExtensions#isSmaller(Comparable, Comparable)} and
	 * {@link ComparatorExtensions#isSame(Comparable, Comparable)}: for each pair exactly one of
	 * them is true, and for two equal persons it is isSame, neither isGreater nor isSmaller
	 *
	 * @param pair
	 *            the pair and the answers
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("comparedPairs")
	public void testIsGreaterIsSmallerAndIsSame(final ComparedPair pair)
	{
		assertEquals(pair.greater(),
			ComparatorExtensions.isGreater(pair.object(), pair.compareWithObject()), "isGreater");
		assertEquals(pair.smaller(),
			ComparatorExtensions.isSmaller(pair.object(), pair.compareWithObject()), "isSmaller");
		assertEquals(pair.same(),
			ComparatorExtensions.isSame(pair.object(), pair.compareWithObject()), "isSame");
	}

	/**
	 * Test for method {@link ComparatorExtensions#compare(Comparable, Comparable)} with the pairs
	 * of {@link #comparedPairs()}: the sign of the comparison is the answer of isGreater, isSmaller
	 * and isSame, positive for the greater first, negative for the smaller first and zero for two
	 * equal persons
	 *
	 * @param pair
	 *            the pair and the answers
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("comparedPairs")
	public void testCompare(final ComparedPair pair)
	{
		final int sign = pair.greater() ? 1 : pair.smaller() ? -1 : 0;
		assertEquals(sign,
			Integer.signum(ComparatorExtensions.compare(pair.object(), pair.compareWithObject())));
	}

	/**
	 * A {@link Comparable} whose {@code compareTo} returns {@link Integer#MIN_VALUE} for a smaller
	 * value, as {@link Comparable#compareTo(Object)} allows: it asks for a negative integer, and
	 * {@link Integer#MIN_VALUE} is one
	 *
	 * @param value
	 *            the value the ranks are ordered by
	 */
	record Rank(int value) implements Comparable<Rank> {
		@Override
		public int compareTo(final Rank other)
		{
			final int comparison = Integer.compare(value, other.value);
			return comparison < 0 ? Integer.MIN_VALUE : comparison;
		}
	}

	/**
	 * A {@link Date} whose {@code compareTo} returns {@link Integer#MIN_VALUE} for an earlier date,
	 * as {@link Rank} does: {@link Date#compareTo(Date)} is not final, so a subclass of
	 * {@link Date} brings that value into {@link DescendingDateComparator}
	 */
	static final class RankedDate extends Date
	{
		private static final long serialVersionUID = 1L;

		RankedDate(final long time)
		{
			super(time);
		}

		@Override
		public int compareTo(final Date other)
		{
			final int comparison = super.compareTo(other);
			return comparison < 0 ? Integer.MIN_VALUE : comparison;
		}
	}

	/**
	 * A comparison and the sign of its result
	 *
	 * @param description
	 *            the name of the case
	 * @param comparison
	 *            the comparison
	 * @param sign
	 *            the sign of its result, -1, 0 or 1
	 */
	record SignedComparison(String description, IntSupplier comparison, int sign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The comparisons of {@link ComparatorExtensions#compare(Comparable, Comparable, SortOrder)}
	 * and of the comparators that go through it, with a {@code compareTo} that returns
	 * {@link Integer#MIN_VALUE} for the smaller value, with null in both orders, and with strings,
	 * whose {@code compareTo} never returns {@link Integer#MIN_VALUE}
	 *
	 * @return the comparisons
	 */
	@SuppressWarnings("deprecation") // DescendingDateComparator is tested as long as it is there
										// (#62)
	static Stream<SignedComparison> sortOrderComparisons()
	{
		final Rank one = new Rank(1);
		final Rank otherOne = new Rank(1);
		final Rank two = new Rank(2);
		final Rank none = null;
		final Comparator<Rank> descending = SortOrderComparator.of(SortOrder.DESCENDING);
		final Date earlier = new RankedDate(0);
		final Date later = new RankedDate(1_000);
		final Comparator<Date> descendingDates = new DescendingDateComparator();
		final Comparator<Date> datesInDescendingOrder = new DateComparator(SortOrder.DESCENDING);
		final Comparator<String> descendingStrings = new StringComparator(SortOrder.DESCENDING);
		return Stream.of(
			new SignedComparison("ascending, 1 with 2: negative, 1 first",
				() -> ComparatorExtensions.compare(one, two, SortOrder.ASCENDING), -1),
			new SignedComparison("ascending, 2 with 1: positive, 1 first",
				() -> ComparatorExtensions.compare(two, one, SortOrder.ASCENDING), 1),
			new SignedComparison(
				"descending, 1 with 2: positive, 2 first, though compareTo returns MIN_VALUE (#23)",
				() -> ComparatorExtensions.compare(one, two, SortOrder.DESCENDING), 1),
			new SignedComparison("descending, 2 with 1: negative, 2 first",
				() -> ComparatorExtensions.compare(two, one, SortOrder.DESCENDING), -1),
			new SignedComparison("descending, 1 with an equal 1: zero",
				() -> ComparatorExtensions.compare(one, otherOne, SortOrder.DESCENDING), 0),
			new SignedComparison("ascending, null with 1: negative, null first",
				() -> ComparatorExtensions.compare(none, one, SortOrder.ASCENDING), -1),
			new SignedComparison("ascending, 1 with null: positive, null first",
				() -> ComparatorExtensions.compare(one, none, SortOrder.ASCENDING), 1),
			new SignedComparison("ascending, null with null: zero",
				() -> ComparatorExtensions.compare(none, none, SortOrder.ASCENDING), 0),
			new SignedComparison("descending, null with 1: positive, null last",
				() -> ComparatorExtensions.compare(none, one, SortOrder.DESCENDING), 1),
			new SignedComparison("descending, 1 with null: negative, null last",
				() -> ComparatorExtensions.compare(one, none, SortOrder.DESCENDING), -1),
			new SignedComparison("descending, null with null: zero",
				() -> ComparatorExtensions.compare(none, none, SortOrder.DESCENDING), 0),
			new SignedComparison(
				"SortOrderComparator descending, 1 with 2: positive, 2 first (#23)",
				() -> descending.compare(one, two), 1),
			new SignedComparison("SortOrderComparator descending, 2 with 1: negative, 2 first",
				() -> descending.compare(two, one), -1),
			new SignedComparison(
				"DescendingDateComparator, earlier with later: positive, later first (#23)",
				() -> descendingDates.compare(earlier, later), 1),
			new SignedComparison(
				"DescendingDateComparator, later with earlier: negative, later first",
				() -> descendingDates.compare(later, earlier), -1),
			new SignedComparison(
				"DateComparator descending, earlier with later: positive, later first (#23, #35)",
				() -> datesInDescendingOrder.compare(earlier, later), 1),
			new SignedComparison(
				"DateComparator descending, later with earlier: negative, later first",
				() -> datesInDescendingOrder.compare(later, earlier), -1),
			new SignedComparison("StringComparator descending, a with b: positive, b first",
				() -> descendingStrings.compare("a", "b"), 1),
			new SignedComparison(
				"StringComparator descending, ab with its prefix a: negative, ab first",
				() -> descendingStrings.compare("ab", "a"), -1));
	}

	/**
	 * Test for method {@link ComparatorExtensions#compare(Comparable, Comparable, SortOrder)} and
	 * the comparators that go through it: {@link SortOrderComparator},
	 * {@link DescendingDateComparator}, {@link DateComparator} and {@link StringComparator}. The
	 * sign of each comparison is what the comparison answers for its order
	 *
	 * @param comparison
	 *            the comparison and the sign of its result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("sortOrderComparisons")
	public void testCompareSortOrderSign(final SignedComparison comparison)
	{
		assertEquals(comparison.sign(), Integer.signum(comparison.comparison().getAsInt()));
	}

	/**
	 * Two persons and the answer of {@link ComparatorExtensions#equalNullCheck(Object, Object)} for
	 * them
	 *
	 * @param description
	 *            the name of the case
	 * @param object
	 *            the first person
	 * @param compareWithObject
	 *            the person to compare with
	 * @param result
	 *            the answer: true or false, or null if the equals method has to decide
	 */
	record EqualNullCheck(String description, Person object, Person compareWithObject,
		Boolean result) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases of {@link ComparatorExtensions#equalNullCheck(Object, Object)}: the same instance,
	 * null included, is equal, one null is not, and two persons, equal or not, are left to the
	 * equals method
	 *
	 * @return the cases
	 */
	static Stream<EqualNullCheck> equalNullChecks()
	{
		final Person al = Person.builder().name("al").build();
		final Person otherAl = Person.builder().name("al").build();
		final Person bert = Person.builder().name("bert").build();
		return Stream.of(new EqualNullCheck("the same instance: true", al, al, true),
			new EqualNullCheck("both null: true", null, null, true),
			new EqualNullCheck("the first null: false", null, al, false),
			new EqualNullCheck("the second null: false", al, null, false),
			new EqualNullCheck("two persons: null, left to equals", al, bert, null),
			new EqualNullCheck("equal, two instances: null, left to equals", al, otherAl, null));
	}

	/**
	 * Test for method {@link ComparatorExtensions#equalNullCheck(Object, Object)}
	 *
	 * @param check
	 *            the persons and the answer
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("equalNullChecks")
	public void testEqualNullCheck(final EqualNullCheck check)
	{
		assertEquals(check.result(),
			ComparatorExtensions.equalNullCheck(check.object(), check.compareWithObject()));
	}

	/**
	 * A null check of two persons and its result
	 *
	 * @param description
	 *            the name of the case
	 * @param nullCheck
	 *            the null check, one of the methods {@code nullCheck}
	 * @param object
	 *            the first person
	 * @param compareWithObject
	 *            the person to compare with
	 * @param result
	 *            the result: -1, 0 or 1, or null if the compare method has to decide
	 */
	record NullCheck(String description, BiFunction<Person, Person, Integer> nullCheck,
		Person object, Person compareWithObject, Integer result) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases of a null check: null is the smaller person, or the greater one with
	 * nullIsGreaterThan; the same instance, null included, is 0, and two persons, equal or not, are
	 * left to the compare method
	 *
	 * @param name
	 *            the name of the null check
	 * @param nullCheck
	 *            the null check
	 * @param nullIsGreaterThan
	 *            the flag the null check is made with
	 * @return the cases
	 */
	static Stream<NullCheck> nullChecksOf(final String name,
		final BiFunction<Person, Person, Integer> nullCheck, final boolean nullIsGreaterThan)
	{
		final Person al = Person.builder().name("al").build();
		final Person otherAl = Person.builder().name("al").build();
		final Person bert = Person.builder().name("bert").build();
		return Stream.of(
			new NullCheck(
				name + ", a person with null: "
					+ (nullIsGreaterThan ? "-1, null last" : "1, null first"),
				nullCheck, al, null, nullIsGreaterThan ? -1 : 1),
			new NullCheck(
				name + ", null with a person: "
					+ (nullIsGreaterThan ? "1, null last" : "-1, null first"),
				nullCheck, null, al, nullIsGreaterThan ? 1 : -1),
			new NullCheck(name + ", null with null: 0", nullCheck, null, null, 0),
			new NullCheck(name + ", the same instance: 0", nullCheck, al, al, 0),
			new NullCheck(name + ", two persons: null, left to compare", nullCheck, al, bert, null),
			new NullCheck(name + ", equal, two instances: null, left to compare", nullCheck, al,
				otherAl, null));
	}

	/**
	 * The cases of {@link ComparatorExtensions#nullCheck(Object, Object)}, which places null as
	 * {@link ComparatorExtensions#nullCheck(Object, Object, boolean)} with false does, and of the
	 * latter with false and with true
	 *
	 * @return the cases
	 */
	static Stream<NullCheck> nullChecks()
	{
		return Stream.of(
			nullChecksOf("nullCheck(object, compareWithObject)", ComparatorExtensions::nullCheck,
				false),
			nullChecksOf("nullCheck(object, compareWithObject, false)",
				(object, compareWithObject) -> ComparatorExtensions.nullCheck(object,
					compareWithObject, false),
				false),
			nullChecksOf("nullCheck(object, compareWithObject, true)",
				(object, compareWithObject) -> ComparatorExtensions.nullCheck(object,
					compareWithObject, true),
				true))
			.flatMap(Function.identity());
	}

	/**
	 * Test for the methods {@link ComparatorExtensions#nullCheck(Object, Object)} and
	 * {@link ComparatorExtensions#nullCheck(Object, Object, boolean)}
	 *
	 * @param check
	 *            the null check, the persons and the result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("nullChecks")
	public void testNullCheck(final NullCheck check)
	{
		assertEquals(check.result(),
			check.nullCheck().apply(check.object(), check.compareWithObject()));
	}

	/**
	 * Test method for {@link ComparatorExtensions} with {@link BeanTester}
	 */
	@Test
	public void testWithBeanTester()
	{
		final BeanTester beanTester = new BeanTester();
		beanTester.testBean(ComparatorExtensions.class);
	}

}
