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

import java.util.Comparator;
import java.util.Locale;

import io.github.astrapi69.comparator.NullCheckComparator;

/**
 * The class {@link LocaleComparator} compares {@linkplain Locale} objects by their
 * {@link Locale#toString()}. It does not check null and throws a {@link NullPointerException} when
 * comparing null; {@link #of()} and {@link #of(boolean)} decorate it with a
 * {@link NullCheckComparator}, which places null.
 */
public class LocaleComparator implements Comparator<Locale>
{

	/**
	 * Instantiates a new {@link LocaleComparator}, which does not check for null and throws a
	 * {@link NullPointerException} when comparing null; the factory methods {@link #of()} and
	 * {@link #of(boolean)} decorate it with a null check.
	 */
	public LocaleComparator()
	{
	}

	/**
	 * Factory method to create a null-safe {@link Comparator} that decorates a new
	 * {@link LocaleComparator} with a {@link NullCheckComparator} in which null is less than every
	 * non-null locale, so null comes first; the same as {@link #of(boolean)} with false.
	 *
	 * @return a new {@link NullCheckComparator} that decorates a new {@link LocaleComparator}
	 */
	public static Comparator<Locale> of()
	{
		return LocaleComparator.of(false);
	}

	/**
	 * Factory method to create a null-safe {@link Comparator} that decorates a new
	 * {@link LocaleComparator} with a {@link NullCheckComparator}.
	 *
	 * @param nullIsGreaterThan
	 *            the flag that specifies if null objects is greater than non null objects.
	 * @return a new {@link NullCheckComparator} that decorates a new {@link LocaleComparator}
	 */
	public static Comparator<Locale> of(boolean nullIsGreaterThan)
	{
		return NullCheckComparator.of(new LocaleComparator(), nullIsGreaterThan);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(Locale o1, Locale o2)
	{
		return o1.toString().compareTo(o2.toString());
	}

}
