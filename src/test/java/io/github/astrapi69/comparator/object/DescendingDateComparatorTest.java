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
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.comparator.SortOrder;
import io.github.astrapi69.date.CreateDateExtensions;

/**
 * The unit test class for the class {@link DescendingDateComparator}, which is deprecated since 3.2
 * and tested as long as it is there (#62)
 */
@SuppressWarnings("deprecation")
public class DescendingDateComparatorTest
{

	/** The comparator. */
	Comparator<Date> comparator;

	/**
	 * Sets up method will be invoked before every unit test method
	 */
	@BeforeEach
	public void setUp()
	{
		comparator = new DescendingDateComparator();
	}

	/**
	 * Test method for {@link DescendingDateComparator#compare(Date, Date)}
	 */
	@Test
	public void testCompare()
	{
		final Date past = CreateDateExtensions.newDate(2009, 3, 26, 10, 37, 4);
		final Date before = CreateDateExtensions.newDate(2010, 3, 26, 10, 37, 4);

		final Date after = CreateDateExtensions.newDate(2010, 3, 27, 10, 37, 4);
		final Date future = CreateDateExtensions.newDate(2011, 3, 27, 10, 37, 4);

		final List<Date> dates = new ArrayList<>();

		dates.add(before);
		dates.add(future);
		dates.add(past);
		dates.add(after);
		dates.add(null);
		Assertions.assertTrue(dates.get(0).equals(before));
		Assertions.assertTrue(dates.get(1).equals(future));
		Assertions.assertTrue(dates.get(2).equals(past));
		Assertions.assertTrue(dates.get(3).equals(after));
		Assertions.assertTrue(dates.get(4) == null);
		// Sort collection with our DescendingDateComparator...
		dates.sort(comparator);
		Assertions.assertTrue(dates.get(0).equals(future));
		Assertions.assertTrue(dates.get(1).equals(after));
		Assertions.assertTrue(dates.get(2).equals(before));
		Assertions.assertTrue(dates.get(3).equals(past));
		Assertions.assertTrue(dates.get(4) == null);
	}

	/**
	 * Two dates to compare, either of them may be null
	 *
	 * @param date
	 *            the first date
	 * @param compareWithDate
	 *            the date to compare with
	 */
	record DatePair(Date date, Date compareWithDate) {
		@Override
		public String toString()
		{
			return name(date) + " with " + name(compareWithDate);
		}

		private static String name(final Date date)
		{
			return date == null ? "null" : String.valueOf(date.getTime());
		}
	}

	/**
	 * Every pair of seven dates: null, the earliest and the latest date that a {@link Date} holds,
	 * two equal dates and two others
	 *
	 * @return the pairs
	 */
	static Stream<DatePair> datePairs()
	{
		final List<Date> dates = Arrays.asList(null, new Date(Long.MIN_VALUE), new Date(-1),
			new Date(0), new Date(0), new Date(1_000), new Date(Long.MAX_VALUE));
		return dates.stream()
			.flatMap(date -> dates.stream().map(other -> new DatePair(date, other)));
	}

	/**
	 * Test method for {@link DescendingDateComparator#compare(Date, Date)}: it compares every pair
	 * as {@code new DateComparator(SortOrder.DESCENDING)} does, which its deprecation names in its
	 * place (#62)
	 *
	 * @param pair
	 *            the dates
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("datePairs")
	public void testComparesAsDateComparatorDescending(final DatePair pair)
	{
		Assertions.assertEquals(
			new DateComparator(SortOrder.DESCENDING).compare(pair.date(), pair.compareWithDate()),
			comparator.compare(pair.date(), pair.compareWithDate()));
	}

	/**
	 * Test that {@link DescendingDateComparator} is a type of its own and not a
	 * {@link DateComparator}, so a caller that tests the type of a comparator sees no change (#62)
	 */
	@Test
	public void testIsNotADateComparator()
	{
		Assertions
			.assertFalse(DateComparator.class.isAssignableFrom(DescendingDateComparator.class));
	}

}
