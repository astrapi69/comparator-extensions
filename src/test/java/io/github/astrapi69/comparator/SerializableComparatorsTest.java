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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.util.Comparator;
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
 * Test for the serialization of the comparators of this library (#60)
 */
public class SerializableComparatorsTest
{

	/**
	 * A constructor or factory method of a comparator, and the class that
	 * {@link ObjectOutputStream} names as not serializable when it writes the comparator
	 *
	 * @param description
	 *            the constructor or factory method
	 * @param comparator
	 *            makes the comparator in the test, not in the method source: PIT counts what the
	 *            method source runs as covered by the first invocation alone
	 * @param notSerializable
	 *            the class that is not serializable
	 */
	record NotSerializable(String description, Supplier<Comparator<?>> comparator,
		Class<?> notSerializable) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The constructors and factory methods whose comparator is not serializable: the {@code of}
	 * factories put one into a {@link NullCheckComparator}, which is {@link java.io.Serializable}
	 *
	 * @return the cases
	 */
	@SuppressWarnings("deprecation") // DescendingDateComparator is tested as long as it is there
										// (#62)
	static Stream<NotSerializable> notSerializable()
	{
		return Stream.of(
			new NotSerializable("StringComparator.of()", () -> StringComparator.of(),
				StringComparator.class),
			new NotSerializable("StringLengthComparator.of()", () -> StringLengthComparator.of(),
				StringLengthComparator.class),
			new NotSerializable("LocaleComparator.of()", () -> LocaleComparator.of(),
				LocaleComparator.class),
			new NotSerializable("SortOrderComparator.of()", () -> SortOrderComparator.of(),
				SortOrderComparator.class),
			new NotSerializable("new DateComparator()", () -> new DateComparator(),
				DateComparator.class),
			new NotSerializable("new DescendingDateComparator()",
				() -> new DescendingDateComparator(), DescendingDateComparator.class));
	}

	/**
	 * Test that the comparators of {@link #notSerializable()} cannot be written: the
	 * {@link ObjectOutputStream} throws a {@link NotSerializableException} that names the class
	 * that is not serializable
	 *
	 * @param notSerializable
	 *            the comparator and the class
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("notSerializable")
	public void testNotSerializable(final NotSerializable notSerializable)
	{
		final Comparator<?> comparator = notSerializable.comparator().get();

		final NotSerializableException exception = assertThrows(NotSerializableException.class,
			() -> serialize(comparator));

		assertEquals(notSerializable.notSerializable().getName(), exception.getMessage());
	}

	/**
	 * Writes the given object with an {@link ObjectOutputStream}
	 *
	 * @param object
	 *            the object
	 * @return the bytes written
	 * @throws IOException
	 *             if the object cannot be written
	 */
	static byte[] serialize(final Object object) throws IOException
	{
		final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		try (ObjectOutputStream out = new ObjectOutputStream(bytes))
		{
			out.writeObject(object);
		}
		return bytes.toByteArray();
	}

}
