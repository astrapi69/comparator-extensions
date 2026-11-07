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

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test for the type of the comparator that {@link NullCheckComparator} decorates: it takes a
 * {@code Comparator<? super T>}, as {@link Comparator#nullsFirst(Comparator)} and
 * {@link Comparator#nullsLast(Comparator)} do, so their forms compile with it and sort as they do
 * (#38). Each form is a statement of this class: it compiles, or the build fails
 */
public class NullCheckComparatorWildcardTest
{

	/** A comparator of a supertype of {@link String}, by the length */
	private static final Comparator<CharSequence> BY_LENGTH = Comparator
		.comparingInt(CharSequence::length);

	/** A comparator of {@link Object}, as {@link Collator} is one */
	private static final Collator GERMAN = Collator.getInstance(Locale.GERMAN);

	/**
	 * A form of {@link Comparator#nullsFirst(Comparator)} or
	 * {@link Comparator#nullsLast(Comparator)} with {@link NullCheckComparator} in its place, and
	 * the JDK comparator it sorts as
	 *
	 * @param description
	 *            the name of the case
	 * @param sort
	 *            sorts the given names with the form, in the test, not in the method source: PIT
	 *            counts what the method source runs as covered by the first invocation alone
	 * @param jdkComparator
	 *            the comparator of the JDK form
	 */
	record Form(String description, Consumer<List<String>> sort, Comparator<String> jdkComparator) {
		@Override
		public String toString()
		{
			return description;
		}
	}

	/**
	 * The seven forms of #38 that did not compile: a comparator whose type the call infers, a
	 * comparator of a supertype where the target type is the subtype, and a comparator of
	 * {@code ? super T} in a generic method
	 *
	 * @return the forms
	 */
	static Stream<Form> forms()
	{
		return Stream.of(
			new Form("of(naturalOrder())",
				names -> names.sort(NullCheckComparator.of(Comparator.naturalOrder())),
				Comparator.nullsFirst(Comparator.naturalOrder())),
			new Form("of(naturalOrder(), true)",
				names -> names.sort(NullCheckComparator.of(Comparator.naturalOrder(), true)),
				Comparator.nullsLast(Comparator.naturalOrder())),
			new Form("new NullCheckComparator<>(naturalOrder())",
				names -> names.sort(new NullCheckComparator<>(Comparator.naturalOrder())),
				Comparator.nullsFirst(Comparator.naturalOrder())),
			new Form("of(a comparator of CharSequence) as a comparator of String", names -> {
				final Comparator<String> comparator = NullCheckComparator.of(BY_LENGTH);
				names.sort(comparator);
			}, Comparator.nullsFirst(BY_LENGTH)),
			new Form("builder() with a comparator of CharSequence", names -> {
				final Comparator<String> comparator = NullCheckComparator.<String> builder()
					.decoratedComparator(BY_LENGTH).build();
				names.sort(comparator);
			}, Comparator.nullsFirst(BY_LENGTH)),
			new Form("of(a Collator, a comparator of Object) as a comparator of String", names -> {
				final Comparator<String> comparator = NullCheckComparator.of(GERMAN);
				names.sort(comparator);
			}, Comparator.nullsFirst(GERMAN)),
			new Form("of(a comparator of ? super T) in a generic method",
				names -> names.sort(NullCheckComparatorWildcardTest.<String> nullSafe(BY_LENGTH)),
				Comparator.nullsFirst(BY_LENGTH)));
	}

	/**
	 * Test that the forms of {@link #forms()} compile with {@link NullCheckComparator} and sort as
	 * the JDK comparator does
	 *
	 * @param form
	 *            the form and the JDK comparator
	 */
	@ParameterizedTest(name = "{0}")
	@MethodSource("forms")
	public void testTheFormsOfTheJdk(final Form form)
	{
		final List<String> names = new ArrayList<>(Arrays.asList("bb", null, "a"));
		final List<String> sortedByTheJdk = new ArrayList<>(names);
		sortedByTheJdk.sort(form.jdkComparator());

		form.sort().accept(names);

		assertEquals(sortedByTheJdk, names);
	}

	/**
	 * A generic helper that takes a comparator of {@code ? super T}, as
	 * {@link Comparator#nullsFirst(Comparator)} does, and decorates it with
	 * {@link NullCheckComparator}
	 *
	 * @param <T>
	 *            the type of the objects to compare
	 * @param comparator
	 *            the comparator of {@code T} or of a supertype of it
	 * @return the decorated comparator
	 */
	private static <T> Comparator<T> nullSafe(final Comparator<? super T> comparator)
	{
		return NullCheckComparator.of(comparator);
	}

}
