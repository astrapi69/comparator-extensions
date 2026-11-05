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
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.comparator.object.LocaleComparator;
import io.github.astrapi69.comparator.object.StringComparator;
import io.github.astrapi69.comparator.object.StringLengthComparator;

/**
 * The unit test class for the class {@link NullCheckComparator}.
 */
public class NullCheckComparatorTest
{

	/**
	 * Test method for {@link NullCheckComparator.NullCheckComparatorBuilder#toString()}: the
	 * builder names its class and its two values
	 */
	@Test
	public void testBuilderToString()
	{
		assertEquals(
			"NullCheckComparator."
				+ "NullCheckComparatorBuilder(decoratedComparator=null, nullIsGreaterThan=false)",
			NullCheckComparator.builder().toString());
	}

	/**
	 * A comparison of a {@link NullCheckComparator} and the sign of its result
	 *
	 * @param description
	 *            the name of the case
	 * @param comparison
	 *            the comparison
	 * @param expectedSign
	 *            the sign of its result: -1, 0 or 1
	 */
	record NullCheckComparison(String description, IntSupplier comparison, int expectedSign) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The cases of a {@link NullCheckComparator}: null where its flag nullIsGreaterThan puts it,
	 * last if it is true and first otherwise, two nulls equal, and two values in the order of the
	 * comparator it decorates
	 *
	 * @param <T>
	 *            the generic type of the objects to compare
	 * @param name
	 *            the name of the comparator
	 * @param comparator
	 *            the comparator
	 * @param nullIsGreaterThan
	 *            the flag the comparator was created with
	 * @param value
	 *            a value
	 * @param smallerValue
	 *            a value that the decorated comparator puts before the value
	 * @return the cases
	 */
	static <T> Stream<NullCheckComparison> comparisonsOf(final String name,
		final Comparator<T> comparator, final boolean nullIsGreaterThan, final T value,
		final T smallerValue)
	{
		final String nullPlace = nullIsGreaterThan ? "null last" : "null first";
		return Stream.of(
			new NullCheckComparison(
				name + ", " + value + " with null: "
					+ (nullIsGreaterThan ? "negative, " : "positive, ") + nullPlace,
				() -> comparator.compare(value, null), nullIsGreaterThan ? -1 : 1),
			new NullCheckComparison(
				name + ", null with " + value + ": "
					+ (nullIsGreaterThan ? "positive, " : "negative, ") + nullPlace,
				() -> comparator.compare(null, value), nullIsGreaterThan ? 1 : -1),
			new NullCheckComparison(name + ", null with null: zero",
				() -> comparator.compare(null, null), 0),
			new NullCheckComparison(
				name + ", " + value + " with " + smallerValue
					+ ": positive, as the decorated comparator",
				() -> comparator.compare(value, smallerValue), 1),
			new NullCheckComparison(
				name + ", " + smallerValue + " with " + value
					+ ": negative, as the decorated comparator",
				() -> comparator.compare(smallerValue, value), -1));
	}

	/**
	 * The cases of the constructors and of the factory methods
	 * {@link NullCheckComparator#of(Comparator)} and
	 * {@link NullCheckComparator#of(Comparator, boolean)}, with {@link TestLocaleComparator}, which
	 * puts de before en_CA, and with the {@link SortOrderComparator} that
	 * {@link TestStringComparator#of(boolean)} decorates, which puts Al before Alex
	 *
	 * @return the cases
	 */
	static Stream<NullCheckComparison> nullCheckComparisons()
	{
		return Stream.of(
			comparisonsOf("NullCheckComparator.of(TestLocaleComparator)",
				NullCheckComparator.of(new TestLocaleComparator()), false, Locale.CANADA,
				Locale.GERMAN),
			comparisonsOf("TestLocaleComparator.of(true)", TestLocaleComparator.of(true), true,
				Locale.CANADA, Locale.GERMAN),
			comparisonsOf("new NullCheckComparator(TestLocaleComparator, false)",
				new NullCheckComparator<>(new TestLocaleComparator(), false), false, Locale.CANADA,
				Locale.GERMAN),
			comparisonsOf("new NullCheckComparator(TestLocaleComparator)",
				new NullCheckComparator<>(new TestLocaleComparator()), false, Locale.CANADA,
				Locale.GERMAN),
			comparisonsOf("TestStringComparator.of(true)", TestStringComparator.of(true), true,
				"Alex", "Al"))
			.flatMap(Function.identity());
	}

	/**
	 * Test method for {@link NullCheckComparator#compare(Object, Object)} with the constructors and
	 * the factory methods: null where the flag nullIsGreaterThan puts it, and two values as the
	 * decorated comparator orders them
	 *
	 * @param comparison
	 *            the comparison and the sign of its result
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("nullCheckComparisons")
	public void testCompare(final NullCheckComparison comparison)
	{
		assertEquals(comparison.expectedSign(), Integer.signum(comparison.comparison().getAsInt()));
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

	/**
	 * A factory method that decorates a new comparator with a {@link NullCheckComparator}, a
	 * comparator as the one it decorates, and the values to sort
	 *
	 * @param <T>
	 *            the generic type of the objects to compare
	 * @param name
	 *            the name of the factory method
	 * @param factory
	 *            the factory method
	 * @param decoratedClass
	 *            the class of the comparator it decorates, which its Javadoc named as the type it
	 *            returns before #46
	 * @param decorated
	 *            a new comparator of the decorated class, with the sort order of the factory method
	 * @param nullIsGreaterThan
	 *            the flag of the factory method, whether null comes last
	 * @param values
	 *            the values to sort, none of them null
	 */
	record DecoratingFactory<T> (String name, Supplier<Comparator<T>> factory,
		Class<?> decoratedClass, Comparator<T> decorated, boolean nullIsGreaterThan,
		List<T> values) {
		@Override
		public String toString()
		{
			return name;
		}
	}

	/**
	 * The {@code of} factory methods of {@link StringComparator}, {@link StringLengthComparator}
	 * and {@link LocaleComparator}, each with the comparator it decorates. The strings sort
	 * differently in each sort order of both string comparators, so that a factory method that
	 * decorated a comparator of another sort order or of the other class would fail
	 *
	 * @return the factory methods
	 */
	static Stream<DecoratingFactory<?>> decoratingFactories()
	{
		final List<String> strings = List.of("Leon", "Alex", "Billy", "Al");
		final List<Locale> locales = List.of(Locale.ENGLISH, Locale.GERMANY, Locale.GERMAN);
		return Stream.<DecoratingFactory<?>> of(
			new DecoratingFactory<>("StringComparator.of()", () -> StringComparator.of(),
				StringComparator.class, new StringComparator(), true, strings),
			new DecoratingFactory<>("StringComparator.of(false)", () -> StringComparator.of(false),
				StringComparator.class, new StringComparator(), false, strings),
			new DecoratingFactory<>("StringComparator.of(DESCENDING)",
				() -> StringComparator.of(SortOrder.DESCENDING), StringComparator.class,
				new StringComparator(SortOrder.DESCENDING), true, strings),
			new DecoratingFactory<>("StringComparator.of(DESCENDING, false)",
				() -> StringComparator.of(SortOrder.DESCENDING, false), StringComparator.class,
				new StringComparator(SortOrder.DESCENDING), false, strings),
			new DecoratingFactory<>("StringLengthComparator.of()",
				() -> StringLengthComparator.of(), StringLengthComparator.class,
				new StringLengthComparator(), true, strings),
			new DecoratingFactory<>("StringLengthComparator.of(false)",
				() -> StringLengthComparator.of(false), StringLengthComparator.class,
				new StringLengthComparator(), false, strings),
			new DecoratingFactory<>("StringLengthComparator.of(DESCENDING)",
				() -> StringLengthComparator.of(SortOrder.DESCENDING), StringLengthComparator.class,
				new StringLengthComparator(SortOrder.DESCENDING), true, strings),
			new DecoratingFactory<>("StringLengthComparator.of(DESCENDING, false)",
				() -> StringLengthComparator.of(SortOrder.DESCENDING, false),
				StringLengthComparator.class, new StringLengthComparator(SortOrder.DESCENDING),
				false, strings),
			new DecoratingFactory<>("LocaleComparator.of()", () -> LocaleComparator.of(),
				LocaleComparator.class, new LocaleComparator(), false, locales),
			new DecoratingFactory<>("LocaleComparator.of(true)", () -> LocaleComparator.of(true),
				LocaleComparator.class, new LocaleComparator(), true, locales));
	}

	/**
	 * Test for the {@code of} factory methods of {@link StringComparator},
	 * {@link StringLengthComparator} and {@link LocaleComparator}: each returns a new
	 * {@link NullCheckComparator}, not an object of its own class, that sorts as the class it
	 * decorates and puts null where its flag says, last for nullIsGreaterThan and first otherwise
	 * (#46)
	 *
	 * @param factory
	 *            the factory method, the comparator it decorates and the values to sort
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("decoratingFactories")
	public void testFactoryReturnsANullCheckComparator(final DecoratingFactory<?> factory)
	{
		assertDecorates(factory);
	}

	/**
	 * Asserts that the given factory method returns a new {@link NullCheckComparator} that sorts as
	 * the comparator it decorates, with null where its flag says
	 *
	 * @param <T>
	 *            the generic type of the objects to compare
	 * @param factory
	 *            the factory method, the comparator it decorates and the values to sort
	 */
	private static <T> void assertDecorates(final DecoratingFactory<T> factory)
	{
		final Comparator<T> comparator = factory.factory().get();
		assertInstanceOf(NullCheckComparator.class, comparator);
		assertFalse(factory.decoratedClass().isInstance(comparator));
		assertNotSame(comparator, factory.factory().get());
		final List<T> expected = new ArrayList<>(factory.values());
		expected.sort(factory.decorated());
		expected.add(factory.nullIsGreaterThan() ? expected.size() : 0, null);
		final List<T> actual = new ArrayList<>(factory.values());
		actual.add(actual.size() / 2, null);
		actual.sort(comparator);
		assertEquals(expected, actual);
	}

}
