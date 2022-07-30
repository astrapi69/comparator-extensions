/**
 * The MIT License
 *
 * Copyright (C) 2015 Asterios Raptis
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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.astrapi69.date.CreateDateExtensions;

/**
 * The unit test class for the class {@link DescendingDateComparator}.
 */
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
		comparator = new DateComparator();
	}

	/**
	 * Tear down method will be invoked after every unit test method
	 */
	@AfterEach
	public void tearDown()
	{
		comparator = null;
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
		comparator = new DescendingDateComparator();
		dates.sort(comparator);
		Assertions.assertTrue(dates.get(0).equals(future));
		Assertions.assertTrue(dates.get(1).equals(after));
		Assertions.assertTrue(dates.get(2).equals(before));
		Assertions.assertTrue(dates.get(3).equals(past));
		Assertions.assertTrue(dates.get(4) == null);
	}

}
