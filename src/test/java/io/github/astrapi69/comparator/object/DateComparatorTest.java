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


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.comparator.SortOrder;
import io.github.astrapi69.date.CreateDateExtensions;
import io.github.astrapi69.test.base.BaseComparatorTestCase;

/**
 * The unit test class for the class {@link DateComparator}
 */
public class DateComparatorTest extends BaseComparatorTestCase<Date>
{

	private final Date after = CreateDateExtensions.newDate(2010, 3, 27, 10, 37, 4);
	private final Date before = CreateDateExtensions.newDate(2010, 3, 26, 10, 37, 4);

	private final Date future = CreateDateExtensions.newDate(2011, 3, 27, 10, 37, 4);
	private final Date past = CreateDateExtensions.newDate(2009, 3, 26, 10, 37, 4);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@BeforeEach
	public void setUp()
	{
		super.setUp();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@AfterEach
	public void tearDown()
	{
		super.tearDown();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void assertTrue(Boolean condition)
	{
		Assertions.assertTrue(condition);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void assertEquals(Date expected, Date actual)
	{
		Assertions.assertEquals(expected, actual);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Date> newActualList()
	{
		final List<Date> actual = new ArrayList<>();
		actual.add(before);
		actual.add(future);
		actual.add(past);
		actual.add(after);
		actual.add(null);
		return actual;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Comparator<Date> newComparator()
	{
		return new DateComparator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Date> newExpectedSortedList()
	{
		final List<Date> expected = new ArrayList<>();
		expected.add(past);
		expected.add(before);
		expected.add(after);
		expected.add(future);
		expected.add(0, null);
		return expected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Date> newExpectedUnsortedList()
	{
		final List<Date> expected = new ArrayList<>();
		expected.add(before);
		expected.add(future);
		expected.add(past);
		expected.add(after);
		expected.add(null);
		return expected;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Date newO1Equal()
	{
		return CreateDateExtensions.newDate(2009, 3, 26, 10, 37, 4);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Date newO1GreaterThan()
	{
		return CreateDateExtensions.newDate(2011, 3, 27, 10, 37, 4);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Date newO1LessThan()
	{
		return CreateDateExtensions.newDate(2009, 3, 26, 10, 37, 4);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Date newO2GreaterThan()
	{
		return CreateDateExtensions.newDate(2010, 3, 26, 10, 37, 4);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Date newO2LessThan()
	{
		return CreateDateExtensions.newDate(2010, 3, 26, 10, 37, 4);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Test
	public void testCompare()
	{
		super.testCompare();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Test
	public void testEqual()
	{
		super.testEqual();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Test
	public void testGreaterThan()
	{
		super.testGreaterThan();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Test
	public void testLessThan()
	{
		super.testLessThan();
	}

	/**
	 * Two dates, the comparator that compares them and the sign of the result
	 *
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            the comparator
	 * @param date
	 *            the first date
	 * @param compareWithDate
	 *            the date to compare with
	 * @param expectedSign
	 *            the sign of the result: -1, 0 or 1
	 */
	record DateComparison(String description, Comparator<Date> comparator, Date date,
		Date compareWithDate, int expectedSign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases of the order of a comparator in the given sort order: ascending puts the earlier
	 * date and null first, descending the later date first and null last
	 *
	 * @param name
	 *            the name of the comparator
	 * @param comparator
	 *            the comparator
	 * @param sortOrder
	 *            the sort order the comparator sorts in
	 * @return the cases
	 */
	static Stream<DateComparison> order(final String name, final Comparator<Date> comparator,
		final SortOrder sortOrder)
	{
		final boolean ascending = sortOrder == SortOrder.ASCENDING;
		final Date earlier = new Date(1_000L);
		final Date later = new Date(2_000L);
		// another instance, equal to earlier, so that the comparison does not end at the same
		// instance
		final Date otherEarlier = new Date(1_000L);
		return Stream.of(
			new DateComparison(
				name + ", earlier with later: "
					+ (ascending ? "negative, earlier first" : "positive, later first"),
				comparator, earlier, later, ascending ? -1 : 1),
			new DateComparison(
				name + ", later with earlier: "
					+ (ascending ? "positive, earlier first" : "negative, later first"),
				comparator, later, earlier, ascending ? 1 : -1),
			new DateComparison(name + ", earlier with an equal earlier: zero", comparator, earlier,
				otherEarlier, 0),
			new DateComparison(
				name + ", null with earlier: "
					+ (ascending ? "negative, null first" : "positive, null last"),
				comparator, null, earlier, ascending ? -1 : 1),
			new DateComparison(
				name + ", earlier with null: "
					+ (ascending ? "positive, null first" : "negative, null last"),
				comparator, earlier, null, ascending ? 1 : -1),
			new DateComparison(name + ", null with null: zero", comparator, null, null, 0));
	}

	/**
	 * The cases of every constructor of {@link DateComparator} and of
	 * {@link DescendingDateComparator}: {@code new DateComparator()} stays ascending, and
	 * {@code new DateComparator(SortOrder.DESCENDING)}, which the class Javadoc promised, sorts as
	 * {@link DescendingDateComparator} (#35), which is deprecated and tested as long as it is there
	 * (#62)
	 *
	 * @return the cases
	 */
	@SuppressWarnings("deprecation")
	static Stream<DateComparison> dateComparisons()
	{
		return Stream.of(order("new DateComparator()", new DateComparator(), SortOrder.ASCENDING),
			order("new DateComparator(ASCENDING)", new DateComparator(SortOrder.ASCENDING),
				SortOrder.ASCENDING),
			order("new DateComparator(DESCENDING)", new DateComparator(SortOrder.DESCENDING),
				SortOrder.DESCENDING),
			order("new DescendingDateComparator()", new DescendingDateComparator(),
				SortOrder.DESCENDING))
			.flatMap(Function.identity());
	}

	/**
	 * Test method for {@link DateComparator#compare(Date, Date)} with every constructor and for
	 * {@link DescendingDateComparator#compare(Date, Date)}: the earlier date and null first in
	 * ascending order, the later date first and null last in descending order
	 *
	 * @param comparison
	 *            the dates, the comparator and the sign of the result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("dateComparisons")
	public void testCompareOrder(final DateComparison comparison)
	{
		Assertions.assertEquals(comparison.expectedSign(), Integer.signum(
			comparison.comparator().compare(comparison.date(), comparison.compareWithDate())));
	}

}
