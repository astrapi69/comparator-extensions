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

import java.util.Comparator;
import java.util.Locale;
import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;


/**
 * The unit test class for the class {@link LocaleComparator}.
 */
public class LocaleComparatorTest
{

	/**
	 * Two locales, the comparator that compares them and the sign of the result
	 *
	 * @param description
	 *            the name of the case
	 * @param comparator
	 *            the comparator
	 * @param object
	 *            the first locale
	 * @param compareWithObject
	 *            the locale to compare with
	 * @param expectedSign
	 *            the sign of the result: -1, 0 or 1
	 */
	record LocaleComparison(String description, Comparator<Locale> comparator, Locale object,
		Locale compareWithObject, int expectedSign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases of the order of a comparator, which is the order of the locales' toString: de
	 * before en_CA
	 *
	 * @param name
	 *            the name of the comparator
	 * @param comparator
	 *            the comparator
	 * @return the cases
	 */
	static Stream<LocaleComparison> order(final String name, final Comparator<Locale> comparator)
	{
		return Stream.of(
			new LocaleComparison(name + ", en_CA with en_CA: zero", comparator, Locale.CANADA,
				Locale.CANADA, 0),
			new LocaleComparison(name + ", en_CA with de: positive, de first", comparator,
				Locale.CANADA, Locale.GERMAN, 1),
			new LocaleComparison(name + ", de with en_CA: negative, de first", comparator,
				Locale.GERMAN, Locale.CANADA, -1));
	}

	/**
	 * The cases of null with a comparator of a factory method, which the flag nullIsGreaterThan
	 * places
	 *
	 * @param name
	 *            the name of the comparator
	 * @param comparator
	 *            the comparator
	 * @param nullIsGreaterThan
	 *            the flag the comparator was created with
	 * @return the cases
	 */
	static Stream<LocaleComparison> nulls(final String name, final Comparator<Locale> comparator,
		final boolean nullIsGreaterThan)
	{
		return Stream.of(
			new LocaleComparison(
				name + ", null with en_CA: "
					+ (nullIsGreaterThan ? "positive, null last" : "negative, null first"),
				comparator, null, Locale.CANADA, nullIsGreaterThan ? 1 : -1),
			new LocaleComparison(
				name + ", en_CA with null: "
					+ (nullIsGreaterThan ? "negative, null last" : "positive, null first"),
				comparator, Locale.CANADA, null, nullIsGreaterThan ? -1 : 1),
			new LocaleComparison(name + ", null with null: zero", comparator, null, null, 0));
	}

	/**
	 * The cases of the constructor and of the factory methods of {@link LocaleComparator}: of()
	 * puts null first, of(true) last
	 *
	 * @return the cases
	 */
	static Stream<LocaleComparison> localeComparisons()
	{
		return Stream.of(order("new LocaleComparator()", new LocaleComparator()),
			order("of()", LocaleComparator.of()), nulls("of()", LocaleComparator.of(), false),
			order("of(true)", LocaleComparator.of(true)),
			nulls("of(true)", LocaleComparator.of(true), true)).flatMap(Function.identity());
	}

	/**
	 * Test method for {@link LocaleComparator#compare(Locale, Locale)} with the constructor and the
	 * factory methods: the order of the locales' toString, and null where the flag
	 * nullIsGreaterThan puts it
	 *
	 * @param comparison
	 *            the locales, the comparator and the sign of the result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("localeComparisons")
	public void testCompare(final LocaleComparison comparison)
	{
		assertEquals(comparison.expectedSign(), Integer.signum(
			comparison.comparator().compare(comparison.object(), comparison.compareWithObject())));
	}

}
