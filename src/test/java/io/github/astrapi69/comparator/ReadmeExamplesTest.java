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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import io.github.astrapi69.comparator.factory.ComparatorFactory;
import io.github.astrapi69.comparator.object.DateComparator;
import io.github.astrapi69.comparator.object.DescendingDateComparator;
import io.github.astrapi69.comparator.object.LocaleComparator;
import io.github.astrapi69.comparator.object.StringComparator;

/**
 * The examples of the README's Quick start, each block as a test, with the values its comments name
 * as assertions. A change here belongs into the README too, and the other way round. One
 * {@code @Test} per block and not a parameterized test, as the other test classes have for the
 * cases of one behaviour (#26): each test mirrors a block of the README, whose statements test
 * different methods.
 */
class ReadmeExamplesTest
{

	/**
	 * README: "Null-safe comparisons"
	 */
	@Test
	void nullSafeComparisons()
	{
		int aToB = ComparatorExtensions.compare("a", "b"); // negative: "a" comes first
		int nullToA = ComparatorExtensions.compare(null, "a"); // -1: null comes before any value
		int aToNull = ComparatorExtensions.compare("a", null); // 1
		Boolean greater = ComparatorExtensions.isGreater(2, 1); // true
		Boolean equalIsGreater = ComparatorExtensions.isGreater(1, 1); // false
		Boolean nullIsSmaller = ComparatorExtensions.isSmaller(null, 1); // true
		Boolean same = ComparatorExtensions.isSame(1, 1); // true
		assertTrue(aToB < 0);
		assertEquals(-1, nullToA);
		assertEquals(1, aToNull);
		assertTrue(greater);
		assertFalse(equalIsGreater);
		assertTrue(nullIsSmaller);
		assertTrue(same);
	}

	/**
	 * README: "In your own compare and equals methods"
	 */
	@Test
	void nullChecks()
	{
		Integer nullFirst = ComparatorExtensions.nullCheck(null, "a"); // -1: null is the smaller
		// 1: null is the greater
		Integer nullLast = ComparatorExtensions.nullCheck(null, "a", true);
		Integer leftToCompare = ComparatorExtensions.nullCheck("a", "b"); // null: compare them
		Boolean notEqual = ComparatorExtensions.equalNullCheck("a", null); // false
		Boolean leftToEquals = ComparatorExtensions.equalNullCheck("a", "b"); // null: compare them
		assertEquals(-1, nullFirst);
		assertEquals(1, nullLast);
		assertNull(leftToCompare);
		assertFalse(notEqual);
		assertNull(leftToEquals);
	}

	/**
	 * README: "The sort order as a value"
	 */
	@Test
	void sortOrder()
	{
		List<Integer> numbers = new ArrayList<>(List.of(2, 3, 1));
		numbers.sort(SortOrderComparator.of()); // [1, 2, 3]: ascending by default
		assertEquals(List.of(1, 2, 3), numbers);
		numbers.sort(SortOrderComparator.of(SortOrder.DESCENDING)); // [3, 2, 1]
		assertEquals(List.of(3, 2, 1), numbers);
	}

	/**
	 * README: "null first or last"
	 */
	@Test
	void nullFirstOrLast()
	{
		Comparator<String> alphabetical = Comparator.naturalOrder();
		List<String> names = new ArrayList<>(Arrays.asList("b", null, "a"));
		names.sort(NullCheckComparator.of(alphabetical)); // [null, a, b]
		assertEquals(Arrays.asList(null, "a", "b"), names);
		names.sort(NullCheckComparator.of(alphabetical, true)); // [a, b, null]
		assertEquals(Arrays.asList("a", "b", null), names);
	}

	/**
	 * README: "Comparators for strings, locales and dates"
	 */
	@Test
	void objectComparators()
	{
		List<String> words = new ArrayList<>(Arrays.asList("b", null, "a"));
		words.sort(StringComparator.of()); // [a, b, null]: null last
		assertEquals(Arrays.asList("a", "b", null), words);
		words.sort(StringComparator.of(SortOrder.DESCENDING)); // [b, a, null]
		assertEquals(Arrays.asList("b", "a", null), words);

		List<Locale> locales = new ArrayList<>(
			Arrays.asList(Locale.ENGLISH, null, Locale.GERMANY, Locale.GERMAN));
		locales.sort(LocaleComparator.of()); // [null, de, de_DE, en]: by toString, null first
		assertEquals(Arrays.asList(null, Locale.GERMAN, Locale.GERMANY, Locale.ENGLISH), locales);

		Date earlier = new Date(0);
		Date later = new Date(1_000);
		List<Date> dates = new ArrayList<>(Arrays.asList(later, null, earlier));
		dates.sort(new DateComparator()); // [null, earlier, later]
		assertEquals(Arrays.asList(null, earlier, later), dates);
		dates.sort(new DescendingDateComparator()); // [later, earlier, null]
		assertEquals(Arrays.asList(later, earlier, null), dates);
	}

	/**
	 * README: "Chains", the block and the JDK expression of its paragraph, which builds the first
	 * chain of the block in one expression (#65)
	 */
	@Test
	void chains()
	{
		ChainableComparator<String> byLength = ChainableComparator
			.of(Comparator.comparingInt(String::length));
		byLength.addComparator(Comparator.naturalOrder()); // then alphabetically
		List<String> words = new ArrayList<>(List.of("bb", "c", "ab", "a"));
		words.sort(byLength); // [a, c, ab, bb]
		assertEquals(List.of("a", "c", "ab", "bb"), words);

		List<String> sameWords = new ArrayList<>(List.of("bb", "c", "ab", "a"));
		sameWords
			.sort(Comparator.comparingInt(String::length).thenComparing(Comparator.naturalOrder()));
		assertEquals(List.of("a", "c", "ab", "bb"), sameWords);

		ChainableComparator<String> longestFirst = ChainableComparator
			.of(Comparator.comparingInt(String::length), true);
		longestFirst.addComparator(Comparator.naturalOrder());
		words.sort(longestFirst); // [ab, bb, a, c]
		assertEquals(List.of("ab", "bb", "a", "c"), words);

		// UnsupportedOperationException: a chain is fixed once it has compared
		assertThrows(UnsupportedOperationException.class,
			() -> byLength.addComparator(Comparator.reverseOrder()));
	}

	/**
	 * README: "By a property or a defined order"
	 */
	@Test
	void factories()
	{
		List<String> words = new ArrayList<>(List.of("ccc", "a", "bb"));
		words.sort(ComparatorFactory.newBeanPropertyComparator(String::length)); // [a, bb, ccc]
		assertEquals(List.of("a", "bb", "ccc"), words);

		List<String> priorities = List.of("high", "medium", "low");
		List<String> tasks = new ArrayList<>(List.of("low", "high", "medium", "high"));
		// [high, high, medium, low]
		tasks.sort(ComparatorFactory.newDefinedOrderComparator(priorities));
		assertEquals(List.of("high", "high", "medium", "low"), tasks);
	}

}
