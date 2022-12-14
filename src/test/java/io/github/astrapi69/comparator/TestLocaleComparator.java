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

import java.util.Comparator;
import java.util.Locale;

/**
 * The class {@link TestLocaleComparator} compares {@linkplain Locale} objects. This Comparator does
 * not check null, for null check you can decorate it with the
 * {@link NullCheckComparator#of(Comparator)}. an example is in the unit test class from
 * {@linkplain NullCheckComparator}.
 */
public class TestLocaleComparator implements Comparator<Locale>
{

	/**
	 * Factory method to create a new {@link TestLocaleComparator} object.
	 *
	 * @return the new {@link TestLocaleComparator} object
	 */
	public static Comparator<Locale> of()
	{
		return TestLocaleComparator.of(false);
	}

	/**
	 * Factory method to create a new {@link TestLocaleComparator} object.
	 *
	 * @param nullIsGreaterThan
	 *            the flag that specifies if null objects is greater than non null objects.
	 * @return the new {@link TestLocaleComparator} object
	 */
	public static Comparator<Locale> of(final boolean nullIsGreaterThan)
	{
		return NullCheckComparator.of(new TestLocaleComparator(), nullIsGreaterThan);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(final Locale o1, final Locale o2)
	{
		return o1.toString().compareTo(o2.toString());
	}

}
