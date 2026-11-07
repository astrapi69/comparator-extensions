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

/**
 * The class {@link TestStringComparator} sorts String objects in ascending order as default. This
 * class is intended only for unit tests
 *
 * @author Asterios Raptis
 */
public class TestStringComparator extends SortOrderComparator<String>
{

	public TestStringComparator()
	{
	}

	/**
	 * Instantiates a new {@link TestStringComparator}.
	 *
	 * @param sortOrder
	 *            the sort order
	 */
	public TestStringComparator(final SortOrder sortOrder)
	{
		super(sortOrder);
	}

	/**
	 * Factory method to create a null-safe {@link Comparator} that decorates a new
	 * {@link SortOrderComparator} with a {@link NullCheckComparator}.
	 *
	 * @return a new {@link NullCheckComparator} that decorates a new {@link SortOrderComparator}
	 */
	@SuppressWarnings("unchecked")
	public static Comparator<String> of()
	{
		return TestStringComparator.of(false);
	}

	/**
	 * Factory method to create a null-safe {@link Comparator} that decorates a new
	 * {@link SortOrderComparator} with a {@link NullCheckComparator}.
	 *
	 * @param nullIsGreaterThan
	 *            the flag that specifies if null objects is greater than non null objects.
	 * @return a new {@link NullCheckComparator} that decorates a new {@link SortOrderComparator}
	 */
	public static Comparator<String> of(final boolean nullIsGreaterThan)
	{
		return NullCheckComparator.of(SortOrderComparator.of(), nullIsGreaterThan);
	}

	/**
	 * Factory method to create a null-safe {@link Comparator} that decorates a new
	 * {@link SortOrderComparator} with a {@link NullCheckComparator}.
	 *
	 * @param sortOrder
	 *            the sort order
	 * @return a new {@link NullCheckComparator} that decorates a new {@link SortOrderComparator}
	 */
	@SuppressWarnings("unchecked")
	public static Comparator<String> of(final SortOrder sortOrder)
	{
		return TestStringComparator.of(sortOrder, false);
	}

	/**
	 * Factory method to create a null-safe {@link Comparator} that decorates a new
	 * {@link SortOrderComparator} with a {@link NullCheckComparator}.
	 *
	 * @param nullIsGreaterThan
	 *            the flag that specifies if null objects is greater than non null objects
	 * @param sortOrder
	 *            the sort order
	 * @return a new {@link NullCheckComparator} that decorates a new {@link SortOrderComparator}
	 */
	public static Comparator<String> of(final SortOrder sortOrder, final boolean nullIsGreaterThan)
	{
		return NullCheckComparator.of(SortOrderComparator.of(sortOrder), nullIsGreaterThan);
	}

}
