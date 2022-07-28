/**
 * The MIT License
 *
 * Copyright (C) 2015 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.comparator;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * The unit test class for the class {@link NullCheckComparator}.
 */
public class NullCheckComparatorTest
{

	/**
	 * Test method for {@link NullCheckComparator#compare(Object, Object)}.
	 */
	@Test
	public void testCompare()
	{
		int expected;
		int actual;
		Comparator<Locale> localeComparator = NullCheckComparator.of(new TestLocaleComparator());
		String toString = NullCheckComparator.builder().toString();
		assertEquals(
			"NullCheckComparator."
				+ "NullCheckComparatorBuilder(decoratedComparator=null, nullIsGreaterThan=false)",
			toString);
		actual = localeComparator.compare(Locale.CANADA, null);
		expected = 1;
		assertEquals(expected, actual);

		actual = localeComparator.compare(null, null);
		expected = 0;
		assertEquals(expected, actual);

		actual = localeComparator.compare(null, Locale.CANADA);
		expected = -1;
		assertEquals(expected, actual);

		// set null flag to true so null are greater...
		localeComparator = TestLocaleComparator.of(true);

		actual = localeComparator.compare(Locale.CANADA, null);
		expected = -1;
		assertEquals(expected, actual);

		actual = localeComparator.compare(null, null);
		expected = 0;
		assertEquals(expected, actual);

		actual = localeComparator.compare(null, Locale.CANADA);
		expected = 1;
		assertEquals(expected, actual);

		localeComparator = new NullCheckComparator<>(new TestLocaleComparator(), false);

		actual = localeComparator.compare(Locale.CANADA, null);
		expected = 1;
		assertEquals(expected, actual);

		actual = localeComparator.compare(Locale.CANADA, Locale.GERMAN);
		expected = 1;
		assertEquals(expected, actual);

		localeComparator = new NullCheckComparator<>(new TestLocaleComparator());

		actual = localeComparator.compare(Locale.CANADA, null);
		expected = 1;
		assertEquals(expected, actual);

		actual = localeComparator.compare(Locale.CANADA, Locale.GERMAN);
		expected = 1;
		assertEquals(expected, actual);

	}

	/**
	 * Test method for {@link NullCheckComparator#compare(Object, Object)}
	 */
	@Test
	public void testCompareStringComparator()
	{
		boolean expected;
		int actual;

		String alex = "Alex";

		Comparator<String> comparator = TestStringComparator.of(true);

		actual = comparator.compare(alex, null);
		expected = actual < 0;
		assertTrue(expected);

		actual = comparator.compare(null, alex);
		expected = 0 < actual;
		assertTrue(expected);

		// Now lets see a demo on a list...
		List<String> list = new ArrayList<>();
		list.add(alex);
		list.add(null);

		expected = list.indexOf(alex) == 0;
		assertTrue(expected);

		Collections.sort(list, comparator);

		expected = list.indexOf(alex) == 0;
		assertTrue(expected);

	}

	@Test
	public void testNullComparator()
	{
		String actual;
		String expected;

		IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class,
			() -> new NullCheckComparator<Locale>(null));
		expected = "Given decoratedComparator may not be null.";
		actual = exception.getMessage();
		assertEquals(expected, actual);
	}

}
