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
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.comparator.object.DateComparator;
import io.github.astrapi69.comparator.object.DescendingDateComparator;
import io.github.astrapi69.comparator.object.LocaleComparator;
import io.github.astrapi69.comparator.object.StringComparator;
import io.github.astrapi69.comparator.object.StringLengthComparator;

/**
 * The unit test class for where the comparators of this library put null, which two of them throw
 * for null, and what a null {@link SortOrder} does, as their Javadoc says (#55). The flags of the
 * {@code of} factories of {@link StringComparator}, {@link StringLengthComparator} and
 * {@link LocaleComparator} are checked with the comparators they decorate in
 * {@link NullCheckComparatorTest}
 */
public class NullPlacementTest
{

	/** The earlier of the two dates */
	private static final Date EARLIER = new Date(0);

	/** The later of the two dates */
	private static final Date LATER = new Date(1_000);

	/**
	 * A comparator, values to sort with it, null among them, and the order it sorts them in
	 *
	 * @param <T>
	 *            the type of the values
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            makes the comparator in the test, not in the method source: PIT counts what the
	 *            method source runs as covered by the first invocation alone, and runs only that
	 *            one against the mutants of a constructor or factory method
	 * @param values
	 *            the values to sort
	 * @param sortedValues
	 *            the values sorted with the comparator
	 */
	record SortedWithNull<T> (String description, Supplier<Comparator<T>> comparator,
		List<T> values, List<T> sortedValues) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The constructors and factory methods whose Javadoc says where they put null, and every member
	 * that takes a {@link SortOrder}, with a null one, which is taken as
	 * {@link SortOrder#ASCENDING}
	 *
	 * @return the cases
	 */
	@SuppressWarnings("deprecation") // DescendingDateComparator is tested as long as it is there
										// (#62)
	static Stream<SortedWithNull<?>> sortedWithNull()
	{
		final List<String> words = Arrays.asList("b", null, "a");
		final List<String> nullFirst = Arrays.asList(null, "a", "b");
		final List<String> nullLast = Arrays.asList("a", "b", null);
		final List<String> descendingNullLast = Arrays.asList("b", "a", null);
		final List<Integer> numbers = Arrays.asList(2, null, 1);
		final List<Integer> numbersNullFirst = Arrays.asList(null, 1, 2);
		final List<Date> dates = Arrays.asList(LATER, null, EARLIER);
		final List<Date> datesNullFirst = Arrays.asList(null, EARLIER, LATER);
		return Stream.<SortedWithNull<?>> of(
			new SortedWithNull<>("new StringComparator(): null first", () -> new StringComparator(),
				words, nullFirst),
			new SortedWithNull<>("new StringComparator(ASCENDING): null first",
				() -> new StringComparator(SortOrder.ASCENDING), words, nullFirst),
			new SortedWithNull<>("new StringComparator(DESCENDING): null last",
				() -> new StringComparator(SortOrder.DESCENDING), words, descendingNullLast),
			new SortedWithNull<>("new StringComparator(null): ascending, null first",
				() -> new StringComparator(null), words, nullFirst),
			new SortedWithNull<>("StringComparator.of(): null last", () -> StringComparator.of(),
				words, nullLast),
			new SortedWithNull<>("StringComparator.of(DESCENDING): null last",
				() -> StringComparator.of(SortOrder.DESCENDING), words, descendingNullLast),
			new SortedWithNull<>("StringComparator.of(null): ascending, null last",
				() -> StringComparator.of((SortOrder)null), words, nullLast),
			new SortedWithNull<>("StringComparator.of(null, false): ascending, null first",
				() -> StringComparator.of(null, false), words, nullFirst),
			new SortedWithNull<>("StringLengthComparator.of(null): ascending, null last",
				() -> StringLengthComparator.of((SortOrder)null),
				Arrays.asList("b", null, "a", "cc"), Arrays.asList("cc", "a", "b", null)),
			new SortedWithNull<>("new StringLengthComparator(null): equal lengths ascending",
				() -> new StringLengthComparator(null), Arrays.asList("b", "a", "cc"),
				Arrays.asList("cc", "a", "b")),
			new SortedWithNull<>("LocaleComparator.of(): null first", () -> LocaleComparator.of(),
				Arrays.asList(Locale.ENGLISH, null, Locale.GERMAN),
				Arrays.asList(null, Locale.GERMAN, Locale.ENGLISH)),
			new SortedWithNull<>("new SortOrderComparator(): null first",
				() -> new SortOrderComparator<Integer>(), numbers, numbersNullFirst),
			new SortedWithNull<>("SortOrderComparator.of(): null first",
				() -> SortOrderComparator.<Integer> of(), numbers, numbersNullFirst),
			new SortedWithNull<>("new SortOrderComparator(DESCENDING): null last",
				() -> new SortOrderComparator<Integer>(SortOrder.DESCENDING), numbers,
				Arrays.asList(2, 1, null)),
			new SortedWithNull<>("new SortOrderComparator(null): ascending, null first",
				() -> new SortOrderComparator<Integer>(null), numbers, numbersNullFirst),
			new SortedWithNull<>("SortOrderComparator.of(null): ascending, null first",
				() -> SortOrderComparator.<Integer> of(null), numbers, numbersNullFirst),
			new SortedWithNull<>("ComparatorExtensions.compare(T, T): null first",
				() -> (Comparator<Integer>)ComparatorExtensions::compare, numbers,
				numbersNullFirst),
			new SortedWithNull<>("ComparatorExtensions.compare(T, T, null): ascending, null first",
				() -> (object, compareWithObject) -> ComparatorExtensions.compare(object,
					compareWithObject, null),
				numbers, numbersNullFirst),
			new SortedWithNull<>("NullCheckComparator.of(Comparator): null first",
				() -> NullCheckComparator.of(Comparator.<String> naturalOrder()), words, nullFirst),
			new SortedWithNull<>("NullCheckComparator.builder(): null first",
				() -> NullCheckComparator.<String> builder()
					.decoratedComparator(Comparator.naturalOrder()).build(),
				words, nullFirst),
			new SortedWithNull<>("new DateComparator(null): ascending, null first",
				() -> new DateComparator(null), dates, datesNullFirst),
			new SortedWithNull<>("new DescendingDateComparator(): null last",
				() -> new DescendingDateComparator(), dates, Arrays.asList(LATER, EARLIER, null)));
	}

	/**
	 * Test for the comparators of {@link #sortedWithNull()}: each sorts the values, null among
	 * them, in the order its Javadoc says
	 *
	 * @param sorted
	 *            the comparator, the values and the order it sorts them in
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("sortedWithNull")
	public void testSortsNullWhereTheJavadocSays(final SortedWithNull<?> sorted)
	{
		assertSorted(sorted);
	}

	/**
	 * Sorts the values of the given case with its comparator and compares the result with the order
	 * of the case
	 *
	 * @param <T>
	 *            the type of the values
	 * @param sorted
	 *            the case
	 */
	private static <T> void assertSorted(final SortedWithNull<T> sorted)
	{
		final List<T> values = new ArrayList<>(sorted.values());

		values.sort(sorted.comparator().get());

		assertEquals(sorted.sortedValues(), values);
	}

	/**
	 * A comparator that throws a {@link NullPointerException} when comparing null, and a value it
	 * compares
	 *
	 * @param <T>
	 *            the type of the value
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            makes the comparator in the test, not in the method source
	 * @param value
	 *            a value that is not null
	 */
	record ThrowsForNull<T> (String description, Supplier<Comparator<T>> comparator, T value) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The constructors whose Javadoc says that the comparator throws a {@link NullPointerException}
	 * when comparing null: those of {@link StringLengthComparator}, whose superclass puts null
	 * first, and the one of {@link LocaleComparator}
	 *
	 * @return the cases
	 */
	static Stream<ThrowsForNull<?>> throwsForNull()
	{
		return Stream.<ThrowsForNull<?>> of(
			new ThrowsForNull<>("new StringLengthComparator()", () -> new StringLengthComparator(),
				"a"),
			new ThrowsForNull<>("new StringLengthComparator(ASCENDING)",
				() -> new StringLengthComparator(SortOrder.ASCENDING), "a"),
			new ThrowsForNull<>("new StringLengthComparator(DESCENDING)",
				() -> new StringLengthComparator(SortOrder.DESCENDING), "a"),
			new ThrowsForNull<>("new LocaleComparator()", () -> new LocaleComparator(),
				Locale.GERMAN));
	}

	/**
	 * Test for the comparators of {@link #throwsForNull()}: each throws a
	 * {@link NullPointerException} for null as the first and as the second argument
	 *
	 * @param throwsForNull
	 *            the comparator and a value
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("throwsForNull")
	public void testThrowsForNull(final ThrowsForNull<?> throwsForNull)
	{
		assertThrowsForNull(throwsForNull);
	}

	/**
	 * Compares null with the value of the given case, in both orders, and expects a
	 * {@link NullPointerException} each time
	 *
	 * @param <T>
	 *            the type of the value
	 * @param throwsForNull
	 *            the case
	 */
	private static <T> void assertThrowsForNull(final ThrowsForNull<T> throwsForNull)
	{
		final Comparator<T> comparator = throwsForNull.comparator().get();
		final T value = throwsForNull.value();

		assertThrows(NullPointerException.class, () -> comparator.compare(null, value),
			"null first");
		assertThrows(NullPointerException.class, () -> comparator.compare(value, null),
			"null second");
	}

}
