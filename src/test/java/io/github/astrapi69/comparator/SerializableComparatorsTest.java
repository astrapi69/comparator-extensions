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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import io.github.astrapi69.comparator.object.DateComparator;
import io.github.astrapi69.comparator.object.DescendingDateComparator;
import io.github.astrapi69.comparator.object.HashCodeComparator;
import io.github.astrapi69.comparator.object.LocaleComparator;
import io.github.astrapi69.comparator.object.StringComparator;
import io.github.astrapi69.comparator.object.StringLengthComparator;

/**
 * Test for the serialization of the comparators of this library: each one survives a round trip
 * through an {@link ObjectOutputStream} and an {@link ObjectInputStream} and sorts as before, and
 * so does a {@link TreeSet} ordered by it, the use that {@link Comparator} names for a serializable
 * comparator (#60)
 */
public class SerializableComparatorsTest
{

	/**
	 * A constructor or factory method of a comparator, and values to sort with it
	 *
	 * @param <T>
	 *            the type of the values
	 * @param description
	 *            the constructor or factory method
	 * @param comparator
	 *            makes the comparator in the test, not in the method source: PIT counts what the
	 *            method source runs as covered by the first invocation alone
	 * @param values
	 *            the values to sort, null among them where the comparator takes null
	 */
	record SerializedComparator<T> (String description, Supplier<Comparator<T>> comparator,
		List<T> values) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * Every constructor and factory method of the comparators of this library that are serializable
	 * since #60, the {@link NullCheckComparator} of a serializable comparator, and
	 * {@link HashCodeComparator}, which was serializable before
	 *
	 * @return the cases
	 */
	@SuppressWarnings("deprecation") // DescendingDateComparator is tested as long as it is there
										// (#62)
	static Stream<SerializedComparator<?>> serializedComparators()
	{
		final List<String> words = Arrays.asList("bb", null, "a", "ccc", "b");
		final List<String> wordsWithoutNull = List.of("bb", "a", "ccc", "b");
		final List<Integer> numbers = Arrays.asList(2, null, 1, 3);
		final List<Locale> locales = Arrays.asList(Locale.ENGLISH, null, Locale.GERMAN);
		final List<Date> dates = Arrays.asList(new Date(1_000), null, new Date(0));
		return Stream.<SerializedComparator<?>> of(
			new SerializedComparator<>("new SortOrderComparator()",
				() -> new SortOrderComparator<Integer>(), numbers),
			new SerializedComparator<>("SortOrderComparator.of(DESCENDING)",
				() -> SortOrderComparator.<Integer> of(SortOrder.DESCENDING), numbers),
			new SerializedComparator<>("new StringComparator(DESCENDING)",
				() -> new StringComparator(SortOrder.DESCENDING), words),
			new SerializedComparator<>("StringComparator.of()", () -> StringComparator.of(), words),
			new SerializedComparator<>("StringComparator.of(DESCENDING, false)",
				() -> StringComparator.of(SortOrder.DESCENDING, false), words),
			new SerializedComparator<>("new StringLengthComparator(DESCENDING)",
				() -> new StringLengthComparator(SortOrder.DESCENDING), wordsWithoutNull),
			new SerializedComparator<>("StringLengthComparator.of()",
				() -> StringLengthComparator.of(), words),
			new SerializedComparator<>("new LocaleComparator()", () -> new LocaleComparator(),
				List.of(Locale.ENGLISH, Locale.GERMANY, Locale.GERMAN)),
			new SerializedComparator<>("LocaleComparator.of()", () -> LocaleComparator.of(),
				locales),
			new SerializedComparator<>("new DateComparator()", () -> new DateComparator(), dates),
			new SerializedComparator<>("new DateComparator(DESCENDING)",
				() -> new DateComparator(SortOrder.DESCENDING), dates),
			new SerializedComparator<>("new DescendingDateComparator()",
				() -> new DescendingDateComparator(), dates),
			new SerializedComparator<>("NullCheckComparator.of(naturalOrder(), true)",
				() -> NullCheckComparator.of(Comparator.<String> naturalOrder(), true), words),
			new SerializedComparator<>("new HashCodeComparator()",
				() -> new HashCodeComparator<String>(), words));
	}

	/**
	 * Test that the comparators of {@link #serializedComparators()} survive a round trip through an
	 * {@link ObjectOutputStream} and an {@link ObjectInputStream}: the copy sorts the values as the
	 * original does, and a {@link TreeSet} ordered by the comparator comes back with its elements
	 * in their order
	 *
	 * @param serialized
	 *            the comparator and the values
	 * @throws IOException
	 *             if the comparator cannot be written or read
	 * @throws ClassNotFoundException
	 *             if a class of the comparator cannot be found when it is read
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("serializedComparators")
	public void testRoundTrip(final SerializedComparator<?> serialized)
		throws IOException, ClassNotFoundException
	{
		assertRoundTrip(serialized);
	}

	/**
	 * Writes and reads the comparator of the given case, and a {@link TreeSet} ordered by it, and
	 * compares the copies with the originals
	 *
	 * @param <T>
	 *            the type of the values
	 * @param serialized
	 *            the case
	 * @throws IOException
	 *             if the comparator cannot be written or read
	 * @throws ClassNotFoundException
	 *             if a class of the comparator cannot be found when it is read
	 */
	private static <T> void assertRoundTrip(final SerializedComparator<T> serialized)
		throws IOException, ClassNotFoundException
	{
		final Comparator<T> comparator = serialized.comparator().get();
		final TreeSet<T> set = new TreeSet<>(comparator);
		set.addAll(serialized.values());

		final Comparator<T> copy = roundTrip(comparator);
		final TreeSet<T> copyOfTheSet = roundTrip(set);

		assertEquals(sorted(comparator, serialized.values()), sorted(copy, serialized.values()),
			"the copy sorts as the original");
		assertEquals(new ArrayList<>(set), new ArrayList<>(copyOfTheSet),
			"the copy of the set holds its elements in their order");
	}

	/**
	 * Sorts a copy of the given values with the given comparator
	 *
	 * @param <T>
	 *            the type of the values
	 * @param comparator
	 *            the comparator
	 * @param values
	 *            the values
	 * @return the sorted copy
	 */
	private static <T> List<T> sorted(final Comparator<T> comparator, final List<T> values)
	{
		final List<T> sorted = new ArrayList<>(values);
		sorted.sort(comparator);
		return sorted;
	}

	/**
	 * Writes the given object with an {@link ObjectOutputStream} and reads it back with an
	 * {@link ObjectInputStream}
	 *
	 * @param <S>
	 *            the type of the object
	 * @param object
	 *            the object
	 * @return the copy read back
	 * @throws IOException
	 *             if the object cannot be written or read
	 * @throws ClassNotFoundException
	 *             if a class of the object cannot be found when it is read
	 */
	@SuppressWarnings("unchecked") // the copy of an object is of its type
	private static <S> S roundTrip(final S object) throws IOException, ClassNotFoundException
	{
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(bytes))
		{
			out.writeObject(object);
		}
		try (ObjectInputStream in = new ObjectInputStream(
			new ByteArrayInputStream(bytes.toByteArray())))
		{
			return (S)in.readObject();
		}
	}

}
